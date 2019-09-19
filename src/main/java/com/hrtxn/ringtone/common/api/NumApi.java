package com.hrtxn.ringtone.common.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.*;
import com.hrtxn.ringtone.project.numcertification.domain.NumOrder;
import com.hrtxn.ringtone.project.numcertification.domain.NumcertificationOrder;
import com.hrtxn.ringtone.project.numcertification.json.NumBaseDataResult;
import com.hrtxn.ringtone.project.numcertification.json.NumBaseResult;
import com.hrtxn.ringtone.project.numcertification.json.NumCgiTokenResult;
import com.hrtxn.ringtone.project.numcertification.json.NumDataResult;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //预占接口
    public final static String CGIOCCUPYADD = NumApi.URLPREFIX + "/cgi/occupy/add";
    public final static String DOWNLOADTEMPLATE = NumApi.URLPREFIX +  "cgi/material/downloadTemplate";
    public final static String CGIMATERIALSUBMIT = NumApi.URLPREFIX + "/cgi/material/submit";

    public static String cgiToken = null;

    @Value("${ringtone.profile-url}")
    private String profileUrl;

    public AjaxResult getCgiToken() throws IOException {
        HashMap map = new HashMap();
        map.put("loginName", NumApi.LOGINNAME);
        map.put("userId", "boss");
        map.put("password", NumApi.PASSWORD);
        String s = sendPost(GETCGITOKEN, map, Const.CONTENT_TYPE_JSON);
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
        if(StringUtils.isEmpty(cgiToken)){
            AjaxResult ajaxResult = getCgiToken();
            if ((Integer) ajaxResult.get("code") == 200) {
                cgiToken = ajaxResult.get("data").toString();
                log.info("执行刷新cgiToken结果{}", cgiToken);
            }
        }

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
        String s = sendPost(NUMBERSELECT + "?cgiToken=" + NumApi.cgiToken, map, Const.CONTENT_TYPE_JSON);
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
     * 预占
     */
    public String cgiOccupyAdd(NumcertificationOrder numcertificationOrder){

        String result = "";
        try {
            HashMap map = new HashMap();
            if(StringUtils.isNotEmpty(numcertificationOrder.getPhoneNum())){
                map.put("applyNumber",numcertificationOrder.getPhoneNum());
            }
            if(StringUtils.isNotEmpty(numcertificationOrder.getCompanyName())){
                map.put("occupyCompany",numcertificationOrder.getCompanyName());
            }
            if(StringUtils.isNotEmpty(numcertificationOrder.getProvince())){
                map.put("useProvince",numcertificationOrder.getProvince());
            }
            if(StringUtils.isNotEmpty(numcertificationOrder.getCity())){
                map.put("useCity",numcertificationOrder.getCity());
            }
            result = sendPost(CGIOCCUPYADD + "?cgiToken=" + NumApi.cgiToken, map, Const.CONTENT_TYPE_JSON);
            log.info("获取数据结果{}", result);
            JSONObject jsonObject = JSONObject.fromObject(result);
            return jsonObject.get("code").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 下载模板
     * @param numcertificationOrder
     * @return
     * @throws IOException
     */
     public String downloadTemplate(NumcertificationOrder numcertificationOrder) throws IOException {
         HashMap map = buildMap(numcertificationOrder);
         String result = sendPost(DOWNLOADTEMPLATE + "?cgiToken=" + NumApi.cgiToken, map,Const.CONTENT_TYPE_FORM_DATA);
         log.info("获取数据结果{}", result);
         JSONObject jsonObject = JSONObject.fromObject(result);
         String code = jsonObject.get("code").toString();
         if("0".equals(jsonObject.get("code").toString())){
            String data = jsonObject.get("data").toString();
            if(StringUtils.isNotEmpty(data)){
                JSONObject dataObject = JSONObject.fromObject(data);
                String taskId = dataObject.get("taskId").toString();
                return taskId;
            }
         }
         return jsonObject.get("code").toString();
     }

    public HashMap buildMap(NumcertificationOrder numcertificationOrder){
        HashMap map = new HashMap();
        map.put("cgiToken",cgiToken);
        map.put("numberCode",numcertificationOrder.getPhoneNum());
        map.put("companyName",numcertificationOrder.getCompanyName());
        map.put("otherLinkMobile",numcertificationOrder.getFixPhone());
        map.put("otherLinkPhone",numcertificationOrder.getLegalPersionPhone());
        map.put("otherLinkEmail",numcertificationOrder.getEmail());
        map.put("otherBindPhone",numcertificationOrder.getBindPhone());
        map.put("otherValidPhone",numcertificationOrder.getLegalPersionPhone());
        map.put("legalCardFileClientUrl",profileUrl+numcertificationOrder.getLegalPersionCardFUrl());
        map.put("operatorCardFileClientUrl",profileUrl+numcertificationOrder.getHandlerPersionCardZUrl());
        return map;
    }


    public String submit(NumcertificationOrder numcertificationOrder){
        String result = "";

        //准备数据
        HashMap<String,String> map = buildSubmitMap(numcertificationOrder);

        return result;
    }

    public HashMap buildSubmitMap(NumcertificationOrder num){
        HashMap<String,String> map = new HashMap();
        map.put("corpSocietyNo",num.getCorpSocietyNo());
        map.put("corpBusinessScope",num.getBusinessScope());
        //企业注册地省份主键
        map.put("corpCompanyProvince","");
        //企业注册地城市代码
        map.put("corpCompanyCity","");
        map.put("corpCompanyDetail",num.getCompanyAddress());
        map.put("corpOfficeProvince","");
        map.put("corpOfficeCity","");
        map.put("corpOfficeDetail","");
        map.put("corpNunmberUsage",num.getCorpNunmberUsage());
        map.put("corpAccountType",num.getCorpAccountType());
        map.put("corpBankName",num.getOpenAccountBank());
        map.put("corpBankNo",num.getBankAccount());
        map.put("legalName",num.getLegalPersionName());
        map.put("legalIdentityId",num.getLegalPersionCardNum());
        map.put("legalEffective",num.getLegalEffective());
        map.put("legalLongEffective",num.getLegalLongEffective());
        map.put("legalHandlerName",num.getLegalHandlerName());
        map.put("legalHandlerIdentityId",num.getLegalHandlerIdentityId());
        map.put("legalHandlerEffective",num.getLegalHandlerEffective());
        map.put("legalHandlerLongEffective",num.getLegalHandlerLongEffective());
        map.put("legalAddress",num.getLegalAddress());
        map.put("legalHandlerAddress",num.getLegalHandlerAddress());
        map.put("otherLinkMobile",num.getFixPhone());
        map.put("otherLinkPhone",num.getLegalPersionPhone());
        map.put("otherLinkEmail",num.getEmail());
        map.put("otherBindPhone",num.getBindPhone());
        map.put("otherValidPhone",num.getLegalPersionPhone());
        //营业执照文件
        map.put("yyzz","");
        //开户许可证文件
        map.put("khxkz","");
        //法人身份证文件，
        map.put("frsfz","");
        //法人授权证明文件
        map.put("frsq","");
        //经办人身份证文件
        map.put("jbrsfz","");
        //经办人授权证明文
        map.put("jbrsq","");
        //缴费发票文件
        map.put("jffp","");
        //受理单文件
        map.put("sld","");
        //服务协议文件
        map.put("fwxy","");
        //安全承诺书文件
        map.put("aqcns","");
        //手持身份证照片文件
        map.put("scsfz","");
        //经营异常证明文件
        map.put("scsfz","");
        //手持营业执照文件
        map.put("ccyyzz","");
        //低消协议文件
        map.put("dxxy","");
        //工商网截图文件
        map.put("gswjt","");
        //其他文件
        map.put("qt","");
        return map;
    }

    /**
     * 自测预占回调用
     */
    public void yuzjgtz(){
        String sign = "";
        String url ="http://127.0.0.1/jsjt?account=80001001&applyNumber=4008166878&timestamp=1560507421999&sign=sign";
         HashMap map = new HashMap<>();
        map.put("applyNumber","4008166878");
        map.put("infoType","occupy_audit");
        Map<String,String> dataMap = new HashMap<>();

        dataMap.put("occupyCompany","预占公司名称");
        dataMap.put("auditStatus","1");
        dataMap.put("applyDate","2019-06-11 17:55:56");
         dataMap.put("applyDate","9576");
         JSONObject jsonObject = JSONObject.fromObject(dataMap);
         String param = jsonObject.toString();
        map.put("data",param);
         try {
             sendPost(url,map, Const.CONTENT_TYPE_JSON);
         } catch (IOException e) {
             e.printStackTrace();
         }
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
    public String sendPost(String url, HashMap map,String contentType) throws IOException {
        JSONObject jsonObject = JSONObject.fromObject(map);
        String param = jsonObject.toString();
        log.info("输出的结果是：" + param);
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, param);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", contentType)
                .addHeader("Cache-Control", "no-cache")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
