package com.hrtxn.ringtone.project.threenets.kedas.kedasites.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaChildOrder;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.service.KedaChildOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Author:zcy
 * Date:2019-08-14 10:04
 * Description:疑难杂单子订单控制层
 */
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
    @GetMapping("addnumber")
    public String toAddnumber() {
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
    public AjaxResult insertKedaChildOrder(KedaChildOrder kedaChildOrder) throws Exception {
        return kedaChildOrderService.insertKedaChildOrder(kedaChildOrder);
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
    public AjaxResult getPhoneInfo(@PathVariable Integer id) throws IOException {
        return kedaChildOrderService.getPhoneInfo(id);
    }

    @DeleteMapping("deleteKedaChildOrder/{id}")
    @ResponseBody
    @Log(title = "删除疑难杂单子级订单",businessType = BusinessType.DELETE,operatorLogType = OperatorLogType.KEDASITES)
    public AjaxResult deleteKedaChildOrder(@PathVariable Integer id){
        return kedaChildOrderService.deleteKedaChildOrder(id);
    }
}
