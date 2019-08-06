package com.hrtxn.ringtone.common.api;

import com.hrtxn.ringtone.common.exception.NoLoginException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.Cookie;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Author:lile
 * Date:2019/8/5 9:34
 * Description:电信api
 */
@Slf4j
public class McardApi {

    private static String add_user_url = "https://mcard.imusic.cn/user/createNormalUser";//新增商户
    private static String to_user_list = "https://mcard.imusic.cn/user/userList";//跳转到商户  get   ?userId=1670298
    private static String add_apersonnel_url = "https://mcard.imusic.cn/user/addApersonnel";//新增成员
    
    private static String upload_file_url = "https://mcard.imusic.cn/file/uploadFile";//文件上传
    private static String saveRing_url = "http://mcard.imusic.cn/file/saveRing";//铃音上传
    private static String settingRing_url = "http://mcard.imusic.cn/ring/setUserRing";//设置铃音

    private static String normal_list = "http://mcard.imusic.cn/user/loadNormalBusinessList";//获取客户列表
    private static String refresh_apersonnel = "http://mcard.imusic.cn/user/refreshApersonnel";//刷新用户信息

    //主渠道商id
    private static String parent = "61203";
    //子渠道商（17712033392）
    private static String child_Distributor_ID_177 = "594095";
    //子渠道商（18159093112）
    private static String child_Distributor_ID_181 = "296577";
    //子渠道商（18888666361）
    private static String child_Distributor_ID_188 = "61204";

    private CookieStore macrdCookie;// 音乐名片登录cookie

    public CookieStore getCookieStore() throws NoLoginException, IOException {

        return this.macrdCookie;
    }
































    //添加商户
    public String addGroup() throws IOException, NoLoginException {
        String result = null;

        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(this.getCookieStore()).build();
        HttpPost httppost = new HttpPost(add_user_url);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();

        formparams.add(new BasicNameValuePair("ausertype", ""));
        formparams.add(new BasicNameValuePair("codeId", ""));
        formparams.add(new BasicNameValuePair("provinceChannel", ""));
        formparams.add(new BasicNameValuePair("isFeeType", "0"));
        formparams.add(new BasicNameValuePair("makeFee", ""));
        formparams.add(new BasicNameValuePair("feeType", "11"));
        formparams.add(new BasicNameValuePair("aUserProvince", "江苏"));
        formparams.add(new BasicNameValuePair("aUserCity", "徐州"));
        formparams.add(new BasicNameValuePair("phoneProvinceCode", "320000"));
        formparams.add(new BasicNameValuePair("checkUnipayphone", ""));
        formparams.add(new BasicNameValuePair("makeFeeType", ""));
        formparams.add(new BasicNameValuePair("phoneCityCode", "320300"));
        formparams.add(new BasicNameValuePair("auserAccount", "15380170139"));
        formparams.add(new BasicNameValuePair("auserName", "九方愉悦商贸"));
        formparams.add(new BasicNameValuePair("auserLinkName", "李乐"));
        formparams.add(new BasicNameValuePair("auserMoney", "11"));
        formparams.add(new BasicNameValuePair("auserPhone", "15380170139"));
        formparams.add(new BasicNameValuePair("auserEmail", ""));
        formparams.add(new BasicNameValuePair("chargingPhone", "15380170139"));
        formparams.add(new BasicNameValuePair("auserWeixin", ""));
        formparams.add(new BasicNameValuePair("auserYi", ""));
        formparams.add(new BasicNameValuePair("auserFengChao", ""));
        formparams.add(new BasicNameValuePair("busiSeizedName", ""));
        formparams.add(new BasicNameValuePair("busiSeizedPhone", ""));
        formparams.add(new BasicNameValuePair("industry", "1"));
        formparams.add(new BasicNameValuePair("isUnifyPay", "2"));
        formparams.add(new BasicNameValuePair("unifyPayPhone", ""));
        formparams.add(new BasicNameValuePair("imageCode", "ts3f"));
        formparams.add(new BasicNameValuePair("auserBlicencePath", "/pic.diy.v1/nets/mcard/DiyFile/image/2019/08/05/4284b0e7-e95d-4738-bbd1-363019be4c95.jpg"));
        formparams.add(new BasicNameValuePair("auserCardidPath", ""));
        formparams.add(new BasicNameValuePair("auserFilePath", "/pic.diy.v1/nets/mcard/DiyFile/image/2019/08/05/9d92e869-4942-4d3a-9e54-dc839ef3a211.jpg"));

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "utf-8");
        httppost.setEntity(entity);

        CloseableHttpResponse response = httpclient.execute(httppost);
        try {
            HttpEntity resEntity = response.getEntity();
            result = EntityUtils.toString(resEntity);
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

    //添加成员


    //上传铃音
    public String uploadRing(File file){
        return null;
    }

    //上传文件
    public String uploadFile(File file) {
        return null;
    }
}
