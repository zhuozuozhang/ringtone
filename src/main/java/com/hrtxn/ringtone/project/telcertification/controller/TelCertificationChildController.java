package com.hrtxn.ringtone.project.telcertification.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.system.consumelog.domain.ConsumeLog;
import com.hrtxn.ringtone.project.system.consumelog.service.ConsumeLogService;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationChildOrder;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationOrder;
import com.hrtxn.ringtone.project.telcertification.service.TelCertificationChildService;
import com.hrtxn.ringtone.project.telcertification.service.TelCertificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

/**
 * @author : yunaye
 * Date:2019-07-11 09:07
 * Description:电话认证成员控制层
 */
@Slf4j
@Controller
@RequestMapping("/telcertify")
public class TelCertificationChildController {

    @Autowired
    private TelCertificationChildService telCertificationChildService;
    @Autowired
    private TelCertificationService telCertificationService;
    @Autowired
    private ConsumeLogService consumeLogService;

    /**
     * 进入成员管理页面
     * @return
     */
    @GetMapping("/toTelMembersPage/{id}")
    public String toTelMembersPage(@PathVariable Integer id,ModelMap map){
        map.put("parentId",id);
        CertificationOrder certificationOrder = telCertificationService.getTelCerOrderById(id,map);
        map.put("childList",certificationOrder);
        return "telcertification/members";
    }

    /**
     * 进入成员管理页面
     * @return
     */
    @GetMapping("/todoTelMembersPage")
    public String todoTelMembersPage(ModelMap map){
        return "telcertification/todoMembers";
    }


    /**
     * 进入成员管理页面
     * @return
     */
    @PostMapping("/todoTelMemberList")
    @ResponseBody
    public AjaxResult todoTelMemberList(Page page,BaseRequest request){
        return telCertificationChildService.todoChildOrderList(page,request);
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
    public AjaxResult getFallDueList(Page page,BaseRequest request){
        try{
            return telCertificationChildService.getFallDueList(page,request);
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
    public AjaxResult getDueList(Page page,BaseRequest request){
        try{
            return telCertificationChildService.getDueList(page,request);
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
            return consumeLogService.getConsumeLogList(page,request);
        }catch (Exception e){
            log.error("获取号码认证子订单消费记录列表数据 方法：getTheTelCerCostLogList 错误信息",e);
        }
        return AjaxResult.error("获取数据失败");
    }

    /**
     * 添加子订单
     * @param certificationChildOrder
     * @return
     */
    @PostMapping("/insertTelCerChild")
    @ResponseBody
    @Log(title = "添加号码认证子订单",businessType = BusinessType.INSERT,operatorLogType = OperatorLogType.TELCERTIFICATION)
    public AjaxResult insertTelCerChild(CertificationChildOrder certificationChildOrder){
        try {
            return telCertificationChildService.insertTelCerChild(certificationChildOrder);
        }catch (Exception e){
            log.error("批量添加号码认证子订单 方法：insertTelCerChild 错误信息", e);
            return AjaxResult.error("保存失败");
        }
    }

    /**
     * 进入续费页面
     * @param map
     * @return
     */
    @GetMapping("/toRenewPage/{id}")
    public String toRenewPage(@PathVariable String id,ModelMap map){
        Integer idInt = Integer.parseInt(id);
        CertificationChildOrder childOrder = telCertificationChildService.getTelCerChildById(idInt,map);
        map.put("phoneNum",childOrder.getTelChildOrderPhone());
        CertificationOrder certificationOrder = telCertificationService.getTelCerOrderById(childOrder.getParentOrderId(),map);
        return "telcertification/renew";
    }

    @PostMapping("/opening")
    @ResponseBody
    @Log(title = "开通子订单订单", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.TELCERTIFICATION)
    public AjaxResult opening( String id){

        return telCertificationChildService.opening(Integer.parseInt(id));
    }

    /**
     * 添加续费记录
     * @param consumeLog
     * @return
     */
    @PostMapping("/addRenewConsumeLog")
    @ResponseBody
    @Log(title = "添加续费记录", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.TELCERTIFICATION)
    public AjaxResult addRenewConsumeLog(ConsumeLog consumeLog,ModelMap map) throws Exception {
        if(StringUtils.isNotNull(consumeLog) && StringUtils.isNotEmpty(consumeLog.getUserTel())){
            return consumeLogService.addRenewConsumeLog(consumeLog);
        }
        return AjaxResult.error("参数格式错误！");

    }

    /**
     * 验证成员号码是否重复
     *
     * @param phoneNum
     * @return
     */
    @PostMapping("/verificationChildNum")
    @ResponseBody
    public AjaxResult verificationChildNum(String phoneNum){
        return telCertificationChildService.isRepetitionByChildNum(phoneNum);
    }
}
