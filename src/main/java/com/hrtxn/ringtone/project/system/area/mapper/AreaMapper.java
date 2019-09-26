package com.hrtxn.ringtone.project.system.area.mapper;


import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.system.area.domain.Area;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaMapper {

    /**
     * 获取公告列表
     *
     * @return
     * @throws Exception
     */
    List<Area> findAllAreaList(@Param("page") Page page, @Param("area") Area area) throws Exception;


    /**
     * 获取公告数量
     *
     * @param Area
     * @return
     */
    int getAreaCount(@Param("area") Area area);

    int insertArea(Area area);

    public Area getAreaById(Integer id);

    public int updateArea(Area area);

    public int deleteArea(Integer id);
}