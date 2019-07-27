package com.hrtxn.ringtone.project.system.log.controller;

import com.hrtxn.ringtone.project.system.log.domain.OperateLog;
import com.hrtxn.ringtone.project.system.log.service.OperateLogService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-09 9:10
 * Description:<描述>
 */
@Controller
public class OperateLogController {

    @Autowired
    private OperateLogService operateLogService;

    @RequiresRoles("admin")
    @GetMapping("/admin/toOperateLogPage")
    public String toOperateLogPage(ModelMap map){
        List<OperateLog> operateLogList = operateLogService.findAllOperateLog();
        map.put("operateLogList",operateLogList);
        return "admin/system/log/operate_log";
    }

    @RequiresRoles("admin")
    @GetMapping("/admin/toOperateLogDetailPage/{operateLogId}")
    public String toOperateLogDetailPage(@PathVariable Integer operateLogId ,ModelMap map){
        try {
            OperateLog operateLog = operateLogService.findOperateLogById(operateLogId);
            map.put("operateLog",operateLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "admin/system/log/operate_log_detail";
    }

}
