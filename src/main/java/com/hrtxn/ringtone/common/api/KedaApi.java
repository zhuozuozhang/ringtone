package com.hrtxn.ringtone.common.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.constant.Constant;
import com.hrtxn.ringtone.common.utils.ConfigUtil;
import com.hrtxn.ringtone.common.utils.SpringUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.system.config.domain.SystemConfig;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaChildOrder;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaRing;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.json.*;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.mapper.KedaChildOrderMapper;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.mapper.KedaRingMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Author:zcy
 * Date:2019-08-17 12:03
 * Description:疑难杂单接口
 */
@Slf4j
public class KedaApi {

    // 添加员工
    private final static String ADD_URL = "http://clcy.adsring.cn/meap-web/ring/emp/addRingEmp";
    private final static String GETRINGEMPLOYEELIST = "http://clcy.adsring.cn/meap-web/ring/emp/getRingEmployeeList";
    private final static String QUERYRINGSTATUS = "http://clcy.adsring.cn/meap-web/ring/emp/queryRingStatus";
    private final static String UPLOAD = "http://clcy.adsring.cn/meap-web/group/upload";
    private final static String SAVEORSETRINGBYUPLOAD = "http://clcy.adsring.cn/meap-web/ring/manage/saveOrSetRingByUpload";
    private final static String QUERYRINGLIST = "http://clcy.adsring.cn/meap-web/ring/manage/queryRingList";
    private final static String SETRING = "http://clcy.adsring.cn/meap-web/ring/manage/setRing";


    /**
     * 创建疑难杂单订单
     *
     * @param kedaChildOrder
     * @return
     * @throws IOException
     */
    public AjaxResult add(KedaChildOrder kedaChildOrder) throws IOException {
        String param = "businessId=21&empPhone=" + kedaChildOrder.getLinkTel() + "&empName=" + kedaChildOrder.getLinkMan() + "&groupId=" + Constant.OPERATEID + "&businessType=ring";
        double rm = (new Random()).nextDouble();
        String res = sendPost(ADD_URL + "?r=" + rm, param);
        log.info("疑难杂单创建订单结果：" + res);
        KedaBaseResult kedaBaseResult = SpringUtils.getBean(ObjectMapper.class).readValue(res, KedaBaseResult.class);
        if ("000000".equals(kedaBaseResult.getRetCode())) {
            return AjaxResult.success(true, "添加成功！");
        } else {
            return AjaxResult.error(kedaBaseResult.getRetMsg());
        }
    }

