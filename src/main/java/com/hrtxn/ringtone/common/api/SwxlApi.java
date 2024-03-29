package com.hrtxn.ringtone.common.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrtxn.ringtone.common.exception.NoLoginException;
import com.hrtxn.ringtone.common.utils.*;
import com.hrtxn.ringtone.freemark.config.systemConfig.RingtoneConfig;
import com.hrtxn.ringtone.project.system.user.domain.User;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreeNetsOrderAttached;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsRing;
import com.hrtxn.ringtone.project.threenets.threenet.json.swxl.*;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsOrderMapper;
import lombok.Getter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Author:zcy
 * Date:2019-07-20 16:17
 * Description:联通商务炫铃彩铃对接系统
 */
@Slf4j
public class SwxlApi implements Serializable {
    private static final long serialVersionUID = 2341400252678996475L;
    public static String LOGIN_URL = "https://swxl.10155.com/swxlapi/web/login";// 登录成功地址
    public static String CODE_URL = "https://swxl.10155.com/swxlapi/web/login/code";// 验证码地址
    public static String swxlrefreshCrbtStatus_url = "https://swxl.10155.com/swxlapi/web/member/refreshStatus";//根据号码查询彩铃功能
    public static String getSwxlRingFenFa_URL = "https://swxl.10155.com/swxlapi/web/ring";// ring/ringId获取铃音分发详细
    public static String getGroupRingInfo_URL = "https://swxl.10155.com/swxlapi/web/ring";// 获取铃音
    public static String ADD_PHONE_URL = "https://swxl.10155.com/swxlapi/web/member";// 增加号码地址
    public static String remindOrderCrbtAndMonth_URL = "https://swxl.10155.com/swxlapi/web/member/openBusiness";// 短信提醒url
    public static String orderCrbtAndMonth_URL = "https://swxl.10155.com/swxlapi/userpay/remindOrderCrbtAndMonth.do";// 直接开通包月url
    public static String Send_PHONESMS_URL = "https://swxl.10155.com/swxlapi/web/member";// 为集团下发开通短信
    public static String addGroup_url = "https://swxl.10155.com/swxlapi/web/group";// 增加商户url
    public static String importSwxlRing_URL = "https://swxl.10155.com/swxlapi/web/ring/add";// 上传铃音
    public static String Send_SMS_SecondMsg_URL = "https://swxl.10155.com/swxlapi/web/member/openBizSpecialChannel";// 用户开通业务失败二次发送短信地址
    public static String PhoneSetRing_URL = "https://swxl.10155.com/swxlapi/web/ring/set";// 用户设置铃音

    public static String getSetRingList_url = "https://swxl.10155.com/ring/getSetRingList.do"; // 获取铃音信息
    public static String importRing_url = "https://swxl.10155.com/group/uploadRing.do";// 铃音上传url
    public static String deleteSwxlRing_URL = "https://swxl.10155.com/swxlapi/web/ring";// 删除铃音
    public static String DELETE_PHONE_URL = "https://swxl.10155.com/swxlapi/web/member";// 删除用户
    public static String groupSetRing_URL = "https://swxl.10155.com/swxlapi/web/ring/setRing.do";// 批量设置铃音
    public static String selectGroup_url = "https://swxl.10155.com/swxlapi/web/group";
    public static String addChild_url = "https://swxl.10155.com/swxlapi/web/manager/child";// 增加商户url
    public static String silentMember_url = "https://swxl.10155.com/swxlapi/web/member/silentMember";//工具箱获取用户信息
    public static String systemLogList_url = "https://swxl.10155.com/swxlapi/web/systemLog/list";//工具箱获取用户信息
    public static String delete_url = "https://swxl.10155.com/swxlapi/web/member/silentMemberDel/";//工具箱删除用户信息
    public static String check_mobiles = "https://swxl.10155.com/swxlapi/web/group/checkMobiles";//检查手机号

    @Getter
    public String USER_NAME = "99397000";// 帐号
    public String PASSWORD = "H836286@";// 密码

    public String PASSWORD2 = "Zg316316@";// 密码

    private CookieStore swxlCookie;// 音乐名片登录cookie
    private Date connectTime;// 最新连接时间

    public CookieStore getSwxlCookie() {
        return swxlCookie;
    }

    /**
     * 更新cookie,顺便更新cookie链接服务器的最新时间
     *
     * @param swxlCookie
     */
    public void setSwxlCookie(CookieStore swxlCookie) {
        this.swxlCookie = swxlCookie;
        this.setConnectTime(new Date());
    }

    public Date getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(Date connectTime) {
        this.connectTime = connectTime;
    }

