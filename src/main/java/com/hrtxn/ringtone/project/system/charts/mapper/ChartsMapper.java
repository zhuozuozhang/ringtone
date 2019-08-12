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


    List<PlotBarPhone> getEchartsData(@Param("start") String start,@Param("operate")  Integer operate);

    int getCount(@Param("start") String start,@Param("operate")  Integer operate);

    List<PlotBarPhone> getUnsubscribeData(@Param("start") String start,@Param("operate")  Integer operate);

    int getYearCount(@Param("start") String start,@Param("operate")  Integer operate);

    List<PlotBarPhone> getYearEchartsData(@Param("start") String start,@Param("operate")  Integer operate);

    List<PlotBarPhone> getYearUnsubscribeData(@Param("start") String start,@Param("operate")  Integer operate);
}
