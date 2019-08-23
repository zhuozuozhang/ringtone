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
        map.put("parentId",id);
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
        try{
            return telCertificationChildService.findTheChildOrder(page,request);
        }catch (Exception e){
            log.error("获取号码认证子订单列表数据 方法：getTelCerMembersList 错误信息",e);
        }
        return AjaxResult.error("获取失败");
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
            list = telCertificationChildService.getFallDueList(page,request);
            int fallDueCount = list.size();
            return AjaxResult.success(list,"查询成功",fallDueCount);
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
            list = telCertificationChildService.getDueList(page,request);
            int dueCount = list.size();
            return AjaxResult.success(list,"查询成功",dueCount);
        }catch (Exception e){
            log.error("获取到期号码 方法：getDueList 错误信息",e);
        }
        return AjaxResult.error("获取失败");
    }

    /**
     * 进入费用支出记录页面
     * @return
     */
    @GetMapping("/toTelCostPage/{phoneNum}")
    public String toTelCostPage(@PathVariable String phoneNum,ModelMap map){
        map.put("phoneNum",phoneNum);
        return "telcertification/cost";
    }

    /**
     * 显示费用支出列表
     * @return
     */
    @PostMapping("/getTelCerCostLogList")
    @ResponseBody
    public AjaxResult getTheTelCerCostLogList(Page page,BaseRequest request){
        try{
            return telCertificationChildService.getTheTelCerCostLogList(page,request);
        }catch (Exception e){
            log.error("获取号码认证子订单消费记录列表数据 方法：getTelCerCostLogList 错误信息",e);
        }
        return AjaxResult.error("获取数据失败");
    }
}
