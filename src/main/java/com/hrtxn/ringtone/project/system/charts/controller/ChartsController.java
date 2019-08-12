package com.hrtxn.ringtone.project.system.charts.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.project.system.charts.service.ChartsService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;

/**
 * Author:zcy
 * Date:2019-08-12 9:13
 * Description:管理端 数据统计
 */
@Controller
public class ChartsController {

    @Autowired
    private ChartsService chartsService;

    /**
     * 跳转到按日统计页面
     *
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/admin/toMonth")
    public String toMonth() {
        return "admin/charts/month";
    }

    /**
     * 跳转到按月统计页面
     *
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/admin/toYear")
    public String toYear() {
        return "admin/charts/year";
    }

    /**
     * 获取数据统计数据
     *
     * @param start
     * @param operate
     * @param type
     * @return
     */
    @RequiresRoles("admin")
    @PostMapping("/admin/echartsData")
    @ResponseBody
    public AjaxResult echartsData(String start, Integer operate, Integer type) {
        try {
            return chartsService.echartsData(start, operate, type);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
