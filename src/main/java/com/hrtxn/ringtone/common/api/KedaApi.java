package com.hrtxn.ringtone.common.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.utils.ConfigUtil;
import com.hrtxn.ringtone.common.utils.SpringUtils;
import com.hrtxn.ringtone.project.system.config.domain.SystemConfig;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaChildOrder;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.json.KedaBaseResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
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

    public AjaxResult add(KedaChildOrder kedaChildOrder) throws IOException {
        // businessid=21&businesstype=ring&empname=15050840350&empphone=15050840350&groupid=441104513194111
        String param = "businessId=21&empPhone="+kedaChildOrder.getLinkTel()+"&empName="+kedaChildOrder.getLinkMan()+"&groupId=441104513194111&businessType=ring";
        double rm = (new Random()).nextDouble();
        String res = sendPost(ADD_URL+"?r="+rm, param);
        System.out.println("添加结果："+res);
        KedaBaseResult kedaBaseResult = SpringUtils.getBean(ObjectMapper.class).readValue(res, KedaBaseResult.class);
        if ("000000".equals(kedaBaseResult.getRetCode())){
            return AjaxResult.success(true,"添加成功！");
        } else {
            return AjaxResult.error(kedaBaseResult.getRetMsg());
        }
    }

    public AjaxResult getMsg(String tel) throws IOException {
        String param = "groupId=441104513194111&businessId=&dueStartTime=&dueEndTime=&keywords="+tel+"&empState=&businessSettingState=&businessState=&pageIndex=1&pageSize=10&province=&city=&carriesCode=0&businessType=ring";//formatUrlParam(map);
        double rm = (new Random()).nextDouble();
        String res = sendPost(GETRINGEMPLOYEELIST+"?r="+rm, param);
        System.out.println("查询结果："+res);
        return null;

    }

    public static void main(String[] args) throws IOException {
        KedaApi kedaApi = new KedaApi();
        AjaxResult msg = kedaApi.getMsg("051687333228");

        KedaChildOrder kedaChildOrder = new KedaChildOrder();
    kedaChildOrder.setLinkTel("15050840350");
    kedaChildOrder.setLinkMan("15050840350");
        AjaxResult add = kedaApi.add(kedaChildOrder);

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
     * 参数格式化
     *
     * @param param 参数
     * @return
     */
    public static String formatUrlParam(Map<String, Object> param) {
        boolean isLower = true; // 是否小写
        String params = "";
        Map<String, Object> map = param;
        try {
            List<Map.Entry<String, Object>> itmes = new ArrayList<Map.Entry<String, Object>>(map.entrySet());
            // 对所有传入的参数按照字段名从小到大排序
            // Collections.sort(items); 默认正序
            // 可通过实现Comparator接口的compare方法来完成自定义排序
            Collections.sort(itmes, new Comparator<Map.Entry<String, Object>>() {
                @Override
                public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                    return (o1.getKey().toString().compareTo(o2.getKey()));
                }
            });
            // 构造URL 键值对的形式
            StringBuffer sb = new StringBuffer();
            for (Map.Entry<String, Object> item : itmes) {
                if (StringUtils.isNotBlank(item.getKey())) {
                    String key = item.getKey();
                    Object val = item.getValue();
                    if (isLower) {
                        sb.append(key.toLowerCase() + "=" + val);
                    } else {
                        sb.append(key + "=" + val);
                    }
                    sb.append("&");
                }
            }
            params = sb.toString();
            if (!params.isEmpty()) {
                params = params.substring(0, params.length() - 1);
            }
        } catch (Exception e) {
            return "";
        }
        return params;
    }


}
