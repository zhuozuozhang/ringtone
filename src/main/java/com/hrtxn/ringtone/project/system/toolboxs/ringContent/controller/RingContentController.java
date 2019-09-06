package com.hrtxn.ringtone.project.system.toolboxs.ringContent.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.system.toolboxs.ringContent.domain.RingContent;
import com.hrtxn.ringtone.project.system.toolboxs.ringContent.service.RingContentService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

/**
 * 铃音内容控制器
 *
 * @Author zcy
 * @Date 2019-09-04 13:21
 */
@Controller
public class RingContentController {

    @Autowired
    private RingContentService ringContentService;

    /**
     * 跳转铃音内容列表页面
     *
     * @author zcy
     * @date 2019-9-5 10:28
     */
    @RequiresRoles("admin")
    @GetMapping("/admin/toRingContent")
    public String toRingContent() {
        return "admin/toolboxs/ringcontent/ringcontent_list";
    }

    /**
     * 管理端--查询铃音内容
     *
     * @author zcy
     * @date 2019-9-5 10:28
     */
    @RequiresRoles("admin")
    @ResponseBody
    @PostMapping("/admin/selectRingContent")
    public AjaxResult selectRingContent(Page page) {
        return ringContentService.selectRingContent(page, null);
    }

    /**
     * 删除铃音内容
     *
     * @author zcy
     * @date 2019-9-5 10:27
     */
    @DeleteMapping("/admin/deleteRingContent/{id}")
    @ResponseBody
    @RequiresRoles("admin")
    @Log(title = "铃音内容删除", businessType = BusinessType.DELETE, operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult deleteRingContent(@PathVariable Integer id) {
        return ringContentService.deleteRingContent(id);
    }

    /**
     * 跳转铃音内容添加页面
     *
     * @author zcy
     * @date 2019-9-5 10:27
     */
    @GetMapping("/admin/toRingcontentAddPage")
    @RequiresRoles("admin")
    public String toRingcontentAddPage() {
        return "admin/toolboxs/ringcontent/ringcontent_add";
    }

    /**
     * 添加铃音内容
     *
     * @author zcy
     * @date 2019-9-5 10:27
     */
    @PostMapping("/admin/insertRingContent")
    @ResponseBody
    @RequiresRoles("admin")
    @Log(title = "工具箱--铃音内容添加", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult insertRingContent(RingContent ringContent) {
        return ringContentService.insertRingContent(ringContent);
    }

    /**
     * 跳转铃音内容修改页面
     *
     * @author zcy
     * @date 2019-9-5 10:27
     */
    @RequiresRoles("admin")
    @GetMapping("/admin/toRingContentUpdate/{id}")
    public String toRingContentUpdate(@PathVariable Integer id, ModelMap map) {
        // 根据ID获取铃音内容信息
        RingContent ringContent = ringContentService.selectById(id);
        map.put("content", ringContent);
        return "admin/toolboxs/ringcontent/ringcontent_update";
    }

    /**
     * 执行修改铃音内容操作
     *
     * @author zcy
     * @date 2019-9-5 10:26
     */
    @PutMapping("/admin/updateRingContent")
    @ResponseBody
    @RequiresRoles("admin")
    @Log(title = "修改铃音内容", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult updateRingContent(RingContent ringContent) {
        return ringContentService.updateRingContent(ringContent);
    }

    /**********************************************************************************************/
    /**
     * 跳转铃音内容页面
     *
     * @author zcy
     * @date 2019-9-5 10:26
     */
    @GetMapping("/toolboxs/toRingContentList")
    public String toRingContentList() {
        return "toolboxs/ringcontent/goringcontentlist";
    }

    /**
     * 获取铃音内容
     *
     * @author zcy
     * @date 2019-9-5 10:26
     */
    @PostMapping("/toolboxs/selectRingContent")
    @ResponseBody
    public AjaxResult select(Page page, String ringcontent) {
        return ringContentService.selectRingContent(page, ringcontent);
    }

    /**
     * 跳转到工具箱页面
     *
     * @author zcy
     * @date 2019-9-5 10:25
     */
    @GetMapping("/toolboxs/toToolboxs")
    public String toToolboxs() {
        return "toolboxs/gotools";
    }
}
