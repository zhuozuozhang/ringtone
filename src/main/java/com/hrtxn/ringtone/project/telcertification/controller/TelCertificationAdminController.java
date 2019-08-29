package com.hrtxn.ringtone.project.telcertification.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationChildOrder;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationConfig;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationOrder;
import com.hrtxn.ringtone.project.telcertification.service.TelCertificationChildService;
import com.hrtxn.ringtone.project.telcertification.service.TelCertificationConfigService;
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
    @Autowired
    private TelCertificationConfigService telCertificationConfigService;

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
     * @param
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/telcertificationConfig")
    public String telcertificationConfig(){
        return "admin/telcertification/telcertification_config";
    }

    @RequiresRoles("admin")
    @PostMapping("/getAllConfig")
    @ResponseBody
    public AjaxResult  getAllConfig(Page page, ModelMap map){
        return telCertificationConfigService.getAllConfig(page,map);
    }

    /**
     * 跳转到修改配置页面
     * @param id
     * @param map
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/toEditTelCerConfig/{id}")
    public String toEditTelCerConfig(@PathVariable Integer id, ModelMap map){
        CertificationConfig certificationConfig = telCertificationConfigService.getTelCerConfigById(id);
        map.put("telCerConfig",certificationConfig);
        return "admin/telcertification/telcertification_config_edit";
    }

    @RequiresRoles("admin")
    @ResponseBody
    @PutMapping("/editTelCerConfig")
    @Log(title = "修改配置信息", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult editTelCerConfig(CertificationConfig certificationConfig) {
        return telCertificationConfigService.editTelCerConfig(certificationConfig);
    }

    /**
     * 进入手机认证充值记录
     * @param map
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/telcertificationRechargeLog")
    public String telcertificationRechargeLog(ModelMap map){
        try {
            map.put("telcertification_recharge_log",null);
        } catch (Exception e) {
            log.error("获取手机充值记录,方法：telcertificationRechargeLog,错误信息",e);
        }
        return "admin/telcertification/telcertification_recharge_log";
    }

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
    @GetMapping("/telcertificationDetail/{id}")
    public String telcertificationDetail(@PathVariable Integer id, ModelMap map){
        try {
            CertificationOrder c = telCertificationService.getTelCerOrderById(id,map);
            map.put("parentId",id);
        } catch (Exception e) {
            log.error("获取手机认证详细信息,方法：telcertificationDetail,错误信息",e);
        }
        return "admin/telcertification/telcertification_detail";
    }

    /**
     * 进入手机认证成员管理列表
     * @param map
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/telcertificationChildList/{id}")
    public String telcertificationChildList(@PathVariable String id, ModelMap map){
        try {
            map.put("parentId",id);
        } catch (Exception e) {
            log.error("获取手机认证成员管理列表,方法：telcertificationChildList,错误信息",e);
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
    @PostMapping("/getTelcertificationChildList")
    @ResponseBody
    @RequiresRoles("admin")
    public AjaxResult getTelcertificationChildList(Page page,BaseRequest request){
        try{
            return telCertificationChildService.findTheChildOrder(page,request);
        }catch (Exception e){
            log.error("获取商户的所有成员 方法：getTelcertificationChildList 错误信息",e);
        }
        return AjaxResult.error("获取失败");
    }

    /**
     * 根据id删除号码认证信息
     *
     * @param id
     * @return
     */
    @ResponseBody
    @RequiresRoles("admin")
    @DeleteMapping("/deleteTelCer/{id}")
    @Log(title = "删除号码认证信息",businessType = BusinessType.DELETE,operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult deleteTelCer(@PathVariable Integer id) {
        return telCertificationService.deleteTelCer(id);
    }

    /**
     * 修改成员号码状态
     * @param certificationChildOrder
     * @return
     */
    @RequiresRoles("admin")
    @PostMapping("/editChildStatus")
    @ResponseBody
    @Log(title = "修改成员号码状态",businessType = BusinessType.UPDATE,operatorLogType = OperatorLogType.TELCERTIFICATION)
    public AjaxResult editChildStatus(CertificationChildOrder certificationChildOrder) {
        try {
            return telCertificationChildService.editChildStatus(certificationChildOrder);
        } catch (Exception e) {
            return AjaxResult.error("修改成员号码状态失败！");
        }
    }

    /**
     * 修改业务反馈
     * @param certificationChildOrder
     * @return
     */
    @RequiresRoles("admin")
    @PostMapping("/editFeedBackById")
    @ResponseBody
    @Log(title = "修改业务反馈",businessType = BusinessType.UPDATE,operatorLogType = OperatorLogType.TELCERTIFICATION)
    public AjaxResult editFeedBackById(CertificationChildOrder certificationChildOrder) {
        try {
            return telCertificationChildService.editFeedBackById(certificationChildOrder);
        } catch (Exception e) {
            return AjaxResult.error("修改业务反馈失败！");
        }
    }

    /**
     * 根据id删除号码认证成员信息
     *
     * @param id
     * @return
     */
    @ResponseBody
    @RequiresRoles("admin")
    @DeleteMapping("/deleteTelCerChild/{id}")
    @Log(title = "删除号码认证成员信息",businessType = BusinessType.DELETE,operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult deleteTelCerChild(@PathVariable Integer id) {
        return telCertificationChildService.deleteTelCerChild(id);
    }

    /**
     * 获取某条消费记录列表
     * @return
     */
    @PostMapping("/getTelcerConsumeLog")
    @ResponseBody
    @RequiresRoles("admin")
    public AjaxResult getTelcerConsumeLog(Page page,BaseRequest request){
        try{
            return telCertificationChildService.getTheTelCerCostLogList(page,request);
        }catch (Exception e){
            log.error("获取管理端消费记录列表数据 方法：getTelcerConsumeLog 错误信息",e);
        }
        return AjaxResult.error("获取数据失败");
    }


    /**
     * 获取全部充值记录
     * @return
     */
    @PostMapping("/getTelCerRechargeLogList")
    @ResponseBody
    @RequiresRoles("admin")
    public AjaxResult getTelCerRechargeLogList(Page page){
        try{
            return telCertificationService.getTelCerRechargeLogList(page);
        }catch (Exception e){
            log.error("获取全部充值记录 方法：getTelCerRechargeLogList 错误信息",e);
        }
        return AjaxResult.error("获取数据失败");
    }



}
