package com.hrtxn.ringtone.common.api;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.exception.NoLoginException;
import com.hrtxn.ringtone.common.utils.ChaoJiYing;
import com.hrtxn.ringtone.common.utils.WebClientDevWrapper;
import com.hrtxn.ringtone.common.utils.json.JsonUtil;
import com.hrtxn.ringtone.common.utils.juhe.JuhePhoneUtils;
import com.hrtxn.ringtone.project.system.json.JuhePhone;
import com.hrtxn.ringtone.project.system.json.JuhePhoneResult;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreeNetsOrderAttached;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsChildOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsRing;
import com.hrtxn.ringtone.project.threenets.threenet.json.mcard.McardAddGroupRespone;
import com.hrtxn.ringtone.project.threenets.threenet.json.mcard.McardAddPhoneRespone;
import com.hrtxn.ringtone.project.threenets.threenet.json.mcard.McardPhoneAddressRespone;
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

import javax.servlet.http.Cookie;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Author:lile
 * Date:2019/8/5 9:34
 * Description:电信api
 */
@Slf4j
public class McardApi {

    private static String to_normal_user = "https://mcard.imusic.cn/user/addNormalUser";
    private static String add_user_url = "https://mcard.imusic.cn/user/createNormalUser";//新增商户
    private static String phone_address_url = "https://mcard.imusic.cn/user/getPhoneAddress";
    private static String to_user_list = "https://mcard.imusic.cn/user/userList";//跳转到商户  get   ?userId=1670298
    private static String add_apersonnel_url = "https://mcard.imusic.cn/user/addApersonnel";//新增成员

    private static String upload_file_url = "https://mcard.imusic.cn/file/uploadFile";//文件上传
    private static String saveRing_url = "http://mcard.imusic.cn/file/saveRing";//铃音上传
    private static String settingRing_url = "http://mcard.imusic.cn/ring/setUserRing";//设置铃音

    private static String normal_list = "http://mcard.imusic.cn/user/loadNormalBusinessList";//获取客户列表
    private static String refresh_apersonnel = "http://mcard.imusic.cn/user/refreshApersonnel";//刷新用户信息

    private static String code_url = "http://mcard.imusic.cn/code/imageCode?d";
    //主渠道商id
    private static String parent = "61203";


    private CookieStore macrdCookie;// 电信cookie
    private Date connectTime;// 最新连接时间


    public CookieStore getCookieStore() {

        return this.macrdCookie;
    }

    public void setMacrdCookie(CookieStore swxlCookie) {
        this.macrdCookie = swxlCookie;
        this.setConnectTime(new Date());
    }

    public void setConnectTime(Date connectTime) {
        this.connectTime = connectTime;
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
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(this.getCookieStore()).build();
        HttpGet httpGet = new HttpGet(getUrl);
        CloseableHttpResponse response = httpclient.execute(httpGet);// 进入
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = response.getEntity();
                String resStr = EntityUtils.toString(resEntity);
                this.setMacrdCookie(this.getCookieStore());
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
     * 调用远程验证码接口，取得验证码
     *
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public String getCodeString() {
        String code = null;
        DefaultHttpClient httpclientCode = new DefaultHttpClient();
        long time = new Date().getTime();
        HttpGet httpCode = new HttpGet(code_url + "?d=" + time);//
        HttpResponse codeResponse;
        try {
            // 获取验证码
            codeResponse = httpclientCode.execute(httpCode);
            InputStream ins1 = codeResponse.getEntity().getContent();
            this.setMacrdCookie(httpclientCode.getCookieStore());
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
     * 获取手机号地址
     *
     * @param phone
     * @return
     */
    public McardPhoneAddressRespone phoneAdd(String phone){
        McardPhoneAddressRespone respone = new McardPhoneAddressRespone();
        String result = null;
        DefaultHttpClient httpclient = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
        HttpPost httppost = new HttpPost(phone_address_url);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("loginPhone", phone));
        formparams.add(new BasicNameValuePair("fee", "1"));
        httpclient.setCookieStore(this.getCookieStore());
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "utf-8");
            httppost.setEntity(entity);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            result = EntityUtils.toString(resEntity);
            respone = (McardPhoneAddressRespone) JsonUtil.getObject4JsonString(result, McardPhoneAddressRespone.class);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            httppost.abort();
            httpclient.getConnectionManager().shutdown();
        }
        return respone;
    }

