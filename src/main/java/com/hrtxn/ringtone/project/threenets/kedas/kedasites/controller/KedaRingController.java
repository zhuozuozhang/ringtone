package com.hrtxn.ringtone.project.threenets.kedas.kedasites.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaOrder;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaRing;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.mapper.KedaOrderMapper;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.service.KedaRingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    @Log(title = "铃音刷新", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.KEDASITES)
    public AjaxResult getKedaRingList(Page page, BaseRequest baseRequest) throws IOException {
        return kedaRingService.getKedaRingList(page, baseRequest);
    }

    /**
     * 跳转到添加铃音页面
     *
     * @return
     */
    @GetMapping("toAddring/{orderId}/{name}")
    public String toAddring(@PathVariable Integer orderId, @PathVariable String name,ModelMap m) {
        m.put("orderId", orderId);
        m.put("name", name);
        return "threenets/kedas/kedasites/merchants/addring";
    }

    /**
     * 添加铃音订单
     *
     * @param kedaRing
     * @param protocolFile
     * @return
     */
    @PostMapping("addKedaRing")
    @ResponseBody
    @Log(title = "科大铃音上传", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.KEDASITES)
    public AjaxResult addKedaRing(KedaRing kedaRing, @RequestParam("ringfile") MultipartFile protocolFile) throws Exception {
        return kedaRingService.addKedaRing(kedaRing, protocolFile);
    }

    /**
     * 跳转到铃音设置页面
     *
     * @return
     */
    @GetMapping("toRingListSet/{id}/{orderId}/{name}")
    public String toRingListSet(@PathVariable Integer id, @PathVariable Integer orderId, @PathVariable String name, ModelMap map) {
        map.put("id", id);
        map.put("orderId", orderId);
        map.put("name", name);
        return "threenets/kedas/kedasites/merchants/ring_list_set";
    }

    /**
     * 疑难杂单删除铃音
     *
     * @param id
     * @return
     */
    @DeleteMapping("deleteRing/{id}")
    @ResponseBody
    @Log(title = "疑难杂单删除铃音", businessType = BusinessType.DELETE, operatorLogType = OperatorLogType.KEDASITES)
    public AjaxResult deleteRing(@PathVariable Integer id) {
        return kedaRingService.deleteRing(id);
    }

    /**
     * 疑难杂单铃音设置子订单
     *
     * @param phones
     * @param orderId
     * @param id
     * @return
     * @throws IOException
     */
    @ResponseBody
    @PutMapping("setRing")
    @Log(title = "疑难杂单铃音设置子订单", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.KEDASITES)
    public AjaxResult setRing(String phones, Integer orderId, Integer id) throws IOException {
        return kedaRingService.setRing(phones, orderId, id);
    }

    /**
     * 获取设置铃音列表
     *
     * @param orderId
     * @return
     */
    @ResponseBody
    @PostMapping("getKedaRingSetting/{orderId}")
    public AjaxResult getKedaRingSetting(@PathVariable Integer orderId) {
        try {
            return kedaRingService.getKedaRingSetting(orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
