package com.hrtxn.ringtone.common.api;

import com.hrtxn.ringtone.common.exception.NoLoginException;
import com.hrtxn.ringtone.common.utils.*;
import com.hrtxn.ringtone.common.utils.json.JsonUtil;
import com.hrtxn.ringtone.project.system.config.domain.SystemConfig;
import com.hrtxn.ringtone.project.system.config.mapper.SystemConfigMapper;
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
import okhttp3.*;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
    private static String to_ring_list = "https://mcard.imusic.cn/ring/loadRingList";
    private static String to_ring_alert_list = "https://mcard.imusic.cn/ring/loadRingAlertList";
    private static String upload_file_url = "https://mcard.imusic.cn/file/uploadFile";//文件上传
    private static String saveRing_url = "http://mcard.imusic.cn/file/saveRing";//铃音上传
    private static String settingRing_url = "http://mcard.imusic.cn/ring/setUserRing";//设置铃音

    private static String load_ring_url = "https://mcard.imusic.cn/ring/loadRingList";//铃音列表
    private static String load_user_url = "https://mcard.imusic.cn/user/loadUserList";//成员列表

    private static String normal_list = "https://mcard.imusic.cn/user/loadNormalBusinessList";//获取客户列表
    private static String refresh_apersonnel = "http://mcard.imusic.cn/user/refreshApersonnel";//刷新用户信息

    private static String code_url = "https://mcard.imusic.cn/code/imageCode";

    /**
     * 电信GET封装
     *
     * @param url
     * @return
     */
    public static String sendGet(String url, String parent) {
        String type = parent.equals(Const.parent_Distributor_ID_188) ? "mcard_cookie_other" : "mcard_cookie_hn";
        ConfigUtil util = new ConfigUtil();
        SystemConfig config = util.getConfigByType(type);
        OkHttpClient client = new OkHttpClient();
        String result = null;
        Request request = new Request.Builder().url(url).addHeader("Cookie", config.getInfo()).build();
        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            log.info("GET跳转失败：" + e);
        }
        return result;
    }

    /**
     * 电信POST封装
     *
     * @param map 参数
     * @param url 接口
     * @return
     */
    public static String sendPost(Map<String, String> map, String url, String parent) {
        String type = parent.equals(Const.parent_Distributor_ID_188) ? "mcard_cookie_other" : "mcard_cookie_hn";
        ConfigUtil util = new ConfigUtil();
        SystemConfig config = util.getConfigByType(type);
        OkHttpClient client = new OkHttpClient();
        String result = null;
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : map.keySet()) {
            if (map.get(key) == null) {
                builder.add(key, "");
            } else {
                builder.add(key, map.get(key));
            }
        }
        FormBody formBody = builder.build();
        Request request = new Request.Builder().url(url).post(formBody).addHeader("Cookie", config.getInfo()).build();
        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            log.info("POST请求失败：" + e);
        }
        return result;
    }

    /**
     * 跳转到对应经销商
     *
     * @param subuserid
     * @return
     */
    public String toNormalUser(String subuserid, String parent) {
        String url = to_normal_user + "?subuserid=" + subuserid + "&parent=" + parent;
        return sendGet(url, parent);
    }

    /**
     * 跳转到对应商户
     *
     * @param userId
     * @return
     */
    public String toUserList(String userId, String parent) {
        String url = to_user_list + "?userId=" + userId;
        return sendGet(url, parent);
    }

    /**
     * 跳转到铃音页面
     *
     * @param parent
     * @return
     */
    public String toRingList(String parent) {
        Map<String, String> map = new HashMap<>();
        map.put("pageSize", "100");
        map.put("pageNo", "1");
        return sendPost(map, to_ring_list, parent);
    }

    /**
     * 铃音弹出框列表
     *
     * @param apersonnelId
     * @param parent
     * @return
     */
    public String toRingAlertList(String apersonnelId, String parent) {
        Map<String, String> map = new HashMap<>();
        map.put("pageSize", "100");
        map.put("pageNo", "1");
        map.put("apersonnelId", apersonnelId);
        return sendPost(map, to_ring_alert_list, parent);
    }

    /**
     * 获取信息
     *
     * @param order
     * @return
     */
    public String refreshBusinessInfo(ThreenetsOrder order, String distributorId) {
        Map<String, String> map = new HashMap<>();
        map.put("pageSize", "100");
        map.put("pageNo", "1");
        map.put("auserParent", "61203");
        map.put("startTime", DateUtils.getPastDate(7));
        map.put("endTime", DateUtils.getFetureDate(1));
        if (order.getCompanyName() != null) {
            map.put("auserName", order.getCompanyName());
        }
        return sendPost(map, normal_list, distributorId);
    }

    /**
     * 加载铃音列表
     *
     * @return
     */
    public String getRingInfo(String distributorId) {
        Map<String, String> map = new HashMap<>();
        map.put("pageSize", "100");
        map.put("pageNo", "1");
        return sendPost(map, load_ring_url, distributorId);
    }

    /**
     * 加载成员列表
     *
     * @return
     */
    public String getUserInfo(String distributorId) {
        Map<String, String> map = new HashMap<>();
        map.put("pageSize", "100");
        map.put("pageNo", "1");
        return sendPost(map, load_user_url, distributorId);
    }

    /**
     * 调用远程验证码接口，取得验证码
     *
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public String getCodeString(String parent) {
        ConfigUtil util = new ConfigUtil();
        String type = parent.equals(Const.parent_Distributor_ID_188) ? "mcard_cookie_other" : "mcard_cookie_hn";
        SystemConfig config = util.getConfigByType(type);
        String code = null;
        DefaultHttpClient httpclientCode = new DefaultHttpClient();
        long time = new Date().getTime();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(code_url + "?d=" + time).addHeader("Cookie", config.getInfo()).build();

        try {
            // 获取验证码
            Response response = client.newCall(request).execute();
            byte[] Picture_bt = response.body().bytes();
            String codestr = ChaoJiYing.PostPic("wwewwe", "wyc19931224", "899622", "4004", "4", Picture_bt);
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
                        getCodeString(parent);
                    }
                } else {
                    getCodeString(parent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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
    public McardPhoneAddressRespone phoneAdd(String phone, String distributorId) {
        McardPhoneAddressRespone respone = new McardPhoneAddressRespone();
        Map<String, String> map = new HashMap<>();
        map.put("loginPhone", phone);
        map.put("fee", "1");
        String result = sendPost(map, phone_address_url, distributorId);
        JSONObject jsonObject = JSONObject.fromObject(result);
        JSONObject jsonData = jsonObject.getJSONObject("data");
        respone.setMNOs(jsonData.getInt("MNOs"));
        respone.setCode(jsonData.getString("code"));
        respone.setProvince(jsonData.getString("province"));
        respone.setCity(jsonData.getString("city"));
        if (jsonData.containsKey("cityCode")) {
            respone.setCityCode(jsonData.getString("cityCode"));
        }else {
            respone.setCityCode("");
        }
        respone.setProvinceCode(jsonData.getString("provinceCode"));
        respone.setFeeValue(jsonData.getString("feeValue"));
        respone.setProvince_code(jsonData.getString("province_code"));
        respone.setFeeType(jsonData.getInt("feeType"));
        respone.setAreacode(jsonData.getString("areacode"));
        respone.setSpid(jsonData.getString("spid"));
        respone.setIntelligence(jsonData.getInt("intelligence"));
        return respone;
    }


    /**
     * 电信添加商户
     *
     * @param order
     * @param attached
     * @return
     */
    public McardAddGroupRespone addGroup(ThreenetsOrder order, ThreeNetsOrderAttached attached) {
        McardAddGroupRespone groupRespone = new McardAddGroupRespone();
        try {
            McardPhoneAddressRespone respone = phoneAdd(order.getLinkmanTel(), attached.getMcardDistributorId());
            Map<String, String> map = new HashMap<>();
            map.put("ausertype", "");
            map.put("codeId", "");
            map.put("provinceChannel", "");
            map.put("isFeeType", "0");
            map.put("makeFee", "");
            map.put("feeType", attached.getMcardPrice().toString());
            map.put("aUserProvince", respone.getProvince());
            map.put("aUserCity", respone.getCity());
            map.put("phoneProvinceCode", respone.getProvinceCode());
            map.put("checkUnipayphone", "");
            map.put("makeFeeType", "");
            map.put("phoneCityCode", respone.getCityCode());
            map.put("auserAccount", order.getLinkmanTel());
            map.put("auserName", order.getCompanyName());
            map.put("auserLinkName", order.getCompanyLinkman());
            map.put("auserMoney", attached.getMcardPrice().toString());
            map.put("auserPhone", order.getLinkmanTel());
            map.put("auserEmail", "");
            map.put("chargingPhone", order.getLinkmanTel());
            map.put("auserWeixin", "");
            map.put("auserYi", "");
            map.put("auserFengChao", "");
            map.put("busiSeizedName", "");
            map.put("busiSeizedPhone", "");
            map.put("industry", "1");
            map.put("isUnifyPay", "2");
            map.put("unifyPayPhone", "");
            map.put("imageCode", getCodeString(attached.getMcardDistributorId()));
            map.put("auserBlicencePath", attached.getBusinessLicense());
            map.put("auserFilePath", attached.getConfirmLetter());
            map.put("auserCardidPath", attached.getSubjectProve() == null ? "" : attached.getSubjectProve());
            String result = sendPost(map, add_user_url, attached.getMcardDistributorId());
            log.info("电信创建集团结果--->" + result);
            JSONObject jsonObject = JSONObject.fromObject(result);
            String code = jsonObject.getString("code");
            if (code.equals("0000")) {
                JSONObject data = jsonObject.getJSONObject("data");
                groupRespone = (McardAddGroupRespone) JSONObject.toBean(data, McardAddGroupRespone.class);
                groupRespone.setCode(jsonObject.getString("code"));
                groupRespone.setMessage(jsonObject.getString("message"));
            }else{
                String message = jsonObject.getString("message");
                if (message.equals("图像验证码错误,请刷新后输入")){
                    groupRespone.setCode("");
                }else{
                    groupRespone.setCode(Const.ILLEFAL_AREA);
                    groupRespone.setMessage(jsonObject.getString("message"));
                }
            }
        } catch (Exception e) {
            log.info("电信添加商户失败" + e);
            groupRespone.setCode(Const.ILLEFAL_AREA);
            groupRespone.setMessage("添加失败");
            return groupRespone;
        } finally {
            return groupRespone;
        }
    }


    /**
     * 添加集团成员
     * 添加成员之前需要进行跳转到对应商户
     *
     * @param childOrder
     * @return
     */
    public McardAddPhoneRespone addApersonnel(ThreenetsChildOrder childOrder, String distributorId) {
        McardAddPhoneRespone addPhoneRespone = new McardAddPhoneRespone();
        Map<String, String> map = new HashMap<>();
        map.put("personnelName", childOrder.getLinkman());
        map.put("personnelPhone", childOrder.getLinkmanTel());
        String result = sendPost(map, add_apersonnel_url, distributorId);
        log.info("电信添加成员结果--->" + result);
        try {
            addPhoneRespone = (McardAddPhoneRespone) JsonUtil.getObject4JsonString(result, McardAddPhoneRespone.class);
        } catch (Exception e) {
            log.info("电信添加成员失败" + e);
            addPhoneRespone.setCode("error");
        }
        return addPhoneRespone;
    }

    /**
     * 设置铃音
     *
     * @param ringId
     * @param apersonnelId
     * @return
     */
    public String settingRing(String ringId, String apersonnelId, String distributorId) {
        Map<String, String> map = new HashMap<>();
        map.put("ringId", ringId);
        map.put("apersonnelId", apersonnelId);
        return sendPost(map, settingRing_url, distributorId);
    }

    /**
     * 铃音上传
     *
     * @param ring
     * @return
     */
    public boolean uploadRing(ThreenetsRing ring) {
        ConfigUtil util = new ConfigUtil();
        SystemConfig config = util.getConfigByType("mcard_cookie_other");
        boolean flag = false;
        String result = null;
        String ringName = ring.getRingName().substring(0, ring.getRingName().indexOf("."));
        OkHttpClient client = new OkHttpClient();
        RequestBody fileBody = RequestBody.create(MediaType.parse("audio/mp3"), ring.getFile());//将file转换成RequestBody文件
        RequestBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("song", ringName)
                .addFormDataPart("ringName", ringName)
                .addFormDataPart("ringText", ring.getRingContent())
                .addFormDataPart("singer", "无")
                .addFormDataPart("ext", "mp3")
                .addFormDataPart("fileName", ring.getRingName())
                .addFormDataPart("ringFile", ringName, fileBody)
                .build();
        Request request = new Request.Builder().url(saveRing_url).post(requestBody).addHeader("Cookie", config.getInfo()).build();
        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
            log.info("电信上传铃音结果--->" + result);
            flag = true;
        } catch (IOException e) {
            log.info("电信铃音上传失败" + e);
        }
        return flag;
    }


    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    public String uploadFile(File file) {
        ConfigUtil util = new ConfigUtil();
        SystemConfig config = util.getConfigByType("mcard_cookie_other");
        OkHttpClient client = new OkHttpClient();
        String result = null;
        RequestBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .build();
        Request request = new Request.Builder().url(upload_file_url).post(requestBody).addHeader("Cookie", config.getInfo()).build();
        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
            //解析数据
            JSONObject jsonObject = JSONObject.fromObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject partDaily = jsonArray.getJSONObject(i);
                String path = partDaily.getString("path");
                result = path;
            }
        } catch (IOException e) {
            log.info("电信文件上传失败" + e);
        }
        return result;
    }

}