    /**
     * 疑难杂单刷新子级订单
     *
     * @param tel
     * @param id
     * @return
     * @throws IOException
     */
    public AjaxResult getMsg(String tel, Integer id) throws IOException {
        // 执行刷新是否包月以及设置状态
        String param = "groupId=" + Constant.OPERATEID + "&businessId=&dueStartTime=&dueEndTime=&keywords=" + tel + "&empState=&businessSettingState=&businessState=&pageIndex=1&pageSize=10&province=&city=&carriesCode=0&businessType=ring";
        double rm = (new Random()).nextDouble();
        String res = sendPost(GETRINGEMPLOYEELIST + "?r=" + rm, param);
        log.info("疑难杂单获取自己订单信息 参数：{},{} 结果：{}", tel, id, res);
        KedaPhoneBaseResult<KedaPhoneResult> kedaPhoneBaseResult = SpringUtils.getBean(ObjectMapper.class).readValue(res, KedaPhoneBaseResult.class);
        KedaChildOrder kedaChildOrder = new KedaChildOrder();
        kedaChildOrder.setId(id);
        if ("000000".equals(kedaPhoneBaseResult.getRetCode())) {
            List<KedaPhoneResult> data = kedaPhoneBaseResult.getData();
            List<KedaPhoneResult> kedaPhoneResults = new ArrayList<>();
            for (Object obj : data) {
                JSONObject jsonObject = JSONObject.fromObject(obj); // 将数据转成json字符串
                KedaPhoneResult kedaPhoneResult = (KedaPhoneResult) JSONObject.toBean(jsonObject, KedaPhoneResult.class);
                kedaPhoneResults.add(kedaPhoneResult);
            }
            int l = kedaPhoneResults.size();
            for (int i = 0; i < l; i++) {
                String businessEmpPhone = kedaPhoneResults.get(i).getBusinessEmpPhone();
                if (businessEmpPhone.equals(tel)) {
                    // 判断是否是未开通
                    if (kedaPhoneResults.get(i).getBusinessState() == 0){
                        // 判断是否恢复短信（是）
                        if (kedaPhoneResults.get(i).getBusinessEmpState() == 2){
                            kedaChildOrder.setIsMonthly(7);
                        } else {
                            kedaChildOrder.setIsMonthly(kedaPhoneResults.get(i).getBusinessState());
                        }
                    } else {
                        kedaChildOrder.setIsMonthly(kedaPhoneResults.get(i).getBusinessState());
                    }
                    // 0.未设置/1.设置成功/2.设置失败/3.设置中/4.审核驳回
                    if ("0".equals(kedaPhoneResults.get(i).getSetStatus())) {
                        kedaChildOrder.setRemark("未设置");
                    } else if ("1".equals(kedaPhoneResults.get(i).getSetStatus())) {
                        kedaChildOrder.setRemark("设置成功");
                    } else if ("2".equals(kedaPhoneResults.get(i).getSetStatus())) {
                        kedaChildOrder.setRemark("设置失败[" + kedaPhoneResults.get(i).getSetRetDesc() + "]");
                    } else if ("3".equals(kedaPhoneResults.get(i).getSetStatus())) {
                        kedaChildOrder.setRemark("设置中");
                    } else {
                        kedaChildOrder.setRemark("审核驳回[" + kedaPhoneResults.get(i).getSetRetDesc() + "]");
                    }
                    kedaChildOrder.setEmployeeId(kedaPhoneResults.get(i).getBusinessEmpId());
                    break;
                }
            }
        } else {
            kedaChildOrder.setRemark(kedaPhoneBaseResult.getRetMsg());
        }
        // 执行刷新是否是彩铃用户
        String param2 = "r=" + rm + "&phone=" + tel;
        String result = sendGet(QUERYRINGSTATUS, param2);
        KedaIsRingUser kedaIsRingUser = SpringUtils.getBean(ObjectMapper.class).readValue(result, KedaIsRingUser.class);
        if ("000000".equals(kedaIsRingUser.getRetCode())) {
            if ("0".equals(kedaIsRingUser.getData())) {
                kedaChildOrder.setIsRingtoneUser(1);
            } else if ("1".equals(kedaIsRingUser.getData())) {
                kedaChildOrder.setIsRingtoneUser(2);
            } else {
                kedaChildOrder.setIsRingtoneUser(3);
            }
        }
        // 执行修改自己订单操作
        int i = SpringUtils.getBean(KedaChildOrderMapper.class).updatKedaChildOrder(kedaChildOrder);
        if (i > 0) {
            return AjaxResult.success(true, "修改成功！");
        }
        return AjaxResult.error("刷新失败！");
    }

    /**
     * 文件上传
     *
     * @param source
     * @return
     * @throws IOException
     */
    public AjaxResult uploadRing(File source) throws IOException {
        String s = sendFile(UPLOAD, source);
        log.info("疑难杂单铃音文件上传----->" + s);
        if (StringUtils.isNotEmpty(s)) {
            KedaBaseResult<KedaUploadRing> kedaBaseResult = SpringUtils.getBean(ObjectMapper.class).readValue(s, KedaBaseResult.class);
            if ("000000".equals(kedaBaseResult.getRetCode())) {
                List<KedaUploadRing> data = kedaBaseResult.getData();
                List<KedaUploadRing> kedaUploadRingList = new ArrayList<>();
                for (Object obj : data) {
                    JSONObject jsonObject = JSONObject.fromObject(obj); // 将数据转成json字符串
                    KedaUploadRing kedaUploadRing = (KedaUploadRing) JSONObject.toBean(jsonObject, KedaUploadRing.class); //将json转成需要的对象
                    kedaUploadRingList.add(kedaUploadRing);
                }
                if (kedaUploadRingList.size() > 0) {
                    return AjaxResult.success(kedaUploadRingList.get(0).getFileUrl());
                }
            }
            return AjaxResult.error(kedaBaseResult.getRetMsg());
        }
        return AjaxResult.error("上传失败！");
    }

