package com.hrtxn.ringtone.project.numcertification.controller;

import com.hrtxn.ringtone.common.api.NumApi;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.numcertification.domain.FourcertificationOrder;
import com.hrtxn.ringtone.project.numcertification.domain.NumOrder;
import com.hrtxn.ringtone.project.numcertification.domain.NumcertificationOrder;
import com.hrtxn.ringtone.project.numcertification.service.FourCertificationService;
import com.hrtxn.ringtone.project.numcertification.service.NumCertificationService;
import com.hrtxn.ringtone.project.system.area.domain.Area;
import com.hrtxn.ringtone.project.system.area.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @param
 * @Author zcy
 * @Date 2019-08-29 10:01
 * @return
 */
@RequestMapping("/400")
@Controller
public class NumCertificationController {

    @Autowired
    private NumCertificationService numCertificationService;

    @Autowired
    private FourCertificationService fourCertificationService;

    @Autowired
    private AreaService areaService;

    /**
     * 跳转400首页
     *
     * @author zcy
     * @date 2019-8-29 10:40
     */
    @GetMapping("/toIndex")
    public String toIndex() {
        return "400/index";
    }
    /**
     * 获取数据
     *
     * @author zcy
     * @date 2019-8-29 17:53
     */
    @PostMapping("/selectData")
    @ResponseBody
    public AjaxResult selectData(Page page, NumOrder numOrder) {
        try {
            return numCertificationService.selectData(page, numOrder);
        } catch (IOException e) {
            return AjaxResult.error(e.getMessage());
        }
    }
    /**
     * 跳转订单预占页面
     *
     * @author zcy
     * @date 2019-8-29 17:56
     */
    @GetMapping("/toOrder/{agentCost}/{phoneNum}")
    public String toOrder(@PathVariable String agentCost, @PathVariable String phoneNum, ModelMap map) {
        String provider = null;
        String s = phoneNum.substring(0, 4);
        if ("4001".equals(s) || "4007".equals(s)) {
            provider = "移动";
        }
        if ("4000".equals(s) || "4006".equals(s)) {
            provider = "联通";
        }
        if ("4008".equals(s) || "4009".equals(s)) {
            provider = "电信";
        }
        List<Area> areaList = areaService.queryAreaByCons("province","0");
        map.put("agentCost", agentCost);
        map.put("applyNumber", phoneNum);
        map.put("provider", provider);
        map.put("areaList",areaList);
        return "400/order";
    }
    /**
     * 创建订单
     *
     * @author zcy
     * @date 2019-8-30 11:40
     */
    @PostMapping("/order")
    @ResponseBody
    public AjaxResult order(FourcertificationOrder fourcertificationOrder) {
          return fourCertificationService.preoccupation(fourcertificationOrder);
    }

    /**
     * 申请模板
     *
     * @author zcy
     * @date 2019-8-30 11:40
     */
    @PostMapping("/perfect")
    @ResponseBody
    public AjaxResult perfect(FourcertificationOrder fourcertificationOrder) {
        return fourCertificationService.perfect(fourcertificationOrder);
    }

    /**
     * 申请模板
     *
     * @author zcy
     * @date 2019-8-30 11:40
     */
    @PostMapping("/commit")
    @ResponseBody
    public AjaxResult commit(FourcertificationOrder fourcertificationOrder) {
        return fourCertificationService.commit(fourcertificationOrder);
    }

    /**
     * 跳转到
     *
     * @author zcy
     * @date 2019-9-2 14:03
     */
    @GetMapping("/commitFile/{orderId}")
    public String commitFile(@PathVariable Long orderId, ModelMap map) {
        map.put("orderId", orderId);

        FourcertificationOrder fourcertificationOrder = fourCertificationService.selectByPrimaryKey(orderId);
        map.put("fourcertificationOrder", fourcertificationOrder);
            return "/400/order_commitFile";
    }


    /**
     * 跳转到
     *
     * @author zcy
     * @date 2019-9-2 14:03
     */
    @GetMapping("/setMeal/{orderId}")
    public String setMeal(@PathVariable Long orderId, ModelMap map) {
        map.put("orderId", orderId);

        FourcertificationOrder fourcertificationOrder = fourCertificationService.selectByPrimaryKey(orderId);
        map.put("fourcertificationOrder", fourcertificationOrder);
        return "/400/order_next";
    }


    /**
     * 申请模板
     *
     * @author zcy
     * @date 2019-8-30 11:40
     */
    @PostMapping("/setMealSave")
    @ResponseBody
    public AjaxResult setMealSave(FourcertificationOrder fourcertificationOrder) {
        return fourCertificationService.setMealSave(fourcertificationOrder);
    }

    /**
     * 跳转到订单列表
     *
     * @author zcy
     * @date 2019-9-2 14:03
     */
    @GetMapping("/next/{orderId}")
    public String next(@PathVariable Long orderId, ModelMap map) {
        map.put("orderId", orderId);

        FourcertificationOrder fourcertificationOrder = fourCertificationService.selectByPrimaryKey(orderId);
        map.put("fourcertificationOrder", fourcertificationOrder);
        return "/400/order_perfect";
    }
    /***
     * 跳转到订单列表
     *
     * @author zcy
     * @date 2019-8-30 15:50
     */
    @GetMapping("/order_list")
    public String order_list() {
        return "400/order_list";
    }
    /**
     * 获取订单列表
     *
     * @author zcy
     * @date 2019-9-3 13:50
     */
    @PostMapping("/selectOrder")
    @ResponseBody
    public AjaxResult selectOrder(Page page) {
        return fourCertificationService.selectOrder(page);
    }
    /**
     * 跳转到订单详情页
     *
     * @author zcy
     * @date 2019-9-3 13:53
     */
    @GetMapping("/toOrderDetail/{id}")
    public String toOrderDetail(@PathVariable Integer id, ModelMap map) {
        // 根据ID获取订单信息
        NumcertificationOrder numcertificationOrder = numCertificationService.selectById(id);
        map.put("numcertificationOrder",numcertificationOrder);
        return "400/order_details";
    }



}
