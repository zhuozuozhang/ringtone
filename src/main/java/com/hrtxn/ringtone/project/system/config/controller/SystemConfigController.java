package com.hrtxn.ringtone.project.system.config.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.system.config.service.SystemConfigService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @RequiresRoles("admin")
    @PostMapping("/admin/getConfigList")
    @ResponseBody
    public AjaxResult getConfigList(Page page) {
        return systemConfigService.getConfigList(page);
    }


}
