package com.hrtxn.ringtone.common.api;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.utils.ChaoJiYing;
import com.hrtxn.ringtone.common.utils.DateUtils;
import com.hrtxn.ringtone.common.utils.HttpUtils;
import com.hrtxn.ringtone.freemark.config.systemConfig.RingtoneConfig;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsOrder;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Author:lile
 * Date:2019/7/26 15:12
 * Description:
 */
@Slf4j
public class McardApi implements Serializable {
    private static final long serialVersionUID = 8245211647467742532L;
    public static String LOGIN_URL = "https://mcard.imusic.cn/user/sLogin-login.action";
    //	public String USER_NAME = "18888666361";
    public String USER_NAME = "17751937950";
    //	public String PASSWORD = "fb2f104e1df86397464385b01e0dced1";
    public String PASSWORD = "wangxuan521.125@";
    public static String deviceid = "10011";
    public static String keyword = "88HFw34kx"; /**/
    public static int ZF10 = 1;// 资费10元
    public static int ZF20 = 2;// 资费20元
    public static String CODE_URL = "https://mcard.imusic.cn/manager/sLogin-RandomCode.action";// 验证码地址
    public static String USER_ENTER_URL = "https://mcard.imusic.cn//user/Auser-aUserList.action?aUserID=";
    public static String addthreenetsOrder_url = "https://mcard.imusic.cn/sIndex-register.action";
    public static String getApgroupId_url = "https://mcard.imusic.cn/user/Auser-getBusiInit.action";
    public static String queryIsPass_url = "https://mcard.imusic.cn/user/Auser-aUserList.action";
    public static String updatethreenetsChildOrder_url = "https://mcard.imusic.cn/user/threenetsChildOrder-updatethreenetsChildOrderStatues.action";
    public static String getthreenetsChildOrderInfo_url = "https://mcard.imusic.cn/sIndex-threenetsChildOrderStatues.action";
    public static String phoneIsexist_url = "https://mcard.imusic.cn/sIndex-threenetsChildOrderStatues.action";
    public static String phoneIsRegister_url = "https://mcard.imusic.cn/iMusicInterface-sendSMS.action";
    public static String openRing_url = "https://mcard.imusic.cn/user/threenetsChildOrder-openRingfunction.action";
    public static String addPhone_url = "https://mcard.imusic.cn/user/threenetsChildOrder-threenetsChildOrder.action?action=SaveAdd";
    public static String loginMcardSub_url = "https://mcard.imusic.cn/user/Auser-getBusiInit.action";
    public static String saveRing_url = "https://mcard.imusic.cn/user/Ring-saveRing.action";
    public static String settingRing_url = "https://mcard.imusic.cn/user/threenetsChildOrder-settingRing.action";
    public static String getRingId_url = "https://mcard.imusic.cn/user/Auser-getRingList.action";
    public static String deleteAperson_url = "https://mcard.imusic.cn/user/threenetsChildOrder-delete.action";
    public static String deleteRing_url = "https://mcard.imusic.cn/user/Ring-delete.action";
    public static String getRingPage_url = "https://mcard.imusic.cn/user/Auser-getRingList.action";
    public CookieStore mcardcookie;
    public String set_cookie;// 字符串Cookie
    public Date connectTime;// 最新连接时间
    public String serviceProtocolFile; // 协议
    public String businesslicenseFile;// 营业执照

    public McardApi() {
        this.serviceProtocolFile = RingtoneConfig.getProfile() + File.separator + "bin" + File.separator + "serviceProtocolFile.png";
        this.businesslicenseFile = RingtoneConfig.getProfile() + File.separator + "bin" + File.separator + "businesslicenseFile.jpg";
    }

    public Date getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(Date connectTime) {
        this.connectTime = connectTime;
    }

    public static void main(String[] args) {
        McardApi mcardApi = new McardApi();
        mcardApi.login();
    }

    /**
     * 设置cookie，更新链接时间
     *
     * @param mcardcookie
     */
    public void setMcardcookie(CookieStore mcardcookie) {
        this.mcardcookie = mcardcookie;
        this.setConnectTime(new Date());
    }

    public void setCookie(String set_cookie) {
        this.set_cookie = set_cookie;
        this.setConnectTime(new Date());
    }

    public CookieStore getMcardcookie() {
        boolean flag = false;
        if (this.mcardcookie == null || !isValidCookieStore()) {
            flag = login();
        } else {
            flag = true;
        }
        if (!flag) {
            // 重新登录第二次
            flag = login();
        }
        if (!flag) {
            // 重新登录第3次
            flag = login();
        }
        return this.mcardcookie;
    }

