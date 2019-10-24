package com.hrtxn.ringtone.common.utils.juhe;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrtxn.ringtone.common.utils.Const;
import com.hrtxn.ringtone.common.utils.SpringUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.system.json.JuhePhone;
import com.hrtxn.ringtone.project.system.json.JuhePhoneResult;
import com.hrtxn.ringtone.project.system.phonePlace.domain.PhonePlace;
import com.hrtxn.ringtone.project.system.phonePlace.mapper.PhonePlaceMapper;
import com.hrtxn.ringtone.project.system.telAscription.domain.TelAscription;
import com.hrtxn.ringtone.project.system.telAscription.mapper.TelAscriptionMapper;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsOrder;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsChildOrderMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Author:zcy
 * Date:2019-07-20 9:30
 * Description:聚合根据手机号获取该手机号归属地以及运营商
 */
@Slf4j
public class JuhePhoneUtils {
    public static final String DEF_CHATSET = "UTF-8";
    public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;
    public static String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";

    //配置您申请的KEY
    private static final String APPKEY = "44ad27368b548d74c37963eae21745bd";
    private static final String URL = "http://apis.juhe.cn/mobile/get";

    private static final String TELURL = "http://op.juhe.cn/onebox/phone/query";
    private static final String TELKEY = "7837c020cdc779d6a10bc35ecd4a889f";

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

    public static String netTel(Map<String, String> params) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        String strUrl = TELURL;
        String method = "POST";
        try {
            params.put("key", TELKEY);
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
    public static JuhePhone getPhone(String phone){
        return acquiringAttribution(phone);
    }

    public static JuhePhone getTel(String tel) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("tel", tel);//需要查询的手机号码或手机号码前7位
        params.put("key", TELKEY);//应用APPKEY(应用详细页查询)
        params.put("dtype", "");//返回数据的格式,xml或json，默认json
        String result = netTel(params);
        JSONObject jsonObject = JSONObject.fromObject(result);
        String reason = jsonObject.getString("reason");
        JuhePhone<JuhePhoneResult> resultJuhePhone = new JuhePhone<>();
        JuhePhoneResult juhePhoneResult = new JuhePhoneResult();
        if ("查询成功".equals(reason)) {
            resultJuhePhone.setResultcode("200");
            JSONObject jsonData = jsonObject.getJSONObject("result");
            juhePhoneResult.setProvince(jsonData.getString("province"));
            juhePhoneResult.setCity(jsonData.getString("city"));
            juhePhoneResult.setCompany("电信");
        } else {
            resultJuhePhone.setResultcode("203");
            resultJuhePhone.setError_code(201103);
        }
        resultJuhePhone.setResult(juhePhoneResult);
        return resultJuhePhone;
    }

    /**
     * 是否固定电话
     * @param fixedPhone
     * @return
     */
    private static boolean isFixedPhone(String fixedPhone) {
        String reg = "(?:(\\(\\+?86\\))(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)|" +
                "(?:(86-?)?(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)";
        return Pattern.matches(reg, fixedPhone);
    }


