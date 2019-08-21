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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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


    /**
     * 添加号码认证商户订单
     * @param certificationOrder
     * @return
     */
    public AjaxResult insertTelCertifyOrder(CertificationOrder certificationOrder) {
        if(!StringUtils.isNotNull(certificationOrder)){
            return AjaxResult.error("参数不正确！");
        }
        int count = certificationOrderMapper.insertTelCertifyOrder(certificationOrder);
        return null;
    }

    /**
     * 获得商户订单数量
     * @return
     */
    public int getCount() {
        return certificationOrderMapper.getCount();
    }


    /**
     * 获得所有商户订单（可通过各种条件查找）
     * @param page
     * @param request
     * @return
     */
    public List<CertificationOrder> findAllTelCertification(Page page, BaseRequest request) throws ParseException {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<CertificationOrder> allTelCer = certificationOrderMapper.findAllTelCertification(page,request);
        List<CertificationOrder> theTelCer = new ArrayList<CertificationOrder>();

        for (CertificationOrder c: allTelCer) {
            formatParamFromDatabase(c);
            int member = certificationChildOrderMapper.getMemberCountByParentId(c.getId());
            c.setMemberNum(member);

        }

        //通过时间段、集团名称、联系人电话、成员号码查到商户信息
        String rangeTime = request.getRangetime();
        String companyName = request.getCompanyName().trim();
        String tel = request.getTel().trim();
        String phoneNum = request.getPhoneNum().trim();
        if((rangeTime != null && rangeTime != "") || (companyName != null && companyName != "") || (tel != null && tel != "") || (phoneNum != null && phoneNum != "")){

            if(rangeTime != null && rangeTime != ""){
                //对时间段的处理
                String s[] = rangeTime.split("-") ;
                String start = s[0]+"-"+s[1]+"-"+s[2]+" 00:00:00";
                String end = s[3]+"-"+s[4]+"-"+s[5]+" 23:59:59";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date dateStart = sdf.parse(start);
                Date dateEnd = sdf.parse(end);
                request.setStart(start);
                request.setEnd(end);

                for (CertificationOrder c: allTelCer) {
                    Date telOrderTime = c.getTelOrderTime();
                    long telCreateTime = telOrderTime.getTime();
                    long longStart = dateStart.getTime();
                    long longEnd = dateEnd.getTime();
                    if(longStart <= telCreateTime && longEnd >= telCreateTime){
                        theTelCer.add(c);
                    }
                }
            }

            //如果商户名称可以不唯一 且 考虑模糊查询
            for(CertificationOrder c : allTelCer){
                if(c.getTelCompanyName().indexOf(companyName) != -1){
                    theTelCer.add(c);
                }
            }

            //对联系人电话的处理
            String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
            Pattern p = Pattern.compile(regex);
            int len = tel.length();
            if (len == 11) {
                Matcher m = p.matcher(tel);
                boolean isMatch = m.matches();
                if (!isMatch) {
                    return theTelCer;//手机号不正确
                }
                for (CertificationOrder c : allTelCer) {
                    if(c.getTelLinkPhone().equals(tel)){
                        theTelCer.add(c);
                    }
                }
            }

            //成员电话号码
            int len2 = phoneNum.length();
            if(len2 == 11) {
                Matcher m = p.matcher(phoneNum);
                boolean isMatch = m.matches();
                if (!isMatch) {
                    return theTelCer;//手机号不正确
                }
                //根据成员电话号查找
                List<CertificationChildOrder> ccList = certificationChildOrderMapper.findAllChildOrder(page, request);
                for (CertificationChildOrder cc : ccList) {
                    request.setId(cc.getParentOrderId());
                    //根据id查找
                    for (CertificationOrder c : allTelCer) {
                        if (cc.getParentOrderId().equals(c.getId())) {
                            theTelCer.add(c);
                        }
                    }
                }
            }

            if(theTelCer != null && theTelCer.size()>0){
                return theTelCer;
            }
            return theTelCer;
        }

        if(allTelCer.size() > 0 && allTelCer != null){
            return allTelCer;
        }
        return null;//没有从数据库获取到数据
    }

    /**
     * 通过订单id获取订单信息
     * @param id
     * @return
     */
    public CertificationOrder getTelCerOrderById(Integer id, ModelMap map) {
        if(StringUtils.isNotNull(id)){
            CertificationOrder c = certificationOrderMapper.getTelCerOrderById(id);

            formatParamFromDatabase(c);

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

    /**
     * 格式化`tb_telcertification_order`数据库中的数据
     * @param c
     */
    private void formatParamFromDatabase(CertificationOrder c) {

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

        //如果备注中的内容为空，则改变值为无
        if(c.getRemark()=="" || c.getRemark()==null){
            c.setRemark("无");
        }
    }
}
