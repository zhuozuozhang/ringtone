package com.hrtxn.ringtone.common.utils;

import com.hrtxn.ringtone.project.system.config.domain.SystemConfig;
import com.hrtxn.ringtone.project.system.config.mapper.SystemConfigMapper;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.Arrays;
import java.util.List;

/**
 * Author:lile
 * Date:2019-08-16 9:52
 * Description:
 */
public class ConfigUtil {

    /**
     * 根据id获取系统配置信息
     *
     * @param id
     * @return
     */
    public SystemConfig getConfigById(Integer id) {
        SystemConfig config = SpringUtils.getBean(SystemConfigMapper.class).getConfigById(id);
        return config;
    }

    /**
     * 根据类型获取系统配置信息
     *
     * @param type
     * @return
     */
    public static SystemConfig getConfigByType(String type) {
        List<SystemConfig> list = SpringUtils.getBean(SystemConfigMapper.class).getConfigByType(type);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return new SystemConfig();
        }
    }

    /**
     * 根据类型获取地区数组
     *
     * @param type
     * @return
     */
    public static Boolean getAreaArray(String type, String area) {
        Boolean flag = false;
        List<SystemConfig> list = SpringUtils.getBean(SystemConfigMapper.class).getConfigByType(type);
        if (list.size() > 0) {
            String info = list.get(0).getInfo();
            info = info.replace("，", ",").replace("、", ",").replace("/", ",");
            String[] array = info.split(",");
            flag = Arrays.asList(array).contains(area);
        }
        return flag;
    }
}
