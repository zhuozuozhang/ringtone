package com.hrtxn.ringtone.project.telcertification.mapper;

import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationOrder;
import com.hrtxn.ringtone.project.telcertification.domain.PlotBarData;
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
    /**
     * 添加渠道商统计信息
     * @param telCerDistributor
     * @return
     */
    int insert(TelCerDistributor telCerDistributor);

    /**
     * 获得全部渠道商信息 或者 根据条件获取
     * @param page
     * @param request
     * @return
     */
    List<TelCerDistributor> getAllTelCerDistributorInfo(@Param("page")Page page, @Param("param") BaseRequest request);

    /**
     * 获得当前账号的全部订购
     * @param userId
     * @return
     */
    TelCerDistributor getServiceNum(Integer userId);

    /**
     * 获得当前账号的上个月订购
     * @param userId
     * @return
     */
    TelCerDistributor getLastMonth(Integer userId);

    /**
     * 获得当前账号的当月订购
     * @param userId
     * @return
     */
    TelCerDistributor getTheMonthService(Integer userId);

    /**
     * 获得当前账号的今日订购
     * @param userId
     * @return
     */
    TelCerDistributor getTodayService(Integer userId);

    /**
     * 更新渠道商信息
     * @param telCerDistributor
     * @return
     */
    int updateByPrimaryKey(TelCerDistributor telCerDistributor);

    List<PlotBarData> getMonthData(@Param("param") BaseRequest request);
}
