package com.hrtxn.ringtone.project.telcertification.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationChildOrder;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationOrder;
import com.hrtxn.ringtone.project.telcertification.service.TelCertificationChildService;
import com.hrtxn.ringtone.project.telcertification.service.TelCertificationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: yuanye
 * Date:2019/8/22 16:26
 * Description:管理端号码认证
 */

@Slf4j
@Controller
@RequestMapping("/admin")
public class TelCertificationAdminController {

    @Autowired
    private TelCertificationService telCertificationService;
    @Autowired
    private TelCertificationChildService telCertificationChildService;

    /**
     * 进入手机认证列表
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

    /**
     * 进入手机认证配置
     * @param map
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/telcertification_config")
    public String telcertification_config(ModelMap map){
        try {
            map.put("telCertifications",null);
        } catch (Exception e) {
            log.error("获取手机认证认证配置,方法：telcertification_config,错误信息",e);
        }
        return "admin/telcertification/telcertification_config";
    }

    /**
     * 进入手机认证充值记录
     * @param map
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/telcertification_recharge_log")
    public String telcertification_recharge_log(ModelMap map){
        try {
            map.put("telcertification_recharge_log",null);
        } catch (Exception e) {
            log.error("获取手机充值记录,方法：telcertification_recharge_log,错误信息",e);
        }
        return "admin/telcertification/telcertification_recharge_log";
    }

    /**
     * 进入手机认证消费记录
     * @param map
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/telcertification_consume_log")
    public String telcertification_consume_log(ModelMap map){
        try {
            map.put("phoneNum",null);
        } catch (Exception e) {
            log.error("获取手机认证消费记录,方法：telcertification_consume_log,错误信息",e);
        }
        return "admin/telcertification/telcertification_consume_log";
    }

    //getTheTelcerConsumeLog
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
     * 进入手机认证详细信息
     * @param map
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/telcertification_detail/{id}")
    public String telcertification_detail(@PathVariable String id, ModelMap map){
        try {
            map.put("parentId",id);
        } catch (Exception e) {
            log.error("获取手机认证详细信息,方法：telcertification_detail,错误信息",e);
        }
        return "admin/telcertification/telcertification_detail";
    }

    /**
     * 进入手机认证成员管理列表
     * @param map
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/telcertification_child_list/{id}")
    public String telcertification_child_list(@PathVariable String id, ModelMap map){
        try {
            map.put("parentId",id);
        } catch (Exception e) {
            log.error("获取手机认证成员管理列表,方法：telcertification_child_list,错误信息",e);
        }
        return "admin/telcertification/telcertification_child_list";
    }


    /**
     * 获取号码认证订单列表
     * @param page
     * @param request
     * @return
     */
    @PostMapping("/getTelCerOrderList")
    @ResponseBody
    @RequiresRoles("admin")
    @Log(title = "获取号码认证订单",operatorLogType = OperatorLogType.TELCERTIFICATION)
    public AjaxResult getTelCerOrderList(Page page, BaseRequest request){
        try{
            return telCertificationService.findAllTelCertification(page,request);
        }catch (Exception e){
            log.error("获取号码认证订单列表数据 方法：getTelCerOrderList 错误信息",e);
        }
        return AjaxResult.error("获取失败");
    }


    /**
     * 获取商户的所有成员
     * @param page
     * @param request
     * @return
     */
    @PostMapping("/getTelcertification_child_list")
    @ResponseBody
    @RequiresRoles("admin")
    public AjaxResult getTelcertification_child_list(Page page,BaseRequest request){
        try{
            return telCertificationChildService.findTheChildOrder(page,request);
        }catch (Exception e){
            log.error("获取商户的所有成员 方法：getTelcertification_child_list 错误信息",e);
        }
        return AjaxResult.error("获取失败");
    }

    //
    /**
     * 获取消费记录列表
     * @return
     */
    @PostMapping("/getTelcertification_consume_log")
    @ResponseBody
    @RequiresRoles("admin")
    public AjaxResult getTelcertification_consume_log(Page page,BaseRequest request){
        try{
            return telCertificationChildService.getTheTelCerCostLogList(page,request);
        }catch (Exception e){
            log.error("获取管理端消费记录列表数据 方法：getTelcertification_consume_log 错误信息",e);
        }
        return AjaxResult.error("获取数据失败");
    }



}
