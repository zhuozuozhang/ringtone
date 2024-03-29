package com.hrtxn.ringtone.project.system.rechargelog.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.system.rechargelog.service.RechargeLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Author:zcy
 * Date:2019-07-08 17:59
 * Description:充值记录控制器
 */
@Slf4j
@Controller
@RequestMapping("/admin")
public class RechargeLogController {

    @Autowired
    private RechargeLogService rechargeLogService;

    /**
     * 获取全部充值记录
     * @return
     */
    @PostMapping("/getRechargeLogList")
    @ResponseBody
    @RequiresRoles("admin")
    public AjaxResult getRechargeLogList(Page page){
        try{
            return rechargeLogService.getRechargeLogList(page);
        }catch (Exception e){
            log.error("获取全部充值记录 方法：getRechargeLogList 错误信息",e);
        }
        return AjaxResult.error("获取数据失败");
    }



}
