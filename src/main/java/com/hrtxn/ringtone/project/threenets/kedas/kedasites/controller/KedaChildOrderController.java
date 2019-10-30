package com.hrtxn.ringtone.project.threenets.kedas.kedasites.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaChildOrder;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.service.KedaChildOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Author:zcy
 * Date:2019-08-14 10:04
 * Description:疑难杂单子订单控制层
 */
@Slf4j
@Controller
@RequestMapping("/threenets/clcy/")
public class KedaChildOrderController {

    @Autowired
    private KedaChildOrderService kedaChildOrderService;

    /**
     * 获取子订单列表
     *
     * @param page
     * @param baseRequest
     * @return
     */
    @PostMapping("getKedaChidList")
    @ResponseBody
    public AjaxResult getKedaChidList(Page page, BaseRequest baseRequest) {
        return kedaChildOrderService.getKedaChidList(page, baseRequest);
    }

    /**
     * 跳转到添加子账号页面
     *
     * @return
     */
    @GetMapping("addnumber/{orderId}")
    public String toAddnumber(@PathVariable Integer orderId, ModelMap map) {
        map.put("orderId", orderId);
        return "threenets/kedas/kedasites/merchants/addnumber";
    }

    /**
     * 添加疑难杂单子级订单
     *
     * @param kedaChildOrder
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("insertKedaChildOrder")
    @Log(title = "添加疑难杂单子级订单", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.KEDASITES)
    public AjaxResult insertKedaChildOrder(KedaChildOrder kedaChildOrder){
        long startTime = System.currentTimeMillis();//获取当前时间
        AjaxResult result = new AjaxResult();
        try {
            result = kedaChildOrderService.insertKedaChildOrder(kedaChildOrder);
        } catch (Exception e) {
            log.error("添加疑难杂单子级订单失败------>", e);
        } finally {
            log.info("添加疑难杂单子级订单，共耗时 -- >" + (System.currentTimeMillis() - startTime) + "ms");
        }
        return result;
    }

    /**
     * 批量添加疑难杂单子级订单
     *
     * @param kedaChildOrder
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("batchInsertKedaChildOrder")
    @Log(title = "批量添加疑难杂单子级订单", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.KEDASITES)
    public AjaxResult batchInsertKedaChildOrder(KedaChildOrder kedaChildOrder){
        long startTime = System.currentTimeMillis();//获取当前时间
        AjaxResult result = new AjaxResult();
        try {
            result = kedaChildOrderService.batchInsertKedaChildOrderNew(kedaChildOrder);
        } catch (Exception e) {
            log.error("批量添加疑难杂单子级订单失败------>", e);
        } finally {
            log.info("批量添加疑难杂单子级订单，共耗时 -- >" + (System.currentTimeMillis() - startTime) + "ms");
        }
        return result;
    }

    /**
     * 刷新用户信息
     *
     * @param id
     * @return
     * @throws IOException
     */
    @PostMapping("getPhoneInfo/{id}")
    @ResponseBody
    @Log(title = "疑难杂单刷新子订单", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.KEDASITES)
    public AjaxResult getPhoneInfo(@PathVariable Integer id){
        long startTime = System.currentTimeMillis();//获取当前时间
        AjaxResult result = new AjaxResult();
        try {
            result = kedaChildOrderService.getPhoneInfo(id);
        } catch (IOException e) {
            log.error("刷新科大成员列表失败------>", e);
        } finally {
            log.info("刷新科大成员列表，共耗时 -- >" + (System.currentTimeMillis() - startTime) + "ms");
        }
        return result;
    }

    /**
     * 刷新用户信息
     *
     * @param id
     * @return
     * @throws IOException
     */
    @PostMapping("listPhoneInfo/{id}")
    @ResponseBody
    @Log(title = "疑难杂单刷新子订单", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.KEDASITES)
    public AjaxResult listPhoneInfo(@PathVariable Integer id){
        long startTime = System.currentTimeMillis();//获取当前时间
        AjaxResult result = new AjaxResult();
        try {
            result = kedaChildOrderService.listPhoneInfo(id);
        } catch (IOException e) {
            log.error("批量刷新科大成员列表失败------>", e);
        } finally {
            log.info("批量刷新科大成员列表，共耗时 -- >" + (System.currentTimeMillis() - startTime) + "ms");
        }
        return result;
    }

    /**
     * 删除疑难杂单子级订单
     *
     * @param id
     * @return
     */
    @DeleteMapping("deleteKedaChildOrder/{id}")
    @ResponseBody
    @Log(title = "删除疑难杂单子级订单", businessType = BusinessType.DELETE, operatorLogType = OperatorLogType.KEDASITES)
    public AjaxResult deleteKedaChildOrder(@PathVariable Integer id) {
        return kedaChildOrderService.deleteKedaChildOrder(id);
    }

    /**
     * 获取铃音设置子订单列表
     *
     * @param orderId
     * @return
     */
    @PostMapping("getKedaChildSettingList")
    @ResponseBody
    public AjaxResult getKedaChildSettingList(Integer orderId) {
        return kedaChildOrderService.getKedaChildSettingList(orderId);
    }

    /**
     * 疑难杂单子订单设置铃音
     *
     * @return
     */
    @ResponseBody
    @PutMapping("setKedaChidOrder")
    @Log(title = "疑难杂单子订单设置铃音", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.KEDASITES)
    public AjaxResult setKedaChidOrder(Integer ringId, String linkTel, String employeeId, Integer childOrderId) throws IOException {
        return kedaChildOrderService.setKedaChidOrder(ringId, linkTel, employeeId, childOrderId);
    }
}
