package com.hrtxn.ringtone.project.system.rechargelog.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.system.rechargelog.domain.RechargeLog;
import com.hrtxn.ringtone.project.system.rechargelog.mapper.RechargeLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-08 14:46
 * Description:<描述>
 */
@Service
public class RechargeLogService {

    @Autowired
    private RechargeLogMapper rechargeLogMapper;
    /**
     * 添加充值记录
     * @param rechargeLog
     * @return
     */
    public boolean insertRechargeLog(RechargeLog rechargeLog){
        return rechargeLogMapper.insertRechargeLog(rechargeLog) > 0 ? true : false;
    }
    /**
     * 根据用户ID查询充值记录
     * @param id
     * @return
     */
    public List<RechargeLog> findRechargeLogByUserId(Integer id) {
        if (StringUtils.isNotNull(id)){
            return rechargeLogMapper.findRechargeLogByUserId(id);
        }
        return null;
    }

    public AjaxResult getRechargeLogList(Page page) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<RechargeLog> allCharge = rechargeLogMapper.getRechargeLogList(page);
        for (RechargeLog r: allCharge) {
            if(r.getRechargeType() == 1){
                r.setRechargeTypeName("号码认证");
            }else if(r.getRechargeType() == 2){
                r.setRechargeTypeName("三网");
            }
        }
        int totalCount = rechargeLogMapper.getCount();
        if(allCharge.size() > 0 && allCharge != null){
            return AjaxResult.success(allCharge,"获取成功",totalCount);
        }
        return AjaxResult.success(allCharge,"获取失败",totalCount);
    }
}
