package com.hrtxn.ringtone.common.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.*;
import com.hrtxn.ringtone.project.numcertification.domain.*;
import com.hrtxn.ringtone.project.numcertification.json.NumBaseDataResult;
import com.hrtxn.ringtone.project.numcertification.json.NumBaseResult;
import com.hrtxn.ringtone.project.numcertification.json.NumCgiTokenResult;
import com.hrtxn.ringtone.project.numcertification.json.NumDataResult;
import com.hrtxn.ringtone.project.numcertification.mapper.FourcertificationOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsChildOrderMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import okhttp3.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

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
//    public final static String LOGINNAME = "江苏中高俊聪";
//    public final static String PASSWORD = "zg316316@";
//    public final static String URLPREFIX = "http://agentweb.sungoin.com/";
    /**
     * 获取接口调用凭证
     */
    public final static String GETCGITOKEN = NumApi.URLPREFIX + "getCgiToken";
    public final static String NUMBERSELECT = NumApi.URLPREFIX + "cgi/numberSelect/list";

    //预占接口
    public final static String CGIOCCUPYADD = NumApi.URLPREFIX + "cgi/occupy/add";
    //接口模板
    public final static String DOWNLOADTEMPLATE = NumApi.URLPREFIX +  "cgi/material/downloadTemplate";
    //提交资料
    public final static String CGIMATERIALSUBMIT = NumApi.URLPREFIX + "cgi/material/submit";

    public static String cgiToken = null;

//    @Value("${ringtone.profile-url}")
    private String profileUrl = "http://272p922i24.qicp.vip:24914/";
