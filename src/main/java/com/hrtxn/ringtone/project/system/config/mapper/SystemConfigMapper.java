package com.hrtxn.ringtone.project.system.config.mapper;

import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.system.config.domain.SystemConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-08-12 17:44
 * Description:系统配置数据访问层
 */
@Repository
public interface SystemConfigMapper {

    /**
     * 获取配置列表
     *
     * @param page
     * @return
     */
    List<SystemConfig> getConfigList(@Param("page") Page page);

    /**
     * 获取总数
     *
     * @return
     */
    int getCount();

    /**
     * 添加配置信息
     *
     * @param systemConfig
     * @return
     */
    int insert(SystemConfig systemConfig);

    /**
     * 根据id获取配置信息
     *
     * @param id
     * @return
     */
    SystemConfig getConfigById(@Param("id") Integer id);


    /**
     * 根据类型获取配置信息
     *
     * @param type
     * @return
     */
    SystemConfig getConfigByType(@Param("type") String type);
    /**
     * 修改配置信息
     *
     * @param systemConfig
     * @return
     */
    int doEditSystemConfig(SystemConfig systemConfig);

    /**
     * 删除配置信息
     *
     * @param id
     * @return
     */
    int deleteConfig(@Param("id") Integer id);
}
