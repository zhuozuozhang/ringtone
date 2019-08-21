package com.hrtxn.ringtone.project.telcertification.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.DateUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationChildOrder;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationConsumeLog;
import com.hrtxn.ringtone.project.telcertification.mapper.CertificationChildOrderMapper;
import com.hrtxn.ringtone.project.telcertification.mapper.CertificationConsumeLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author:lile
 * Date:2019-07-11 09:07
 * Description:电话认证成员业务层
 */
@Service
public class TelCertificationChildService {
    @Autowired
    private CertificationChildOrderMapper certificationChildOrderMapper;
    @Autowired
    private CertificationConsumeLogMapper certificationConsumeLogMapper;

    public int getCount() {
        return certificationChildOrderMapper.getCount();
    }

    /**
     * 获取所有成员信息或者根据条件获取
     * @param page
     * @param request
     * @return
     */
    public List<CertificationChildOrder> findAllChildOrder(Page page,BaseRequest request) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<CertificationChildOrder> allChildOrderList = certificationChildOrderMapper.findAllChildOrder(page,request);
        for (CertificationChildOrder cc : allChildOrderList) {
            if(cc.getBusinessFeedback() == null || cc.getBusinessFeedback() == ""){
                cc.setBusinessFeedback("无");
            }
        }
        List<CertificationChildOrder> theChildList = new ArrayList<CertificationChildOrder>();
        if(request.getId() != null){
            for (CertificationChildOrder cc : allChildOrderList) {
                if(cc.getParentOrderId() == request.getId()){
                    theChildList.add(cc);
                }
            }
            return theChildList;
        }
        if(allChildOrderList.size() > 0 && allChildOrderList != null){
            return allChildOrderList;
        }
        return allChildOrderList;
    }

    public int getConsumeLogCount() {
        return certificationConsumeLogMapper.getConsumeLogCount();
    }

    public List<CertificationConsumeLog> findAllConsumeLog() {
        return certificationConsumeLogMapper.findAllConsumeLog();
    }

//    public int getFallDueCount() {
//        return certificationChildOrderMapper.getFallDueCount();
//    }

    /**
     * 获取即将到期的号码
     * @param page
     * @param request
     * @return
     */
    public List<CertificationChildOrder> getFallDueList(Page page, BaseRequest request) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<CertificationChildOrder> allChildOrderList = certificationChildOrderMapper.findAllChildOrder(page,request);
        List<CertificationChildOrder> fallDueList = new ArrayList<CertificationChildOrder>();
        if(allChildOrderList.size() > 0){
            for(CertificationChildOrder c : allChildOrderList){
                Timestamp nowTS = new Timestamp(System.currentTimeMillis());
                long now = nowTS.getTime();
                long extime = DateUtils.getNow(c.getTelChildOrderExpireTime());
                long result = (extime-now)/24/60/60/1000;
                if(result < 60 && result >= 0){
                    fallDueList.add(c);
                    c.setDue(2);
                }

                //号码认证子订单状态（1.开通中/2.开通成功/3.开通失败/4.续费中/5.续费成功/6.续费失败）
                if(c.getTelChildOrderStatus() == 1){
                    c.setStatusName("开通中");
                }else if(c.getTelChildOrderStatus() == 2){
                    c.setStatusName("开通成功");
                }else if(c.getTelChildOrderStatus() == 3){
                    c.setStatusName("开通失败");
                } else if(c.getTelChildOrderStatus() == 4){
                    c.setStatusName("续费中");
                }else if(c.getTelChildOrderStatus() == 5){
                    c.setStatusName("续费成功");
                }else if(c.getTelChildOrderStatus() == 6){
                    c.setStatusName("续费失败");
                }else{
                    c.setStatusName("未知");
                }
            }
            return fallDueList;
        }

        return null;
    }


    public List<CertificationChildOrder> getDueList(Page page, BaseRequest request) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<CertificationChildOrder> allChildOrderList = certificationChildOrderMapper.findAllChildOrder(page,request);
        List<CertificationChildOrder> dueList = new ArrayList<CertificationChildOrder>();
        if(allChildOrderList.size() > 0){
            for(CertificationChildOrder c : allChildOrderList){
                Timestamp nowTS = new Timestamp(System.currentTimeMillis());
                long now = nowTS.getTime();
                long extime = DateUtils.getNow(c.getTelChildOrderExpireTime());
                long result = (extime-now)/24/60/60/1000;
                if(result < 0){
                    dueList.add(c);
                    c.setDue(1);
                }

                //号码认证子订单状态（1.开通中/2.开通成功/3.开通失败/4.续费中/5.续费成功/6.续费失败）
                if(c.getTelChildOrderStatus() == 1){
                    c.setStatusName("开通中");
                }else if(c.getTelChildOrderStatus() == 2){
                    c.setStatusName("开通成功");
                }else if(c.getTelChildOrderStatus() == 3){
                    c.setStatusName("开通失败");
                } else if(c.getTelChildOrderStatus() == 4){
                    c.setStatusName("续费中");
                }else if(c.getTelChildOrderStatus() == 5){
                    c.setStatusName("续费成功");
                }else if(c.getTelChildOrderStatus() == 6){
                    c.setStatusName("续费失败");
                }else{
                    c.setStatusName("未知");
                }
            }
            return dueList;
        }
        return null;
    }

    public CertificationChildOrder getTelCerChildById(Integer id, ModelMap map) {
        if(StringUtils.isNotNull(id)){
            CertificationChildOrder c = certificationChildOrderMapper.getTelCerChildById(id);

            //号码认证子订单状态（1.开通中/2.开通成功/3.开通失败/4.续费中/5.续费成功/6.续费失败）
            if(c.getTelChildOrderStatus() == 1){
                c.setStatusName("开通中");
            }else if(c.getTelChildOrderStatus() == 2){
                c.setStatusName("开通成功");
            }else if(c.getTelChildOrderStatus() == 3){
                c.setStatusName("开通失败");
            } else if(c.getTelChildOrderStatus() == 4){
                c.setStatusName("续费中");
            }else if(c.getTelChildOrderStatus() == 5){
                c.setStatusName("续费成功");
            }else if(c.getTelChildOrderStatus() == 6){
                c.setStatusName("续费失败");
            }else{
                c.setStatusName("未知");
            }

            map.put("telCerChild",c);
            return c;
        }
        return null;
    }

}
