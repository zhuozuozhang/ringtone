package com.hrtxn.ringtone.project.system.consumelog.controller;
/**
 * @Author: yuanye
 * @Date: Created in 10:29 2019/8/30
 * @Description: 充值记录实体类
 * @Modified By:
 */

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.system.consumelog.service.ConsumeLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/admin")
public class ConsumeLogController {

    @Autowired
    private ConsumeLogService consumeLogService;

    /**
     * 进入手机认证消费记录
     * @param map
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/telcertificationConsumeLog")
    public String telcertificationConsumeLog(ModelMap map){
        try {
            map.put("phoneNum",null);
        } catch (Exception e) {
            log.error("获取手机认证消费记录,方法：telcertificationConsumeLog,错误信息",e);
        }
        return "admin/telcertification/telcertification_consume_log";
    }

    /**
     * 获取全部消费记录
     * @return
     */
    @PostMapping("/getConsumeLogList")
    @ResponseBody
    @RequiresRoles("admin")
    public AjaxResult getConsumeLogList(Page page,BaseRequest request){
        try{
            return consumeLogService.getConsumeLogList(page,request);
        }catch (Exception e){
            log.error("获取全部充值记录 方法：getConsumeLogList 错误信息",e);
        }
        return AjaxResult.error("获取数据失败");
    }


    /**
     * 进入某条手机认证消费记录
     * @param map
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/getTheTelcerConsumeLog/{phoneNum}")
    public String getTheTelcerConsumeLog(@PathVariable String phoneNum, ModelMap map){
        try {
            map.put("phoneNum",phoneNum);
        } catch (Exception e) {
            log.error("获取手机认证消费记录,方法：getTheTelcerConsumeLog,错误信息",e);
        }
        return "admin/telcertification/telcertification_consume_log";
    }

    /**
     * 获取某条消费记录列表
     * @return
     */
    @PostMapping("/getTelcerConsumeLog")
    @ResponseBody
    @RequiresRoles("admin")
    public AjaxResult getTelcerConsumeLog(Page page, BaseRequest request){
        try{
            return consumeLogService.getConsumeLogList(page,request);
        }catch (Exception e){
            log.error("获取管理端消费记录列表数据 方法：getTelcerConsumeLog 错误信息",e);
        }
        return AjaxResult.error("获取数据失败");
    }

}
