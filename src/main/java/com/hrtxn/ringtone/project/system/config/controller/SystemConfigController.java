package com.hrtxn.ringtone.project.system.config.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.system.config.domain.SystemConfig;
import com.hrtxn.ringtone.project.system.config.service.SystemConfigService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

/**
 * Author:zcy
 * Date:2019-08-12 17:32
 * Description:系统配置控制器
 */
@Controller
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 跳转到系统配置页面
     *
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/admin/toSystemConfig")
    public String toSystemConfig() {
        return "admin/system/config/config";
    }

    /**
     * 获取系统配置数据列表
     *
     * @param page
     * @return
     */
    @RequiresRoles("admin")
    @PostMapping("/admin/getConfigList")
    @ResponseBody
    public AjaxResult getConfigList(Page page) {
        return systemConfigService.getConfigList(page);
    }

    /**
     * 跳转到系统配置添加页
     *
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/admin/toConfigAdd")
    public String toConfigAdd() {
        return "admin/system/config/config_add";
    }

    /**
     * 添加系統配置
     *
     * @param systemConfig
     * @return
     */
    @PostMapping("/admin/insertSystemConfig")
    @RequiresRoles("admin")
    @ResponseBody
    @Log(title = "添加系统配置", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult insertSystemConfig(SystemConfig systemConfig) {
        return systemConfigService.insertSystemConfig(systemConfig);
    }

    /**
     * 跳转到修改配置信息页面
     *
     * @param id
     * @param map
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/admin/editConfig/{id}")
    public String editConfig(@PathVariable Integer id, ModelMap map) {
        SystemConfig systemConfig = systemConfigService.getConfigById(id);
        map.put("systemConfig", systemConfig);
        return "admin/system/config/config_edit";
    }

    /**
     * 修改配置信息
     *
     * @param systemConfig
     * @return
     */
    @RequiresRoles("admin")
    @ResponseBody
    @PutMapping("/admin/doEditSystemConfig")
    @Log(title = "修改配置信息", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult doEditSystemConfig(SystemConfig systemConfig) {
        return systemConfigService.doEditSystemConfig(systemConfig);
    }

    /**
     * 删除配置信息
     *
     * @param id
     * @return
     */
    @ResponseBody
    @RequiresRoles("admin")
    @DeleteMapping("/admin/deleteConfig/{id}")
    @Log(title = "删除配置信息",businessType = BusinessType.DELETE,operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult deleteConfig(@PathVariable Integer id) {
        return systemConfigService.deleteConfig(id);
    }

}
