package com.hrtxn.ringtone.common.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import static com.hrtxn.ringtone.common.utils.ChaoJiYing.MD5;

/**
 * Author:lile
 * Date:2019/7/26 16:05
 * Description:
 */
public class YunSu {
    /**
     * http://www.ysdm.net/home/PriceType 上传题目图片返回结果
     *
     * @param username 用户名 mutouyang
     * @param password 密码 sgoo361
     * @param typeid 题目类型 4位英数混合:3040 、4位纯数字：1040
     * @param timeout 任务超时时间
     * @param softid 软件ID
     * @param softkey 软件KEY
     * @param filePath 题目截图或原始图二进制数据路径
     * @return
     * @throws IOException
     */
    public static String createByPost(String username, String password,String typeid, String timeout, String softid, String softkey, byte[] byteArr) {
        String result = "";
        String param = String.format("username=%s&password=%s&typeid=%s&timeout=%s&softid=%s&softkey=%s",
                username, password, typeid, timeout, softid, softkey);
        try {
            result = YunSu.httpPostImage("http://api.ysdm.net/create.xml",param, byteArr);
        } catch (Exception e) {
            result = "未知问题";
        }
        return result;
    }

    /**
     * 答题
     *
     * @param url 请求URL，不带参数 如：http://api.ysdm.net/register.xml
     * @param param 请求参数，如：username=test&password=1
     * @param data 图片二进制流
     * @return 平台返回结果XML样式
     * @throws IOException
     */
    public static String httpPostImage(String url, String param, byte[] data) throws IOException {
        long time = (new Date()).getTime();
        URL u = null;
        HttpURLConnection con = null;
        String boundary = "----------" + MD5(String.valueOf(time));
        String boundarybytesString = "\r\n--" + boundary + "\r\n";
        OutputStream out = null;
        u = new URL(url);
        con = (HttpURLConnection) u.openConnection();
        con.setRequestMethod("POST");
        // con.setReadTimeout(95000);
        con.setConnectTimeout(95000); // 此值与timeout参数相关，如果timeout参数是90秒，这里就是95000，建议多5秒
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setUseCaches(true);
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        out = con.getOutputStream();
        for (String paramValue : param.split("[&]")) {
            out.write(boundarybytesString.getBytes("UTF-8"));
            String paramString = "Content-Disposition: form-data; name=\"" + paramValue.split("[=]")[0] + "\"\r\n\r\n" + paramValue.split("[=]")[1];
            out.write(paramString.getBytes("UTF-8"));
        }
        out.write(boundarybytesString.getBytes("UTF-8"));
        String paramString = "Content-Disposition: form-data; name=\"image\"; filename=\"" + "sample.gif" + "\"\r\nContent-Type: image/gif\r\n\r\n";
        out.write(paramString.getBytes("UTF-8"));
        out.write(data);
        String tailer = "\r\n--" + boundary + "--\r\n";
        out.write(tailer.getBytes("UTF-8"));
        out.flush();
        out.close();
        StringBuffer buffer = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
        String temp;
        while ((temp = br.readLine()) != null) {
            buffer.append(temp);
            buffer.append("\n");
        }
        return buffer.toString();
    }
}
