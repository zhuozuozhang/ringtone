package com.hrtxn.ringtone.project.threenets.kedas.kedapublic.WxUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @Author:zcy
 * @Date:2019-08-26 9:49
 * @Description:<描述>
 */
public class HttpsGetUtil {
    public static String doHttpsGetJson(String Url)
    {
        String message = "";
        try
        {
            System.out.println("doHttpsGetJson");//TODO:dd
            URL urlGet = new URL(Url);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET");      //必须是get方式请求    24
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");//连接超时30秒28
            System.setProperty("sun.net.client.defaultReadTimeout", "30000"); //读取超时30秒29 30
            http.connect();
            InputStream is =http.getInputStream();
            int size =is.available();
            byte[] jsonBytes =new byte[size];
            is.read(jsonBytes);
            message=new String(jsonBytes,"UTF-8");
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return message;
    }
}
