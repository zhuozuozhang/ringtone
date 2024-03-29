package com.hrtxn.ringtone.common.api;

import com.hrtxn.ringtone.common.exception.NoLoginException;
import com.hrtxn.ringtone.common.utils.*;
import com.hrtxn.ringtone.common.utils.json.JsonUtil;
import com.hrtxn.ringtone.project.system.user.domain.User;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreeNetsOrderAttached;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsRing;
import com.hrtxn.ringtone.project.threenets.threenet.json.migu.MiguAddGroupRespone;
import com.hrtxn.ringtone.project.threenets.threenet.json.migu.MiguAddPhoneRespone;
import com.hrtxn.ringtone.project.threenets.threenet.json.migu.MiguAddRingRespone;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsOrderMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Author:zcy
 * Date:2019-07-20 16:17
 * Description:咪咕企业彩铃对接系统
 */
@Slf4j
public class MiguApi implements Serializable {
    private static final long serialVersionUID = -8779307345117075476L;
    public static String LOGIN_URL = "http://211.137.107.18:8888/cm/loginmanager!login.action";//咪咕平台登录地址
    public static String CODE_URL = "http://211.137.107.18:8888/page/cm/image.jsp";//验证码地址
    public static String refreshCrbtStatus_url = "http://211.137.107.18:8888/cm/userpay!refreshCrbtStatus.action"; // 查看企业彩铃状态
    public static String refreshMonthlyStatus_url = "http://211.137.107.18:8888/cm/userpay!refreshMonthlyStatus.action"; // 查看包月状态
    public static String getRingPage_url = "http://211.137.107.18:8888/cm/userpay!findGroupMember.action"; // 获取铃音列表页面
    public static String getPhonePage_url = "http://211.137.107.18:8888/cm/userpay!findGroupMemberLikePhoneNoOrName.action"; // 查询用户信息
    public static String refreshVbrtStatus_url = "http://211.137.107.18:8888/cm/userpay!refreshVrbtStatus.action"; // 刷新视频彩铃功能
    public static String remindOrderCrbtAndMonth_URL = "http://211.137.107.18:8888/cm/userpay!remindOrderCrbtAndMonth.action";//短信提醒url
    public static String orderCrbtAndMonth_URL = "http://211.137.107.18:8888/cm/userpay!orderCrbtAndMonth.action";//直接开通包月url
    public static String addGroup_url = "http://211.137.107.18:8888/cm/groupInfo!addGroup.action";//增加商户url
    public static String findCircleRingPageById_url = "http://211.137.107.18:8888/cm/setRingAction!findCircleRingById.action"; // 获取铃音信息
    public static String ADD_PHONE_URL = "http://211.137.107.18:8888/cm/groupInfo!inviteGroupUsers.action";//增加号码地址
    public static String importRing_url = "http://211.137.107.18:8888/cm/cmRing!importRing.action";//增加铃音url
    public static String uploadRing_url = "http://211.137.107.18:8888/cm/cmRing!uploadRing.action";//增加铃音url
    public static String settingRing_url = "http://211.137.107.18:8888/cm/cmCircleRing!setCircleRingById4User.action"; // 铃音设置

