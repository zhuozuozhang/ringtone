package com.hrtxn.ringtone.project.telcertification.mapper;

import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationChildOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author:yuanye
 * Date:2019/8/16 14:14
 * Description:号码认证子订单数据访问层
 */
@Repository
public interface CertificationChildOrderMapper {

    int getCount();

    List<CertificationChildOrder> findAllChildOrder(@Param("page") Page page, @Param("param") BaseRequest request);


    int getMemberCountByParentId(int i);
}
