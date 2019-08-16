package com.hrtxn.ringtone.project.telcertification.mapper;

import com.hrtxn.ringtone.project.telcertification.domain.CertificationOrder;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author:yuanye
 * Date:2019/8/15 17:26
 * Description:号码认证订单数据访问层
 */
@Repository
public interface CertificationOrderMapper {

    int insertTelCertifyOrder(CertificationOrder certificationOrder);

    List<CertificationOrder> findAllTelCertification();

    int getCount();
}
