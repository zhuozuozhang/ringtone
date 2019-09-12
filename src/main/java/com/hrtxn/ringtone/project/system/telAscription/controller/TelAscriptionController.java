package com.hrtxn.ringtone.project.system.telAscription.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.system.notice.domain.Notice;
import com.hrtxn.ringtone.project.system.notice.service.NoticeService;
import com.hrtxn.ringtone.project.system.telAscription.domain.TelAscription;
import com.hrtxn.ringtone.project.system.telAscription.service.TelAscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-09 16:26
 * Description:公告管理
 */
@Controller
@Slf4j
public class TelAscriptionController {

    @Autowired
    private TelAscriptionService telAscriptionService;

    /**
     * 获取公告列表
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/telAscription/toTelAscriptionPage")
    public String toNoticeListPage(ModelMap map){
        try {
        } catch (Exception e) {
            log.error("获取固话号码归属地列表 方法：toNoticeListPage 错误信息：",e);
        }
        return "admin/telAscription/telAscription_list";
    }

    /**
     * 获取公告列表
     *
     * @param page
     * @return
     */
    @ResponseBody
    @PostMapping("/telAscription/queryTelAscriptionList")
    public AjaxResult queryTelAscriptionList(Page page,TelAscription telAscription){
        try {
            return telAscriptionService.findAllTelAscriptionList(page,telAscription);
        } catch (Exception e) {
            log.error("获取固话号码归属地列表 方法：queryTelAscriptionList 错误信息：",e);
            return AjaxResult.error(e.getMessage());
        }
    }

    @RequiresRoles("admin")
    @GetMapping("/admin/toTelAscriptionAdd")
    public String toNoticeAddPage(){
        return "admin/telAscription/telAscription_add";
    }

    /**
     * 添加固话归属地
     * @param telAscription
     * @return
     */
    @RequiresRoles("admin")
    @PostMapping("/telAscription/addTelAscription")
    @ResponseBody
    @Log(title = "添加公告",businessType = BusinessType.INSERT,operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult insertNotice(TelAscription telAscription){
        return telAscriptionService.insertTelAscription(telAscription);
    }


    @RequiresRoles("admin")
    @GetMapping("/admin/getTelAscriptionById/{id}")
    public String getTelAscriptionById(@PathVariable Integer id ,ModelMap map){
        try {
            TelAscription telAscription = telAscriptionService.getTelAscriptionById(id);
            map.put("telAscription",telAscription);
        } catch (Exception e) {
            log.error("跳转固话号码归属地页面 方法：toUpdateNoticePage 错误信息",e);
        }
        return "admin/telAscription/telAscription_update";
    }

    /**
     * 修改固话归属地
     * @param telAscription
     * @return
     */
    @RequiresRoles("admin")
    @ResponseBody
    @PutMapping("/telAscription/updateTelAscription")
    @Log(title = "修改公告信息",businessType = BusinessType.UPDATE,operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult updateTelAscription(TelAscription telAscription){
        if (StringUtils.isNotNull(telAscription)){
            return telAscriptionService.updateTelAscription(telAscription);
        }
        return AjaxResult.error("修改失败");
    }

    /**
     * 删除公告
     * @param id
     * @return
     */
    @ResponseBody
    @RequiresRoles("admin")
    @DeleteMapping("/admin/deleteTelAscription/{id}")
    @Log(title = "删除公告",businessType = BusinessType.DELETE,operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult deleteTelAscription(@PathVariable Integer id){
        return telAscriptionService.deleteTelAscription(id);
    }

}