    public static String findCircleMsgList_url = "http://211.137.107.18:8888/cm/groupInfo!findCircleMsgList.action";// 查看消息
    public static String checkAdminMsisdn_url = "http://211.137.107.18:8888/cm/groupInfo!checkAdminMsisdn.action";
    public static String checkGroupName_url = "http://211.137.107.18:8888/cm/groupInfo!checkGroupName.action";
    public static String ringSetting_url = "http://211.137.107.18:8888/cm/groupInfo!ringSetting.action";
    public static String deleteCircleRingById_url = "http://211.137.107.18:8888/cm/cmCircleRing!deleteCircleRingById.action";
    public static String deletePhone_url = "http://211.137.107.18:8888/cm/groupInfo!deleteUserByMemberId.action";
    public static String getRingSettingListByMsisdn_url = "http://211.137.107.18:8888/cm/toolbox!getRingSettingListByMsisdn.action"; //获取个人铃音设置信息
    public static String getRingListByMsisdn_url = "http://211.137.107.18:8888/cm/toolbox!getRingListByMsisdn.action"; //获取个人铃音库信息
    public static String delRingSetting_url = "http://211.137.107.18:8888/cm/toolbox!delRingSetting.action"; //删除个人铃音设置
    public static String delOtherRing_url = "http://211.137.107.18:8888/cm/toolbox!delRing.action"; //删除个人铃音库
    public static String refreshRingOrder_url = "http://211.137.107.18:8888/cm/groupInfo!findList.action";
    public static String toolbox_userInfo_url = "http://211.137.107.18:8888/qycl/platform/platform!getUserInfo.action";
    public static String findCircleMsgList = "http://211.137.107.18:8888/cm/groupInfo!findCircleMsgList.action";  //查看消息
    public static String ringRetryHandOut_url = "http://211.137.107.18:8888/cm/cmCircleRing!ringRetryHandOut.action";


    public String USER_NAME = "中高俊聪";// 帐号
    public String PASSWORD = "zgjc@ZG330@@";// 密码

    private CookieStore miguCookie;//音乐名片登录cookie
    private Date connectTime;//最新连接时间

    public MiguApi() {
    }

    public CookieStore getMiguCookie() {
        return miguCookie;
    }

    /**
     * 更新cookie,顺便更新cookie链接服务器的最新时间
     *
     * @param miguCookie
     */
    public void setMiguCookie(CookieStore miguCookie) {
        this.miguCookie = miguCookie;
        this.setConnectTime(new Date());
    }

    public Date getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(Date connectTime) {
        this.connectTime = connectTime;
    }

    /**
     * 取得咪咕登录cookie
     *
     * @return
     */
    public CookieStore getCookieStore() throws NoLoginException, IOException {
        // 为空的话，先去取
        if (this.miguCookie == null) {
            // 重新登录获取
            boolean flag = loginAuto();
            if (!flag) {
                throw new NoLoginException("未登录");
            }
        }
        // cookie超时，使用时间判断，这样去链接验证，会造成服务器决绝。
        if (!this.isValidCookieStore()) {
            // 重新登录获取，调用远程验证码接口。
            boolean flag = loginAuto();
            if (!flag) {
                throw new NoLoginException("未登录");
            }
        }
        return this.miguCookie;
    }

    /**
     * 判断cookie是否有效
     *
     * @return
     */
    public boolean isValidCookieStore() {
        boolean isLogin = false;
        Date now = new Date();
        long mills = now.getTime() - this.getConnectTime().getTime();
        int betw = (int) (mills / 1000);
        if (betw < (15 * 60)) {//超过15分钟，cookie失效，重新登录
            isLogin = true;
        }
        return isLogin;
    }

    /**
     * 调用远程验证码接口，自动登录
     * 重复登录3次
     *
     * @return
     */
    public boolean loginAuto() throws NoLoginException, IOException {
        Subject currentUser = org.apache.shiro.SecurityUtils.getSubject();
        String userId = currentUser.getPrincipal().toString();
        boolean flag = true;
        int i = 0;
        while (flag) {
            flag = !login(getCodeString(), userId);
            i++;
            if (i > 3) {
                break;
            }
        }
        return flag;
    }

    public boolean loginAutoParam(String userName) throws NoLoginException, IOException {
        boolean flag = true;
        int i = 0;
        while (flag) {
            flag = !login(getCodeString(), userName);
            i++;
            if (i > 3) {
                break;
            }
        }
        return flag;
    }

