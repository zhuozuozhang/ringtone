package com.hrtxn.ringtone.project.telcertification.mapper;

import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.telcertification.domain.TelCerDistributor;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: yuanye
 * @Date: Created in 14:52 2019/9/19
 * @Description:
 * @Modified By:
 */
@Repository
public interface TelCerDistributorMapper {
    int insert(TelCerDistributor telCerDistributor);

    List<TelCerDistributor> getAllTelCerDistributorInfo(@Param("page")Page page, @Param("param") BaseRequest request);

    TelCerDistributor getServiceNum(Integer userId);

    TelCerDistributor getTheMonthService(Integer userId);

    TelCerDistributor getTodayService(Integer userId);

    int updateByPrimaryKey(TelCerDistributor telCerDistributor);
}
