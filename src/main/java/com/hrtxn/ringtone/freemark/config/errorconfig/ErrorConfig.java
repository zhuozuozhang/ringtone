package com.hrtxn.ringtone.freemark.config.errorconfig;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Author:zcy
 * Date:2019-06-24 10:07
 * Description:错误页面跳转配置
 */
@Component
public class ErrorConfig implements ErrorPageRegistrar {

    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        ErrorPage error400Page = new ErrorPage(HttpStatus.BAD_REQUEST,"/error/error400Page");
        ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND,"/error/error404Page");
        ErrorPage error405Page = new ErrorPage(HttpStatus.NOT_FOUND,"/error/error405Page");
        ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR,"/error/error500Page");
        registry.addErrorPages(error400Page,error404Page,error405Page,error500Page);
    }
}
