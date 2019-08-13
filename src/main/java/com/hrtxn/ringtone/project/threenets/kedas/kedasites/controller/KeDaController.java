package com.hrtxn.ringtone.project.threenets.kedas.kedasites.controller;

import com.hrtxn.ringtone.project.system.notice.domain.Notice;
import com.hrtxn.ringtone.project.system.notice.service.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
            map.put("noticeList",noticeListByModul);
        } catch (Exception e) {
            log.error("疑难杂单获取公告列表--->"+e);
        }
        return "threenets/kedas/kedasites/index/index";
    }

}