    /**
     * 调用远程验证码接口，取得验证码
     *
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public String getCodeString() {
        String code = null;
        DefaultHttpClient httpclientCode = new DefaultHttpClient();
        double rm = (new Random()).nextDouble();
        HttpGet httpCode = new HttpGet(CODE_URL + "?abc=" + rm);//
        HttpResponse codeResponse;
        try {
            // 获取验证码
            codeResponse = httpclientCode.execute(httpCode);
            InputStream ins1 = codeResponse.getEntity().getContent();
            this.setMiguCookie(httpclientCode.getCookieStore());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = ins1.read(buff, 0, 100)) > 0) {
                os.write(buff, 0, rc);
            }
            byte[] in2b = os.toByteArray();
            String codestr = ChaoJiYing.PostPic("wwewwe", "wyc19931224", "899622", "4004", "4", in2b);
            //{"err_no":0,"err_str":"OK","pic_id":"3068410332412700001","pic_str":"0572","md5":"9f847dfdb63ca1ddf9d5beb86a4c8594"}
            System.out.println("验证码识别：" + codestr);
            JSONArray jsonStr = JSONArray.fromObject("[" + codestr + "]");
            for (int i = 0; i < jsonStr.size(); i++) {
                JSONObject json = jsonStr.getJSONObject(i);
                Integer err_no = Integer.parseInt(json.getString("err_no"));
                code = json.getString("pic_str");
                if (err_no == 0) {
                    if (code != null && code.length() == 4) {
                        code = json.getString("pic_str");
                        break;
                    } else {
                        getCodeString();
                    }
                } else {
                    getCodeString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpCode.abort();
            httpclientCode.getConnectionManager().shutdown();
        }
        return code;
    }

    public String getCreateSHCodeString() throws NoLoginException, IOException {
        String code = null;
        DefaultHttpClient httpclientCode = new DefaultHttpClient();
        httpclientCode.setCookieStore(this.getCookieStore());
        double rm = (new Random()).nextDouble();
        System.out.println(rm);
        HttpGet httpCode = new HttpGet(CODE_URL + "?abc=" + rm);
        HttpResponse codeResponse;
        try {
            // 获取验证码
            codeResponse = httpclientCode.execute(httpCode);
            InputStream ins1 = codeResponse.getEntity().getContent();
            this.setMiguCookie(httpclientCode.getCookieStore());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = ins1.read(buff, 0, 100)) > 0) {
                os.write(buff, 0, rc);
            }
            byte[] in2b = os.toByteArray();
            HttpUtils.downloadNet(ins1);
            String codestr = ChaoJiYing.PostPic("wwewwe", "wyc19931224", "899622", "4004", "4", in2b);
            //{"err_no":0,"err_str":"OK","pic_id":"3068410332412700001","pic_str":"0572","md5":"9f847dfdb63ca1ddf9d5beb86a4c8594"}
            System.out.println("验证码识别：" + codestr);
            JSONArray jsonStr = JSONArray.fromObject("[" + codestr + "]");
            for (int i = 0; i < jsonStr.size(); i++) {
                JSONObject json = jsonStr.getJSONObject(i);
                Integer err_no = Integer.parseInt(json.getString("err_no"));
                code = json.getString("pic_str");
                if (err_no == 0) {
                    if (code != null && code.length() == 4) {
                        code = json.getString("pic_str");
                        break;
                    } else {
                        getCodeString();
                    }
                } else {
                    getCodeString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpCode.abort();
            httpclientCode.getConnectionManager().shutdown();
        }
        return code;
    }

    /**
     * 登录到咪咕
     *
     * @param vcode
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public boolean login(String vcode, String userName) throws NoLoginException, IOException {
        boolean isSuccess = false;
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(this.getCookieStore())
                .build();
        HttpPost httppost = new HttpPost(MiguApi.LOGIN_URL);
        List<NameValuePair> formParams = new ArrayList<NameValuePair>();
//        String userName = ShiroUtils.getSysUser().getUserName();
        if ("睿智广告001".equals(userName)) {
            formParams.add(new BasicNameValuePair("manager.loginName", "中高俊聪022"));
            String password = new Md5Hash(new Md5Hash("Cl@4006612530").toString() + vcode).toString();
            formParams.add(new BasicNameValuePair("manager.password", password));
        } else {
            formParams.add(new BasicNameValuePair("manager.loginName", USER_NAME));
            String password = new Md5Hash(new Md5Hash(PASSWORD).toString() + vcode).toString();
            formParams.add(new BasicNameValuePair("manager.password", password));
        }
        formParams.add(new BasicNameValuePair("vcode", vcode));
        if (this.miguCookie == null) {
            return false;
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, "utf-8");
        httppost.setEntity(entity);
        CloseableHttpResponse response = httpclient.execute(httppost);
        try {
            HttpEntity resEntity = response.getEntity();
            String s = EntityUtils.toString(resEntity);
            log.info("移动登录结果--->", s);
            System.out.println(s);
            isSuccess = !s.contains("输入错误");
            // 获取登录cookie
            if (isSuccess) {
                this.setMiguCookie(this.miguCookie);
            }
        } catch (Exception e) {
            log.error("移动登录 方法：[{login}] 错误信息[{}]", e);
        } finally {
            httppost.abort();
            response.close();
        }
        return isSuccess;
    }

    /**
     * 查看企业彩铃状态
     *
     * @param msisdn
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public String refreshCrbtStatus(String msisdn) throws NoLoginException, IOException {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("msisdn", msisdn);
        String result = sendPost(map, refreshCrbtStatus_url);
        log.info("移动查看企业彩铃状态 参数：{} --->", msisdn);
        return result;
    }

    /**
     * 查看包月状态
     *
     * @param msisdn
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public String refreshIsMonthly(String msisdn) throws NoLoginException, IOException {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("msisdn", msisdn);
        String result = sendPost(map, refreshMonthlyStatus_url);
        log.info("移动查看包月状态 参数：{}", msisdn);
        return result;
    }

    /**
     * 查询号码信息
     *
     * @param linkmanTel
     * @param operateId
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public String getPhoneInfo(String linkmanTel, String operateId) throws NoLoginException, IOException {
        //需要先更新移动成员包月状态
        refreshIsMonthly(linkmanTel);
        //更新彩铃状态
        refreshCrbtStatus(linkmanTel);
        //更新视频彩铃状态
        refreshVbrtStatus(linkmanTel);
        //获取移动成员包月状态
        String getUrl = getPhonePage_url + "?provinceCode=&phoneNoOrName=" + linkmanTel + "&groupId=" + operateId + "&groupName=&freezeStatus=-1&payType=0&pageView.currentpage=1";
        String result = sendGet(getUrl);
        log.info("移动获取铃音列表页面 参数：{},{}", linkmanTel, operateId);
        return result;
    }

    public String updatePhoneInfo(String tel, String operateId) throws NoLoginException, IOException {
        String getUrl = getPhonePage_url + "?provinceCode=&phoneNoOrName=" + tel + "&groupId=" + operateId + "&groupName=&freezeStatus=-1&payType=0&pageView.currentpage=1";
        String result = sendGet(getUrl);
        log.info("移动获取铃音列表页面 参数：{},{}", tel, operateId);
        return result;
    }

    /**
     * 发送短信
     *
     * @param phoneNo
     * @param phoneMiguId
     * @param groupId
     * @param smsConfirm
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public String remindOrderCrbtAndMonth(String phoneNo, String phoneMiguId, String groupId, boolean smsConfirm) throws IOException, NoLoginException {
        String reslut = null;
        String msisdnList = phoneNo + "|" + phoneMiguId + ",";
        HashMap map = new HashMap();
        map.put("allFlag", "0");
        map.put("msisdnList", msisdnList);
        map.put("groupId", groupId);
        if (smsConfirm) {
            reslut = sendPost(map, orderCrbtAndMonth_URL); // 免短信开通
        } else {
            reslut = sendPost(map, remindOrderCrbtAndMonth_URL); // 短信下发
        }

        log.info("移动发送短信 参数：{},{},{} 结果：{}", phoneNo, phoneMiguId, groupId, smsConfirm);
        return reslut;
    }


    /**
     * 刷新视频彩铃功能
     *
     * @param phoneNo
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public String refreshVbrtStatus(String phoneNo) throws IOException, NoLoginException {
        HashMap map = new HashMap();
        map.put("msisdn", phoneNo);
        String result = sendPost(map, refreshVbrtStatus_url);
        log.info("移动刷新视频彩铃功能 参数：{}", phoneNo);
        return result;
    }

    /**
     * 获取铃音列表页面
     *
     * @param circleID
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public String getRingPage(String circleID) throws NoLoginException, IOException {
        String getUrl = getRingPage_url + "?groupId=" + circleID;
        String result = sendGet(getUrl);
        log.info("移动获取铃音列表页面--->");
        return result;
    }

    /**
     * 获取铃音信息
     *
     * @param circleID
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public String findCircleRingPageById(String circleID) throws NoLoginException, IOException {
        String getUrl = findCircleRingPageById_url + "?circleID=" + circleID;
        String result = sendGet(getUrl);
        log.info("移动获取铃音信息 参数：{}", circleID);
        return result;
    }

    /**
     * 设置铃音
     *
     * @param phones
     * @param ringId
     * @param circleId
     * @return {"msg":"验证码输入错误","success":false}
     * @throws IOException
     * @throws NoLoginException
     */
    public String setCircleRingById4User(String phones, String ringId, String circleId) throws IOException, NoLoginException {
        String result = null;
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(this.getCookieStore()).build();
        HttpPost httppost = new HttpPost(settingRing_url);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        String[] phoness = phones.split(",");
        for (String phone : phoness) {
            formparams.add(new BasicNameValuePair("checkbox", phone));
        }
        formparams.add(new BasicNameValuePair("circleID", circleId));
        formparams.add(new BasicNameValuePair("ringID", ringId));
        formparams.add(new BasicNameValuePair("data", phones));
        formparams.add(new BasicNameValuePair("payType", "0"));
        formparams.add(new BasicNameValuePair("allFlag", ""));
        formparams.add(new BasicNameValuePair("vcode", getCreateSHCodeString()));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "utf-8");
        httppost.setEntity(entity);
        CloseableHttpResponse response = httpclient.execute(httppost);
        try {
            HttpEntity resEntity = response.getEntity();
            result = EntityUtils.toString(resEntity);
            log.info("移动 设置铃音 参数：{},{},{}", phones, ringId, circleId);
            this.setMiguCookie(this.getCookieStore());
        } catch (Exception e) {
            log.error("移动 设置铃音 错误信息", e);
        } finally {
            try {
                httppost.abort();
                response.close();
            } catch (IOException e) {
                log.error("response.close(); 错误信息", e);
            }
        }
        return result;
    }

    /**
     * 移动GET方式封装
     *
     * @param getUrl
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    private String sendGet(String getUrl) throws NoLoginException, IOException {
        String result = null;
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(this.getCookieStore()).build();
        HttpGet httpGet = new HttpGet(getUrl);
        CloseableHttpResponse response = httpclient.execute(httpGet);// 进入
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = response.getEntity();
                result = EntityUtils.toString(resEntity);
                this.setMiguCookie(this.getCookieStore());
            }
        } catch (Exception e) {
            log.error("移动sendGet 错误信息", e);
        } finally {
            httpGet.abort();
            response.close();
        }
        return result;
    }

    /**
     * 移动POST方式
     *
     * @param paramMap
     * @param url
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    private String sendPost(HashMap<String, String> paramMap, String url) throws IOException, NoLoginException {
        String result = null;
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(this.getCookieStore()).build();
        HttpPost httppost = new HttpPost(url);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        for (String key : paramMap.keySet()) {
            String value = paramMap.get(key);
            formparams.add(new BasicNameValuePair(key, value));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "utf-8");
        httppost.setEntity(entity);
        CloseableHttpResponse response = httpclient.execute(httppost);
        try {
            HttpEntity resEntity = response.getEntity();
            result = EntityUtils.toString(resEntity);
            this.setMiguCookie(this.getCookieStore());
        } catch (Exception e) {
            log.error("移动 sendPost 错误信息", e);
        } finally {
            try {
                httppost.abort();
                response.close();
            } catch (IOException e) {
                log.error("response.close(); 错误信息", e);
            }
        }
        return result;
    }


    /**
     * 增加商户
     *
     * @param ringOrder
     * @param attached
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public MiguAddGroupRespone add1(ThreenetsOrder ringOrder, ThreeNetsOrderAttached attached) throws IOException, NoLoginException {
        MiguAddGroupRespone addGroupRespone = null;
        User user = ShiroUtils.getSysUser();
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(this.getCookieStore()).build();
        HttpPost httppost = new HttpPost(addGroup_url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("vcode", getCreateSHCodeString(), ContentType.TEXT_PLAIN.withCharset("UTF-8"));
        builder.addTextBody("circle.ID", "");
        builder.addTextBody("circle.name", ringOrder.getCompanyName(), ContentType.TEXT_PLAIN.withCharset("UTF-8"));
        builder.addTextBody("circle.cmName", user.getUserName(), ContentType.TEXT_PLAIN.withCharset("UTF-8"));
        builder.addTextBody("circle.cmMsisdn", user.getUserTel());
        builder.addTextBody("groupManagerName", ShiroUtils.getSysUser().getUserName(), ContentType.TEXT_PLAIN.withCharset("UTF-8"));
        builder.addTextBody("circle.owner.msisdn", ringOrder.getLinkmanTel(), ContentType.TEXT_PLAIN.withCharset("UTF-8"));
        builder.addTextBody("manager.password", ringOrder.getLinkmanTel(), ContentType.TEXT_PLAIN.withCharset("UTF-8"));
        builder.addTextBody("manager.status", "1");
        builder.addTextBody("province", "福建省", ContentType.TEXT_PLAIN.withCharset("UTF-8"));
        builder.addTextBody("city", "福州市", ContentType.TEXT_PLAIN.withCharset("UTF-8"));
        builder.addTextBody("county", "鼓楼区", ContentType.TEXT_PLAIN.withCharset("UTF-8"));
        builder.addTextBody("groupStreet", "六一北路92号", ContentType.TEXT_PLAIN.withCharset("UTF-8"));
        builder.addTextBody("circle.payType", "0");
        if (attached.getMiguPrice() <= 5) {
            builder.addTextBody("circle.price", attached.getMiguPrice().toString());
            builder.addTextBody("circle.specialPrice", "");
        } else {
            builder.addTextBody("circle.specialPrice", attached.getMiguPrice().toString());
        }
        builder.addTextBody("circle.applyForSmsNotification", "0");
        if (ringOrder.getUpLoadAgreement() != null) {
            builder.addBinaryBody("file", ringOrder.getUpLoadAgreement());
        }
        builder.addTextBody("circle.isNormal", "0");
        builder.addTextBody("circle.memo", "");
        HttpEntity httpEntity = builder.build();
        httppost.setEntity(httpEntity);
        HttpResponse response = httpclient.execute(httppost);
        try {
            HttpEntity resEntity = response.getEntity();
            String result = EntityUtils.toString(resEntity);
            if (result.contains("loginForm")) {
                loginAuto();
                add1(ringOrder, attached);
            }
            log.info("移动商户建立=>");
            this.setMiguCookie(this.getCookieStore());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                log.debug("服务器正常响应.....");
                addGroupRespone = (MiguAddGroupRespone) JsonUtil.getObject4JsonString(result, MiguAddGroupRespone.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httppost.abort();
            httpclient.getConnectionManager().shutdown();
        }
        return addGroupRespone;
    }

    /**
     * 上传铃音
     *
     * @param ring
     * @param trade
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public MiguAddRingRespone saveRing(ThreenetsRing ring, String trade) throws IOException, NoLoginException {
        MiguAddRingRespone miguAddRingRespone = new MiguAddRingRespone();
        String ringName = ring.getRingName().substring(0, ring.getRingName().indexOf("."));
        String result = null;
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(this.getCookieStore()).build();
        HttpPost httppost = new HttpPost(importRing_url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        // 上传的文件
        builder.addBinaryBody("file", ring.getFile());
        builder.addTextBody("ringName", ringName, ContentType.TEXT_PLAIN.withCharset("UTF-8"));
        builder.addTextBody("circleID", ring.getOperateId(), ContentType.TEXT_PLAIN.withCharset("UTF-8"));
        builder.addTextBody("trade", trade, ContentType.TEXT_PLAIN.withCharset("UTF-8"));
        builder.addTextBody("singer", "");
        builder.addTextBody("songName", "");
        builder.addTextBody("RingContentController", ring.getRingContent(), ContentType.TEXT_PLAIN.withCharset("UTF-8"));
        builder.addTextBody("autoSetType", "1");
        HttpEntity httpEntity = builder.build();
        httppost.setEntity(httpEntity);
        HttpResponse response = httpclient.execute(httppost);
        try {
            HttpEntity resEntity = response.getEntity();
            result = EntityUtils.toString(resEntity);
            if (result.contains("loginForm")) {
                loginAuto();
                saveRing(ring, trade);
            }
            this.setMiguCookie(this.getCookieStore());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                log.debug("铃音上传服务器正常响应2.....");
                //String result = EntityUtils.toString(response1.getEntity());
                if (result.contains("铃音名称已经存在，请修改")) {
                    miguAddRingRespone.setMsg("铃音名称已经存在，请修改");
                    miguAddRingRespone.setSuccess(false);
                } else if (result.contains("集团还有铃音正在分发中，不能上传铃音")) {
                    miguAddRingRespone.setMsg("集团还有铃音正在分发中，不能上传铃音");
                    miguAddRingRespone.setSuccess(false);
                } else {
                    miguAddRingRespone.setMsg("激活中");
                    miguAddRingRespone.setSuccess(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httppost.abort();
            httpclient.getConnectionManager().shutdown();
        }
        return miguAddRingRespone;
    }

    /**
     * 向移动咪咕增加号码
     *
     * @param data
     * @param circleID
     * @return
     * @throws
     */
    public MiguAddPhoneRespone addPhone(String data, String circleID) {
        MiguAddPhoneRespone miguAddPhoneRespone = new MiguAddPhoneRespone();
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(ADD_PHONE_URL);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("data", data));
        formparams.add(new BasicNameValuePair("circleID", circleID));
        formparams.add(new BasicNameValuePair("autoSetRing", "1"));
        formparams.add(new BasicNameValuePair("addAll", "0"));
        try {
            httpclient.setCookieStore(this.getCookieStore());
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "utf-8");
            httppost.setEntity(entity);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            String result = EntityUtils.toString(resEntity);
            if (result.contains("loginForm")) {
                loginAuto();
                addPhone(data, circleID);
            }
            miguAddPhoneRespone = (MiguAddPhoneRespone) JsonUtil.getObject4JsonString(result, MiguAddPhoneRespone.class);
            JSONObject jsonObject = JSONObject.fromObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject partDaily = jsonArray.getJSONObject(i);
                String info = partDaily.getString("info");
                miguAddPhoneRespone.setMessage(info);
            }
            this.setMiguCookie(httpclient.getCookieStore());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httppost.abort();
            httpclient.getConnectionManager().shutdown();
        }
        return miguAddPhoneRespone;
    }

    public static void main(String[] args) throws NoLoginException, IOException {
//        MiguApi miguApi = new MiguApi();
//        String s = miguApi.getRingPage("c9a7ffb7-876c-40aa-88ef-14185f79930b");
//        System.out.println(s);
    }

    /**
     * 移动工具箱-->用户信息
     *
     * @param msisdn
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public String getUserInfoByRingMsisdn(String msisdn) throws NoLoginException, IOException {
        HashMap map = new HashMap();
        map.put("msisdn", msisdn);
        String result = sendPost(map, toolbox_userInfo_url);
        log.info("移动工具箱-->用户信息 参数：{}", msisdn);
        return result;
    }

    /**
     * 工具箱-->删除铃音-->取得个人铃音设置列表
     *
     * @param msisdn
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public String getRingSettingListByMsisdn(String msisdn) throws NoLoginException, IOException {
        HashMap map = new HashMap();
        map.put("msisdn", msisdn);
        String result = sendPost(map, getRingSettingListByMsisdn_url);
        log.info("移动工具箱-->删除铃音-->取得个人铃音设置列表 参数：{}", msisdn);
        return result;
    }

    /**
     * 工具箱-->删除铃音-->删除个人铃音设置列表
     *
     * @param data
     * @param msisdn
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public String delRingSetting(String data, String msisdn) throws NoLoginException, IOException {
        HashMap map = new HashMap();
        map.put("data", data);
        map.put("msisdn", msisdn);
        String result = sendPost(map, delRingSetting_url);
        log.info("移动工具箱-->用户信息-->删除个人铃音设置列表 参数：{}", data, msisdn);
        return result;
    }

    /**
     * 个人铃音库列表
     *
     * @param msisdn
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public String getRingListByMsisdn(String msisdn) throws NoLoginException, IOException {
        HashMap map = new HashMap();
        map.put("msisdn", msisdn);
        String result = sendPost(map, getRingListByMsisdn_url);
        log.info("移动工具箱-->用户信息-->获得个人铃音库列表 参数：{}", msisdn);
        return result;
    }

    /**
     * 小工具，删除多余铃音
     *
     * @param toneIds 铃音id
     * @param msisdn  号码
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public String delOtherRing(String toneIds, String msisdn) throws NoLoginException, IOException {
        HashMap map = new HashMap();
        map.put("toneIds", toneIds);
        map.put("msisdn", msisdn);
        String result = sendPost(map, delOtherRing_url);
        log.info("移动工具箱-->删除铃音-->删除多余铃音 参数：{}", toneIds, msisdn);
        return result;
    }

    /**
     * 商户列表-->信息处理
     *
     * @param migu_id
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public String findCricleMsgList(String migu_id) throws IOException, NoLoginException {
        HashMap map = new HashMap();
        map.put("circleId", migu_id);
        String result = sendPost(map, findCircleMsgList);
        log.info("商户列表-->处理信息 参数：{}", migu_id);
        return result;
    }

    /**
     * 重新激活铃音
     *
     * @param ring
     * @param name
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public String reactivateRing(ThreenetsRing ring, String name) throws IOException, NoLoginException {
        String groupName = URLEncoder.encode(name, "utf-8");
        String getUrl = ringRetryHandOut_url + "?ringID=" + ring.getOperateRingId() + "&circleID=" + ring.getOperateId() + "&groupName=" + groupName;
        String result = sendGet(getUrl);
        log.info("移动铃音重新激活 参数：{}", ring.getRingName());
        return result;
    }


    public String refreshOrderId(Integer orderId) {
        String id = "";
        try {
            ThreenetsOrder order = SpringUtils.getBean(ThreenetsOrderMapper.class).selectByPrimaryKey(orderId);
            String groupName = URLEncoder.encode(order.getCompanyName(), "utf-8");
            String url = refreshRingOrder_url + "?msgProcessStatus=2&payType=0&isNormal=2&freezeStatus=2&msisdn=&groupName=" + groupName;
            String result = sendGet(url);
            if (StringUtils.isNotEmpty(result)) {
                Document doc = Jsoup.parse(result);
                Elements contents = doc.getElementsByClass("tbody_lis");
                Elements datas = contents.get(0).getElementsByClass("tbody_lis");
                Element ele = datas.get(0);
                Elements trs = ele.getElementsByTag("tr");
                if (trs.size() > 0) {// 没值的话，跳过
                    Elements tds = trs.get(0).getElementsByTag("td");
                    Element temp = tds.get(9).child(1);
                    String t = temp.attr("onclick");
                    String[] split = t.split("'");
                    id = split[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoLoginException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            return id;
        }
    }
}
