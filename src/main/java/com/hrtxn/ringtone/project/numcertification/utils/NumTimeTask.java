package com.hrtxn.ringtone.project.numcertification.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

/**
 * 400定时器
 * @author zcy
 * @date 2019-8-29 10:43
 */
@Component
@Slf4j
public class NumTimeTask {


    /**
     * 每隔50分钟获取cgiToken
     * @author zcy
     * @date 2019-8-29 10:55
     */



    public void  get(HttpSession session){
        Object cgiToken = session.getAttribute("cgiToken");
        System.out.println(cgiToken);

    }


}
