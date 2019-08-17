package com.hrtxn.ringtone.project.threenets.kedas.kedasites.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.service.KedaChildOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

}
