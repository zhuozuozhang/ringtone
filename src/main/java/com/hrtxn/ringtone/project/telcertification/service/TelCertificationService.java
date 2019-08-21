package com.hrtxn.ringtone.project.telcertification.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationChildOrder;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationOrder;
import com.hrtxn.ringtone.project.telcertification.mapper.CertificationChildOrderMapper;
import com.hrtxn.ringtone.project.telcertification.mapper.CertificationOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author:lile
 * Date:2019-07-10 16:07
 * Description:电话认证业务层
 */
@Service
public class TelCertificationService {

    @Autowired
    private CertificationOrderMapper certificationOrderMapper;
    @Autowired
    private CertificationChildOrderMapper certificationChildOrderMapper;


    public AjaxResult insertTelCertifyOrder(CertificationOrder certificationOrder) {
        if(!StringUtils.isNotNull(certificationOrder)){
            return AjaxResult.error("参数不正确！");
        }
        int count = certificationOrderMapper.insertTelCertifyOrder(certificationOrder);
        return null;
    }

    public int getCount() {
        return certificationOrderMapper.getCount();
    }


    public List<CertificationOrder> findAllTelCertification(Page page, BaseRequest request) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<CertificationOrder> allTelCer = certificationOrderMapper.findAllTelCertification(page,request);
        System.out.println(request);
        //通过集团名称获取商户信息
        String companyName = request.getCompanyName().trim();
        List<CertificationOrder> theTelCer = new ArrayList<CertificationOrder>();
        if(companyName != null && companyName != ""){
            //如果商户名称可以不唯一
            for (CertificationOrder c: allTelCer) {
                if(c.getTelCompanyName().equals(companyName)){
                    request.setCompanyName(companyName);
                    theTelCer.add(c);
                }
            }
            return theTelCer;

        }
//        通过成员号码获得商户信息
        String phoneNum = request.getPhoneNum().trim();
//        if(request.getPhoneNum().trim() != null && request.getPhoneNum().trim() != ""){
        if(phoneNum != null && phoneNum != ""){
            String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
//            String phoneNum = request.getPhoneNum().trim();
            int len = phoneNum.length();
            if(len == 11){

                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(phoneNum);
                boolean isMatch = m.matches();
                if (!isMatch) {
                    return null;//手机号不正确
                }
                //根据成员电话号查找
                List<CertificationChildOrder> ccList = certificationChildOrderMapper.findAllChildOrder(page,request);
                for (CertificationChildOrder cc: ccList) {
                    request.setId(cc.getParentOrderId());
                    //根据id查找
                    allTelCer = certificationOrderMapper.findAllTelCertification(page,request);
                }
                return allTelCer;
            }else{
                return null;//手机号的长度不为11位
            }
        }

        if(allTelCer.size() > 0 && allTelCer != null){
            for (CertificationOrder c: allTelCer) {
                int member = certificationChildOrderMapper.getMemberCountByParentId(c.getId());

                c.setMemberNum(member);
                //号码认证订单状态（1.开通中/2.开通成功/3.开通失败/4.续费中/5.续费成功/6.续费失败）
                if(c.getTelOrderStatus() == 1){
                    c.setStatusName("开通中");
                }else if(c.getTelOrderStatus() == 2){
                    c.setStatusName("开通成功");
                }else if(c.getTelOrderStatus() == 3){
                    c.setStatusName("开通失败");
                }else if(c.getTelOrderStatus() == 4){
                    c.setStatusName("续费中");
                }else if(c.getTelOrderStatus() == 5){
                    c.setStatusName("续费成功");
                }else if(c.getTelOrderStatus() == 6){
                    c.setStatusName("续费失败");
                }else{
                    c.setStatusName("未知");
                }

            }
            return allTelCer;
        }
        return null;
    }

    /**
     * 通过订单id获取订单信息
     * @param id
     * @return
     */
    public CertificationOrder getTelCerOrderById(Integer id, ModelMap map) {
        if(StringUtils.isNotNull(id)){
            CertificationOrder c = certificationOrderMapper.getTelCerOrderById(id);
            //号码认证订单状态（1.开通中/2.开通成功/3.开通失败/4.续费中/5.续费成功/6.续费失败）
            if(c.getTelOrderStatus() == 1){
                c.setStatusName("开通中");
            }else if(c.getTelOrderStatus() == 2){
                c.setStatusName("开通成功");
            }else if(c.getTelOrderStatus() == 3){
                c.setStatusName("开通失败");
            }else if(c.getTelOrderStatus() == 4){
                c.setStatusName("续费中");
            }else if(c.getTelOrderStatus() == 5){
                c.setStatusName("续费成功");
            }else if(c.getTelOrderStatus() == 6){
                c.setStatusName("续费失败");
            }else{
                c.setStatusName("未知");
            }

            String json = c.getProductName();
            JSONObject js = JSON.parseObject(json);
            Map<String, Object> product = js;
            Object service = null;
            for (Map.Entry<String, Object> entry : product.entrySet()) {
                service = entry.getValue();
            }
            int num = certificationChildOrderMapper.getMemberCountByParentId(id);
            c.setMemberNum(num);
            map.put("service",service);
            map.put("telCerOrder",c);
            return c;
        }
        return null;
    }
}
