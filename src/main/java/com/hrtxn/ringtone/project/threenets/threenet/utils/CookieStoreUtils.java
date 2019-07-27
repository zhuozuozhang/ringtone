package com.hrtxn.ringtone.project.threenets.threenet.utils;

import com.hrtxn.ringtone.common.api.MiguApi;
import com.hrtxn.ringtone.common.api.SwxlApi;
import com.hrtxn.ringtone.common.exception.NoLoginException;
import com.hrtxn.ringtone.common.utils.ChaoJiYing;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Random;

/**
 * Author:zcy
 * Date:2019-07-22 15:01
 * Description:三网登录验证
 */
public class CookieStoreUtils {
    private CookieStore cookie;//音乐名片登录cookie
    private Date connectTime;//最新连接时间

    /**
     * 更新cookie,顺便更新cookie链接服务器的最新时间
     *
     * @param cookie
     */
    public void setCookie(CookieStore cookie) {
        this.cookie = cookie;
        this.setConnectTime(new Date());
    }
    public CookieStore getCookie() {
        return cookie;
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
    public CookieStore getCookieStore(Integer operate, String url) throws NoLoginException, IOException {
        // 为空的话，先去取
        if (this.cookie == null) {
            // 重新登录获取
            boolean flag = loginAuto(operate,url);
            if (!flag) {
                throw new NoLoginException("未登录");
            }
        }
        // cookie超时，使用时间判断，这样去链接验证，会造成服务器拒绝。
        if (!this.isValidCookieStore()) {
            // 重新登录获取，调用远程验证码接口。
            boolean flag = loginAuto(operate,url);
            if (!flag) {
                throw new NoLoginException("未登录");
            }
        }
        return this.cookie;
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
        if (betw < (15 * 60)) {//超过10分钟，cookie失效，重新登录
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
    public boolean loginAuto(Integer operate, String url) throws IOException, NoLoginException {
        MiguApi miguApi = new MiguApi();
        SwxlApi swxlApi = new SwxlApi();
        String code = getCodeString(url);
        int i = 0;
        boolean flag;
        if (operate == 1){ // 移动登录
            flag = miguApi.login(code);
            if (!flag) {
                i++;
                if (i < 3) {
                    flag = miguApi.login(getCodeString(url));
                }
            }
        }else{ // 联通登录
            flag = swxlApi.login(code);
            if (!flag) {
                i++;
                if (i < 3) {
                    flag = swxlApi.login(getCodeString(url));
                }
            }
        }
        return flag;
    }

    /**
     * 调用远程验证码接口，取得验证码
     *
     * @return
     */
    public String getCodeString(String url) throws IOException {
        String code = null;
        CloseableHttpClient httpclient = HttpClients.custom().build();
        double rm = (new Random()).nextDouble();
        System.out.println(rm);
        HttpGet httpCode = new HttpGet(url + "?abc=" + rm);//
        HttpResponse codeResponse;
        try {
            //获取验证码
            codeResponse = httpclient.execute(httpCode);
            InputStream ins1 = codeResponse.getEntity().getContent();
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
                        getCodeString(url);
                    }
                } else {
                    getCodeString(url);
                }
            }
            System.out.println("验证码识别：" + code);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpCode.abort();
            httpclient.close();
        }
        return code;
    }
}