    public CookieStore getCookieStore() throws NoLoginException {
        // 为空的话，先去取
        if (this.swxlCookie == null) {
            // 重新登录获取
            //   boolean flag = loginAuto();
            boolean flag = loginAutoParam(ShiroUtils.getSysUser().getUserName());
            if (!flag) {
                throw new NoLoginException("未登录！");
            }
        }
        // cookie超时，使用时间判断，这样去链接验证，会造成服务器决绝。
        if (!this.isValidCookieStore()) {
            // 重新登录获取，调用远程验证码接口。
            //   boolean flag = loginAuto();
            boolean flag = loginAutoParam(ShiroUtils.getSysUser().getUserName());
            if (!flag) {
                throw new NoLoginException("未登录！");
            }
        }
        return this.swxlCookie;
    }

    public boolean loginAutoParam(String userName) {
        int i = 0;
        boolean flag = loginParam(getCodeString(), userName);
        if (!flag) {
            i++;
            if (i < 3) {
                flag = loginParam(getCodeString(), userName);
            }
        }
        return flag;
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
        if (betw < (15 * 60)) {// 超过15分钟，cookie失效，重新登录
            isLogin = true;
        }
        return isLogin;
    }

    /**
     * 调用远程验证码接口，自动登录
     *
     * @return
     */
    public boolean loginAuto() throws IOException, NoLoginException {
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
     * 获取验证码
     *
     * @return {"err_no":0,"err_str":"OK","pic_id":"9075211432412704171","pic_str":"2395","md5":"a64aa9ccea2a582337f70f8e3e8d6850"}
     */
    public String getCodeString() {
        String code = null;
        DefaultHttpClient httpclientCode = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
        httpclientCode = WebClientDevWrapper.wrapClient(httpclientCode);
        HttpGet httpCode = new HttpGet(CODE_URL);
        HttpResponse codeResponse;
        try {
            // 获取验证码
            codeResponse = httpclientCode.execute(httpCode);
            InputStream ins1 = codeResponse.getEntity().getContent();
            this.setSwxlCookie(httpclientCode.getCookieStore());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = ins1.read(buff, 0, 100)) > 0) {
                os.write(buff, 0, rc);
            }
            byte[] in2b = os.toByteArray();
            String codestr = ChaoJiYing.PostPic("wwewwe", "wyc19931224", "899622", "4004", "4", in2b);
            //{"err_no":0,"err_str":"OK","pic_id":"3068410332412700001","pic_str":"0572","md5":"9f847dfdb63ca1ddf9d5beb86a4c8594"}
            log.info("验证码识别：" + codestr);
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
     * 登录到商务炫铃
     *
     * @param vcode
     * @return {"recode":"000000","message":"成功","data":null,"success":true}
     */
    @Synchronized
    public boolean login(String vcode) throws IOException, NoLoginException {
        boolean isSucess = false;
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(this.getCookieStore())
                .build();
        HttpPost httppost = new HttpPost(SwxlApi.LOGIN_URL);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("username", USER_NAME));
        formparams.add(new BasicNameValuePair("password", PASSWORD));
        formparams.add(new BasicNameValuePair("vcode", vcode));
        if (this.swxlCookie == null) {
            return false;
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "utf-8");
        httppost.setEntity(entity);
        CloseableHttpResponse response = httpclient.execute(httppost);
        try {
            HttpEntity resEntity = response.getEntity();
            String s = EntityUtils.toString(resEntity);
            log.info("联通登录结果" + s);
            isSucess = !s.contains("验证码输入错误");
            // 获取登录cookie
            if (isSucess) {
                this.setSwxlCookie(this.swxlCookie);
            }
        } catch (Exception e) {
            log.error("联通登录 方法：[{login}] 错误信息[{}]", e);
        } finally {
            httppost.abort();
            response.close();
        }
        return isSucess;
    }


