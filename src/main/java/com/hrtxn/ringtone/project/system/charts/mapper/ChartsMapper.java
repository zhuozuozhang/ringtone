package com.hrtxn.ringtone.project.system.charts.mapper;

import com.hrtxn.ringtone.project.threenets.threenet.domain.PlotBarPhone;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-08-12 10:58
 * Description:管理端 数据统计 数据访问层
 */
@Repository
public interface ChartsMapper {

    /**
     * 按日统计获取数据
     *
     * @param start
     * @param operate
     * @return
     */
    List<PlotBarPhone> getEchartsData(@Param("start") String start, @Param("operate") Integer operate);

    /**
     * 按日统计总数
     *
     * @param start
     * @param operate
     * @return
     */
    int getCount(@Param("start") String start, @Param("operate") Integer operate);

    /**
     * 按日统计退訂
     *
     * @param start
     * @param operate
     * @return
     */
    List<PlotBarPhone> getUnsubscribeData(@Param("start") String start, @Param("operate") Integer operate);

    /**
     * 按月统计总数
     *
     * @param start
     * @param operate
     * @return
     */
    int getYearCount(@Param("start") String start, @Param("operate") Integer operate);

    /**
     * 按月统计数据
     *
     * @param start
     * @param operate
     * @return
     */
    List<PlotBarPhone> getYearEchartsData(@Param("start") String start, @Param("operate") Integer operate);

    /**
     * 按月统计退订
     *
     * @param start
     * @param operate
     * @return
     */
    List<PlotBarPhone> getYearUnsubscribeData(@Param("start") String start, @Param("operate") Integer operate);
}
