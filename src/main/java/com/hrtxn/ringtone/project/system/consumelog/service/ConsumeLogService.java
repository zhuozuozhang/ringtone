package com.hrtxn.ringtone.project.system.consumelog.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.Const;
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
        return AjaxResult.success(false,"获取失败");
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
    public AjaxResult addRenewConsumeLog(ConsumeLog consumeLog) {
        if(StringUtils.isNotNull(consumeLog) && StringUtils.isNotEmpty(consumeLog.getUserTel())){
            CertificationOrder certificationOrder = certificationOrderMapper.getTelCerOrderByChildOrder(consumeLog.getUserTel());
            CertificationChildOrder certificationChildOrder = certificationChildOrderMapper.getTelcerChildByPhoneNum(consumeLog.getUserTel());
            if(!Const.TEL_CER_STATUS_SUCCESS_OPENING.equals(certificationChildOrder.getTelChildOrderStatus())){
                return AjaxResult.error("当前业务在管理端开通后才可以再续费");
            }
            Float price = certificationOrder.getUnitPrice();
            Float money = ShiroUtils.getSysUser().getTelcertificationAccount();
            Float theRestMoney = money - price;
            if(theRestMoney >= 0){
                User user = new User();
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
                    consumeLog.setConsumeType(1);
                    consumeLog.setTelConsumeLogType(2);
                    consumeLog.setTelConsumeLogStatus(5);
                    consumeLog.setTelConsumeLogOpenTime(null);
                    consumeLog.setTelConsumeLogExpireTime(null);
                    consumeLog.setUserId(ShiroUtils.getSysUser().getId());
                    consumeLog.setConsumeRemark("");
                    int count = consumeLogMapper.insert(consumeLog);
                    if(count > 0){
                        CertificationChildOrder childOrder = new CertificationChildOrder();
                        childOrder.setTelChildOrderStatus(4);
                        childOrder.setId(certificationChildOrder.getId());
                        childOrder.setTelChildOrderCtime(new Date());
                        childOrder.setTelChildOrderOpenTime(certificationChildOrder.getTelChildOrderExpireTime());
                        int afterRenew = certificationChildOrderMapper.editChildAfterRenew(childOrder);
                        if(afterRenew > 0){
                            return AjaxResult.success(true,"添加成功！");
                        }
                        return AjaxResult.error("修改状态、提交时间、开通时间、到期时间失败");
                    }
                    return AjaxResult.error("添加失败！");
                }
                return AjaxResult.error("修改账户余额失败!");
            }
            return AjaxResult.success("余额不足，请充值。");
        }
        return AjaxResult.error("参数格式错误！");
    }
}