//    private String profileUrl ="http://120.27.226.14/";
//    private String profileUrl ="http://139.217.118.184/";

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
//        if(StringUtils.isEmpty(cgiToken)){
//            AjaxResult ajaxResult = getCgiToken();
//            if ((Integer) ajaxResult.get("code") == 200) {
//                cgiToken = ajaxResult.get("data").toString();
//                log.info("执行刷新cgiToken结果{}", cgiToken);
//            }
//        }
        if(StringUtils.isBlank(cgiToken)){
            getCgiToken();
            return AjaxResult.error("获取失败!,请重新获取");
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
//        map.put("source","3");
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
                map.put("useProvince",numcertificationOrder.getProvince(

                ));
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
     * 预占
     */
    public String preoccupation(FourcertificationOrder fourcertificationOrder){

        String result = "";
        try {
            HashMap map = new HashMap();
            if(StringUtils.isNotEmpty(fourcertificationOrder.getApplyNumber())){
                map.put("applyNumber",fourcertificationOrder.getApplyNumber());
            }
            if(StringUtils.isNotEmpty(fourcertificationOrder.getCompanyName())){
                map.put("occupyCompany",fourcertificationOrder.getCompanyName());
            }
            if(StringUtils.isNotEmpty(fourcertificationOrder.getUserProvince())){
                map.put("useProvince",fourcertificationOrder.getUserProvince());
            }
            if(StringUtils.isNotEmpty(fourcertificationOrder.getUserCity())){
                map.put("useCity",fourcertificationOrder.getUserCity());
            }
            result = sendPost(CGIOCCUPYADD + "?cgiToken=" + NumApi.cgiToken, map, Const.CONTENT_TYPE_JSON);
            log.info("获取数据结果{}", result);
            JSONObject jsonObject = JSONObject.fromObject(result);
            String code = jsonObject.get("code").toString();
            if("0".equals(code)){
                return code;
            }else{
                return jsonObject.get("msg").toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 下载模板
     * @param fourcertificationOrder
     * @return
     * @throws IOException
     */
     public JSONObject downloadTemplate(FourcertificationOrder fourcertificationOrder) throws IOException {
         HashMap map = buildMap(fourcertificationOrder);
         String result = sendPostFile(DOWNLOADTEMPLATE + "?cgiToken=" + NumApi.cgiToken, map,Const.CONTENT_TYPE_FORM_DATA);
         log.info("获取数据结果{}", result);
         JSONObject jsonObject = JSONObject.fromObject(result);
         return jsonObject;

     }

    public HashMap buildMap(FourcertificationOrder fourcertificationOrder){
        HashMap map = new HashMap();
        map.put("cgiToken",cgiToken);
        map.put("numberCode",fourcertificationOrder.getApplyNumber());
        map.put("companyName",fourcertificationOrder.getCompanyName());
        map.put("otherLinkMobile",fourcertificationOrder.getOtherLinkMobile());
        map.put("otherLinkPhone",fourcertificationOrder.getOtherLinkPhone());
        map.put("otherLinkEmail",fourcertificationOrder.getOtherLinkEmail());
        map.put("otherBindPhone",fourcertificationOrder.getOtherBindPhone());
        map.put("otherValidPhone",fourcertificationOrder.getOtherValidPhone());
        map.put("legalCardFileClientUrl",profileUrl+fourcertificationOrder.getLegalCardUrl());
        map.put("operatorCardFileClientUrl",profileUrl+fourcertificationOrder.getOperatorCardUrl());
        return map;
    }


    public String submit(FourcertificationOrder fourcertificationOrder) throws IOException{

        //准备数据
        FourcertificationOrderSubmit submit = buildSubmit(fourcertificationOrder);
        String result = sendPostSubmit(CGIMATERIALSUBMIT + "?cgiToken=" + NumApi.cgiToken, submit,Const.CONTENT_TYPE_JSON);
        log.info("获取数据结果{}", result);
        JSONObject jsonObject = JSONObject.fromObject(result);
        String code = jsonObject.get("code").toString();
        if("0".equals(code)){
            return code;
        }else{
            return jsonObject.get("msg").toString();
        }
    }

    public FourcertificationOrderSubmit buildSubmit(FourcertificationOrder four){
        HashMap<String,String> map = new HashMap();
        FourcertificationOrderSubmit submit = new FourcertificationOrderSubmit();

        submit.setNumberCode(four.getApplyNumber());
        submit.setCorpSocietyNo(four.getCorpSocietyNo());
        submit.setCorpBusinessScope(four.getCorpBusinessScope());
        //企业注册地省份主键
        submit.setCorpCompanyProvince(four.getCorpCompanyProvince());
        //企业注册地城市代码
        submit.setCorpCompanyCity(four.getCorpCompanyCity());
        submit.setCorpCompanyDetail(four.getCorpCompanyDetail());
        submit.setCorpOfficeProvince(four.getCorpOfficeProvince());
        submit.setCorpOfficeCity(four.getCorpOfficeCity());
        submit.setCorpOfficeDetail(four.getCorpOfficeDetail());
        submit.setCorpNunmberUsage(four.getCorpNunmberUsage());
        submit.setCorpAccountType(four.getCorpAccountType());
        submit.setCorpBankName(four.getCorpBankName());
        submit.setCorpBankNo(four.getCorpBankNo());
        submit.setLegalName(four.getLegalName());
        submit.setLegalIdentityId(four.getLegalIdentityId());
        submit.setLegalEffective(four.getLegalEffective());
        submit.setLegalLongEffective(four.getLegalLongEffective());
        submit.setLegalHandlerName(four.getLegalHandlerName());
        submit.setLegalHandlerIdentityId(four.getLegalHandlerIdentityId());
        submit.setLegalHandlerEffective(four.getLegalHandlerEffective());
        submit.setLegalHandlerLongEffective(four.getLegalHandlerLongEffective());
        submit.setLegalAddress(four.getLegalAddress());
        submit.setLegalHandlerAddress(four.getLegalHandlerAddress());
        submit.setOtherLinkMobile(four.getOtherLinkMobile());
        submit.setOtherLinkPhone(four.getOtherLinkPhone());
        submit.setOtherLinkEmail(four.getOtherLinkEmail());
        submit.setOtherBindPhone(four.getOtherBindPhone());
        submit.setOtherValidPhone(four.getOtherValidPhone());
        //营业执照文件
        submit.setYyzz(formatFileUrl(four.getYyzz()));
        //开户许可证文件
        submit.setKhxkz(formatFileUrl(four.getKhxkz()));
        //法人身份证文件，
        submit.setFrsfz(formatFileUrl(four.getFrsfz()));
        //法人授权证明文件
        submit.setFrsq(formatFileUrl(four.getFrsq()));
        //经办人身份证文件
        submit.setJbrsfz(formatFileUrl(four.getJbrsfz()));
        //经办人授权证明文
        submit.setJbrsq(formatFileUrl(four.getJbrsq()));
        //缴费发票文件
        submit.setJffp(formatFileUrl(four.getJffp()));
        //受理单文件
        submit.setSld(formatFileUrl(four.getSld()));
        //服务协议文件
        submit.setFwxy(formatFileUrl(four.getFwxy()));
        //安全承诺书文件
        submit.setAqcns(formatFileUrl(four.getAqcns()));
        //手持身份证照片文件
        submit.setScsfz(formatFileUrl(four.getScsfz()));
        //经营异常证明文件
        submit.setJyyczm(formatFileUrl(four.getJyyczm()));
        //手持营业执照文件
        submit.setCcyyzz(formatFileUrl(four.getCcyyzz()));
        //低消协议文件
        submit.setDxxy(formatFileUrl(four.getDxxy()));
        //工商网截图文件
        submit.setGswjt(formatFileUrl(four.getGswjt()));
        //其他文件
        submit.setQt(formatFileUrl(four.getQt()));
        return submit;
    }

    public List<OrderFileBean> formatFileUrl(String fileUrl){
        if(StringUtils.isBlank(fileUrl)){
            return null;
        }

        String[] files = fileUrl.split("\\|");
        List<OrderFileBean> strList = new ArrayList<>();
        for(String str : files){
            OrderFileBean orderFileBean = new OrderFileBean();
            String suffix = str.substring(str.lastIndexOf(".") + 1);
            String fUrl = profileUrl + "profile"+str.replace("\\","/");
            orderFileBean.setFileType(suffix);
            orderFileBean.setUrl(fUrl);
            strList.add(orderFileBean);
        }
        return strList;
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
     * 每隔60分钟修改预占状态
     *
     * @author zcy
     * @date 2019-8-29 13:55
     */
    @Scheduled(fixedRate = 3600000)
    public void checkFourTime(){
        log.info("****************************开始执行查询预占订单是否有超时******************************************");
        //查询所有预占结束时间超过当前时间的号码，然后状态为10以下的
        SpringUtils.getBean(FourcertificationOrderMapper.class).updateAvailability();
        log.info("****************************结束执行查询预占订单是否有超时******************************************");
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
        log.info("参数：" + param);
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse(contentType);
        RequestBody body = RequestBody.create(mediaType, param);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
//                .addHeader("Content-Type", "application/json")
                .addHeader("Content-Type", contentType)
                .addHeader("Cache-Control", "no-cache")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String sendPostSubmit(String url, FourcertificationOrderSubmit map,String contentType) throws IOException {
        JSONObject jsonObject = JSONObject.fromObject(map);
        String param = jsonObject.toString();
        log.info("输出的结果是：" + param);
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse(contentType);
        RequestBody body = RequestBody.create(mediaType, param);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Content-Type", contentType)
                .addHeader("Cache-Control", "no-cache")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }


    /**
     * post封装
     *
     * @author zcy
     * @date 2019-8-29 15:28
     */
    public String sendPostFile(String url, HashMap map,String contentType) throws IOException {
        JSONObject jsonObject = JSONObject.fromObject(map);
        String param = jsonObject.toString();
        log.info("输出的结果是：" + param);
        OkHttpClient client = new OkHttpClient();
//        MediaType mediaType = MediaType.parse("multipart/form-data");
//        RequestBody body = RequestBody.create(mediaType, param);
        Set<String> keySet = map.keySet();
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for(String key:keySet) {
            String value = "";
            if(map.get(key) != null){
                value = (String)map.get(key);
            }
            formBodyBuilder.add(key,value);
        }
        FormBody formBody = formBodyBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
//                .addHeader("Content-Type", "multipart/form-data")
//                .addHeader("Cache-Control", "no-cache")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }



}
