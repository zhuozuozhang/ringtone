package com.hrtxn.ringtone.project.threenets.kedas.kedasites.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.system.notice.domain.Notice;
import com.hrtxn.ringtone.project.system.notice.service.NoticeService;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.service.KedaChildOrderService;
import com.hrtxn.ringtone.project.threenets.threenet.domain.PlotBarPhone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-08-13 14:59
 * Description:疑难杂单控制层
 */
@Controller
@RequestMapping("/threenets/clcy/")
@Slf4j
public class KeDaController {

    @Autowired
    private NoticeService noticeService;
    @Autowired
    private KedaChildOrderService kedaChildOrderService;

    /**
     * 跳转科大待办任务
     *
     * @return
     */
    @GetMapping("toIndex")
    public String toIndex(ModelMap map) {
        // 获取公告
        try {
            List<Notice> noticeListByModul = noticeService.findNoticeListByModul("8");
            map.put("noticeList", noticeListByModul);
            // 获取近5日信息
            List<PlotBarPhone> plotBarPhoneList = kedaChildOrderService.getFiveData();
            map.put("plotBarPhoneList", plotBarPhoneList);
        } catch (Exception e) {
            log.error("疑难杂单获取公告列表--->" + e);
        }
        return "threenets/kedas/kedasites/index/index";
    }

    /**
     * 待办任务 等待铃音设置
     *
     * @param page
     * @param baseRequest
     * @return
     */
    @ResponseBody
    @PostMapping("getKeDaChildOrderBacklogList")
    public AjaxResult getKeDaChildOrderBacklogList(Page page, BaseRequest baseRequest) {
        return kedaChildOrderService.getKeDaChildOrderBacklogList(page, baseRequest);
    }

    /**
     * 跳转到公告列表页
     *
     * @return
     */
    @GetMapping("toAnnouncementPage")
    public String toAnnouncementPage() {
        return "threenets/kedas/kedasites/announcement/announcement";
    }

    /**
     * 获取疑难杂单类型公告
     *
     * @param page
     * @param notice
     * @return
     */
    @PostMapping("getNoticeList")
    @ResponseBody
    public AjaxResult getNoticeList(Page page, Notice notice) {
        return noticeService.getNoticeList(page, notice);
    }

    /**
     * 跳转到业务发展数据
     *
     * @return
     */
    @GetMapping("toBusiness")
    public String toBusiness() {
        return "threenets/kedas/kedasites/business/dbusiness";
    }


}
