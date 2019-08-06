package com.hrtxn.ringtone.freemark.config.webconfig;

import com.hrtxn.ringtone.freemark.config.webconfig.lnterceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Author:zcy
 * Date:2019-06-24 9:56
 * Description:配置视图
 */
@Configuration
public class MyWebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    /**
     * 配置静态资源页跳转
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 设置统一登录入口
        registry.addViewController("/").setViewName("system/login");
        registry.addViewController("/admin/index").setViewName("admin/index");
        registry.addViewController("/admin/welcome").setViewName("admin/welcome");
        // 跳转到商户列表
        registry.addViewController("/threenets/threeNetsOrderList").setViewName("threenets/threenet/merchants/merchants");
    }

    /**
     * 实现登录拦截
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration interceptorRegistry = registry.addInterceptor(loginInterceptor);
        // 设置不被拦截资源
        interceptorRegistry.excludePathPatterns("/");
        //interceptorRegistry.excludePathPatterns("/login");
//        interceptorRegistry.excludePathPatterns("/admin/**");
        interceptorRegistry.excludePathPatterns("/public/**");
//        interceptorRegistry.excludePathPatterns("/system/**");
        interceptorRegistry.excludePathPatterns("/client/**");
        interceptorRegistry.excludePathPatterns("/imageCode");
        interceptorRegistry.excludePathPatterns("/test/**");

        // 设置拦截资源
        interceptorRegistry.addPathPatterns("/**");
    }
}
