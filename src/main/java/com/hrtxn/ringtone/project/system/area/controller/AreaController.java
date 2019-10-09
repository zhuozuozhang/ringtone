package com.hrtxn.ringtone.project.system.area.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.system.area.domain.Area;
import com.hrtxn.ringtone.project.system.area.service.AreaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-09 16:26
 * Description:公告管理
 */
@Controller
@Slf4j
public class AreaController {

    @Autowired
    private AreaService areaService;

    /**
     * 获取手机号码归属地列
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/area/toAreaPage")
    public String toAreaPage(ModelMap map){
        try {
        } catch (Exception e) {
            log.error("获取手机号码归属地列表 方法：toAreaPage 错误信息：",e);
        }
        return "admin/area/area_list";
    }

    /**
     * 获取手机号码归属地列
     *
     * @param page
     * @return
     */
    @ResponseBody
    @PostMapping("/area/queryAreaList")
    public AjaxResult queryAreaList(Page page, Area Area){
        try {
            return areaService.findAllAreaList(page,Area);
        } catch (Exception e) {
            log.error("获取手机号码归属地列表 方法：queryAreaList 错误信息：",e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 地区
     * @return
     */
    @ResponseBody
    @PostMapping("/area/queryAreaCons")
    public List<Area> queryAreaCons(String type, String pid){
        return areaService.queryAreaByCons(type,pid);
    }


    @GetMapping("/area/toAreaAdd")
    public String toNoticeAddPage(){
        return "admin/area/area_add";
    }

    /**
     * 添加手机归属地
     * @param Area
     * @return
     */
    @RequiresRoles("admin")
    @PostMapping("/area/addArea")
    @ResponseBody
    @Log(title = "添加公告",businessType = BusinessType.INSERT,operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult insertNotice(Area Area){
        return areaService.insertArea(Area);
    }


    @GetMapping("/area/getAreaById/{id}")
    public String getAreaById(@PathVariable Integer id ,ModelMap map){
        try {
            Area Area = areaService.getAreaById(id);
            map.put("Area",Area);
        } catch (Exception e) {
            log.error("跳转手机号码归属地页面 方法：toUpdateNoticePage 错误信息",e);
        }
        return "admin/area/area_update";
    }

    /**
     * 修改手机归属地
     * @param Area
     * @return
     */
    @RequiresRoles("admin")
    @ResponseBody
    @PutMapping("/area/updateArea")
    @Log(title = "修改公告信息",businessType = BusinessType.UPDATE,operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult updateArea(Area Area){
        if (StringUtils.isNotNull(Area)){
            return areaService.updateArea(Area);
        }
        return AjaxResult.error("修改失败");
    }

    /**
     * 删除公告
     * @param id
     * @return
     */
    @ResponseBody
    @DeleteMapping("/area/deleteArea/{id}")
    @Log(title = "删除公告",businessType = BusinessType.DELETE,operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult deleteArea(@PathVariable Integer id){
        return areaService.deleteArea(id);
    }

}
