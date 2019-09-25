package com.hrtxn.ringtone.project.system.notice.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.system.notice.domain.Notice;
import com.hrtxn.ringtone.project.system.notice.service.NoticeRecordService;
import com.hrtxn.ringtone.project.system.notice.service.NoticeService;
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
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private NoticeRecordService noticeRecordService;

    /**
     * 获取公告列表
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/admin/toNoticeListPage")
    public String toNoticeListPage(ModelMap map){
        try {
//            List<Notice> noticeList = noticeService.findAllNoticeList();
//            map.put("noticeList",noticeList);
        } catch (Exception e) {
            log.error("获取公告列表 方法：toNoticeListPage 错误信息：",e);
        }
        return "admin/notice/notice_list";
    }

    /**
     * 获取公告列表
     *
     * @param page
     * @return
     */
    @RequiresRoles("admin")
    @ResponseBody
    @PostMapping("/admin/getNoticeList")
    public AjaxResult getNoticeList(Page page){
        try {
            return noticeService.findAllNoticeList(page);
        } catch (Exception e) {
            log.error("获取公告列表 方法：getNoticeList 错误信息：",e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 跳转到公告添加页面
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/admin/toNoticeAddPage")
    public String toNoticeAddPage(){
        return "admin/notice/notice_add";
    }

    /**
     * 添加公告
     * @param notice
     * @return
     */
    @RequiresRoles("admin")
    @PostMapping("/admin/insertNotice")
    @ResponseBody
    @Log(title = "添加公告",businessType = BusinessType.INSERT,operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult insertNotice(Notice notice){
        return noticeService.insertNotice(notice);
    }

    /**
     * 修改公告状态
     * @param id
     * @param noticeStatus
     * @return
     */
    @ResponseBody
    @RequiresRoles("admin")
    @PutMapping("/admin/updataNotiecStatus/{id}")
    @Log(title = "修改公告状态",businessType = BusinessType.UPDATE,operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult updataNotiecStatus(@PathVariable Integer id,boolean noticeStatus){
        if (id != null && id != 0){
            return noticeService.updataNotiecStatus(id,noticeStatus);
        }
        return AjaxResult.error("修改公告状态出错");
    }

    /**
     * 跳转公告详情页
     * @param id
     * @param map
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/admin/toNoticeDetaiPage/{id}")
    public String toNoticeDetai(@PathVariable Integer id,ModelMap map){
        Notice notice = null;
        try {
            notice = noticeService.findNoticeById(id);
        } catch (Exception e) {
            log.error("根据ID获取公告详情出错 方法：toNoticeDetai 错误信息",e);
        }
        map.put("notice",notice);
        return "admin/notice/notice_detail";
    }

    @RequiresRoles("admin")
    @GetMapping("/admin/toUpdateNoticePage/{id}")
    public String toUpdateNoticePage(@PathVariable Integer id ,ModelMap map){
        try {
            Notice notice = noticeService.findNoticeById(id);
            map.put("notice",notice);
        } catch (Exception e) {
            log.error("跳转修改公告页面 方法：toUpdateNoticePage 错误信息",e);
        }
        return "admin/notice/notice_update";
    }

    /**
     * 修改公告信息
     * @param notice
     * @return
     */
    @RequiresRoles("admin")
    @ResponseBody
    @PutMapping("/admin/updateNotice")
    @Log(title = "修改公告信息",businessType = BusinessType.UPDATE,operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult updateNotice(Notice notice){
        System.out.println(notice.toString());
        if (StringUtils.isNotNull(notice)){
            return noticeService.updateNotice(notice);
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
    @DeleteMapping("/admin/deleteNotice/{id}")
    @Log(title = "删除公告",businessType = BusinessType.DELETE,operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult deleteNotice(@PathVariable Integer id){
        return noticeService.deleteNotice(id);
    }

    /**********************************************大大的分界线**************************************************************/

    /**
     * 获取公告列表
     * @param map
     * @return
     */
    @GetMapping("/system/toClinetNoticeListPage")
    public String toClinetNoticeListPage(ModelMap map){
        List<Notice> _noticeList = new ArrayList<>();
        try {
            Page page = new Page();
            page.setPage(1);
            page.setPagesize(1000);
            AjaxResult allNoticeList = noticeService.findAllNoticeList(page);
            List<Notice> noticeList = (List<Notice>) allNoticeList.get("data");
            if (StringUtils.isNotNull(noticeList)&&noticeList.size() > 0){
                for (Notice notice : noticeList) {
                    if (notice.getNoticeStatus()){
                        _noticeList.add(notice);
                    }
                }
            }
            map.put("noticeList",_noticeList);
        } catch (Exception e) {
            log.error("获取公告列表 方法：toClinetNoticeListPage 错误信息",e);
        }
        return "system/notice";
    }

}
