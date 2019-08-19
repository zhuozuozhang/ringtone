package com.hrtxn.ringtone.project.telcertification.mapper;

import com.hrtxn.ringtone.project.telcertification.domain.CertificationConsumeLog;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificationConsumeLogMapper {
    int getConsumeLogCount();

    List<CertificationConsumeLog> findAllConsumeLog();
}
