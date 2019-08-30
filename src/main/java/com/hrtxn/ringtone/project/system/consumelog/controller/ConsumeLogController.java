package com.hrtxn.ringtone.project.system.consumelog.controller;
/**
 * @Author: yuanye
 * @Date: Created in 10:29 2019/8/30
 * @Description: 充值记录实体类
 * @Modified By:
 */

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.system.consumelog.service.ConsumeLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/admin")
public class ConsumeLogController {

    @Autowired
    private ConsumeLogService consumeLogService;

    /**
     * 获取全部消费记录
     * @return
     */
    @PostMapping("/getConsumeLogList")
    @ResponseBody
    @RequiresRoles("admin")
    public AjaxResult getConsumeLogList(Page page){
        try{
            return consumeLogService.getConsumeLogList(page);
        }catch (Exception e){
            log.error("获取全部充值记录 方法：getConsumeLogList 错误信息",e);
        }
        return AjaxResult.error("获取数据失败");
    }

}