    /**
     * 获取归属地
     */
    private static JuhePhone acquiringAttribution(String tel){
        JuhePhone<JuhePhoneResult> juhePhone = new JuhePhone<>();
        JuhePhoneResult phoneResult = new JuhePhoneResult();
        juhePhone.setResultcode("200");
        try{
            if (isFixedPhone(tel)){
                String telephone = tel.substring(0, tel.length() - 8);
                if (tel.contains("-")){
                    telephone = tel.substring(0,tel.indexOf("-"));
                }
                TelAscription telAscriptionByTel = SpringUtils.getBean(TelAscriptionMapper.class).getTelAscriptionByTel(telephone);
                if (telAscriptionByTel == null){
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("tel", tel);//需要查询的手机号码或手机号码前7位
                    params.put("key", TELKEY);//应用APPKEY(应用详细页查询)
                    params.put("dtype", "");//返回数据的格式,xml或json，默认json
                    String result = netTel(params);
                    JSONObject jsonObject = JSONObject.fromObject(result);
                    String reason = jsonObject.getString("reason");
                    if ("查询成功".equals(reason)) {
                        JSONObject jsonData = jsonObject.getJSONObject("result");
                        phoneResult.setProvince(jsonData.getString("province"));
                        phoneResult.setCity(jsonData.getString("city"));
                        phoneResult.setCompany("电信");
                        telAscriptionByTel = new TelAscription();
                        telAscriptionByTel.setCity(phoneResult.getCity());
                        telAscriptionByTel.setAreaCode(telephone);
                        telAscriptionByTel.setProvince(phoneResult.getProvince());
                        SpringUtils.getBean(TelAscriptionMapper.class).insertTelAscription(telAscriptionByTel);
                    }
                }else {
                    phoneResult.setCompany("电信");
                    phoneResult.setProvince(telAscriptionByTel.getProvince());
                    phoneResult.setCity(telAscriptionByTel.getCity());
                }
            }else{
                //手机
                String phone = tel.substring(0,7);
                //从本地查询，获取归属地
                PhonePlace place = SpringUtils.getBean(PhonePlaceMapper.class).getPhonePlaceByPhone(phone);
                //本地不存在，从接口获取归属地
                if (place == null){
                    place = new PhonePlace();
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("phone", phone);//需要查询的手机号码或手机号码前7位
                    params.put("key", APPKEY);//应用APPKEY(应用详细页查询)
                    params.put("dtype", "");//返回数据的格式,xml或json，默认json
                    String result = net(params);
                    JSONObject jsonObject = JSONObject.fromObject(result);
                    String reason = jsonObject.getString("reason");
                    if ("Return Successd!".equals(reason)) {
                        juhePhone.setResultcode("200");
                        JSONObject jsonData = jsonObject.getJSONObject("result");
                        //本地储存
                        place.setPhone(phone);
                        place.setProvince(jsonData.getString("province"));
                        place.setCity(jsonData.getString("city"));
                        if (StringUtils.isEmpty(jsonData.getString("company"))){
                            place.setOperator(isChinaMobilePhoneNum(phone));
                        }else{
                            place.setOperator(jsonData.getString("company"));
                        }
                        SpringUtils.getBean(PhonePlaceMapper.class).insertPhonePlace(place);
                    }else{
                        place.setOperator(isChinaMobilePhoneNum(phone));
                    }
                }
                //返回
                phoneResult.setProvince(place.getProvince());
                phoneResult.setCity(place.getCity());
                phoneResult.setCompany(place.getOperator());
            }
        }catch(Exception e) {
            juhePhone.setResultcode("203");
            juhePhone.setError_code(201103);
            log.info("获取号码归属地失败----->",e);
        }finally {
            juhePhone.setResult(phoneResult);
            return juhePhone;
        }
    }

    /**
     * 查询电话属于哪个运营商
     *
     * @param tel 手机号码
     * @return 0：不属于任何一个运营商，1:移动，2：联通，3：电信
     */
    private static String isChinaMobilePhoneNum(String tel) {
        boolean b1 = tel == null || tel.trim().equals("") ? false : match(Const.CHINA_MOBILE_PATTERN, tel);
        if (b1) {
            return "移动";
        }
        b1 = tel == null || tel.trim().equals("") ? false : match(Const.CHINA_UNICOM_PATTERN, tel);
        if (b1) {
            return "联通";
        }
        b1 = tel == null || tel.trim().equals("") ? false : match(Const.CHINA_TELECOM_PATTERN, tel);
        if (b1) {
            return "电信";
        }
        return "其他";
    }

    /**
     * 匹配函数
     *
     * @param regex
     * @param tel
     * @return
     */
    private static boolean match(String regex, String tel) {
        return Pattern.matches(regex, tel);
    }

    /**
     * 获取号码归属地并转义
     *
     * @param result
     * @return
     */
    public static Integer getOperate(JuhePhoneResult result) {
        if (result.getCompany().equals("移动")) {
            return 1;
        } else if (result.getCompany().equals("联通")) {
            return 3;
        } else if (result.getCompany().equals("电信")) {
            return 2;
        } else {
            return 0;
        }
    }

    /**
     * @param order
     * @return
     * @throws Exception
     */
    public static ThreenetsOrder getPhone(ThreenetsOrder order) throws Exception {
        JuhePhone phone = getPhone(order.getLinkmanTel());
        if (phone.getResult() == null) {
            return order;
        }
        JuhePhoneResult result = (JuhePhoneResult) phone.getResult();
        order.setOperator(JuhePhoneUtils.getOperate(result));
        order.setProvince(result.getProvince());
        order.setCity(result.getCity());
        return order;
    }


    public static void main(String[] args) throws Exception {
        //JuhePhone phone = getTel("08328829669");
//        JuhePhone phone = acquiringAttribution("15150013617");
        String tel = "01010000000";
        String telephone = tel.substring(0, tel.length() - 8);
        System.out.println(telephone);
    }
}
