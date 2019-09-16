package com.hrtxn.ringtone.project.system.numcertification.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.numcertification.domain.NumcertificationPrice;
import com.hrtxn.ringtone.project.numcertification.service.NumCertificateionPriceService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

/**
 * 400号码价格配置
 *
 * @Author zcy
 * @Date 2019-08-31 17:07
 */
@Controller
@RequestMapping("/admin")
public class NumCertificateionController {

    @Autowired
    private NumCertificateionPriceService numCertificateionPriceService;

    /**
     * 跳转价格设置页面
     *
     * @author zcy
     * @date 2019-8-31 17:11
     */
    @RequiresRoles("admin")
    @GetMapping("/set_price")
    public String setPrice() {
        return "admin/numcertification/price_list.html";
    }
    /**
     * 获取价格配置列表
     *
     * @author zcy
     * @date 2019-8-31 17:49
     */
    @RequiresRoles("admin")
    @PostMapping("/getNumPriceList")
    @ResponseBody
    public AjaxResult getNumPriceList(Page page) {
        return numCertificateionPriceService.getNumPriceList(page);
    }
    /**
     * 跳转到价格修改页面
     *
     * @author zcy
     * @date 2019-8-31 17:50
     */
    @RequiresRoles("admin")
    @GetMapping("/toUpdatePricePage/{id}")
    public String toUpdatePricePage(@PathVariable Integer id, ModelMap map) {
        map.put("id", id);
        // 根据ID获取价格信息
        NumcertificationPrice numcertificationPrice = numCertificateionPriceService.selectById(id);
        map.put("numcertificationPrice", numcertificationPrice);
        return "admin/numcertification/price_update";
    }
    /**
     * 修改价格配置信息
     *
     * @author zcy
     * @date 2019-9-2 13:32
     */
    @PutMapping("/updateNumcertificationPrice")
    @RequiresRoles("admin")
    @ResponseBody
    public AjaxResult updateNumcertificationPrice(NumcertificationPrice numcertificationPrice) {
        return numCertificateionPriceService.updateNumcertificationPrice(numcertificationPrice);
    }

}
