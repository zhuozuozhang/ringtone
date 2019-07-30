package com.hrtxn.ringtone.common.api;

import com.hrtxn.ringtone.common.exception.NoLoginException;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreeNetsOrderAttached;
import com.hrtxn.ringtone.project.threenets.threenet.json.migu.MiguAddGroupRespone;
import com.hrtxn.ringtone.common.utils.ChaoJiYing;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.json.JsonUtil;
import com.hrtxn.ringtone.project.system.user.domain.User;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsOrder;
import com.hrtxn.ringtone.project.threenets.threenet.json.migu.MiguAddGroupRespone;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
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

    public static String findCircleMsgList_url = "http://211.137.107.18:8888/cm/groupInfo!findCircleMsgList.action";// 查看消息
    public static String ADD_PHONE_URL = "http://211.137.107.18:8888/cm/groupInfo!inviteGroupUsers.action";//增加号码地址
    public static String importRing_url = "http://211.137.107.18:8888/cm/cmRing!importRing.action";//增加铃音url
    public static String checkAdminMsisdn_url = "http://211.137.107.18:8888/cm/groupInfo!checkAdminMsisdn.action";
    public static String checkGroupName_url = "http://211.137.107.18:8888/cm/groupInfo!checkGroupName.action";
    public static String ringSetting_url = "http://211.137.107.18:8888/cm/groupInfo!ringSetting.action";
    public static String deleteCircleRingById_url = "http://211.137.107.18:8888/cm/cmCircleRing!deleteCircleRingById.action";
    public static String deletePhone_url = "http://211.137.107.18:8888/cm/groupInfo!deleteUserByMemberId.action";
    public static String getRingSettingListByMsisdn_url = "http://211.137.107.18:8888/cm/toolbox!getRingSettingListByMsisdn.action";
    public static String getRingListByMsisdn_url = "http://211.137.107.18:8888/cm/toolbox!getRingListByMsisdn.action";
    public static String delOtherRing_url = "http://211.137.107.18:8888/cm/toolbox!delRing.action";
    public static String refreshRingOrder_url = "http://211.137.107.18:8888/cm/groupInfo!findList.action";

    public String USER_NAME = "中高俊聪";// 帐号
    public String PASSWORD = "zgjc@ZG330@";// 密码

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
        String code = getCodeString();
        int i = 0;
        boolean flag = login(code);
        if (!flag) {
            i++;
            if (i < 3) {
                flag = login(getCodeString());
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

    /**
     * 登录到咪咕
     *
     * @param vcode
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public boolean login(String vcode) throws NoLoginException, IOException {
        boolean isSuccess = false;
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(this.getCookieStore())
                .build();
        HttpPost httppost = new HttpPost(MiguApi.LOGIN_URL);
        List<NameValuePair> formParams = new ArrayList<NameValuePair>();
        String userName = ShiroUtils.getSysUser().getUserName();
        if ("睿智广告001".equals(userName)) {
            formParams.add(new BasicNameValuePair("manager.loginName", "中高俊聪022"));
            String password = new Md5Hash(new Md5Hash("Cmcc1mgyy2%").toString() + vcode).toString();
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
            log.info("移动登录结果--->" + s);
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
     * （废弃 使用getPhoneInfo替代）
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
        log.info("移动查看企业彩铃状态 参数：{} ---> 结果： --- >{}", msisdn, result);
        return result;
    }

    /**
     * 查看包月状态
     * （废弃 使用getPhoneInfo替代）
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
        log.info("移动查看包月状态 参数：{} 结果：{}", msisdn, result);
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
        String getUrl = getPhonePage_url + "?provinceCode=&phoneNoOrName=" + linkmanTel + "&groupId=" + operateId + "&groupName=&freezeStatus=-1&payType=0&pageView.currentpage=1";
        String result = sendGet(getUrl);
        log.info("移动获取铃音列表页面 参数：{},{} 结果：{}--->", linkmanTel, operateId, result);
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
        log.info("移动刷新视频彩铃功能 参数：{} 结果：{}", phoneNo, result);
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
        log.info("移动获取铃音列表页面--->" + result);
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
        log.info("移动获取铃音信息 参数：{} 结果：{}", circleID, result);
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
                this.setMiguCookie(this.miguCookie);
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
     * 增加商户(短信回复类型)，带管理员手机。（不含铃音，和手机号码）
     *
     * @param ringOrder
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public MiguAddGroupRespone add(ThreenetsOrder ringOrder, ThreeNetsOrderAttached attached) throws IOException, NoLoginException {
        User user = ShiroUtils.getSysUser();
        MiguAddGroupRespone addGroupRespone = null;
        String vcode = getCodeString();
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(addGroup_url);
        httpclient.setCookieStore(this.getCookieStore());
        try {
            MultipartEntity reqEntity = new MultipartEntity();
            HttpParams params = httpclient.getParams();
            params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, Charset.forName("UTF-8"));
            reqEntity.addPart("vcode", new StringBody(vcode));// 集团简介
            reqEntity.addPart("circle.ID", new StringBody(""));
            reqEntity.addPart("circle.name", new StringBody(ringOrder.getCompanyName(), Charset.forName("UTF-8")));// 集团名称
            reqEntity.addPart("circle.cmName", new StringBody(user.getUserName(), Charset.forName("UTF-8")));// 客户经理姓名
            reqEntity.addPart("circle.cmMsisdn", new StringBody(user.getUserTel()));// 客服号码
            reqEntity.addPart("groupManagerName", new StringBody(user.getUserName(), Charset.forName("UTF-8")));// 管理员姓名
            reqEntity.addPart("circle.owner.msisdn", new StringBody(ringOrder.getLinkmanTel(), Charset.forName("UTF-8")));// 集团管理员手机号
            reqEntity.addPart("manager.password", new StringBody(ringOrder.getLinkmanTel(), Charset.forName("UTF-8")));// 集团管理员登陆密码
            reqEntity.addPart("manager.status", new StringBody("1"));// 0启用集团管理员账户1禁止集团管理员账户
            reqEntity.addPart("province", new StringBody("福建省", Charset.forName("UTF-8")));// 省
            reqEntity.addPart("city", new StringBody("福州市", Charset.forName("UTF-8")));// 市
            reqEntity.addPart("county", new StringBody("鼓楼区", Charset.forName("UTF-8")));// 区
            reqEntity.addPart("groupStreet", new StringBody("六一北路92号", Charset.forName("UTF-8")));// 集团所在街道
            reqEntity.addPart("circle.payType", new StringBody("0"));// 集团统付
            if (attached.getMiguPrice() <= 5) {
                reqEntity.addPart("circle.price", new StringBody(attached.getMiguPrice() + ""));// 资费，默认值为0
                reqEntity.addPart("circle.specialPrice", new StringBody(""));// 资费，默认值为0
            } else {
                reqEntity.addPart("circle.specialPrice", new StringBody(attached.getMiguPrice() + ""));// 资费，默认值为0
            }
            //reqEntity.addPart("circle.specialDiscount", new StringBody(""));// 折扣
            reqEntity.addPart("circle.applyForSmsNotification", new StringBody("0"));// 申请免短信
            reqEntity.addPart("circle.isNormal", new StringBody("0"));// 集团类型、正式
            //reqEntity.addPart("circle.trialTimeType", new StringBody(""));// 试用时间
            reqEntity.addPart("circle.memo", new StringBody(""));// 集团简介
            if (ringOrder.getUpLoadAgreement() != null) {
                reqEntity.addPart("myfile", new FileBody(ringOrder.getUpLoadAgreement()));
            }
            httppost.setEntity(reqEntity);
            HttpResponse response1 = httpclient.execute(httppost);
            int statusCode = response1.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                log.debug("服务器正常响应.....");
                HttpEntity resEntity = response1.getEntity();
                String res = EntityUtils.toString(resEntity);
                System.out.println("res" + res);
                addGroupRespone = (MiguAddGroupRespone) JsonUtil.getObject4JsonString(res, MiguAddGroupRespone.class);
                this.setMiguCookie(httpclient.getCookieStore());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httppost.abort();
            httpclient.getConnectionManager().shutdown();
        }
        return addGroupRespone;
        //            HashMap<String,String> map = new HashMap<>();
//            map.put("vcode", vcode);// 集团简介
//            map.put("circle.ID", "");
//            map.put("circle.name",ringOrder.getCompanyName());// 集团名称
//            map.put("circle.cmName", user.getUserName());// 客户经理姓名
//            map.put("circle.cmMsisdn", user.getUserTel());// 客服号码
//            map.put("groupManagerName", user.getUserName());// 管理员姓名
//            map.put("circle.owner.msisdn", ringOrder.getLinkmanTel());// 集团管理员手机号
//            map.put("manager.password", ringOrder.getLinkmanTel());// 集团管理员登陆密码
//            map.put("manager.status","1");// 0启用集团管理员账户1禁止集团管理员账户
//            map.put("province", "福建省");// 省
//            map.put("city", "福州市");// 市
//            map.put("county","鼓楼区");// 区
//            map.put("groupStreet","六一北路92号");// 集团所在街道
//            map.put("circle.payType", "0");// 集团统付
//            if (attached.getMiguPrice() <= 5) {
//                map.put("circle.price", attached.getMiguPrice() + "");// 资费，默认值为0
//                map.put("circle.specialPrice", "");// 资费，默认值为0
//            } else {
//                map.put("circle.specialPrice",attached.getMiguPrice() + "");// 资费，默认值为0
//            }
//            map.put("circle.specialDiscount","");// 折扣
//            map.put("circle.applyForSmsNotification","0");// 申请免短信
//            map.put("circle.isNormal", "0");// 集团类型、正式
//            map.put("circle.trialTimeType", "");// 试用时间
//            map.put("circle.memo","");// 集团简介
//            String sendPost = sendPost(map, addGroup_url);
//            System.out.printf(sendPost);
    }

    public static void main(String[] args) throws NoLoginException, IOException {
//        MiguApi miguApi = new MiguApi();
//        String s = miguApi.getRingPage("c9a7ffb7-876c-40aa-88ef-14185f79930b");
//        System.out.println(s);
    }
}
