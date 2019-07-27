package com.hrtxn.ringtone.project.system.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Author:zcy
 * Date:2019-06-24 11:04
 * Description:自定义异常跳转页面
 */
@Controller
@RequestMapping("/error")
@Slf4j
public class ErrorController {

    @GetMapping(value = "/error400Page")
    public String error400Page() {
        log.error("400 -->");
        return "error/400";
    }
    @GetMapping(value = "/error404Page")
    public String error404Page(Model model) {
        log.error("404 -->");
        return "error/404";
    }
    @GetMapping(value = "/error405Page")
    public String error405Page(Model model) {
        log.error("405 -->");
        return "error/405";
    }
    @GetMapping(value = "/error403Page")
    public String error403Page(Model model) {
        log.error("403 -->");
        return "error/403";
    }
    @GetMapping(value = "/error500Page")
    public String error500Page(Model model) {
        log.error("500 -->");
        return "error/500";
    }
}
