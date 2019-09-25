package com.hrtxn.ringtone.project.system.phonePlace.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.system.phonePlace.domain.PhonePlace;
import com.hrtxn.ringtone.project.system.phonePlace.service.PhonePlaceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

/**
 * Author:zcy
 * Date:2019-07-09 16:26
 * Description:公告管理
 */
@Controller
@Slf4j
public class PhonePlaceController {

    @Autowired
    private PhonePlaceService PhonePlaceService;

    /**
     * 获取手机号码归属地列
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/phonePlace/toPhonePlacePage")
    public String toPhonePlacePage(ModelMap map){
        try {
        } catch (Exception e) {
            log.error("获取手机号码归属地列表 方法：toPhonePlacePage 错误信息：",e);
        }
        return "admin/phonePlace/phonePlace_list";
    }

    /**
     * 获取手机号码归属地列
     *
     * @param page
     * @return
     */
    @ResponseBody
    @PostMapping("/phonePlace/queryPhonePlaceList")
    public AjaxResult queryPhonePlaceList(Page page, PhonePlace phonePlace){
        try {
            return PhonePlaceService.findAllPhonePlaceList(page,phonePlace);
        } catch (Exception e) {
            log.error("获取手机号码归属地列表 方法：queryPhonePlaceList 错误信息：",e);
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("/phonePlace/toPhonePlaceAdd")
    public String toNoticeAddPage(){
        return "admin/phonePlace/phonePlace_add";
    }

    /**
     * 添加手机归属地
     * @param phonePlace
     * @return
     */
    @RequiresRoles("admin")
    @PostMapping("/phonePlace/addPhonePlace")
    @ResponseBody
    @Log(title = "添加公告",businessType = BusinessType.INSERT,operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult insertNotice(PhonePlace phonePlace){
        return PhonePlaceService.insertPhonePlace(phonePlace);
    }


    @GetMapping("/phonePlace/getPhonePlaceById/{id}")
    public String getPhonePlaceById(@PathVariable Integer id ,ModelMap map){
        try {
            PhonePlace phonePlace = PhonePlaceService.getPhonePlaceById(id);
            map.put("phonePlace",phonePlace);
        } catch (Exception e) {
            log.error("跳转手机号码归属地页面 方法：toUpdateNoticePage 错误信息",e);
        }
        return "admin/phonePlace/phonePlace_update";
    }

    /**
     * 修改手机归属地
     * @param phonePlace
     * @return
     */
    @RequiresRoles("admin")
    @ResponseBody
    @PutMapping("/phonePlace/updatePhonePlace")
    @Log(title = "修改公告信息",businessType = BusinessType.UPDATE,operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult updatePhonePlace(PhonePlace phonePlace){
        if (StringUtils.isNotNull(phonePlace)){
            return PhonePlaceService.updatePhonePlace(phonePlace);
        }
        return AjaxResult.error("修改失败");
    }

    /**
     * 删除公告
     * @param id
     * @return
     */
    @ResponseBody
    @DeleteMapping("/phonePlace/deletePhonePlace/{id}")
    @Log(title = "删除公告",businessType = BusinessType.DELETE,operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult deletePhonePlace(@PathVariable Integer id){
        return PhonePlaceService.deletePhonePlace(id);
    }

}
