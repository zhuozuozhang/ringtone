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
    public AjaxResult findTheChildOrder(Page page,BaseRequest request) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<CertificationChildOrder> allChildOrderList = certificationChildOrderMapper.findTheChildOrder(page,request);
        for (CertificationChildOrder cc : allChildOrderList) {
            formatTelCerChildFromDatabase(cc);
        }
        //通过id查找对应的成员信息
        List<CertificationChildOrder> theChildList = new ArrayList<CertificationChildOrder>();
        if(request.getParentId() != null){
            for (CertificationChildOrder cc : allChildOrderList) {
                formatTelCerChildFromDatabase(cc);
                if(cc.getParentOrderId() == request.getParentId()){
                    theChildList.add(cc);
                }
            }
            return AjaxResult.success(theChildList,"查询到了",theChildList.size()); //没有对应id的成员信息
        }
        return AjaxResult.success(theChildList,"获取失败",theChildList.size());
    }

    /**
     * 显示费用支出列表
     * @param page
     * @param request
     * @return
     */
    public AjaxResult getTheTelCerCostLogList(Page page,BaseRequest request) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<CertificationConsumeLog> allCostLogList = certificationConsumeLogMapper.getAllCostLogList(page,request);
        List<CertificationConsumeLog> theCostLogList = new ArrayList<CertificationConsumeLog>();
        if(request.getPhoneNum() != null && request.getPhoneNum() != ""){
            theCostLogList = certificationConsumeLogMapper.getTheTelCerCostLogList(page,request);
            return AjaxResult.success(theCostLogList,"获取某条消费记录",theCostLogList.size());
        }
        return AjaxResult.success(allCostLogList,"获取全部消费记录",allCostLogList.size());
    }

    /**
     * 获取即将到期的号码
     * @param page
     * @param request
     * @return
     */
    public List<CertificationChildOrder> getFallDueList(Page page, BaseRequest request) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<CertificationChildOrder> allChildOrderList = certificationChildOrderMapper.findTheChildOrder(page,request);
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
                formatTelCerChildFromDatabase(c);
            }
            return fallDueList;
        }
        return fallDueList;
    }


    /**
     * 获取到期的号码
     * @param page
     * @param request
     * @return
     */
    public List<CertificationChildOrder> getDueList(Page page, BaseRequest request) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<CertificationChildOrder> allChildOrderList = certificationChildOrderMapper.findTheChildOrder(page,request);
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

                formatTelCerChildFromDatabase(c);
            }
            return dueList;
        }
        return null;
    }


    public CertificationChildOrder getTelCerChildById(Integer id, ModelMap map) {
        if(StringUtils.isNotNull(id)){
            CertificationChildOrder c = certificationChildOrderMapper.getTelCerChildById(id);

            formatTelCerChildFromDatabase(c);

            map.put("telCerChild",c);
            return c;
        }
        return null;
    }


    /**
     * 格式化tb_telcertification_child_order 数据库中的数据
     * @param c
     */
    private void formatTelCerChildFromDatabase(CertificationChildOrder c) {
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

        if(c.getBusinessFeedback() == null || c.getBusinessFeedback() == ""){
            c.setBusinessFeedback("无");
        }

    }

}
