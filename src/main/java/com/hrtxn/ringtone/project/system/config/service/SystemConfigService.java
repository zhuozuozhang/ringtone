package com.hrtxn.ringtone.project.system.config.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.system.config.domain.SystemConfig;
import com.hrtxn.ringtone.project.system.config.mapper.SystemConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Author:zcy
 * Date:2019-08-12 17:41
 * Description:系统配置业务处理层
 */
@Service
public class SystemConfigService {

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    /**
     * 获取配置信息列表
     *
     * @param page
     * @return
     */
    public AjaxResult getConfigList(Page page) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        // 获取配置列表
        List<SystemConfig> systemConfigList = systemConfigMapper.getConfigList(page);
        // 获取总数
        int count = systemConfigMapper.getCount();
        if (systemConfigList.size() > 0) {
            return AjaxResult.success(systemConfigList, "获取数据成功！", count);
        }
        return AjaxResult.error("无数据！");
    }

    /**
     * 添加系统配置
     *
     * @param systemConfig
     * @return
     */
    public AjaxResult insertSystemConfig(SystemConfig systemConfig) {
        if (StringUtils.isNotNull(systemConfig) && StringUtils.isNotEmpty(systemConfig.getType()) && StringUtils.isNotEmpty(systemConfig.getInfo())) {
            systemConfig.setCreaTime(new Date());
            // 执行添加配置操作
            int count = systemConfigMapper.insert(systemConfig);
            if (count > 0) {
                return AjaxResult.success(true, "添加成功！");
            } else {
                return AjaxResult.error("添加失败！");
            }
        }
        return AjaxResult.error("参数格式错误！");
    }

    /**
     * 根据id获取配置信息
     *
     * @param id
     * @return
     */
    public SystemConfig getConfigById(Integer id) {
        if (StringUtils.isNotNull(id) && id != 0) {
            return systemConfigMapper.getConfigById(id);
        }
        return null;
    }

    /**
     * 修改配置信息
     *
     * @param systemConfig
     * @return
     */
    public AjaxResult doEditSystemConfig(SystemConfig systemConfig) {
        if (StringUtils.isNotNull(systemConfig) && StringUtils.isNotNull(systemConfig.getId()) && systemConfig.getId() != 0) {
            // 执行修改配置信息操作
            int count = systemConfigMapper.doEditSystemConfig(systemConfig);
            if (count > 0) {
                return AjaxResult.success(true, "修改成功！");
            }
            return AjaxResult.error("修改失败！");
        }
        return AjaxResult.error("参数格式错误！");
    }

    /**
     * 删除配置信息
     *
     * @param id
     * @return
     */
    public AjaxResult deleteConfig(Integer id) {
        if (StringUtils.isNotNull(id) && id != 0) {
            int count = systemConfigMapper.deleteConfig(id);
            if (count > 0) {
                return AjaxResult.success(true, "删除成功！");
            }
            return AjaxResult.error("删除失败！");
        }
        return AjaxResult.error("参数格式错误！");
    }
}
