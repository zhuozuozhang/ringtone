package com.hrtxn.ringtone.project.system.consumelog.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: yuanye
 * @Date: Created in 10:29 2019/8/30
 * @Description: 消费记录业务逻辑层
 * @Modified By:
 */
@Service
public class ConsumeLogService {
    @Autowired
    private ConsumeLogMapper consumeLogMapper;
    @Autowired
    private CertificationOrderMapper certificationOrderMapper;
    @Autowired
    private CertificationChildOrderMapper certificationChildOrderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 获得全部消费记录
     * @param page
     * @param request
     * @return
     */
    public AjaxResult getConsumeLogList(Page page,BaseRequest request) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<ConsumeLog> allConsume = consumeLogMapper.getConsumeLogList(page,request);
        List<ConsumeLog> theCostLogList = new ArrayList<ConsumeLog>();

        formatConsumeLog(allConsume);

        if(request.getPhoneNum() != null && request.getPhoneNum() != ""){
            theCostLogList = consumeLogMapper.getTheTelCerCostLogList(page,request);
            formatConsumeLog(theCostLogList);
            return AjaxResult.success(theCostLogList,"获取成员的消费记录",theCostLogList.size());
        }
        int totalCount = consumeLogMapper.getCount();
        if(allConsume.size() > 0 && allConsume != null){
            return AjaxResult.success(allConsume,"获取成功",totalCount);
        }
        return AjaxResult.success(allConsume,"获取失败",totalCount);
    }

    /**
     * 通过userId获取消费记录
     * @param id
     * @return
     */
    public List<ConsumeLog> findConsumeLogByUserId(Integer id) {
        if(StringUtils.isNotNull(id)){
            return consumeLogMapper.findConsumeLogByUserId(id);
        }
        return null;
    }


    /**
     * 格式化消费记录
     * @param consumeLogs
     */
    private void formatConsumeLog(List<ConsumeLog> consumeLogs) {
        for (ConsumeLog c: consumeLogs) {
            if(c.getConsumeType() == 1){
                c.setConsumeTypeName("号码认证");
            }else if(c.getConsumeType() == 2){
                c.setConsumeTypeName("三网");
            }
        }
    }

    /**
     * 续费
     * @param consumeLog
     * @return
     */
    public AjaxResult addRenewConsumeLog(ConsumeLog consumeLog) throws Exception {
        if(StringUtils.isNotNull(consumeLog) && StringUtils.isNotEmpty(consumeLog.getUserTel())){
            CertificationOrder certificationOrder = certificationOrderMapper.getTelCerOrderByChildOrder(consumeLog.getUserTel());
            CertificationChildOrder certificationChildOrder = certificationChildOrderMapper.getTelcerChildByPhoneNum(consumeLog.getUserTel());
            if(Const.TEL_CER_STATUS_OPENING.equals(certificationChildOrder.getTelChildOrderStatus()) ||
                    Const.TEL_CER_STATUS_DEFAULT_OPENING.equals(certificationChildOrder.getTelChildOrderStatus())){
                return AjaxResult.error("当前业务需要在管理端开通后才可以再续费");
            }
            Float price = certificationOrder.getUnitPrice();
            User user = userMapper.findUserById(ShiroUtils.getSysUser().getId());
            Float money = user.getTelcertificationAccount();
            Float theRestMoney = money - price;
            if(theRestMoney >= 0){
                user.setId(ShiroUtils.getSysUser().getId());
                user.setTelcertificationAccount(theRestMoney);
                int updateUserById = userMapper.updateUserById(user);
                if(updateUserById > 0){
                    consumeLog.setConsumePrice(certificationOrder.getUnitPrice());
                    consumeLog.setConsumeTime(new Date());
                    consumeLog.setConsumeMoney(theRestMoney);
                    consumeLog.setConsumeOperator(ShiroUtils.getSysUser().getUserName());
                    consumeLog.setUserName(ShiroUtils.getSysUser().getUserName());
                    consumeLog.setUserTel(consumeLog.getUserTel());
                    consumeLog.setConsumeType(Const.SYS_CONSUME_LOG_TYPE_TEL_CER);
                    consumeLog.setTelConsumeLogType(Const.TEL_CER_CONSUME_LOG_TYPE_RENEW);
                    consumeLog.setTelConsumeLogStatus(Const.TEL_CER_STATUS_SUCCESS_RENEWAL);
                    consumeLog.setUserId(ShiroUtils.getSysUser().getId());
                    consumeLog.setConsumeRemark("");

                    SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.FORMAT_DEFAULT);
                    Integer serviceYear = certificationChildOrderMapper.getTelCerChildById(certificationChildOrder.getId()).getYears();

                    String nowYear = sdf.format(new Date()).split("-")[0];
                    Integer expireYear = Integer.parseInt(nowYear) + serviceYear;
                    String expireStr = expireYear.toString() + sdf.format(new Date()).split(nowYear)[1];
                    Date expireTime = DateUtils.getDate(expireStr,DateUtils.FORMAT_DEFAULT);

                    CertificationChildOrder childOrder = new CertificationChildOrder();
                    childOrder.setTelChildOrderStatus(Const.TEL_CER_STATUS_SUCCESS_RENEWAL);
                    childOrder.setId(certificationChildOrder.getId());
                    childOrder.setTelChildOrderCtime(new Date());

                    if(StringUtils.isNotNull(certificationChildOrder.getTelChildOrderExpireTime())){
                        long now = (new Date()).getTime();
                        long exprie = (certificationChildOrder.getTelChildOrderExpireTime()).getTime();
                        //判断现在的时间 是否大于 获取到的到期时间
                        //如果大于
                        // 开通时间： 就是现在
                        // 到期时间： 现在的时间+续费时间
                        if(now > exprie){
                            childOrder.setTelChildOrderOpenTime(new Date());
                            childOrder.setTelChildOrderExpireTime(expireTime);
                            consumeLog.setTelConsumeLogOpenTime(new Date());
                            consumeLog.setTelConsumeLogExpireTime(expireTime);
                        }
                        //如果小于
                        // 开通时间： 不变
                        // 到期时间： 获取到的到期时间+续费时间
                        String ifLess = sdf.format(certificationChildOrder.getTelChildOrderExpireTime()).split("-")[0];
                        Integer expireYearIfLess = Integer.parseInt(ifLess) + serviceYear;
                        String expireStrIfLess = expireYearIfLess.toString() + sdf.format(certificationChildOrder.getTelChildOrderExpireTime()).split(ifLess)[1];
                        Date expireTimeIfLess = DateUtils.getDate(expireStrIfLess,DateUtils.FORMAT_DEFAULT);

                        childOrder.setTelChildOrderExpireTime(expireTimeIfLess);

                        consumeLog.setTelConsumeLogOpenTime(certificationChildOrder.getTelChildOrderOpenTime());
                        consumeLog.setTelConsumeLogExpireTime(expireTimeIfLess);
                    } else {
                        consumeLog.setTelConsumeLogOpenTime(new Date());
                        consumeLog.setTelConsumeLogExpireTime(expireTime);
                        childOrder.setTelChildOrderOpenTime(new Date());
                        childOrder.setTelChildOrderExpireTime(expireTime);
                    }
                    int count = consumeLogMapper.insert(consumeLog);
                    if(count > 0){
                        int afterRenew = certificationChildOrderMapper.editChildOrderIfStatusChanged(childOrder);
                        if(afterRenew > 0){
                            return AjaxResult.success(true,"续费成功！");
                        }
                        return AjaxResult.error("修改状态、提交时间、开通时间、到期时间失败");
                    }
                    return AjaxResult.error("添加失败！");
                }
                return AjaxResult.error("修改账户余额失败!");
            }
            return AjaxResult.error("余额不足，请充值。");
        }
        return AjaxResult.error("参数格式错误！");
    }
}
