package com.hrtxn.ringtone.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;


/**
 * 1、新建一个Class,这里取名为GlobalDefaultExceptionHandler
 * 2、在class上添加注解，@ControllerAdvice;
 * 3、在class中添加一个方法
 * 4、在方法上添加@ExcetionHandler拦截相应的异常信息；
 * 5、如果返回的是View -- 方法的返回值是ModelAndView，本类采用的就是此方法;
 * 6、如果返回的是String或者是Json数据，那么需要在方法上添加@ResponseBody注解.
 *
 * Author:zcy
 * Date:2019-07-2 13:49
 * Description:自定义全局异常捕获
 */
@ControllerAdvice
@Slf4j
public class GlobalDefaultExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ModelAndView defaultExceptionHandler(HttpServletRequest req, Exception e) {
        log.error("UnauthorizedException --> ",e);
        return new ModelAndView("error/403");
    }

    @ExceptionHandler({ RuntimeException.class })
    @ResponseStatus(HttpStatus.OK)
    public ModelAndView processException(RuntimeException exception) {
        log.error("自定义异常处理-RuntimeException"+exception);
        ModelAndView m = new ModelAndView("error/500");
        m.addObject("roncooException", exception.getMessage());
        return m;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView Exception(HttpServletRequest req, Exception e) {
        log.error("Exception --> ",e);
        return new ModelAndView("error/500");
    }
}