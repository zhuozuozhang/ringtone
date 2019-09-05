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
import com.hrtxn.ringtone.project.telcertification.domain.CertificationRequest;
import com.hrtxn.ringtone.project.telcertification.service.TelCertificationChildService;
import com.hrtxn.ringtone.project.telcertification.service.TelCertificationConfigService;
import com.hrtxn.ringtone.project.telcertification.service.TelCertificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author:yuanye
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
    private TelCertificationConfigService telCertificationConfigService;
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
    public String toTelMerchantsPage(ModelMap map){
        Page page = new Page();
        page.setPage(1);
        page.setPagesize(4);
        AjaxResult list = telCertificationConfigService.getAllConfig(page,map);
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
    public AjaxResult getTelCerOrderList(Page page, BaseRequest request,ModelMap map){
        try{
            return telCertificationService.findAllTelCertification(page,request,map);
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
    @GetMapping("/toTeldetailsOnePage/{id}")
    public String toTeldetailsOnePage(@PathVariable Integer id, ModelMap map){
        CertificationChildOrder telcerChild = telCertificationChildService.getTelCerChildById(id,map);
        return "telcertification/details_one";
    }

    /**
     * 进入添加号码认证商户
     * @return
     */
    @GetMapping("/toAddTelCerMerchantPage")
    public String toAddTelCerMerchantPage(ModelMap map){
        Page page = new Page();
        page.setPage(1);
        page.setPagesize(4);
        AjaxResult list = telCertificationConfigService.getAllConfig(page,map);
        return "telcertification/addTelCerMerchant";
    }

    /**
     * 进入修改商户页面
     * @param id
     * @param map
     * @return
     */
    @ResponseBody
    @PostMapping("/toTelEditPage")
    public AjaxResult toTelEditPage(Integer id,ModelMap map){
        map.put("id",id);
        CertificationOrder certificationOrder = telCertificationService.getTelCerOrderById(id,map);
        return AjaxResult.success(certificationOrder,"商户信息回显");
    }

    /**
     * 修改商户信息
     * @param certificationOrder
     * @return
     */
    @PostMapping("/editTelCerOrderById")
    @ResponseBody
    @Log(title = "修改商户信息", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.TELCERTIFICATION)
    public AjaxResult editTelCerOrderById(CertificationOrder certificationOrder) {
        return telCertificationService.update(certificationOrder);
    }

    /**
     * 获取号码认证配置全部信息
     * @param page
     * @param map
     * @return
     */
    @PostMapping("/getAllConfig")
    @ResponseBody
    public AjaxResult getAllConfig(Page page, ModelMap map){
        page = new Page();
        page.setPage(1);
        page.setPagesize(10);
        return telCertificationConfigService.getAllConfig(page,map);
    }

    /**
     * 新增商户
     * @param certificationProduct
     * @return
     */
    @PostMapping("/addTelCertifyOrder")
    @ResponseBody
    @Log(title = "新增商户", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.TELCERTIFICATION)
    public AjaxResult addTelCertifyOrder(CertificationRequest certificationProduct){
        try{
            return telCertificationService.addTelCertifyOrder(certificationProduct);
        }catch (Exception e){
            log.error("保存商户（号码认证订单） 方法：addTelCertifyOrder 错误信息", e);
            return AjaxResult.error("保存失败");
        }
    }

    /**
     * 验证商户名称是否重复
     *
     * @param telCompanyName
     * @return
     */
    @PostMapping("/verificationName")
    @ResponseBody
    public AjaxResult verificationName(String telCompanyName){
        return telCertificationService.isRepetitionByName(telCompanyName);
    }

}
