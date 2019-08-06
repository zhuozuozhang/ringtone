package com.hrtxn.ringtone.project.system.log.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.system.log.domain.OperateLog;
import com.hrtxn.ringtone.project.system.log.service.OperateLogService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Author:zcy
 * Date:2019-07-09 9:10
 * Description:操作记录
 */
@Controller
public class OperateLogController {

    @Autowired
    private OperateLogService operateLogService;

    @RequiresRoles("admin")
    @GetMapping("/admin/toOperateLogPage")
    public String toOperateLogPage(){
//        List<OperateLog> operateLogList = operateLogService.findAllOperateLog();
//        map.put("operateLogList",operateLogList);
        return "admin/system/log/operate_log";
    }

    @PostMapping("/admin/findAllOperateLog")
    @RequiresRoles("admin")
    @ResponseBody
    public AjaxResult findAllOperateLog(Page page){
        return operateLogService.findAllOperateLog(page);
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
