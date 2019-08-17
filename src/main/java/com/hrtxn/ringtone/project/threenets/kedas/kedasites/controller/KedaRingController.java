package com.hrtxn.ringtone.project.threenets.kedas.kedasites.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.service.KedaRingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

/**
 * Author:zcy
 * Date:2019-08-14 17:32
 * Description:疑难杂单铃音
 */
@Controller
@RequestMapping("/threenets/clcy/")
public class KedaRingController {

    @Autowired
    private KedaRingService kedaRingService;

    /**
     * 跳转到铃音管理页
     *
     * @param id
     * @param name
     * @param map
     * @return
     */
    @GetMapping("toRingList/{id}/{name}")
    public String toRingList(@PathVariable Integer id, @PathVariable String name, ModelMap map) {
        map.put("id", id);
        map.put("name", name);
        return "threenets/kedas/kedasites/merchants/ring_list";
    }

    /**
     * 获取铃音列表
     *
     * @param page
     * @param baseRequest
     * @return
     */
    @PostMapping("getKedaRingList")
    @ResponseBody
    public AjaxResult getKedaRingList(Page page, BaseRequest baseRequest) {
        return kedaRingService.getKedaRingList(page, baseRequest);
    }

    /**
     * 跳转到添加铃音页面
     *
     * @return
     */
    @GetMapping("toAddring")
    public String toAddring() {
        return "threenets/kedas/kedasites/merchants/addring";
    }

    /**
     * 跳转到铃音设置页面
     *
     * @return
     */

    @GetMapping("toRingListSet")
    public String toRingListSet() {
        return "threenets/kedas/kedasites/merchants/ring_list_set";
    }

}
