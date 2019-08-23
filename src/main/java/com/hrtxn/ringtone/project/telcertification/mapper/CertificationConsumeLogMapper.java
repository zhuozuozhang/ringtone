package com.hrtxn.ringtone.project.telcertification.mapper;

import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationConsumeLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificationConsumeLogMapper {

    List<CertificationConsumeLog> getTheTelCerCostLogList(@Param("page") Page page, @Param("param") BaseRequest request);

    List<CertificationConsumeLog> getAllCostLogList(@Param("page") Page page, @Param("param") BaseRequest request);
}
