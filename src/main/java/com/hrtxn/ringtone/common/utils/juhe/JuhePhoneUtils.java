package com.hrtxn.ringtone.common.utils.juhe;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrtxn.ringtone.project.system.json.JuhePhone;
import com.hrtxn.ringtone.project.system.json.JuhePhoneResult;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsOrder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Author:zcy
 * Date:2019-07-20 9:30
 * Description:聚合根据手机号获取该手机号归属地以及运营商
 */
public class JuhePhoneUtils {
    public static final String DEF_CHATSET = "UTF-8";
    public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;
    public static String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";

    //配置您申请的KEY
    private static final String APPKEY = "44ad27368b548d74c37963eae21745bd";
    private static final String URL = "http://apis.juhe.cn/mobile/get";

    /**
     * 聚合获取号码信息
     *
     * @param params 请求参数
     * @return 网络请求字符串
     * @throws Exception
     */
    public static String net(Map<String, String> params) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        String strUrl = URL;
        String method = "POST";
        try {
            params.put("key", APPKEY);
            StringBuffer sb = new StringBuffer();
            if (method == null || method.equals("GET")) {
                strUrl = strUrl + "?" + urlencode(params);
            }
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            if (method == null || method.equals("GET")) {
                conn.setRequestMethod("GET");
            } else {
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }
            conn.setRequestProperty("User-agent", userAgent);
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            if (params != null && method.equals("POST")) {
                try {
                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                    out.writeBytes(urlencode(params));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rs;
    }

    //将map型转为请求参数型
    public static String urlencode(Map<String, String> data) {
        StringBuilder sb = new StringBuilder();
        for (@SuppressWarnings("rawtypes") Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue() + "", "UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 工具分装方法，具体调用该方法执行
     *
     * @param phone
     * @return
     * @throws Exception
     */
    public static JuhePhone getPhone(String phone) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("phone", phone);//需要查询的手机号码或手机号码前7位
        params.put("key", APPKEY);//应用APPKEY(应用详细页查询)
        params.put("dtype", "");//返回数据的格式,xml或json，默认json
        String result = net(params);
        ObjectMapper mapper = new ObjectMapper();
        JuhePhone<JuhePhoneResult> resultJuhePhone = mapper.readValue(result, new TypeReference<JuhePhone<JuhePhoneResult>>() {
        });
        System.out.println(resultJuhePhone.toString());
        return resultJuhePhone;
    }

    /**
     * 获取号码归属地并转义
     *
     * @param result
     * @return
     */
    public static Integer getOperate(JuhePhoneResult result){
        if (result.getCompany().equals("移动")) {
            return 1;
        } else if (result.getCompany().equals("联通")) {
            return 3;
        } else {
            return 2;
        }
    }

    /**
     *
     * @param order
     * @return
     * @throws Exception
     */
    public static ThreenetsOrder getPhone(ThreenetsOrder order)throws Exception{
        JuhePhone phone = getPhone(order.getLinkmanTel());
        if (phone.getResult() == null){
            return order;
        }
        JuhePhoneResult result = (JuhePhoneResult) phone.getResult();
        order.setOperator(JuhePhoneUtils.getOperate(result));
        order.setProvince(result.getProvince());
        order.setCity(result.getCity());
        return order;
    }


    public static void main(String[] args) throws Exception {
        JuhePhone phone = getPhone("15050840350");
        System.out.println(phone);
    }
}
