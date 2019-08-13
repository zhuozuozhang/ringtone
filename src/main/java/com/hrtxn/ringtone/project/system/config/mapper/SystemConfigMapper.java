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


    List<SystemConfig> getConfigList(@Param("page") Page page);

    int getCount();
}