    /**
     * 添加铃音
     *
     * @param fileUrl
     * @param ringName
     * @return
     * @throws IOException
     */
    public AjaxResult addRing(String fileUrl, String ringName) throws IOException {
        ringName = URLEncoder.encode(URLEncoder.encode(URLEncoder.encode(URLEncoder.encode(ringName, "utf-8"), "utf-8"), "utf-8"),"utf-8");
        String param = "groupId=" + Constant.OPERATEID + "&name=" + ringName + "&ringPrice=0&ringActivePrice=0&addSetting=0&uploadMusicName=" + URLEncoder.encode(fileUrl, "UTF-8") + "&businessType=ring";
        double rm = (new Random()).nextDouble();
        String s = sendPost(SAVEORSETRINGBYUPLOAD + "?r=" + rm, param);
        log.info("铃音文件暂存------>" + s);
        KedaRingBaseResult<KedaSaveRingBase> kedaRingBaseResult = SpringUtils.getBean(ObjectMapper.class).readValue(s, KedaRingBaseResult.class);
        if ("000000".equals(kedaRingBaseResult.getRetCode())) {
            Map<String, KedaSaveRingBase> data = kedaRingBaseResult.getData();
            Object obj = data.get("ringContent");
            JSONObject jsonObject = JSONObject.fromObject(obj); // 将数据转成json字符串
            KedaSaveRing kedaSaveRing = (KedaSaveRing) JSONObject.toBean(jsonObject, KedaSaveRing.class);
            if (StringUtils.isNotNull(kedaSaveRing)) {
                return AjaxResult.success(kedaSaveRing.getId().toString());
            }
        }
        return AjaxResult.error(kedaRingBaseResult.getRetMsg());
    }

    /**
     * 刷新铃音
     *
     * @param kedaRingList
     * @return
     * @throws IOException
     */
    public List<KedaRing> refreshRingInfo(List<KedaRing> kedaRingList) throws IOException {
        if (kedaRingList.size() > 0) {
            for (int i = 0; i < kedaRingList.size(); i++) {
                String param = "groupId=" + Constant.OPERATEID + "&pageSize=10000&pageIndex=1&name=" + kedaRingList.get(i).getRingName();
                double rm = (new Random()).nextDouble();
                String s = sendPost(QUERYRINGLIST + "?r=" + rm, param);
                log.info("疑难杂单刷新铃音信息:{}", s);
                KedaPhoneBaseResult<KedaRefresRingInfo> kedaRingBaseResult = SpringUtils.getBean(ObjectMapper.class).readValue(s, KedaPhoneBaseResult.class);
                if ("000000".equals(kedaRingBaseResult.getRetCode())) {
                    List<KedaRefresRingInfo> data = kedaRingBaseResult.getData();
                    List<KedaRefresRingInfo> kedaRefresRingInfos = new ArrayList<>();
                    if (StringUtils.isNotNull(data)) {
                        for (Object obj : data) {
                            JSONObject jsonObject = JSONObject.fromObject(obj); // 将数据转成json字符串
                            KedaRefresRingInfo kedaRefresRingInfo = (KedaRefresRingInfo) JSONObject.toBean(jsonObject, KedaRefresRingInfo.class); //将json转成需要的对象
                            kedaRefresRingInfos.add(kedaRefresRingInfo);
                        }
                        if (kedaRefresRingInfos.size() > 0) {
                            for (int j = 0; j < kedaRefresRingInfos.size(); j++) {
                                if (kedaRingList.get(i).getRingNum().equals(kedaRefresRingInfos.get(j).getId().toString())) {
                                    if (kedaRefresRingInfos.get(j).getStatus() == 0) { // 待审核
                                        kedaRingList.get(i).setRingStatus(2);
                                    } else if (kedaRefresRingInfos.get(j).getStatus() == 1) { // 审核中
                                        kedaRingList.get(i).setRingStatus(2);
                                    } else if (kedaRefresRingInfos.get(j).getStatus() == 2) { // 审核通过
                                        kedaRingList.get(i).setRingStatus(3);
                                    } else { // 审核失败
                                        kedaRingList.get(i).setRingStatus(4);
                                    }
                                    kedaRingList.get(i).setRemark(kedaRefresRingInfos.get(j).getAuditDesc());
                                    // 执行修改铃音操作
                                    int count = SpringUtils.getBean(KedaRingMapper.class).updateKedaRing(kedaRingList.get(i));
                                    log.info("疑难杂单刷新铃音修改结果:{}", count);
                                }
                            }
                        }
                    }
                }
            }
        }
        return kedaRingList;
    }

