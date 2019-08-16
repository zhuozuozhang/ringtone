package com.hrtxn.ringtone.project.telcertification.mapper;

import com.hrtxn.ringtone.project.telcertification.domain.CertificationChildOrder;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author:yuanye
 * Date:2019/8/16 14:14
 * Description:号码认证子订单数据访问层
 */
@Repository
public interface CertificationChildOrderMapper {

    List<CertificationChildOrder> findAllChildOrder();
}
