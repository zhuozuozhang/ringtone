package com.hrtxn.ringtone.project.system.ring.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.system.ring.service.RingService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Author:zcy
 * Date:2019-07-23 17:10
 * Description:管理端铃音管理
 */
@Controller
public class RingController {

    @Autowired
    private RingService ringService;


    @GetMapping("/admin/ringList")
    @RequiresRoles("admin")
    public String toRingListPage(){
        return "admin/ring/ring_list";
    }

    /**
     * 获取铃音列表
     * @param page
     * @return
     */
    @ResponseBody
    @RequiresRoles("admin")
    @PostMapping("/admin/getRingList")
    public AjaxResult getRingList(Page page){
        return ringService.getRingList(page);
    }
}