    /**
     * 設置鈴音
     *
     * @param ringNum
     * @param businessEmpIdList
     * @param phones
     * @return
     * @throws IOException
     */
    public AjaxResult setRing(String ringNum, String businessEmpIdList, String phones) throws IOException {
        String param = "groupId=" + Constant.OPERATEID + "&id=" + ringNum + "&businessEmpIdList=" + businessEmpIdList + "&businessEmpPhoneList=" + phones + "&businessCircleEmpPhoneList=&businessType=ring";
        double rm = (new Random()).nextDouble();
        String s = sendPost(SETRING + "?r=" + rm, param);
        log.info("疑难杂单设置铃音：{}", s);
        KedaPhoneBaseResult<Object> kedaRingBaseResult = SpringUtils.getBean(ObjectMapper.class).readValue(s, KedaPhoneBaseResult.class);
        if ("000000".equals(kedaRingBaseResult.getRetCode())) {
            return AjaxResult.success(true, "设置成功！");
        }
        return AjaxResult.error(kedaRingBaseResult.getRetMsg());
    }


    /**
     * 铃音文件上传
     *
     * @param url
     * @param file
     * @return
     * @throws IOException
     */
    public String sendFile(String url, File file) throws IOException {
        SystemConfig kedaCookie = ConfigUtil.getConfigByType("kedaCookie");
        String info = kedaCookie.getInfo();
        OkHttpClient client = new OkHttpClient();
        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        RequestBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("groupId", Constant.OPERATEID)
                .addFormDataPart("fileType", "music")
                .addFormDataPart("files", file.getName(), fileBody)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Cookie", info)
                .addHeader("Cache-Control", "no-cache")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * 封装工具类
     * 发送post方式
     *
     * @param url   发送链接
     * @param param 发送参shu 以&符号拼接
     * @return
     * @throws IOException
     */
    public String sendPost(String url, String param) throws IOException {
        SystemConfig kedaCookie = ConfigUtil.getConfigByType("kedaCookie");
        String info = kedaCookie.getInfo();

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, param);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Cookie", info)
                .addHeader("Cache-Control", "no-cache")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * 封装工具类
     * 发送GET方式
     *
     * @param url   发送链接
     * @param param 发送参shu 以&符号拼接
     * @return
     * @throws IOException
     */
    public String sendGet(String url, String param) throws IOException {
        SystemConfig kedaCookie = ConfigUtil.getConfigByType("kedaCookie");
        String info = kedaCookie.getInfo();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url + "?" + param)
                .get()
                .addHeader("Cookie", info)
                .addHeader("Cache-Control", "no-cache")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * 参数格式化
     *
     * @param param 参数
     * @return
     */
//    public static String formatUrlParam(Map<String, Object> param) {
//        boolean isLower = true; // 是否小写
//        String params = "";
//        Map<String, Object> map = param;
//        try {
//            List<Map.Entry<String, Object>> itmes = new ArrayList<Map.Entry<String, Object>>(map.entrySet());
//            // 对所有传入的参数按照字段名从小到大排序
//            // Collections.sort(items); 默认正序
//            // 可通过实现Comparator接口的compare方法来完成自定义排序
//            Collections.sort(itmes, new Comparator<Map.Entry<String, Object>>() {
//                @Override
//                public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
//                    return (o1.getKey().toString().compareTo(o2.getKey()));
//                }
//            });
//            // 构造URL 键值对的形式
//            StringBuffer sb = new StringBuffer();
//            for (Map.Entry<String, Object> item : itmes) {
//                if (StringUtils.isNotBlank(item.getKey())) {
//                    String key = item.getKey();
//                    Object val = item.getValue();
//                    if (isLower) {
//                        sb.append(key.toLowerCase() + "=" + val);
//                    } else {
//                        sb.append(key + "=" + val);
//                    }
//                    sb.append("&");
//                }
//            }
//            params = sb.toString();
//            if (!params.isEmpty()) {
//                params = params.substring(0, params.length() - 1);
//            }
//        } catch (Exception e) {
//            return "";
//        }
//        return params;
//    }

}
