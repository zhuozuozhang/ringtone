package com.hrtxn.ringtone.project.telcertification.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Author: yuanye
 * Date:2019/8/22 16:26
 * Description:
 */

@Slf4j
@Controller
@RequestMapping("/admin")
public class TelCertificationAdminController {

    /**
     * 获取手机认证列表
     * @param map
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/telCertificationList")
    public String getTelCertificationList(ModelMap map){
        try {
            map.put("telCertifications",null);
        } catch (Exception e) {
            log.error("获取手机认证列表,方法：getTelCertificationList,错误信息",e);
        }
        return "admin/telcertification/telcertification_list";
    }
}
