package com.hrtxn.ringtone.project.telcertification.mapper;

import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationRechargeLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *  Author:yuanye
 *  Date:2019/8/29 14:11
 *  Description:号码认证充值记录数据访问层
 */
@Repository
public interface CertificationRechargeLogMapper {

    List<CertificationRechargeLog> getTelCerRechargeLogList(@Param("page") Page page);

    int getCount();
}
