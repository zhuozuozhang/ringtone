package com.hrtxn.ringtone.project.telcertification.controller;

import com.hrtxn.ringtone.project.telcertification.service.TelCertificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Author:lile
 * Date:2019-07-10 16:07
 * Description:电话认证控制器
 */
@Slf4j
@Controller
public class TelCertificationController {

    @Autowired
    private TelCertificationService telCertificationService;


    /**
     * 获取手机认证列表
     * @param map
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/admin/telCertificationList")
    public String getTelCertificationList(ModelMap map){
        try {
            map.put("telCertifications",null);
        } catch (Exception e) {
            log.error("获取手机认证列表,方法：getTelCertificationList,错误信息",e);
        }
        return "admin/telcertification/telcertification_list";
    }
}
