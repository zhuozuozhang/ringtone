package com.hrtxn.ringtone.project.system.log.controller;

import com.hrtxn.ringtone.project.system.log.domain.LoginLog;
import com.hrtxn.ringtone.project.system.log.service.LoginLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-09 14:13
 * Description:登录记录控制器
 */
@Controller
@Slf4j
public class LoginLogController {

    @Autowired
    private LoginLogService loginLogService;

    @RequiresRoles("admin")
    @GetMapping("/admin/toLoginLogPage")
    public String findAllLoginLog(ModelMap map){
        try {
            List<LoginLog> loginLogList = loginLogService.findAllLoginLog();
            map.put("loginLogList",loginLogList);
        } catch (Exception e) {
            log.error("获取登录记录，方法：findAllLoginLog,错误信息：",e);
        }
        return "admin/system/log/login_log";
    }
}