    /**
     * 判断cookie是否有效 ,采用时间区间判断
     *
     * @return
     */
    private boolean isValidCookieStore() {
        boolean isLogin = false;
        Date now = new Date();
        Date conncetime = this.getConnectTime();
        if (conncetime == null) {
            conncetime = new Date();
        }
        long mills = now.getTime() - conncetime.getTime();
        int betw = (int) (mills / 1000);
        System.out.println("cookie,有效时间：" + betw);
        if (betw < (15 * 60)) {// 超过10分钟，cookie失效，重新登录
            isLogin = true;
        }
        return isLogin;
    }

    /**
     * 爱音乐签名方法
     *
     * @param httppost
     */
    public void httpSetHeader(HttpPost httppost) {
        httppost.setHeader("deviceid", deviceid);
        String timestamp = DateUtils.getTimestamp();
        httppost.setHeader("timestamp", timestamp);
        httppost.setHeader("signature", McardApi.getMD5Str(keyword + timestamp));
    }


    // 进入无制作费
    public void enterWzzfPage(DefaultHttpClient httpclient) {
        String postUrl = "https://mcard.imusic.cn/sIndex-registerExamine.action?ausertype=2&ziqudaoMakeFee=0&provinceChannel=";
        HttpGet httpget = new HttpGet(postUrl);
        try {
            httpclient.setCookieStore(getMcardcookie());
            HttpResponse response1 = httpclient.execute(httpget);
            @SuppressWarnings("unused")
            int statusCode = response1.getStatusLine().getStatusCode();
            String s = EntityUtils.toString(response1.getEntity());
            System.out.println("result:" + s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 进入商户列表
     */
    public void enterList() {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget1 = new HttpGet("https://mcard.imusic.cn/user/Auser-aUserList.action");
        @SuppressWarnings("unused")
        HttpGet httpget2 = new HttpGet("https://mcard.imusic.cn/sIndex-registerExamine.action?ausertype=2&ziqudaoMakeFee=0&provinceChannel=");

        try {
            httpclient.setCookieStore(getMcardcookie());
            HttpResponse response1 = httpclient.execute(httpget1);
            @SuppressWarnings("unused")
            int statusCode = response1.getStatusLine().getStatusCode();
            String s = EntityUtils.toString(response1.getEntity());
            System.out.println("result:" + s);
            /*
             * HttpResponse response2 =httpclient.execute(httpget2);
             *
             * int statusCode2 = response2.getStatusLine().getStatusCode();
             * String s2 = EntityUtils.toString(response2.getEntity());
             * System.out.println("result:"+s2);
             */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 无制作费
     *
     * @param threenetsOrder
     * @return
     */
    public AjaxResult addRingWzzfOrder(ThreenetsOrder threenetsOrder) {
        AjaxResult result = new AjaxResult();

        if (threenetsOrder.getServiceProtocolFile() == null) {
            threenetsOrder.setServiceProtocolFile(new File(serviceProtocolFile));// 设置默认的协议文件
        }
        if (threenetsOrder.getBusinesslicenseFile() == null) {
            threenetsOrder.setBusinesslicenseFile(new File(businesslicenseFile));// 设置默认的协议文件
        }

        DefaultHttpClient httpclient = new DefaultHttpClient();

        enterWzzfPage(httpclient);

        String vcode = getsIndexShCodeString(httpclient);

        HttpPost httppost = new HttpPost("https://mcard.imusic.cn/sIndex-register.action");
        try {
            httpclient.setCookieStore(getMcardcookie());

            MultipartEntity reqEntity = new MultipartEntity();
            HttpParams params = httpclient.getParams();

            params.setParameter("http.protocol.content-charset", Charset.forName("UTF-8"));
            reqEntity.addPart("codeId", new StringBody("61204"));

            reqEntity.addPart("ausertype", new StringBody("2"));

            reqEntity.addPart("isFeeType", new StringBody("0"));
            reqEntity.addPart("provinceChannel", new StringBody(""));
            if (10 == threenetsOrder.getPaymentPrice()) {
                reqEntity.addPart("feeType", new StringBody("2"));
            } else if (20 == threenetsOrder.getPaymentPrice()) {
                reqEntity.addPart("feeType", new StringBody("11"));
            }

            reqEntity.addPart("aUserProvince", new StringBody(threenetsOrder.getProvince(), Charset.forName("UTF-8")));
            reqEntity.addPart("aUserCity", new StringBody(threenetsOrder.getCity(), Charset.forName("UTF-8")));
            reqEntity.addPart("phoneProvinceCode", new StringBody("350000"));
            reqEntity.addPart("auserName", new StringBody(threenetsOrder.getCompanyName(), Charset.forName("UTF-8")));
            reqEntity.addPart("phone", new StringBody(threenetsOrder.getLinkmanTel(), Charset.forName("UTF-8")));
            reqEntity.addPart("auserLinkName", new StringBody(threenetsOrder.getCompanyLinkman(), Charset.forName("UTF-8")));
            reqEntity.addPart("auserPhone", new StringBody(threenetsOrder.getLinkmanTel(), Charset.forName("UTF-8")));
            reqEntity.addPart("auserEmail", new StringBody(""));
            reqEntity.addPart("auserFengChao", new StringBody(""));
            reqEntity.addPart("auserWeixin", new StringBody(""));
            reqEntity.addPart("auserYi", new StringBody(""));
            reqEntity.addPart("auserMoney", new StringBody(threenetsOrder.getPaymentPrice() + ""));

            reqEntity.addPart("feePhone", new StringBody(""));
            reqEntity.addPart("verifycode", new StringBody(""));
            /*
             * reqEntity.addPart("feePhone", new
             * StringBody(threenetsOrder.getFeePhone()));
             * reqEntity.addPart("verifycode", new
             * StringBody(threenetsOrder.getVerifycode()));
             */ reqEntity.addPart("busiSeizedName", new StringBody(""));
            reqEntity.addPart("busiSeizedPhone", new StringBody(""));

            reqEntity.addPart("imageVerifyCode", new StringBody(vcode));

            if (threenetsOrder.getBusinesslicenseFile() != null) {
                reqEntity.addPart("file", new FileBody(threenetsOrder.getBusinesslicenseFile()));
            }

            reqEntity.addPart("hascared", new StringBody("1"));

            if (threenetsOrder.getServiceProtocolFile() != null) {
                reqEntity.addPart("file", new FileBody(threenetsOrder.getServiceProtocolFile()));
            }

            /*
             * reqEntity.addPart("file",new
             * FileBody(threenetsOrder.getServiceProtocolFile()));
             *
             * reqEntity.addPart("file",new
             * FileBody(threenetsOrder.getServiceProtocolFile()));
             * reqEntity.addPart("file",new
             * FileBody(threenetsOrder.getServiceProtocolFile()));
             * reqEntity.addPart("file",new
             * FileBody(threenetsOrder.getServiceProtocolFile()));
             */

            // reqEntity.addPart("file",new
            // FileBody(threenetsOrder.getServiceProtocolFile()));//临时
            // reqEntity.addPart("file",new
            // FileBody(threenetsOrder.getServiceProtocolFile()));//临时

            httppost.setEntity(reqEntity);

            HttpResponse response1 = httpclient.execute(httppost);

            int statusCode = response1.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                log.debug("服务器正常响应.....");

                String s = EntityUtils.toString(response1.getEntity());
                System.out.println("result:" + s);

                if (s != null && s.contains("验证码错误")) {
                    result.error("验证码错误，请重新输入。");
                }
                if (s != null && s.contains("新增成功")) {
                    result.success(true,"新增成功！请耐心等待审核结果。");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httppost.abort();
            httpclient.getConnectionManager().shutdown();
        }

        return result;
    }


    /**
     * 登录，获取cookie
     *
     * @return
     */
    public boolean login() {
        boolean flag = false;
        log.info("电信重新登录中....");
        String code = this.getCreateSHCodeString();
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(LOGIN_URL);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("loginname", this.USER_NAME));
        formparams.add(new BasicNameValuePair("loginType", "1"));
        formparams.add(new BasicNameValuePair("auserType", "1"));
        formparams.add(new BasicNameValuePair("imgCode", code));
        formparams.add(new BasicNameValuePair("password", this.PASSWORD));
        formparams.add(new BasicNameValuePair("verifycode2", ""));
        formparams.add(new BasicNameValuePair("sub", ""));

//		formparams.add(new BasicNameValuePair("mobile", this.USER_NAME));
//		formparams.add(new BasicNameValuePair("channelType", "3"));
//		formparams.add(new BasicNameValuePair("imageCode", code));
//		formparams.add(new BasicNameValuePair("password", this.PASSWORD));
//		formparams.add(new BasicNameValuePair("AjaxResultCode", ""));
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "utf-8");
            httppost.setEntity(entity);
            /////
            // httppost.setHeader("Cookie", this.set_cookie);
            httpclient.setCookieStore(this.mcardcookie);
            /////////
            HttpResponse response = httpclient.execute(httppost);
            String s = EntityUtils.toString(response.getEntity());
            System.out.println("s=" + s);
            @SuppressWarnings("unused")
            Header login_success2 = response.getFirstHeader("Location");
            String login_success = response.getFirstHeader("Location").getValue();
            System.out.println("login_success:" + login_success);
            flag = !s.contains("图形验证码");
            enterList();
            // this.setMcardcookie(httpclient.getCookieStore());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httppost.abort();
            httpclient.getConnectionManager().shutdown();
        }
        return flag;
    }

    /**
     * 爱音乐签名加密
     *
     * @param str
     *            keyword+timestamp
     * @return
     */
    public static String getMD5Str(String str) {
        MessageDigest MessageDigest = null;
        try {
            MessageDigest = MessageDigest.getInstance("MD5");
            MessageDigest.reset();
            MessageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] byteArray = MessageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }

    /**
     * 获取登录验证码
     *
     * @return
     */
    public String getCreateSHCodeString() {
        String code = null;
        // this.getLoginCookie();

        DefaultHttpClient httpclientCode = new DefaultHttpClient();
        double rm = (new Random()).nextDouble();
        HttpGet httpCode = new HttpGet(CODE_URL + "?abc=" + rm);//
        httpclientCode.setCookieStore(this.mcardcookie);
        HttpResponse codeResponse;
        try {
            // 获取验证码
            codeResponse = httpclientCode.execute(httpCode);
            InputStream ins1 = codeResponse.getEntity().getContent();
            //////
            String set_cookie = codeResponse.getFirstHeader("Set-Cookie").getValue();
            System.out.println("set_cookie:" + set_cookie);
            this.setCookie(set_cookie);
            /////////
            this.setMcardcookie(httpclientCode.getCookieStore());
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = ins1.read(buff, 0, 100)) > 0) {
                os.write(buff, 0, rc);
            }
            byte[] in2b = os.toByteArray();
            HttpUtils.downloadNet(ins1);
            String codestr = ChaoJiYing.PostPic("wwewwe", "wyc19931224", "899622", "4004", "4", in2b);
            System.out.println("验证码识别："+codestr);
            JSONArray jsonStr = JSONArray.fromObject("["+codestr+"]");
            for (int i = 0; i < jsonStr.size(); i++) {
                JSONObject json = jsonStr.getJSONObject(i);
                Integer err_no = Integer.parseInt(json.getString("err_no"));
                code = json.getString("pic_str");
                if (err_no == 0) {
                    if(code!= null && code.length() == 4){
                        code = json.getString("pic_str");
                        break;
                    }else {
                        getCreateSHCodeString();
                    }
                } else {
                    getCreateSHCodeString();
                }
            }
            System.out.println("验证码识别："+code);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            httpCode.abort();
            httpclientCode.getConnectionManager().shutdown();
        }

        return code;
    }

    /**
     * 获取创建商户验证码
     *
     * @return
     */
    public String getsIndexShCodeString(DefaultHttpClient httpclientCode) {
        String code = null;

        HttpGet httpCode = new HttpGet("https://mcard.imusic.cn/sIndex-RandomCode.action?codeId=61204");//
        httpclientCode.setCookieStore(getMcardcookie());

        HttpResponse codeResponse;
        try {
            // 获取验证码
            codeResponse = httpclientCode.execute(httpCode);
            InputStream ins1 = codeResponse.getEntity().getContent();
            //////
            /*
             * String set_cookie =
             * codeResponse.getFirstHeader("Set-Cookie").getValue();
             * System.out.println("set_cookie:"+set_cookie);
             * this.setCookie(set_cookie);
             */
            /////////
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = ins1.read(buff, 0, 100)) > 0) {
                os.write(buff, 0, rc);
            }
            byte[] in2b = os.toByteArray();
            HttpUtils.downloadNet(ins1);
            String codestr = YunSu.createByPost("mutouyang", "sgoo361", "3040", "90", "21380", "6ab0fcb48cb74c0b9c73dc0aa9672e8d", in2b);
            // String codestr = AliyunCode.AliyunPaseCode(in2b);
            System.out.println("codestr=" + codestr);
            int begin = codestr.indexOf("<Result>") + 8;
            code = codestr.substring(begin, begin + 4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

}
