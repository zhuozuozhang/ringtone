package com.hrtxn.ringtone.project.threenets.kedas.kedasites.controller;

import com.hrtxn.ringtone.common.api.KedaApi;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.DateUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaOrder;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.service.KedaOrderService;
import org.aspectj.weaver.loadtime.Aj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

/**
 * Author:zcy
 * Date:2019-08-14 13:52
 * Description:疑难杂单父级订单
 */
@Controller
@RequestMapping("/threenets/clcy/")
public class KedaOrderController {

    @Autowired
    private KedaOrderService kedaOrderService;

    /**
     * 获取父级订单列表
     *
     * @param page
     * @param baseRequest
     * @return
     */
    @ResponseBody
    @PostMapping("getKeDaOrderList")
    public AjaxResult getKeDaOrderList(Page page, BaseRequest baseRequest) {
        KedaApi kedaApi = new KedaApi();
        kedaApi.editGroup(null);
        return kedaOrderService.getKeDaOrderList(page, baseRequest);
    }

    /**
     * 修改父级订单商户名称
     *
     * @param kedaOrder
     * @return
     */
    @PutMapping("updateCompanyName")
    @ResponseBody
    @Log(title = "修改商户名称", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.KEDASITES)
    public AjaxResult updateCompanyName(KedaOrder kedaOrder) {
        return kedaOrderService.updateCompanyName(kedaOrder);
    }

    /**
     * 跳转到号码管理页
     *
     * @param id
     * @param map
     * @return
     */
    @GetMapping("toNumberList/{id}/{name}")
    public String toNumberList(@PathVariable Integer id, @PathVariable String name, ModelMap map) {
        //kedaOrderService.updateBusinessStatus(id,name);
        if (StringUtils.isNotNull(id) && id != 0) {
            map.put("id", id);
            map.put("name", name);
            return "threenets/kedas/kedasites/merchants/number_list";
        }
        return null;
    }

    /**
     * 跳转到添加渠道商页面
     *
     * @return
     */
    @GetMapping("toAddmerchants")
    public String toAddmerchants() {
        return "threenets/kedas/kedasites/merchants/addmerchants";
    }

    /**
     * 添加父级订单
     *
     * @param kedaOrder
     * @return
     * @throws Exception
     */
    @PostMapping("addKedaOrder")
    @ResponseBody
    @Log(title = "添加疑难杂单父级订单", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.KEDASITES)
    public AjaxResult addKedaOrder(KedaOrder kedaOrder) throws Exception {
        return kedaOrderService.addKedaOrder(kedaOrder);
    }

    /**
     * 疑难杂单父级订单删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("deleteKedaOrder")
    @ResponseBody
    @Log(title = "删除疑难杂单父级订单", businessType = BusinessType.DELETE, operatorLogType = OperatorLogType.KEDASITES)
    public AjaxResult deleteKedaOrder(Integer id) {
        return kedaOrderService.deleteKedaOrder(id);
    }

    /**
     * 验证商户名称是否重复
     *
     * @param companyName
     * @return
     */
    @PostMapping("isItRedundantByName")
    @ResponseBody
    public AjaxResult isItRedundantByName(String companyName){
        Boolean itRedundantByName = kedaOrderService.isItRedundantByName(companyName);
        if (itRedundantByName) {
            return AjaxResult.error("商户名称不允许重复！");
        } else {
            return AjaxResult.success(DateUtils.getTime(), "");
        }
    };

    /**
     * 验证手机号
     *
     * @param tels
     * @return
     */
    @PostMapping("formatPhoneNumber")
    @ResponseBody
    public AjaxResult formatPhoneNumber(String tels,Integer orderId){
        return kedaOrderService.formatPhoneNumber(tels,orderId);
    }

    /**
     * 修改信息
     *
     * @param order
     * @return
     */
    @PostMapping("updateKedaOrderInfo")
    @ResponseBody
    public AjaxResult updateKedaOrderInfo(KedaOrder order){
        return kedaOrderService.updateKedaOrderInfo(order);
    }
}