    /**
     * 跳转到对应经销商
     *
     * @param subuserid
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public String toNormalUser(String subuserid)throws IOException,NoLoginException {
        String getUrl = to_normal_user + "?subuserid=" + subuserid+"&parent=61204";
        String result = sendGet(getUrl);
        return result;
    }

    //添加商户
    public McardAddGroupRespone addGroup(ThreenetsOrder order,ThreeNetsOrderAttached attached) throws IOException{
        McardPhoneAddressRespone respone = phoneAdd(order.getLinkmanTel());
        String result = null;
        McardAddGroupRespone groupRespone = new McardAddGroupRespone();
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(this.getCookieStore()).build();
        HttpPost httppost = new HttpPost(add_user_url);

        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("ausertype", ""));
        formparams.add(new BasicNameValuePair("codeId", ""));
        formparams.add(new BasicNameValuePair("provinceChannel", ""));
        formparams.add(new BasicNameValuePair("isFeeType", "0"));
        formparams.add(new BasicNameValuePair("makeFee", ""));
        formparams.add(new BasicNameValuePair("feeType", attached.getMcardPrice().toString()));
        formparams.add(new BasicNameValuePair("aUserProvince", respone.getProvince()));
        formparams.add(new BasicNameValuePair("aUserCity", respone.getCity()));
        formparams.add(new BasicNameValuePair("phoneProvinceCode", respone.getProvinceCode()));
        formparams.add(new BasicNameValuePair("checkUnipayphone", ""));
        formparams.add(new BasicNameValuePair("makeFeeType", ""));
        formparams.add(new BasicNameValuePair("phoneCityCode", respone.getCityCode()));
        formparams.add(new BasicNameValuePair("auserAccount", order.getLinkmanTel()));
        formparams.add(new BasicNameValuePair("auserName", order.getCompanyName()));
        formparams.add(new BasicNameValuePair("auserLinkName", order.getCompanyLinkman()));
        formparams.add(new BasicNameValuePair("auserMoney", attached.getMcardPrice().toString()));
        formparams.add(new BasicNameValuePair("auserPhone", order.getLinkmanTel()));
        formparams.add(new BasicNameValuePair("auserEmail", ""));
        formparams.add(new BasicNameValuePair("chargingPhone", order.getLinkmanTel()));
        formparams.add(new BasicNameValuePair("auserWeixin", ""));
        formparams.add(new BasicNameValuePair("auserYi", ""));
        formparams.add(new BasicNameValuePair("auserFengChao", ""));
        formparams.add(new BasicNameValuePair("busiSeizedName", ""));
        formparams.add(new BasicNameValuePair("busiSeizedPhone", ""));
        formparams.add(new BasicNameValuePair("industry", "1"));
        formparams.add(new BasicNameValuePair("isUnifyPay", "2"));
        formparams.add(new BasicNameValuePair("unifyPayPhone", ""));
        formparams.add(new BasicNameValuePair("imageCode", getCodeString()));
        formparams.add(new BasicNameValuePair("auserBlicencePath", attached.getBusinessLicense()));
        formparams.add(new BasicNameValuePair("auserCardidPath", attached.getConfirmLetter()));
        formparams.add(new BasicNameValuePair("auserFilePath", attached.getSubjectProve()));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "utf-8");
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpclient.execute(httppost);
        try {
            HttpEntity resEntity = response.getEntity();
            result = EntityUtils.toString(resEntity);
            groupRespone = (McardAddGroupRespone) JsonUtil.getObject4JsonString(result, McardAddGroupRespone.class);
        } catch (Exception e) {
            log.error("电信 新增商户 错误", e);
        } finally {
            try {
                httppost.abort();
                response.close();
            } catch (IOException e) {
                log.error("response.close(); 错误信息", e);
            }
        }
        return groupRespone;
    }

    /**
     * 跳转到对应商户
     *
     * @param userId
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public String toUserList(String userId)throws IOException,NoLoginException {
        String getUrl = to_user_list + "?userId=" + userId;
        String result = sendGet(getUrl);
        return result;
    }

    /**
     * 添加集团成员
     * 添加成员之前需要进行跳转到对应商户
     *
     * @param childOrder
     * @return
     */
    public McardAddPhoneRespone addApersonnel(ThreenetsChildOrder childOrder) {
        McardAddPhoneRespone addPhoneRespone = new McardAddPhoneRespone();
        String result = null;
        DefaultHttpClient httpclient = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
        HttpPost httppost = new HttpPost(add_apersonnel_url);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("personnelName", childOrder.getLinkman()));
        formparams.add(new BasicNameValuePair("personnelPhone", childOrder.getLinkmanTel()));
        httpclient.setCookieStore(this.getCookieStore());
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "utf-8");
            httppost.setEntity(entity);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            result = EntityUtils.toString(resEntity);
            addPhoneRespone = (McardAddPhoneRespone) JsonUtil.getObject4JsonString(result, McardAddPhoneRespone.class);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            httppost.abort();
            httpclient.getConnectionManager().shutdown();
        }
        return addPhoneRespone;
    }

    /**
     * 铃音上传
     *
     * @param ring
     * @return
     */
    public boolean uploadRing(ThreenetsRing ring) {
        boolean flag = false;
        String ringName = ring.getRingName().substring(0,ring.getRingName().indexOf("."));
        String result = null;
        DefaultHttpClient httpclient = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
        HttpPost httppost = new HttpPost(saveRing_url);
        httpclient.setCookieStore(this.getCookieStore());
        try {
            MultipartEntity reqEntity = new MultipartEntity();
            HttpParams params = httpclient.getParams();
            params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, Charset.forName("UTF-8"));
            reqEntity.addPart("song", new StringBody(ringName, Charset.forName("UTF-8")));
            reqEntity.addPart("ringName", new StringBody(ringName, Charset.forName("UTF-8")));
            reqEntity.addPart("ringText", new StringBody(ring.getRingContent(), Charset.forName("UTF-8")));
            reqEntity.addPart("singer", new StringBody("无", Charset.forName("UTF-8")));
            reqEntity.addPart("ringFile", new FileBody(ring.getFile(), "audio/mp3"));
            reqEntity.addPart("ext", new StringBody("mp3", Charset.forName("UTF-8")));
            reqEntity.addPart("fileName", new StringBody(ring.getRingName(), Charset.forName("UTF-8")));
            httppost.setEntity(reqEntity);
            HttpResponse response1 = httpclient.execute(httppost);
            int statusCode = response1.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                log.debug("铃音上传服务器正常响应2.....");
                // HttpEntity resEntity = response1.getEntity();
                result = EntityUtils.toString(response1.getEntity());
                flag = true;
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
     * 设置铃音
     *
     * @param ringId
     * @param apersonnelId
     * @return
     */
    public String settingRing(String ringId, String apersonnelId) {
        String result = null;
        DefaultHttpClient httpclient = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
        HttpPost httppost = new HttpPost(settingRing_url);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("ringId", ringId));
        formparams.add(new BasicNameValuePair("apersonnelId", apersonnelId));
        httpclient.setCookieStore(this.getCookieStore());
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "utf-8");
            httppost.setEntity(entity);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            result = EntityUtils.toString(resEntity);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            httppost.abort();
            httpclient.getConnectionManager().shutdown();
        }
        return result;
    }

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    public String uploadFile(File file) {
        String result = null;
        DefaultHttpClient httpclient = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
        HttpPost httppost = new HttpPost(upload_file_url);
        httpclient.setCookieStore(this.getCookieStore());
        try {
            MultipartEntity reqEntity = new MultipartEntity();
            HttpParams params = httpclient.getParams();
            params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, Charset.forName("UTF-8"));
            reqEntity.addPart("file", new FileBody(file));
            httppost.setEntity(reqEntity);
            HttpResponse response1 = httpclient.execute(httppost);
            int statusCode = response1.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                log.debug("电信文件上传成功.....");
                result = EntityUtils.toString(response1.getEntity());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httppost.abort();
            httpclient.getConnectionManager().shutdown();
        }
        return result;
    }
}
