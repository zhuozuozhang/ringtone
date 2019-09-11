package com.hrtxn.ringtone.project.telcertification.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.Const;
import com.hrtxn.ringtone.common.utils.DateUtils;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.system.consumelog.domain.ConsumeLog;
import com.hrtxn.ringtone.project.system.consumelog.mapper.ConsumeLogMapper;
import com.hrtxn.ringtone.project.system.user.domain.User;
import com.hrtxn.ringtone.project.system.user.mapper.UserMapper;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationChildOrder;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationOrder;
import com.hrtxn.ringtone.project.telcertification.mapper.CertificationChildOrderMapper;
import com.hrtxn.ringtone.project.telcertification.mapper.CertificationOrderMapper;
import org.aspectj.weaver.loadtime.Aj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private CertificationOrderMapper certificationOrderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ConsumeLogMapper consumeLogMapper;

    public int getCount(BaseRequest request) {

        return certificationChildOrderMapper.getCount(request);
    }

    /**
     * 获取所有成员信息或者根据条件获取
     *
     * @param page
     * @param request
     * @return
     */
    public AjaxResult findTheChildOrder(Page page, BaseRequest request) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<CertificationChildOrder> allChildOrderList = certificationChildOrderMapper.findTheChildOrder(page, request);
        CertificationOrder certificationOrder = certificationOrderMapper.getTelCerOrderById(request.getParentId());
        String productJson = certificationOrder.getProductName();
        for (CertificationChildOrder cc : allChildOrderList) {
            cc.setProduct(productJson);
            formatTelCerChildFromDatabase(cc);
        }
        int count = certificationChildOrderMapper.getCount(request);
        return AjaxResult.success(allChildOrderList, "获取成功！", count);
    }


    /**
     * 获取即将到期的号码
     *
     * @param page
     * @param request
     * @return
     */
    public List<CertificationChildOrder> getFallDueList(Page page, BaseRequest request) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<CertificationChildOrder> allChildOrderList = certificationChildOrderMapper.findTheChildOrder(page, request);
        List<CertificationChildOrder> fallDueList = new ArrayList<CertificationChildOrder>();
        List<CertificationChildOrder> emptyTimeList = new ArrayList<CertificationChildOrder>();
        if (allChildOrderList.size() > 0) {
            for (CertificationChildOrder c : allChildOrderList) {
                Timestamp nowTS = new Timestamp(System.currentTimeMillis());
                long now = nowTS.getTime();
                if(StringUtils.isNull(c.getTelChildOrderExpireTime())){
                    emptyTimeList.add(c);
                }else {
                    long extime = DateUtils.getNow(c.getTelChildOrderExpireTime());
                    long result = (extime - now) / 24 / 60 / 60 / 1000;
                    if (result < 60 && result >= 0) {
                        fallDueList.add(c);
                        c.setDue(2);
                    }
                    formatTelCerChildFromDatabase(c);
                }

            }
            return fallDueList;
        }
        return fallDueList;
    }


    /**
     * 获取到期的号码
     *
     * @param page
     * @param request
     * @return
     */
    public List<CertificationChildOrder> getDueList(Page page, BaseRequest request) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<CertificationChildOrder> allChildOrderList = certificationChildOrderMapper.findTheChildOrder(page, request);
        List<CertificationChildOrder> dueList = new ArrayList<CertificationChildOrder>();
        List<CertificationChildOrder> emptyTimeList = new ArrayList<CertificationChildOrder>();
        if (allChildOrderList.size() > 0) {
            for (CertificationChildOrder c : allChildOrderList) {
                Timestamp nowTS = new Timestamp(System.currentTimeMillis());
                long now = nowTS.getTime();
                if(StringUtils.isNull(c.getTelChildOrderExpireTime())){
                    emptyTimeList.add(c);
                }else{
                    long extime = DateUtils.getNow(c.getTelChildOrderExpireTime());
                    long result = (extime - now) / 24 / 60 / 60 / 1000;
                    if (result < 0) {
                        dueList.add(c);
                        c.setDue(1);
                    }
                    formatTelCerChildFromDatabase(c);
                }
            }
            return dueList;
        }
        return null;
    }


    public CertificationChildOrder getTelCerChildById(Integer id, ModelMap map) {
        if (StringUtils.isNotNull(id)) {
            CertificationChildOrder c = certificationChildOrderMapper.getTelCerChildById(id);

            formatTelCerChildFromDatabase(c);

            map.put("telCerChild", c);
            return c;
        }
        return null;
    }


    /**
     * 格式化tb_telcertification_child_order 数据库中的数据
     *
     * @param c
     */
    private void formatTelCerChildFromDatabase(CertificationChildOrder c) {
        //号码认证子订单状态（1.开通中/2.开通成功/3.开通失败/4.续费中/5.续费成功/6.续费失败）
        if (c.getTelChildOrderStatus() == 1) {
            c.setStatusName("开通中");
        } else if (c.getTelChildOrderStatus() == 2) {
            c.setStatusName("开通成功");
        } else if (c.getTelChildOrderStatus() == 3) {
            c.setStatusName("开通失败");
        } else if (c.getTelChildOrderStatus() == 4) {
            c.setStatusName("续费中");
        } else if (c.getTelChildOrderStatus() == 5) {
            c.setStatusName("续费成功");
        } else if (c.getTelChildOrderStatus() == 6) {
            c.setStatusName("续费失败");
        } else {
            c.setStatusName("未知");
        }

    }

    /**
     * 根据id删除号码认证
     *
     * @param id
     * @return
     */
    public AjaxResult deleteTelCerChild(Integer id) {
        if (StringUtils.isNotNull(id) && id != 0) {
            int count = certificationChildOrderMapper.deleteByPrimaryKey(id);
            if (count > 0) {
                return AjaxResult.success(true, "删除成功！");
            }
            return AjaxResult.error("删除失败！");
        }
        return AjaxResult.error("参数格式错误！");
    }

    /**
     * 修改号码认证子订单的状态
     *
     * @param certificationChildOrder
     * @return
     */
    public AjaxResult editChildStatus(CertificationChildOrder certificationChildOrder) throws Exception {
        CertificationChildOrder childOrder = certificationChildOrder;
        if (StringUtils.isNotNull(certificationChildOrder) && StringUtils.isNotNull(certificationChildOrder.getId()) && certificationChildOrder.getId() != 0) {
            SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.FORMAT_DEFAULT);
            CertificationChildOrder theTelCer = certificationChildOrderMapper.getTelCerChildById(certificationChildOrder.getId());
            Integer serviceYear = theTelCer.getYears();
            String nowYear = sdf.format(new Date()).split("-")[0];
            Integer expireYear = Integer.parseInt(nowYear) + serviceYear;
            String expireStr = expireYear.toString() + sdf.format(new Date()).split(nowYear)[1];
            Date expireTime = DateUtils.getDate(expireStr,DateUtils.FORMAT_DEFAULT);
            int changeCount = 0;
            int changeAccount = 0;
            if(Const.TEL_CER_STATUS_OPENING.equals(certificationChildOrder.getTelChildOrderStatus())) {
                if (Const.TEL_CER_STATUS_DEFAULT_OPENING.equals(theTelCer.getTelChildOrderStatus())) {
                    certificationChildOrder.setTelChildOrderCtime(new Date());
                    certificationChildOrder.setTelChildOrderOpenTime(null);
                    certificationChildOrder.setTelChildOrderExpireTime(null);
                    changeCount = -1;
                } else {
                    return AjaxResult.error("当业务开通失败时，才可以设置重新开通");
                }
            }
            if(Const.TEL_CER_STATUS_SUCCESS_OPENING.equals(certificationChildOrder.getTelChildOrderStatus())){
                if(Const.TEL_CER_STATUS_OPENING.equals(theTelCer.getTelChildOrderStatus())){
                    certificationChildOrder.setTelChildOrderCtime(new Date());
                    changeAccount = 1;
                }else {
                    return AjaxResult.error("当业务是开通中时，才可以设置开通成功");
                }
            }
            if(Const.TEL_CER_STATUS_DEFAULT_OPENING.equals(certificationChildOrder.getTelChildOrderStatus())){
                if(Const.TEL_CER_STATUS_OPENING.equals(theTelCer.getTelChildOrderStatus())){
                    certificationChildOrder.setTelChildOrderCtime(new Date());
                    certificationChildOrder.setTelChildOrderOpenTime(null);
                    certificationChildOrder.setTelChildOrderExpireTime(null);
                    changeCount = -1;
                }else {
                    return AjaxResult.error("当业务是开通中时，才可以设置开通失败");
                }
            }
            if(Const.TEL_CER_STATUS_RENEWAL.equals(certificationChildOrder.getTelChildOrderStatus())){
                if(Const.TEL_CER_STATUS_OPENING.equals(theTelCer.getTelChildOrderStatus())){
                    return AjaxResult.error("业务开通中不可设置为续费中");
                }else if(Const.TEL_CER_STATUS_DEFAULT_OPENING.equals(theTelCer.getTelChildOrderStatus())){
                    return AjaxResult.error("业务开通失败不可设置为续费中");
                }else {
                    certificationChildOrder.setTelChildOrderCtime(new Date());
                    certificationChildOrder.setTelChildOrderOpenTime(theTelCer.getTelChildOrderOpenTime());
                    certificationChildOrder.setTelChildOrderExpireTime(theTelCer.getTelChildOrderExpireTime());
                    changeCount = -1;
                }
            }
            if(Const.TEL_CER_STATUS_SUCCESS_RENEWAL.equals(certificationChildOrder.getTelChildOrderStatus())){
                if(Const.TEL_CER_STATUS_OPENING.equals(theTelCer.getTelChildOrderStatus())){
                    return AjaxResult.error("业务开通中不可直接续费");
                }else if(Const.TEL_CER_STATUS_DEFAULT_OPENING.equals(theTelCer.getTelChildOrderStatus())){
                    return AjaxResult.error("业务开通失败不可直接续费");
                }else {
                    certificationChildOrder.setTelChildOrderCtime(new Date());
                    changeAccount = 2;
                }
            }
            if(Const.TEL_CER_STATUS_DEFAULT_RENEWAL.equals(certificationChildOrder.getTelChildOrderStatus())){
                if(Const.TEL_CER_STATUS_RENEWAL.equals(theTelCer.getTelChildOrderStatus())){
                    certificationChildOrder.setTelChildOrderCtime(new Date());
                    certificationChildOrder.setTelChildOrderOpenTime(null);
                    certificationChildOrder.setTelChildOrderExpireTime(null);
                    changeCount = -1;
                }else{
                    return AjaxResult.error("当业务是续费中时，才可以设置续费失败");
                }
            }
            if(changeCount < 0){
                changeCount = certificationChildOrderMapper.editChildOrderIfStatusChanged(certificationChildOrder);
                if(changeCount > 0){
                    return AjaxResult.success("操作成功！");
                }
                return AjaxResult.error("操作失败！");
            }

            //开通和续费消费
            if(changeAccount > 0){
                User user = userMapper.findUserById(ShiroUtils.getSysUser().getId());
                Float price = theTelCer.getPrice().floatValue();
                Float money = user.getTelcertificationAccount();
                Float restMoney = money - price;
                if(restMoney >= 0){
                    user.setTelcertificationAccount(restMoney);
                    int updateUserAccountById = userMapper.updateUserById(user);
                    if(updateUserAccountById > 0){
                        ConsumeLog consumeLog = new ConsumeLog();
                        consumeLog.setConsumePrice(theTelCer.getPrice());
                        consumeLog.setConsumeTime(new Date());
                        consumeLog.setConsumeMoney(restMoney);
                        consumeLog.setConsumeOperator(ShiroUtils.getSysUser().getUserName());
                        consumeLog.setUserName(ShiroUtils.getSysUser().getUserName());
                        consumeLog.setUserTel(theTelCer.getTelChildOrderPhone());
                        consumeLog.setConsumeType(Const.SYS_CONSUME_LOG_TYPE_TEL_CER);
                        consumeLog.setUserId(ShiroUtils.getSysUser().getId());
                        consumeLog.setConsumeRemark("");
                        //开通成功
                        if(changeAccount == 1){
                            consumeLog.setTelConsumeLogType(Const.TEL_CER_CONSUME_LOG_TYPE_FIRST);
                            consumeLog.setTelConsumeLogStatus(Const.TEL_CER_STATUS_SUCCESS_OPENING);
                        }else if(changeAccount == 2){
                            //续费成功
                            consumeLog.setTelConsumeLogType(Const.TEL_CER_CONSUME_LOG_TYPE_RENEW);
                            consumeLog.setTelConsumeLogStatus(Const.TEL_CER_STATUS_SUCCESS_RENEWAL);
                        }
                        if(StringUtils.isNotNull(certificationChildOrder.getTelChildOrderExpireTime())){
                            long now = (new Date()).getTime();
                            long exprie = (theTelCer.getTelChildOrderExpireTime()).getTime();

                            if(now > exprie){
                                childOrder.setTelChildOrderOpenTime(new Date());
                                childOrder.setTelChildOrderExpireTime(expireTime);
                                consumeLog.setTelConsumeLogOpenTime(new Date());
                                consumeLog.setTelConsumeLogExpireTime(expireTime);
                            }

                            String ifLess = sdf.format(theTelCer.getTelChildOrderExpireTime()).split("-")[0];
                            Integer expireYearIfLess = Integer.parseInt(ifLess) + serviceYear;
                            String expireStrIfLess = expireYearIfLess.toString() + sdf.format(theTelCer.getTelChildOrderExpireTime()).split(ifLess)[1];
                            Date expireTimeIfLess = DateUtils.getDate(expireStrIfLess,DateUtils.FORMAT_DEFAULT);
                            consumeLog.setTelConsumeLogOpenTime(theTelCer.getTelChildOrderOpenTime());
                            consumeLog.setTelConsumeLogExpireTime(expireTimeIfLess);
                            childOrder.setTelChildOrderExpireTime(expireTimeIfLess);
                        } else {
                            consumeLog.setTelConsumeLogOpenTime(new Date());
                            consumeLog.setTelConsumeLogExpireTime(expireTime);
                            childOrder.setTelChildOrderOpenTime(new Date());
                            childOrder.setTelChildOrderExpireTime(expireTime);
                        }
                        int insertConsumeLog = consumeLogMapper.insert(consumeLog);
                        if(insertConsumeLog > 0){
                            int afterSuccess = certificationChildOrderMapper.editChildOrderIfStatusChanged(childOrder);
                            if(afterSuccess > 0){
                                return AjaxResult.success("操作成功！");
                            }
                            return AjaxResult.error("操作失败!");
                        }
                        return AjaxResult.error("添加消费日志失败！");
                    }
                    return AjaxResult.error("修改账户余额失败！");
                }
                return AjaxResult.error("余额不足，请充值");
            }
            int count = certificationChildOrderMapper.editChildOrderIfStatusChanged(certificationChildOrder);
            if (count > 0) {
                return AjaxResult.success( "操作成功！");
            }
            return AjaxResult.error("操作失败！");
        }
        return AjaxResult.error("参数格式错误！");
    }

    /**
     * 实时编辑业务反馈
     *
     * @param certificationChildOrder
     * @return
     */
    public AjaxResult editFeedBackById(CertificationChildOrder certificationChildOrder) {
        if (StringUtils.isNotNull(certificationChildOrder) && StringUtils.isNotNull(certificationChildOrder.getId()) && certificationChildOrder.getId() != 0) {
            int count = certificationChildOrderMapper.editFeedBackById(certificationChildOrder);
            if (count > 0) {
                return AjaxResult.success(true, "修改成功！");
            }
            return AjaxResult.error("修改失败！");
        }
        return AjaxResult.error("参数格式错误！");
    }

    /**
     * 添加号码认证子订单
     *
     * @param certificationChildOrder
     * @return
     */
    @Transactional
    public AjaxResult insertTelCerChild(CertificationChildOrder certificationChildOrder) {
        if (!StringUtils.isNotNull(certificationChildOrder)) {
            return AjaxResult.error("参数格式不正确");
        }
        String[] phoneNumberArray = certificationChildOrder.getPhoneNumberArray();

        CertificationOrder certificationOrder = certificationOrderMapper.getTelCerOrderById(certificationChildOrder.getParentOrderId());
        List<CertificationChildOrder> list = new ArrayList<CertificationChildOrder>();
        for (int i = 0; i < phoneNumberArray.length; i++) {
            String[] phoneNum = phoneNumberArray[i].split("\"");
            CertificationChildOrder childOrder = new CertificationChildOrder();
            for (int j = 0; j < 1; j++) {
                childOrder.setTelChildOrderNum(phoneNum[1]);
                childOrder.setTelChildOrderPhone(phoneNum[1]);

                String productName = certificationOrder.getProductName();
                String[] a = productName.split("periodOfValidity")[1].split("年")[0].split("\"");
                childOrder.setYears(Integer.parseInt(a[2]));
                childOrder.setPrice(certificationOrder.getUnitPrice());
                childOrder.setTelChildOrderStatus(Const.TEL_CER_STATUS_OPENING);
                childOrder.setBusinessFeedback("暂无");
                childOrder.setTelChildOrderCtime(new Date());
                childOrder.setTelChildOrderOpenTime(null);
                childOrder.setTelChildOrderExpireTime(null);
                childOrder.setParentOrderId(certificationChildOrder.getParentOrderId());
                childOrder.setConsumeLogId(1);
            }
            list.add(childOrder);
        }
        int count = certificationChildOrderMapper.batchInsertChildOrder(list);
        if (count > 0) {
            return AjaxResult.success(true, "保存成功");
        }
        return AjaxResult.error("保存失败");
    }

    /**
     * 根据电话号码获取号码认证子订单的父id
     *
     * @param phoneNum
     * @return
     */
    public int getTelcerChildParentIdByPhoneNum(String phoneNum) {
        return certificationChildOrderMapper.getTelcerChildParentIdByPhoneNum(phoneNum);
    }
}
