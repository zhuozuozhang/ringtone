package com.hrtxn.ringtone.project.threenets.kedas.kedapublic.WxUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hrtxn.ringtone.common.utils.StringUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author zcy
 * @date 2019-08-26 10:09
 * 微信配置
 */
public class WxAuthUtil {

    /**
     * APPID
     */
    public static final String APPID = "wx07e8064902d2df4a";
    /**
     * APPSECRET
     */
    public static final String APPSECRET = "768facb11001551e9c027d4e5bbe336b";
    /**
     * 自助下单微信登陆回调地址
     */
    public static final String INDEXBACKURL = "http://zc54es.natappfree.cc/public/indexCallBack";
    public static final String ONLINEBACKURL = "http://zc54es.natappfree.cc/public/onlineCallBack";

//    private static final String TOKEN = "d29ab865de4773c5f98942e08777ed05";

    public static JSONObject doGetJson(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Cache-Control", "no-cache")
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            String result = response.body().string();
            if (StringUtils.isNotEmpty(result)) {
                return JSON.parseObject(result);
            }
        }
        return null;
    }
}
