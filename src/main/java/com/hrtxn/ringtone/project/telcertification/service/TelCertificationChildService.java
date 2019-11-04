package com.hrtxn.ringtone.project.telcertification.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.Const;
import com.hrtxn.ringtone.common.utils.DateUtils;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.system.consumelog.domain.ConsumeLog;
import com.hrtxn.ringtone.project.system.consumelog.mapper.ConsumeLogMapper;
import com.hrtxn.ringtone.project.system.user.domain.User;
import com.hrtxn.ringtone.project.system.user.mapper.UserMapper;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationChildOrder;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationOrder;
import com.hrtxn.ringtone.project.telcertification.domain.TelCerDistributor;
import com.hrtxn.ringtone.project.telcertification.mapper.CertificationChildOrderMapper;
import com.hrtxn.ringtone.project.telcertification.mapper.CertificationOrderMapper;
import org.apache.ibatis.annotations.Param;
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

    /**
     * 获得号码认证成员号码订单数量
     * @param request
     * @return
     */
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
        if(ShiroUtils.getSysUser().getId() != 16){
            request.setUserId(ShiroUtils.getSysUser().getId());
        }
        List<CertificationChildOrder> allChildOrderList = certificationChildOrderMapper.findTheChildOrder(page, request);
        CertificationOrder certificationOrder = certificationOrderMapper.getTelCerOrderById(request.getParentId());
        String productJson = certificationOrder.getProductName();
        for (CertificationChildOrder cc : allChildOrderList) {
            cc.setProduct(productJson);
            formatTelCerChildFromDatabase(cc);
        }
        int count = certificationChildOrderMapper.getCount(request);
        if(count > 0){
            return AjaxResult.success(allChildOrderList, "获取成功！", count);
        }
        return AjaxResult.success(false,"未获取到成员信息",count);
    }
    /**
     * 获取即将到期的号码
     *
     * @param page
     * @return
     */
    public AjaxResult getFallDueList(Page page,BaseRequest request) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        request.setUserId(ShiroUtils.getSysUser().getId());
        List<CertificationChildOrder> fallDueList = certificationChildOrderMapper.getFallDueList(page,request);
        int fallDueListCount = certificationChildOrderMapper.getFallDueListCount(page,request);
        if(fallDueList.size() > 0 && fallDueListCount > 0){
            for (CertificationChildOrder childOrder : fallDueList) {
                formatTelCerChildFromDatabase(childOrder);
            }
            return AjaxResult.success(fallDueList,"查询成功",fallDueListCount);
        }
        return AjaxResult.success(false,"参数格式错误",fallDueListCount);
    }
    /**
     * 获取到期的号码
     *
     * @param page
     * @return
     */
    public AjaxResult getDueList(Page page,BaseRequest request) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<CertificationChildOrder> dueList = certificationChildOrderMapper.getDueList(page,request);
        int dueListCount = certificationChildOrderMapper.getDueListCount(page,request);
        if(dueList.size() > 0 && dueListCount > 0){
            for (CertificationChildOrder childOrder : dueList) {
                formatTelCerChildFromDatabase(childOrder);
            }
            return AjaxResult.success(dueList,"查询成功",dueListCount);
        }
        return AjaxResult.success(false,"参数格式错误",dueListCount);
    }

    /**
     * 进入即将到期号码查看详情页面
     * @param id
     * @param map
     * @return
     */
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
    @Transactional
    public AjaxResult editChildStatus(CertificationChildOrder certificationChildOrder) throws Exception {
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
                    certificationChildOrder.setTelChildOrderCtime(theTelCer.getTelChildOrderCtime());
                    certificationChildOrder.setTelChildOrderOpenTime(null);
                    certificationChildOrder.setTelChildOrderExpireTime(null);
                    changeCount = -1;
                } else {
                    return AjaxResult.error("当业务开通失败时，才可以设置重新开通");
                }
            }
            if(Const.TEL_CER_STATUS_SUCCESS_OPENING.equals(certificationChildOrder.getTelChildOrderStatus())){
                if(Const.TEL_CER_STATUS_OPENING.equals(theTelCer.getTelChildOrderStatus())){
                    certificationChildOrder.setTelChildOrderCtime(theTelCer.getTelChildOrderCtime());
                    changeAccount = 1;
                }else {
                    return AjaxResult.error("当业务是开通中时，才可以设置开通成功");
                }
            }
            if(Const.TEL_CER_STATUS_DEFAULT_OPENING.equals(certificationChildOrder.getTelChildOrderStatus())){
                if(Const.TEL_CER_STATUS_OPENING.equals(theTelCer.getTelChildOrderStatus())){
                    certificationChildOrder.setTelChildOrderCtime(theTelCer.getTelChildOrderCtime());
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
                    certificationChildOrder.setTelChildOrderCtime(theTelCer.getTelChildOrderCtime());
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
                    certificationChildOrder.setTelChildOrderCtime(theTelCer.getTelChildOrderCtime());
                    changeAccount = 2;
                }
            }
            if(Const.TEL_CER_STATUS_DEFAULT_RENEWAL.equals(certificationChildOrder.getTelChildOrderStatus())){
                if(Const.TEL_CER_STATUS_RENEWAL.equals(theTelCer.getTelChildOrderStatus())){
                    certificationChildOrder.setTelChildOrderCtime(theTelCer.getTelChildOrderCtime());
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
                    return AjaxResult.success(200,changeCount,"操作成功！",changeCount);
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
                                certificationChildOrder.setTelChildOrderOpenTime(new Date());
                                certificationChildOrder.setTelChildOrderExpireTime(expireTime);
                                consumeLog.setTelConsumeLogOpenTime(new Date());
                                consumeLog.setTelConsumeLogExpireTime(expireTime);
                            }

                            String ifLess = sdf.format(theTelCer.getTelChildOrderExpireTime()).split("-")[0];
                            Integer expireYearIfLess = Integer.parseInt(ifLess) + serviceYear;
                            String expireStrIfLess = expireYearIfLess.toString() + sdf.format(theTelCer.getTelChildOrderExpireTime()).split(ifLess)[1];
                            Date expireTimeIfLess = DateUtils.getDate(expireStrIfLess,DateUtils.FORMAT_DEFAULT);
                            consumeLog.setTelConsumeLogOpenTime(theTelCer.getTelChildOrderOpenTime());
                            consumeLog.setTelConsumeLogExpireTime(expireTimeIfLess);
                            certificationChildOrder.setTelChildOrderExpireTime(expireTimeIfLess);
                        } else {
                            consumeLog.setTelConsumeLogOpenTime(new Date());
                            consumeLog.setTelConsumeLogExpireTime(expireTime);
                            certificationChildOrder.setTelChildOrderOpenTime(new Date());
                            certificationChildOrder.setTelChildOrderExpireTime(expireTime);
                        }
                        int insertConsumeLog = consumeLogMapper.insert(consumeLog);
                        if(insertConsumeLog > 0){
                            int afterSuccess = certificationChildOrderMapper.editChildOrderIfStatusChanged(certificationChildOrder);
                            if(afterSuccess > 0){

                                return AjaxResult.success(200,afterSuccess,"扣费成功！当前账户余额为："+restMoney,afterSuccess);
                            }
                            return AjaxResult.error("扣费失败!");
                        }
                        return AjaxResult.error("添加消费日志失败！");
                    }
                    return AjaxResult.error("修改账户余额失败！");
                }
                return AjaxResult.error("余额不足，请充值");
            }
            int count = certificationChildOrderMapper.editChildOrderIfStatusChanged(certificationChildOrder);
            if (count > 0) {
                return AjaxResult.success(200,count, "操作成功！",count);
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
                Float price = certificationOrder.getUnitPrice();
                childOrder.setPrice(price);
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

    /**
     * 根据id获取号码认证子订单的父id
     *
     * @param id
     * @return
     */
    public int getTelcerChildParentIdById(String id) {
        return certificationChildOrderMapper.getTelcerChildParentIdById(id);
    }

    /**
     * 验证成员号码是否重复
     * @param phoneNum
     * @return
     */
    public AjaxResult isRepetitionByChildNum(String phoneNum) {
        if(phoneNum != "" && phoneNum != null) {
            List<CertificationChildOrder> list = certificationChildOrderMapper.isRepetitionByChildNum(phoneNum);
            if(list != null && list.size() >= 1){
                for (int j = 0; j < list.size(); j++) {
                    String cTelPhone = list.get(j).getTelChildOrderPhone();
                    if(cTelPhone.length() <= 13 && cTelPhone.length() >10){
                        if(cTelPhone.equals(phoneNum)){
                            return AjaxResult.error(500,"电话"+phoneNum+"重复,请重新输入！");
                        }else{
                            continue;
                        }
                    }
                }
            }
            return AjaxResult.success("联系人电话未重复！");
        }
        return AjaxResult.error("参数不正确");
    }

    public AjaxResult opening(Integer id){
        try {
            //根据id获取子订单
            CertificationChildOrder certificationChildOrder = certificationChildOrderMapper.getTelCerChildById(id);
            //查询用户账户余额是否充足
            //扣款金额
            Float price = certificationChildOrder.getPrice();
            CertificationOrder certificationOrder = certificationOrderMapper.getTelCerOrderById(certificationChildOrder.getParentOrderId());
            User user = userMapper.findUserById(certificationOrder.getUserId());
            if(price > user.getTelcertificationAccount()){
                certificationChildOrder.setTelChildOrderStatus(7);
                certificationChildOrderMapper.updateExamineById(certificationChildOrder);
                return AjaxResult.error("开通失败，余额不足");
            }
            //余额足，开通业务，扣款，并记录明细
            certificationChildOrder.setTelChildOrderStatus(3);
            certificationChildOrder.setTelChildOrderOpenTime(new Date());
            //开通年限
            Integer years = certificationChildOrder.getYears();
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.YEAR, 1);//增加一年
            certificationChildOrder.setTelChildOrderExpireTime(cal.getTime());
            certificationChildOrderMapper.updateExamineById(certificationChildOrder);
            //扣款
            Float money = user.getTelcertificationAccount() - price;
            user.setTelcertificationAccount(money);
            userMapper.updateUserById(user);
            //记录消费明细
            ConsumeLog consumeLog = new ConsumeLog();
            consumeLog.setConsumePrice(price);
            consumeLog.setConsumeTime(new Date());
            consumeLog.setConsumeMoney(money);
            consumeLog.setConsumeOperator(ShiroUtils.getSysUser().getUserName());
            consumeLog.setUserName(ShiroUtils.getSysUser().getUserName());
            consumeLog.setUserTel(certificationChildOrder.getTelChildOrderPhone());
            consumeLog.setConsumeType(Const.SYS_CONSUME_LOG_TYPE_TEL_CER);
            consumeLog.setUserId(ShiroUtils.getSysUser().getId());
            consumeLog.setConsumeRemark("");
            consumeLog.setTelConsumeLogType(Const.TEL_CER_CONSUME_LOG_TYPE_FIRST);
            consumeLog.setTelConsumeLogStatus(Const.TEL_CER_STATUS_SUCCESS_OPENING);
            consumeLog.setTelConsumeLogOpenTime(new Date());
            consumeLog.setTelConsumeLogExpireTime(cal.getTime());
            int insertConsumeLog = consumeLogMapper.insert(consumeLog);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return AjaxResult.success("开通成功！");
    }


    /**
     * 审核总价
     * @param pid
     * @return
     */
    Float queryPriceByPid(Integer pid){
        return certificationChildOrderMapper.queryPriceByPid(pid);
    }


    /**
     * 获取所有成员信息或者根据条件获取
     *
     * @param page
     * @param request
     * @return
     */
    public AjaxResult todoChildOrderList(Page page, BaseRequest request) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        if(ShiroUtils.getSysUser().getId() != 16){
            request.setUserId(ShiroUtils.getSysUser().getId());
        }
        List<CertificationChildOrder> allChildOrderList = certificationChildOrderMapper.todoChildOrderList(page, request);
        Integer count = certificationChildOrderMapper.getTodoCount(request);
        if(count > 0){
            return AjaxResult.success(allChildOrderList, "获取成功！", count);
        }
        return AjaxResult.success(false,"未获取到成员信息",count);
    }


}
