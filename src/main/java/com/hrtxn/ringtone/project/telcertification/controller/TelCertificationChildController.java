package com.hrtxn.ringtone.project.telcertification.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationChildOrder;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationConsumeLog;
import com.hrtxn.ringtone.project.telcertification.service.TelCertificationChildService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.loadtime.Aj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:lile
 * Date:2019-07-11 09:07
 * Description:电话认证成员控制层
 */
@Slf4j
@Controller
@RequestMapping("/telcertify")
public class TelCertificationChildController {

    @Autowired
    private TelCertificationChildService telCertificationChildService;

    /**
     * 进入成员管理页面
     * @return
     */
    @GetMapping("/toTelMembersPage/{id}")
    public String toTelMembersPage(@PathVariable String id,ModelMap map){
        map.put("memberId",id);
        return "telcertification/members";
    }

    /**
     * 获取所有成员 或者 获取商户的所有成员
     * @param page
     * @param request
     * @return
     */
    @PostMapping("/getTelCerMembersList")
    @ResponseBody
    public AjaxResult getTelCerMembersList(Page page,BaseRequest request){
        List<CertificationChildOrder> list = new ArrayList<CertificationChildOrder>();
        try{
            int totalCount = telCertificationChildService.getCount();
            list = telCertificationChildService.findAllChildOrder(page,request);
            return AjaxResult.success(list,"查询成功",totalCount);
        }catch (Exception e){
            log.error("获取号码认证子订单列表数据 方法：getTelCerMembersList 错误信息",e);
        }
        return null;
    }

    /**
     * 获取即将到期号码 以及 根据成员号码查询
     * @param page
     * @param request
     * @return
     */
    @PostMapping("/getFallDueList")
    @ResponseBody
    @Log(title = "获取即将到期号码",operatorLogType = OperatorLogType.TELCERTIFICATION)
    public AjaxResult getFallDueList(Page page, BaseRequest request){
        List<CertificationChildOrder> list = null;
        try{
//            int totalCount = telCertificationChildService.getFallDueCount();
            int totalCount = telCertificationChildService.getCount();
            list = telCertificationChildService.getFallDueList(page,request);
            return AjaxResult.success(list,"查询成功",totalCount);
        }catch (Exception e){
            log.error("获取即将到期号码 方法：getFallDueList 错误信息",e);
        }
        return AjaxResult.error("获取失败");
    }

    /**
     * 获取到期号码 以及 根据成员号码查询
     * @param page
     * @param request
     * @return
     */
    @PostMapping("/getDueList")
    @ResponseBody
    @Log(title = "获取到期号码",operatorLogType = OperatorLogType.TELCERTIFICATION)
    public AjaxResult getDueList(Page page, BaseRequest request){
        List<CertificationChildOrder> list = null;
        try{
//            int totalCount = telCertificationChildService.getFallDueCount();
            int totalCount = telCertificationChildService.getCount();
            list = telCertificationChildService.getDueList(page,request);
            return AjaxResult.success(list,"查询成功",totalCount);
        }catch (Exception e){
            log.error("获取到期号码 方法：getDueList 错误信息",e);
        }
        return AjaxResult.error("获取失败");
    }

    /**
     * 进入费用支出记录页面
     * @return
     */
    @GetMapping("/toTelCostPage")
    public String toTelCostPage(){
        return "telcertification/cost";
    }

    @PostMapping("/getTelCerCostLogList")
    @ResponseBody
    public AjaxResult getTelCerCostLogList(){
        List<CertificationConsumeLog> list = null;
        try{
            int totalCount = telCertificationChildService.getConsumeLogCount();
            list = telCertificationChildService.findAllConsumeLog();
            return AjaxResult.success(list,"查询成功",totalCount);
        }catch (Exception e){
            log.error("获取号码认证子订单消费记录列表数据 方法：getTelCerCostLogList 错误信息",e);
        }
        return null;
    }



}
