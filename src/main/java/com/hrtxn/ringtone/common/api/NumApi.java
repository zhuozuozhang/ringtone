package com.hrtxn.ringtone.common.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.SpringUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.numcertification.domain.NumOrder;
import com.hrtxn.ringtone.project.numcertification.json.NumBaseDataResult;
import com.hrtxn.ringtone.project.numcertification.json.NumBaseResult;
import com.hrtxn.ringtone.project.numcertification.json.NumCgiTokenResult;
import com.hrtxn.ringtone.project.numcertification.json.NumDataResult;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import okhttp3.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 400API
 *
 * @author zcy
 * @date 2019-8-29 10:43
 */
@Slf4j
@Component
public class NumApi {

    public final static String LOGINNAME = "江苏中高俊聪";
    public final static String PASSWORD = "111111";
    public final static String URLPREFIX = "http://180.166.192.26:8910/";
    /**
     * 获取接口调用凭证
     */
    public final static String GETCGITOKEN = NumApi.URLPREFIX + "getCgiToken";
    public final static String NUMBERSELECT = NumApi.URLPREFIX + "cgi/numberSelect/list";

    public static String cgiToken = null;

    public AjaxResult getCgiToken() throws IOException {
        HashMap map = new HashMap();
        map.put("loginName", NumApi.LOGINNAME);
        map.put("userId", "boss");
        map.put("password", NumApi.PASSWORD);
        String s = sendPost(GETCGITOKEN, map);
        log.info("执行获取接口调用凭证结果{}", s);
        NumBaseResult<NumCgiTokenResult> numBaseResult = SpringUtils.getBean(ObjectMapper.class).readValue(s, NumBaseResult.class);
        if (numBaseResult.getCode() == 0) {
            Object obj = numBaseResult.getData();
            JSONObject jsonObject = JSONObject.fromObject(obj);
            NumCgiTokenResult numCgiTokenResult = (NumCgiTokenResult) JSONObject.toBean(jsonObject, NumCgiTokenResult.class);
            if (StringUtils.isNotNull(numCgiTokenResult)) {
                String cgiToken = numCgiTokenResult.getCgiToken();
                if (StringUtils.isNotEmpty(cgiToken)) {
                    return AjaxResult.success(cgiToken, "获取成功！");
                }
            }
        }
        return AjaxResult.error("执行错误！");
    }

    /**
     * 获取数据
     *
     * @author zcy
     * @date 2019-8-29 13:42
     */
    public AjaxResult getData(Page page, NumOrder numOrder) throws IOException {
//        if(StringUtils.isEmpty(cgiToken)){
//            AjaxResult ajaxResult = getCgiToken();
//            if ((Integer) ajaxResult.get("code") == 200) {
//                cgiToken = ajaxResult.get("data").toString();
//                log.info("执行刷新cgiToken结果{}", cgiToken);
//            }
//        }

        HashMap map = new HashMap();
        map.put("pageSize", page.getPagesize());
        map.put("pageNo", page.getPage());
        if (StringUtils.isNotNull(numOrder.getCategory())) {
            map.put("category", numOrder.getCategory());
        }
        if (StringUtils.isNotEmpty(numOrder.getNotInclude())) {
            map.put("notInclude", numOrder.getNotInclude());
        }
        if (StringUtils.isNotEmpty(numOrder.getPhoneNum())) {
            map.put("phoneNum", numOrder.getPhoneNum());
        }
        if (StringUtils.isNotNull(numOrder.getProvider())) {
            map.put("provider", numOrder.getProvider());
        }
        String s = sendPost(NUMBERSELECT + "?cgiToken=" + NumApi.cgiToken, map);
        log.info("获取数据结果{}", s);
        NumBaseResult<NumBaseDataResult> numBaseResult = SpringUtils.getBean(ObjectMapper.class).readValue(s, NumBaseResult.class);
        if (numBaseResult.getCode() == 0) {
            Object obj = numBaseResult.getData();
            JSONObject jsonObject = JSONObject.fromObject(obj);
            NumBaseDataResult numBaseDataResult = (NumBaseDataResult) JSONObject.toBean(jsonObject, NumBaseDataResult.class);
            String dataCount = numBaseDataResult.getDataCount();
            List<NumDataResult> dataList = numBaseDataResult.getDataList();
            List<NumDataResult> numDataResultList = new ArrayList<>();
            for (Object obje : dataList) {
                JSONObject jsonObj = JSONObject.fromObject(obje);
                NumDataResult numDataResult = (NumDataResult) JSONObject.toBean(jsonObj, NumDataResult.class);
                numDataResultList.add(numDataResult);
            }
            HashMap m = new HashMap();
            m.put("dataCount", dataCount);
            m.put("dataList", numDataResultList);
            return AjaxResult.success(m, "获取数据成功！");
        }
        return AjaxResult.error(numBaseResult.getMsg());
    }

    /**
     * 每隔50分钟获取cgiToken
     *
     * @author zcy
     * @date 2019-8-29 13:55
     */
    @Scheduled(fixedRate = 3000000)
    public void getCgiTokenTask() throws IOException {
        log.info("************************开始执行刷新cgiToken操作************************");
        NumApi numApi = new NumApi();
        AjaxResult ajaxResult = numApi.getCgiToken();
        if ((Integer) ajaxResult.get("code") == 200) {
            cgiToken = ajaxResult.get("data").toString();
            log.info("执行刷新cgiToken结果{}", cgiToken);
        }
    }

    /**
     * post封装
     *
     * @author zcy
     * @date 2019-8-29 15:28
     */
    public String sendPost(String url, HashMap map) throws IOException {
        JSONObject jsonObject = JSONObject.fromObject(map);
        String param = jsonObject.toString();
        log.info("输出的结果是：" + param);
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, param);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cache-Control", "no-cache")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
