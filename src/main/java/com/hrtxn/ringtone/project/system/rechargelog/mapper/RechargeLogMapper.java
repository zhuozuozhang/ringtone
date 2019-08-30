package com.hrtxn.ringtone.project.system.rechargelog.mapper;

import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.system.rechargelog.domain.RechargeLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-08 14:48
 * Description:充值记录数据访问层
 */
@Repository
public interface RechargeLogMapper {

    /**
     * 添加充值记录
     * @param rechargeLog
     * @return
     */
    int insertRechargeLog(RechargeLog rechargeLog);

    /**
     * 根据用户ID查询充值记录
     * @param id
     * @return
     */
    List<RechargeLog> findRechargeLogByUserId(Integer id);

    List<RechargeLog> getRechargeLogList(@Param("page") Page page);

    int getCount();
}