    @Synchronized
    public boolean loginParam(String vcode, String userName) {
        String password = PASSWORD2;
        if (userName.equals(USER_NAME)) {
            password = PASSWORD;
        }
        boolean isSucess = false;
        DefaultHttpClient httpclient = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
        HttpPost httppost = new HttpPost(SwxlApi.LOGIN_URL);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("username", userName));
        formparams.add(new BasicNameValuePair("password", password));
        formparams.add(new BasicNameValuePair("vcode", vcode));
        if (this.swxlCookie == null) {
            return false;
        }
        httpclient.setCookieStore(this.swxlCookie);
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "utf-8");
            httppost.setEntity(entity);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            String s = EntityUtils.toString(resEntity);
            System.out.println("s" + s);
            isSucess = !s.contains("验证码输入错误");
            if (isSucess) {
                isSucess = !s.contains("验证码不正确");
            }
            // 获取登录cookie
            if (isSucess) {
                this.setSwxlCookie(httpclient.getCookieStore());
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            httppost.abort();
            httpclient.getConnectionManager().shutdown();
        }
        return isSucess;
    }

    /**
     * 获取铃音信息
     *
     * @param operateId
     * @return {"recode":"000000","message":"成功",
     * "data":{
     * "data":[
     * {"id":"9178900020190716121088","ringFilePath":"/v1/ring/2019/07/16/bc439a5facdb44809d11b9e7ca920a22.mp3",
     * "groupId":"9b4e684484b94531a08e06f5f2ef1e72",
     * "ringName":"朗诗德集团广东","ctime":"2019-07-16 11:09:11","status":"2","remark":"铃音审核通过"}
     * ],"recordsTotal":1},"success":true}
     * @throws NoLoginException
     * @throws IOException
     */
    public String getRingInfo(String operateId) throws NoLoginException, IOException {
        long longTime = new Date().getTime();
        String url = getGroupRingInfo_URL + "?order=asc&maxresult=5&offset=0&currentpage=1&draw=1&start=0&groupId=" + operateId+"&_="+longTime;
        String result = sendGet(url);
        log.info("获取铃音信息 参数：{} 结果：{}", operateId, result);
        return result;
    }


    /**
     * 根据号码查询彩铃功能
     * (废弃 已使用getRingInfo代替)
     *
     * @param msisdn
     * @param typeFlag
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public String swxlrefreshCrbtStatus(String msisdn, int typeFlag) throws NoLoginException, IOException {
        Integer type = 1;// 查询炫铃功能是否开通
        if (typeFlag == 1) {
            type = 2;// 查询包月功能是否开通
        }
        String url = swxlrefreshCrbtStatus_url + "?msisdn=" + msisdn + "&type=" + type;
        String result = sendGet(url);
        log.info("联通根据号码查询彩铃功能--->" + result);
        return result;
    }

    /**
     * 获取铃音分发详细
     *
     * @param ringid
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public String getSwxlRingFenFaAreaInfo(String ringid) throws NoLoginException, IOException {
        String getUrl = getSwxlRingFenFa_URL + "/" + ringid + "?&maxresult=50";
        String result = sendGet(getUrl);
        log.info("联通获取铃音分发详细 参数：{} 结果：{} ", ringid, result);
        return result;
    }

    /**
     * 查询用户信息
     *
     * @param phone
     * @param groupId
     * @return
     * @throws NoLoginException
     */
    public String getPhoneInfo(String phone, String groupId) throws NoLoginException, IOException {
        //刷新彩铃状态
        //swxlrefreshCrbtStatus(phone, 1);
        //swxlrefreshCrbtStatus(phone, 2);
        //
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("msisdn", phone));
        formparams.add(new BasicNameValuePair("groupId", groupId));
        String url = ADD_PHONE_URL + "?" + URLEncodedUtils.format(formparams, "UTF-8");
        String result = sendGet(url);
        log.info("联通查询用户信息--->" + result);
        return result;
    }

    /**
     * 获取成员信息
     *
     * @param phone
     * @param groupId
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public String refreshUserStatus(String phone, String groupId) throws NoLoginException, IOException {
        long startTime = System.currentTimeMillis();
        String url = "https://swxl.10155.com/swxlapi/web/member?order=asc&maxresult=10&offset=0&currentpage=1&draw=1&start=0&msisdn=&crbtStatus=&monthStatus=&groupId=" + groupId + "&_=" + startTime;
        //String nes = "https://swxl.10155.com/swxlapi/web/member?order=asc&maxresult=10&offset=0&currentpage=1&draw=1&start=0&msisdn=&crbtStatus=&monthStatus=&groupId=c3aa9aa94a1c4da7b94187c4d4392ada&_=1570884087972";
        String result = sendGet(url);
//        log.info("联通查询用户信息--->" + result);
        //
//        List<NameValuePair> formparams = new ArrayList<NameValuePair>();JSONObject jsonObject
//        formparams.add(new BasicNameValuePair("msisdn", phone));
//        formparams.add(new BasicNameValuePair("groupId", groupId));
//        String url = ADD_PHONE_URL + "?" + URLEncodedUtils.format(formparams, "UTF-8");
//        String result = sendGet(url);
//        log.info("联通查询用户信息--->" + result);
        return result;
    }

    /**
     * 发送短信
     *
     * @param phoneNo
     * @param groupId
     * @param smsConfirm
     * @return {"recode":"000000","message":"成功","data":null,"success":true}
     * @throws NoLoginException
     * @throws IOException
     */
    public String remindOrderCrbtAndMonth(String phoneNo, String groupId, boolean smsConfirm) throws NoLoginException, IOException {
        String result = null;
        HashMap map = new HashMap();
        map.put("allFlag", "0");
        map.put("msisdn", phoneNo);
        map.put("groupId", groupId);
        if (smsConfirm) {
            result = sendPost(map, orderCrbtAndMonth_URL); // 免短信开通
        } else {
            result = sendPost(map, remindOrderCrbtAndMonth_URL); // 短信下发
        }
        log.info("联通发送短信 参数：{},{},{} 结果：{}", phoneNo, groupId, smsConfirm, result);
        return result;
    }

    /**
     * 发送链接短信
     *
     * @param msisdn
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public String swxlSendSMSByCRBTFail(String msisdn) throws NoLoginException, IOException {
        String result = null;
        HashMap map = new HashMap();
        map.put("msisdn", msisdn);
        map.put("bizType", "CRBT_AND_MONTH");
        map.put("ringId", msisdn);
        map.put("confirmType", "CONFIRM_BY_WEB");
        result = sendPost(map, Send_SMS_SecondMsg_URL);
        log.info("联通发送链接短信 参数：{} 结果：{}", msisdn, result);
        return result;
    }

    /**
     * 设置铃音
     *
     * @param members
     * @param ringId
     * @return
     */
    public String setRingForPhone(String members, String ringId) throws NoLoginException, IOException {
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("ringId", ringId));
        formparams.add(new BasicNameValuePair("msisdn", members));
        HashMap map = new HashMap();
        map.put("ringId", ringId);
        map.put("msisdn", members);
        String result = sendPost(map, PhoneSetRing_URL);
        log.info("联通 设置铃音 参数：{},{} 结果：{}", members, ringId, result);
        return result;
    }

    /**
     * 联通GET方式封装
     *
     * @param getUrl
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    private String sendGet(String getUrl) throws NoLoginException, IOException {
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(this.getCookieStore())
                .build();
        HttpGet httpGet = new HttpGet(getUrl);
        CloseableHttpResponse response = httpclient.execute(httpGet);// 进入
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = response.getEntity();
                String resStr = EntityUtils.toString(resEntity);
                this.setSwxlCookie(this.getCookieStore());
                return resStr;
            }
        } catch (Exception e) {
            log.error("联通 sendGet 错误信息", e);
        } finally {
            httpGet.abort();
            response.close();
        }
        return null;
    }

    /**
     * 联通POST方式封装
     *
     * @param paramMap
     * @param url
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    private String sendPost(HashMap<String, String> paramMap, String url) throws NoLoginException, IOException {
        String result = null;
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(this.getCookieStore())
                .build();
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
            this.setSwxlCookie(this.getCookieStore());
        } catch (Exception e) {
            log.error("联通 sendPost 错误信息", e);
        } finally {
            httppost.abort();
            response.close();
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
    public SwxlGroupResponse addGroup(ThreenetsOrder ringOrder, ThreeNetsOrderAttached attached) throws IOException, NoLoginException {
        SwxlGroupResponse swxlAddGroupRespone = null;
        DefaultHttpClient httpclient = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
        HttpPost httppost = new HttpPost(addGroup_url);
        httpclient.setCookieStore(this.getCookieStore());
        try {
            MultipartEntity reqEntity = new MultipartEntity();
            HttpParams params = httpclient.getParams();
            params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, Charset.forName("UTF-8"));
            reqEntity.addPart("groupName", new StringBody(ringOrder.getCompanyName(), Charset.forName("UTF-8")));// 集团名称
            reqEntity.addPart("tel", new StringBody(ringOrder.getLinkmanTel()));//
            reqEntity.addPart("payType", new StringBody("0"));
            String applyForSmsNotification = "0";
            if (StringUtils.isNotEmpty(attached.getAvoidShortAgreement()) && ringOrder.getMianduan().equals("1")) {
                applyForSmsNotification = "1";
                String[] split = attached.getAvoidShortAgreement().split(";");
                for (int i = 0; i < split.length; i++) {
                    if (StringUtils.isEmpty(split[i])) {
                        continue;
                    }
                    File file = new File(RingtoneConfig.getProfile() + split[i]);
                    reqEntity.addPart("smsFile", new FileBody(file));
                }
            }
            reqEntity.addPart("applyForSmsNotification", new StringBody(applyForSmsNotification));// 免短信
            reqEntity.addPart("qualificationFile", new StringBody(""));
            if (attached.getSwxlPrice() == 5) {
                reqEntity.addPart("productId", new StringBody("0000003165"));//价格5元
            } else if (attached.getSwxlPrice() == 10) {
                reqEntity.addPart("productId", new StringBody("225"));//价格10元
            } else {
                reqEntity.addPart("productId", new StringBody("224"));//价格20元
            }
            if (ringOrder.getUpLoadAgreement() != null && StringUtils.isNotEmpty(ringOrder.getRingName())) {
                reqEntity.addPart("ringName", new StringBody(ringOrder.getRingName(), Charset.forName("UTF-8")));// 铃音名称
                reqEntity.addPart("ringFile", new FileBody(ringOrder.getUpLoadAgreement())); // 铃音文件
            }
            reqEntity.addPart("msisdns", new StringBody(ringOrder.getPhones()));// 号码
            reqEntity.addPart("bizCodes", new StringBody(""));
            httppost.setEntity(reqEntity);
            HttpResponse response1 = httpclient.execute(httppost);
            int statusCode = response1.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                log.debug("服务器正常响应.....");
                HttpEntity resEntity = response1.getEntity();
                String res = EntityUtils.toString(resEntity);
                log.info("联通商户建立=>" + res);
                //res{"recode":"000000","message":"成功","data":{"groupId":"2e33a3e9c0e1452d83a6fa3e5a8ad2e2"},"success":true}
                ObjectMapper mapper = new ObjectMapper();
                if (res.contains("000000")) {
                    // 创建集团成功
                    // 查询集团信息
                    for (int i = 0; i < 3; i++) {
                        swxlAddGroupRespone = getGRoupInfo(ringOrder);
                        if (swxlAddGroupRespone != null) {
                            break;
                        }
                    }
                    if (swxlAddGroupRespone == null){
                        JSONObject jsonObject = JSONObject.fromObject("res");
                        JSONObject data = jsonObject.getJSONObject("data");
                        String groupId = data.getString("groupId");
                        swxlAddGroupRespone.setStatus(0);
                        swxlAddGroupRespone.setId(groupId);
                    }
                    this.setSwxlCookie(httpclient.getCookieStore());
                } else {
                    SwxlBaseBackMessage<SwxlAddPhoneNewResult> createGroupInfo = mapper.readValue(res, new TypeReference<SwxlBaseBackMessage<SwxlAddPhoneNewResult>>() {
                    });
                    swxlAddGroupRespone = new SwxlGroupResponse();
                    swxlAddGroupRespone.setStatus(-1);
                    if (createGroupInfo.getData() != null) {
                        List<SwxlAddPhoneFailInfo> list = createGroupInfo.getData().getFailedList();
                        StringBuffer msg = new StringBuffer("同步失败:");
                        if (list != null && list.size() > 0) {
                            for (SwxlAddPhoneFailInfo info : list) {
                                msg.append(info.getMsisdn());
                                msg.append(info.getInfo());
                            }
                        }
                        if (list == null) {
                            list = new ArrayList<SwxlAddPhoneFailInfo>();
                            msg.append("创建集团失败！");
                            log.debug("创建集团失败.....");
                        }
                        swxlAddGroupRespone.setRemark(msg.toString());
                    } else {
                        swxlAddGroupRespone.setRemark(createGroupInfo.getMessage());
                    }
                }
            } else {
                log.debug("联通服务器相应失败.....");
            }
        } catch (Exception e) {
            swxlAddGroupRespone = new SwxlGroupResponse();
            swxlAddGroupRespone.setStatus(1);
            swxlAddGroupRespone.setRemark("添加商户异常");
            log.error("添加商户异常[{}]", e.getMessage());
        } finally {
            httppost.abort();
            httpclient.getConnectionManager().shutdown();
        }
        return swxlAddGroupRespone;
    }

    /**
     * 查询商户（集团）信息
     *
     * @param order
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public SwxlGroupResponse getGRoupInfo(ThreenetsOrder order) throws IOException, NoLoginException {
        SwxlGroupResponse content = new SwxlGroupResponse();
        DefaultHttpClient httpclient = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
        long longTime = new Date().getTime();
        String getUrl = "https://swxl.10155.com/swxlapi/web/group?order=asc&maxresult=15&offset=0&currentpage=1&draw=1&start=0&groupName=" + order.getCompanyName() + "&msisdn=" + order.getLinkmanTel() + "&status=&noSMS=&payType=&payWay=&_=" + longTime;
        getUrl = getUrl.replaceAll(" ", "%20");
        HttpGet httpGet = new HttpGet(getUrl);
        httpclient.setCookieStore(this.getCookieStore());
        try {
            HttpResponse response = httpclient.execute(httpGet);// 进入
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = response.getEntity();
                String resStr = EntityUtils.toString(resEntity);
                this.setSwxlCookie(httpclient.getCookieStore());
                log.debug("更新号码，取得的内容：" + resStr);
                if (resStr.contains("000000")) {
                    ObjectMapper mapper = new ObjectMapper();
                    //{"recode":"000000","message":"成功","data":{"recordsFiltered":1,"data":[{"id":"982c6622e0a1449aba42f8993bbbae4e","managerId":"2c6387a2f3b041c38eaad7ce5f311a0a","managerName":"江苏中高俊聪","managerMsisdn":"15601635530","groupName":"华东花卉总代209942","productId":"225","ctime":"2019-10-18 09:22:48","memberCount":1,"applyForSmsNotification":0,"status":0,"platformId":null,"remark":null,"platformQdId":null,"payType":0,"productName":"商务炫铃10元","productPrice":"10","tel":"15605226309","payWay":0,"discount":null,"days":null,"source":null}],"recordsTotal":1},"success":true}
                    JSONObject jsonObject = JSONObject.fromObject(resStr);
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray jsonArray = data.getJSONArray("data");
                    for (int i = 0; i < jsonArray.size(); i++) {
                        //"platformId":null,"remark":null,"platformQdId":null,"payType":0,"productName":"商务炫铃10元","productPrice":"10","tel":"15605226309","payWay":0,"discount":null,"days":null,"source":null}
                        JSONObject partDaily = jsonArray.getJSONObject(i);
                        content.setId(partDaily.getString("id"));
                        content.setManagerId(partDaily.getString("managerId"));
                        content.setManagerName(partDaily.getString("managerName"));
                        content.setManagerMsisdn(partDaily.getString("managerMsisdn"));
                        content.setGroupName(partDaily.getString("groupName"));
                        content.setProductId(partDaily.getString("productId"));
                        content.setCtime(partDaily.getString("ctime"));
                        content.setMemberCount(partDaily.getInt("memberCount"));
                        content.setApplyForSmsNotification(partDaily.getInt("applyForSmsNotification"));
                        content.setStatus(partDaily.getInt("status"));
                        content.setPlatformId(partDaily.getString("platformId"));
                        content.setRemark(partDaily.getString("remark"));
                        content.setPlatformQdId(partDaily.getString("platformQdId"));
                        content.setPayType(partDaily.getString("payType"));
                        content.setProductName(partDaily.getString("productName"));
                        content.setProductPrice(partDaily.getString("productPrice"));
                        content.setTel(partDaily.getString("tel"));
                        content.setPayWay(partDaily.getString("payWay"));
                        content.setDiscount(partDaily.getString("discount"));
                        content.setDays(partDaily.getString("days"));
                    }
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpGet.abort();
            httpclient.getConnectionManager().shutdown();
        }
        return content;
    }


    /**
     * 为商务炫铃--整个集团下发 开通彩铃短信以及开通包月短信
     *
     * @param groupId
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public boolean SwxlSendPhoneSMS(String groupId) throws IOException, NoLoginException {
        boolean result = false;
        DefaultHttpClient httpclient = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
        HttpPost httppost = new HttpPost(Send_PHONESMS_URL + "/" + groupId);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        httpclient.setCookieStore(this.getCookieStore());
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "utf-8");
            httppost.setEntity(entity);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            String responseStr = EntityUtils.toString(resEntity);
            System.out.println("发送短信息结果:" + responseStr);
            if (responseStr.contains("000000")) {
                result = true;
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            httppost.abort();
            httpclient.getConnectionManager().shutdown();
        }
        return result;
    }

    /**
     * 向商务炫铃增加号码
     *
     * @param members
     * @param groupId
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public SwxlBaseBackMessage addPhone(String members, String groupId) throws NoLoginException {
        String result = null;
        SwxlBaseBackMessage<SwxlAddPhoneNewResult> info = null;
        DefaultHttpClient httpclient = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
        HttpPost httppost = new HttpPost(ADD_PHONE_URL);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("groupId", groupId));
        formparams.add(new BasicNameValuePair("msisdns", members));
        httpclient.setCookieStore(this.getCookieStore());
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "utf-8");
            httppost.setEntity(entity);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            result = EntityUtils.toString(resEntity);
            //{"recode":"200015","message":"成员属于其他企业,无法操作","data":null,"success":false}
            info = SpringUtils.getBean(ObjectMapper.class).readValue(result, SwxlBaseBackMessage.class);
            System.out.println("result:" + result);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            httppost.abort();
            httpclient.getConnectionManager().shutdown();
        }
        return info;
    }

    /**
     * 添加成员信息，免短专用
     *
     * @param members
     * @param groupId
     * @param avoidShortAgreement
     * @return
     * @throws NoLoginException
     */
    public SwxlBaseBackMessage addPhone(String members, String groupId, String avoidShortAgreement) throws NoLoginException {
        String result = null;
        SwxlBaseBackMessage<SwxlAddPhoneNewResult> info = null;
        DefaultHttpClient httpclient = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
        HttpPost httppost = new HttpPost(ADD_PHONE_URL);
        httpclient.setCookieStore(this.getCookieStore());
        try {
            MultipartEntity reqEntity = new MultipartEntity();
            HttpParams params = httpclient.getParams();
            params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, Charset.forName("UTF-8"));
            reqEntity.addPart("groupId", new StringBody(groupId, Charset.forName("UTF-8")));// 集团名称
            reqEntity.addPart("msisdns", new StringBody(members, Charset.forName("UTF-8")));//
            if (StringUtils.isNotEmpty(avoidShortAgreement)) {
                String[] split = avoidShortAgreement.split(";");
                for (int i = 0; i < split.length; i++) {
                    if (StringUtils.isEmpty(split[i])) {
                        continue;
                    }
                    File file = new File(RingtoneConfig.getProfile() + split[i]);
                    reqEntity.addPart("smsFile", new FileBody(file));
                }
            }
            httppost.setEntity(reqEntity);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            result = EntityUtils.toString(resEntity);
            //{"recode":"200015","message":"成员属于其他企业,无法操作","data":null,"success":false}
            info = SpringUtils.getBean(ObjectMapper.class).readValue(result, SwxlBaseBackMessage.class);
            System.out.println("result:" + result);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            httppost.abort();
            httpclient.getConnectionManager().shutdown();
        }
        return info;
    }

    /***
     * 上传铃音--商务炫铃
     * @param ring
     * return flag:true 返回 ring.ringId 为 商务炫铃铃音ID
     */
    public boolean addRing(ThreenetsRing ring, String circleID) throws IOException, NoLoginException {
        boolean flag = false;
        DefaultHttpClient httpclient = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
        HttpPost httppost = new HttpPost(importSwxlRing_URL);
        httpclient.setCookieStore(this.getCookieStore());
        try {
            MultipartEntity reqEntity = new MultipartEntity();
            HttpParams params = httpclient.getParams();
            params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, Charset.forName("UTF-8"));
            reqEntity.addPart("ringName", new StringBody(ring.getRingName().split("\\.")[0], Charset.forName("UTF-8")));
            reqEntity.addPart("groupId", new StringBody(circleID, Charset.forName("UTF-8")));
            reqEntity.addPart("ringFile", new FileBody(ring.getFile(), "audio/mp3"));
            httppost.setEntity(reqEntity);
            HttpResponse response1 = httpclient.execute(httppost);
            int statusCode = response1.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                log.debug("铃音上传服务器正常响应.....");
                // HttpEntity resEntity = response1.getEntity();
                String result = EntityUtils.toString(response1.getEntity());
                flag = !result.contains("铃音名称已经存在，请修改");
                // 铃音文件时长超过48秒
                this.setSwxlCookie(httpclient.getCookieStore());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httppost.abort();
            httpclient.getConnectionManager().shutdown();
        }
        return flag;
    }

    /**
     * 创建子渠道商
     *
     * @param user
     * @return {"recode":"000000","message":"成功","data":null,"success":true}
     * @throws NoLoginException
     * @throws IOException
     */
    public String addChild(User user) throws NoLoginException, IOException {
        String result = null;
        DefaultHttpClient httpclient = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
        HttpPost httppost = new HttpPost(addChild_url);
        httpclient.setCookieStore(this.getCookieStore());
        try {
            MultipartEntity reqEntity = new MultipartEntity();
            HttpParams params = httpclient.getParams();
            params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, Charset.forName("UTF-8"));
            reqEntity.addPart("realname", new StringBody(user.getUserName(), Charset.forName("UTF-8")));
            reqEntity.addPart("name", new StringBody(user.getUserName(), Charset.forName("UTF-8")));
            reqEntity.addPart("msisdn", new StringBody(PhoneUtils.getTel(), Charset.forName("UTF-8")));
            reqEntity.addPart("customerPhone", new StringBody(user.getUserTel(), Charset.forName("UTF-8")));
            reqEntity.addPart("province", new StringBody(user.getProvince(), Charset.forName("UTF-8")));
            reqEntity.addPart("password", new StringBody(PASSWORD2, Charset.forName("UTF-8")));
            reqEntity.addPart("qqNum", new StringBody(user.getUserQq(), Charset.forName("UTF-8")));
            if (user.getIdGroupFrontFile() != null) {
                reqEntity.addPart("idGroupFrontFile", new FileBody(user.getIdGroupFrontFile()));
            }
            if (user.getIdGroupReverseFile() != null) {
                reqEntity.addPart("idGroupReverseFile", new FileBody(user.getIdGroupReverseFile()));
            }
            reqEntity.addPart("parentId", new StringBody("7940f534-138d-43d3-88f1-352d4232b9fa", Charset.forName("UTF-8")));
            httppost.setEntity(reqEntity);
            HttpResponse response1 = httpclient.execute(httppost);
            int statusCode = response1.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = response1.getEntity();
                result = EntityUtils.toString(resEntity);
                log.debug("创建子账号 参数：{} 结果：{}", user.toString(), result);
            }
        } catch (Exception e) {
            log.error("创建子账号 错误信息", e);
        } finally {
            httppost.abort();
            httpclient.getConnectionManager().shutdown();
        }
        return result;
    }

    /**
     * 工具箱-->联通 获取沉默用户信息
     *
     * @param msisdn
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public String getSilentMemberByMsisdn(String msisdn) throws NoLoginException, IOException {
//        String result = null;
//        HashMap map = new HashMap();
//        map.put("msisdn", msisdn);
//        result = sendPost(map, silentMember_url+"?msisdn="+msisdn);
//        log.info("联通发送链接短信 参数：{} 结果：{}", msisdn, result);
//        return result;

        String content = null;
        DefaultHttpClient httpclient = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
        HttpGet httpGet = new HttpGet(silentMember_url + "?msisdn=" + msisdn);
        httpclient.setCookieStore(this.getCookieStore());
        try {
            HttpResponse response = httpclient.execute(httpGet);// 进入
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = response.getEntity();
                content = EntityUtils.toString(resEntity);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpGet.abort();
            httpclient.getConnectionManager().shutdown();
        }
        return content;
    }

    /**
     * 工具箱-->联通 获取用户操作记录
     *
     * @param msisdn
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public String getSystemLogListByMsisdn(String msisdn) throws NoLoginException, IOException {
//        String result = null;
//        HashMap map = new HashMap();
//        map.put("msisdn", msisdn);
//        result = sendPost(map, systemLogList_url+"?msisdn="+msisdn+"&pageSize=100");
//        log.info("联通发送链接短信 参数：{} 结果：{}", msisdn, result);
//        return result;

        String content = null;
        DefaultHttpClient httpclient = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
        HttpGet httpGet = new HttpGet(systemLogList_url + "?msisdn=" + msisdn + "&pageSize=100");
        httpclient.setCookieStore(this.getCookieStore());
        try {
            HttpResponse response = httpclient.execute(httpGet);// 进入
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = response.getEntity();
                content = EntityUtils.toString(resEntity);
//				this.setMiguCookie(httpclient.getCookieStore());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpGet.abort();
            httpclient.getConnectionManager().shutdown();
        }
        return content;
    }

    /**
     * 工具箱-》联通删除用户信息
     *
     * @param msisdn
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public String deleteSilentMemberByMsisdn(String msisdn) throws NoLoginException, IOException {
        String content = null;
        DefaultHttpClient httpclient = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
        HttpDelete httpDel = new HttpDelete(delete_url + msisdn);
        httpclient.setCookieStore(this.getCookieStore());
        try {
            HttpResponse response = httpclient.execute(httpDel);// 进入
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = response.getEntity();
                content = EntityUtils.toString(resEntity);
//				this.setMiguCookie(httpclient.getCookieStore());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpDel.abort();
            httpclient.getConnectionManager().shutdown();
        }
        return content;
    }


    /**
     * 检查手机号是否可以添加
     *
     * @param phone
     * @return
     */
    public String checkMobiles(String phone) {
        String result = null;
        SwxlBaseBackMessage<SwxlAddPhoneNewResult> info = null;
        DefaultHttpClient httpclient = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
        HttpPost httppost = new HttpPost(check_mobiles);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("msisdns", phone));
        formparams.add(new BasicNameValuePair("applyForSmsNotification", "0"));
        formparams.add(new BasicNameValuePair("productId", "225"));
        try {
            httpclient.setCookieStore(this.getCookieStore());
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "utf-8");
            httppost.setEntity(entity);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            result = EntityUtils.toString(resEntity);
            //{"recode":"200015","message":"成员属于其他企业,无法操作","data":null,"success":false}
            System.out.println("result:" + result);
        } catch (NoLoginException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            httppost.abort();
            httpclient.getConnectionManager().shutdown();
            return result;
        }
    }

    public String refreshOrderId(Integer orderId){
        String id = "";
        ThreenetsOrder order = new ThreenetsOrder();
        try{
            order = SpringUtils.getBean(ThreenetsOrderMapper.class).selectByPrimaryKey(orderId);
        }catch(Exception e) {
            e.printStackTrace();
        }
        DefaultHttpClient httpclient = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
        long longTime = new Date().getTime();
        String getUrl = "https://swxl.10155.com/swxlapi/web/group?order=asc&maxresult=15&offset=0&currentpage=1&draw=1&start=0&groupName=" + order.getCompanyName() + "&msisdn=" + order.getLinkmanTel() + "&status=&noSMS=&payType=&payWay=&_=" + longTime;
        getUrl = getUrl.replaceAll(" ", "%20");
        HttpGet httpGet = new HttpGet(getUrl);
        try {
            httpclient.setCookieStore(this.getCookieStore());
            HttpResponse response = httpclient.execute(httpGet);// 进入
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = response.getEntity();
                String resStr = EntityUtils.toString(resEntity);
                this.setSwxlCookie(httpclient.getCookieStore());
                log.debug("更新号码，取得的内容：" + resStr);
                if (resStr.contains("000000")) {
                    ObjectMapper mapper = new ObjectMapper();
                    //{"recode":"000000","message":"成功","data":{"recordsFiltered":1,"data":[{"id":"982c6622e0a1449aba42f8993bbbae4e","managerId":"2c6387a2f3b041c38eaad7ce5f311a0a","managerName":"江苏中高俊聪","managerMsisdn":"15601635530","groupName":"华东花卉总代209942","productId":"225","ctime":"2019-10-18 09:22:48","memberCount":1,"applyForSmsNotification":0,"status":0,"platformId":null,"remark":null,"platformQdId":null,"payType":0,"productName":"商务炫铃10元","productPrice":"10","tel":"15605226309","payWay":0,"discount":null,"days":null,"source":null}],"recordsTotal":1},"success":true}
                    JSONObject jsonObject = JSONObject.fromObject(resStr);
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray jsonArray = data.getJSONArray("data");
                    for (int i = 0; i < jsonArray.size(); i++) {
                        //"platformId":null,"remark":null,"platformQdId":null,"payType":0,"productName":"商务炫铃10元","productPrice":"10","tel":"15605226309","payWay":0,"discount":null,"days":null,"source":null}
                        JSONObject partDaily = jsonArray.getJSONObject(i);
                        id = partDaily.getString("id");
                    }
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoLoginException e) {
            e.printStackTrace();
        } finally {
            httpGet.abort();
            httpclient.getConnectionManager().shutdown();
        }
        return id;
    }
}
