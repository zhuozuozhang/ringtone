package com.hrtxn.ringtone.project.telcertification.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.system.notice.domain.Notice;
import com.hrtxn.ringtone.project.system.notice.service.NoticeService;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationChildOrder;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationOrder;
import com.hrtxn.ringtone.project.telcertification.service.TelCertificationChildService;
import com.hrtxn.ringtone.project.telcertification.service.TelCertificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:lile
 * Date:2019-07-10 16:07
 * Description:电话认证控制器
 */
@Slf4j
@Controller
@RequestMapping("/telcertify")
public class TelCertificationController {

    private final String MODULE_CERTIFICATION = "1";

    @Autowired
    private TelCertificationService telCertificationService;
    @Autowired
    private TelCertificationChildService telCertificationChildService;
    @Autowired
    private NoticeService noticeService;

    /**
     * 进入号码认证页面
     * @return
     */
    @GetMapping("/toTelIndexPage")
    public String toTelOrderIndexPage(){
        return "telcertification/index";
    }

    /**
     * 进入号码认证公告页面
     * @return
     */
    @GetMapping("/toTelNoticePage")
    public String toTelNoticePage(ModelMap map){
        try{
            List<Notice> noticeList = noticeService.findNoticeListByModul(MODULE_CERTIFICATION);
            map.put("noticeList", noticeList);
        }catch (Exception e){
            log.error("获取公告列表 方法：toTelNoticePage 错误信息",e);
        }
        return "telcertification/notice";
    }

    /**
     * 进入管理员页面
     * @return
     */
    @GetMapping("/toTelAdminPage")
    public String toTelAdminPage(){
        return "telcertification/admin";
    }

    /**
     * 进入号码认证订购页面
     * @return
     */
    @GetMapping("/toTelChoosePage")
    public String toTelChoosePage(){
        return "telcertification/choose";
    }

    /**
     * 进入订单列表页面
     * @return
     */
    @GetMapping("/toTelMerchantsPage")
    @Log(title = "订单管理页面",operatorLogType = OperatorLogType.TELCERTIFICATION)
    public String toTelMerchantsPage(){
        return "telcertification/merchants";
    }


    /**
     * 获取号码认证订单
     * @param page
     * @param request
     * @return
     */
    @PostMapping("/getTelCerOrderList")
    @ResponseBody
    @Log(title = "获取号码认证订单",operatorLogType = OperatorLogType.TELCERTIFICATION)
    public AjaxResult getTelCerOrderList(Page page, BaseRequest request){
        List<CertificationOrder> list = new ArrayList<CertificationOrder>();
        try{

            return telCertificationService.findAllTelCertification(page,request);
        }catch (Exception e){
            log.error("获取号码认证订单列表数据 方法：getTelCerOrderList 错误信息",e);
        }
        return AjaxResult.error("获取失败");
    }

    /**
     * 进入我的订单查看详情页面
     * @return
     */
    @GetMapping("/toTeldetailsPage/{id}")
    public String toTeldetailsPage(@PathVariable Integer id, ModelMap map){
        CertificationOrder telcerOrder = telCertificationService.getTelCerOrderById(id,map);
        return "telcertification/details";
    }


    /**
     * 进入即将到期号码查看详情页面
     * @return
     */
    @GetMapping("/toTeldetails_onePage/{id}")
    public String toTeldetails_onePage(@PathVariable Integer id, ModelMap map){
        CertificationChildOrder telcerChild = telCertificationChildService.getTelCerChildById(id,map);
        return "telcertification/details_one";
    }


    /**
     * 新增商户
     * @param certificationOrder
     * @return
     */
    @PostMapping("/insertTelCertifyOrder")
    @ResponseBody
    @Log(title = "新增商户", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.TELCERTIFICATION)
    public AjaxResult insertTelCertifyOrder(CertificationOrder certificationOrder){
        try{
            return telCertificationService.insertTelCertifyOrder(certificationOrder);
        }catch (Exception e){
           log.error("保存商户（号码认证订单） 方法：insertTelCertifyOrder 错误信息", e);
           return AjaxResult.error("保存失败");
        }
    }
}
