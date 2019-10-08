package com.hrtxn.ringtone.freemark.config.asyncConfig;

import com.hrtxn.ringtone.common.utils.AddressUtils;
import com.hrtxn.ringtone.common.utils.SpringUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.system.log.domain.LoginLog;
import com.hrtxn.ringtone.project.system.log.domain.OperateLog;
import com.hrtxn.ringtone.project.system.log.service.LoginLogService;
import com.hrtxn.ringtone.project.system.log.service.OperateLogService;
import com.hrtxn.ringtone.project.system.user.domain.User;
import com.hrtxn.ringtone.project.system.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.Executor;

/**
 * Author:zcy
 * Date:2019-06-25 13:41
 * Description:Springboot 异步任务
 */
@Component
@Slf4j
public class AsyncConfig {

    private int corePoolSize = 10;
    private int maxPoolSize = 200;
    private int queueCapacity = 10;

    /**
     * 单例模式
     */
    private static AsyncConfig ac = new AsyncConfig();

    public static AsyncConfig ac(){
        return ac;
    }

    @Bean
    public Executor logTaskExecutor(){
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(corePoolSize);
        ex.setMaxPoolSize(maxPoolSize);
        ex.setQueueCapacity(queueCapacity);
        ex.setThreadNamePrefix("自定义操作日志线程");
        ex.initialize();
        return ex;
    }
    /**
     * 执行任务
     *
     * @param operateLog 任务
     */
    @Async(value = "logTaskExecutor")
    public void operateLogTask(OperateLog operateLog){
        try {
            log.info("日志添加到数据库开始====================");
            // 远程查询操作地点
            operateLog.setOperateLogLocation(AddressUtils.getRealAddressByIP(operateLog.getIpAddress()));
            boolean b = SpringUtils.getBean(OperateLogService.class).insertOperateLog(operateLog);
            log.info("日志添加到数据库结束，结果：===================="+b);
        } catch (BeansException e) {
            log.info("获取地理位置异常  --->  "+operateLog.getIpAddress());
//            e.printStackTrace();
        }
    }

    /**
     * 执行修改用户登录时间、ip以及添加登录记录
     * @param user
     */
    @Async(value = "logTaskExecutor")
    public void loginLogTask(User user , LoginLog loginLog){
//        loginLog.setLoginLocation(AddressUtils.getRealAddressByIP(loginLog.getIpAdress()));
        loginLog.setLoginLocation("--");
        if(StringUtils.isNotNull(loginLog)){
            try {
                log.info(" 开始执行添加登录记录操作 -->");
                if (StringUtils.isNotNull(user)){
                    // 1、修改用户登录时间、登录IP
                    System.out.println(user.toString());
                    User _user = new User();
                    _user.setLoginIp(loginLog.getIpAdress());
                    _user.setLoginTime(new Date());
                    _user.setId(user.getId());
                    int b1 = SpringUtils.getBean(UserMapper.class).updateUserById(_user);
                    log.info(" 执行修改登录时间、ip，结果：【{}】",b1);
                }
                // 2、添加登录记录
                boolean b = SpringUtils.getBean(LoginLogService.class).insertLoginLog(loginLog);
                log.info("添加登录记录,结果:【{}】 --> ",b);
                log.info(" 执行添加登录记录操作结束 -->");
            } catch (Exception e) {
                log.error("执行修改用户登录时间、ip以及添加登录记录,方法：【{}】,错误信息：【{}】","loginLogTask",e.getMessage());
            }
        }
    }
}
