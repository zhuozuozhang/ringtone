package com.hrtxn.ringtone.freemark.config.shiroConfig;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.hrtxn.ringtone.common.constant.Constant;
import com.hrtxn.ringtone.common.utils.MD5Utils;
import com.hrtxn.ringtone.common.utils.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author zcy
 * @Date 2019-06-26 17:45
 * @Description Shiro 配置类
 */
@Slf4j
@Configuration
public class ShiroConfig {
    /**
     * Session超时时间，单位为毫秒（默认30分钟）
     */
    @Value("${shiro.session.expireTime}")
    private int expireTime;
    /**
     * 设置Cookie的过期时间，秒为单位
     */
    @Value("${shiro.cookie.maxAge}")
    private int maxAge;

    @Bean
    public FilterRegistrationBean delegatingFilterProxy() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("shiroFilter");
        filterRegistrationBean.setFilter(proxy);
        return filterRegistrationBean;
    }

    /**
     * shiro 拦截器
     *
     * @param securityManager
     * @return
     */
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
        log.info("启动ShiroFilter--时间是：" + new Date());
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // shiro拦截器.
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        // 配置不会被拦截的链接 顺序判断
        // 配置登录页不被拦截
        filterChainDefinitionMap.put("/login", "anon");
        // 配置图片验证码不被拦截
        filterChainDefinitionMap.put("/imageCode", "anon");
        // 配置静态资源不被拦截
        filterChainDefinitionMap.put("/admin/**", "anon");
        filterChainDefinitionMap.put("/client/**", "anon");
        filterChainDefinitionMap.put("/**/css/**", "anon");
        filterChainDefinitionMap.put("/**/dataTables/**", "anon");
        filterChainDefinitionMap.put("/**/file/**", "anon");
        filterChainDefinitionMap.put("/**/images/**", "anon");
        filterChainDefinitionMap.put("/**/js/**", "anon");
        filterChainDefinitionMap.put("/**/layui/**", "anon");
        filterChainDefinitionMap.put("/**/center/**", "anon");
        filterChainDefinitionMap.put("/**/error/**", "anon");
        filterChainDefinitionMap.put("/**/home/**", "anon");
        filterChainDefinitionMap.put("/**/login/**", "anon");
        filterChainDefinitionMap.put("/**/notice/**", "anon");
        filterChainDefinitionMap.put("/favicon.ico", "anon");
//        filterChainDefinitionMap.put("/public/**", "anon");
//        /**设置拦截所有资源*/
        filterChainDefinitionMap.put("/**", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        // 如果不设置默认会自动寻找Web工程根目录下的login页面
        shiroFilterFactoryBean.setLoginUrl("/");
        // 登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("system/index");
        // 未授权界面
        shiroFilterFactoryBean.setUnauthorizedUrl("/error/error403Page");
        return shiroFilterFactoryBean;
    }

    /**
     * 开启缓存
     * shiro-ehcache实现
     *
     * @return
     */
    @Bean
    public EhCacheManager ehCacheManager() {
        EhCacheManager ehCacheManager = new EhCacheManager();
        ehCacheManager.setCacheManagerConfigFile("classpath:ehcache/ehcache-shiro.xml");
        return ehCacheManager;
    }

    /**
     * 自定义sessionManager
     *
     * @return
     */
    @Bean
    public SessionManager sessionManager() {
        ShiroSessionManager shiroSessionManager = new ShiroSessionManager();
        Cookie cookie = new SimpleCookie(Constant.COOKIENAME);
        cookie.setMaxAge(maxAge * 24 * 60 * 60);
        shiroSessionManager.setSessionIdCookie(cookie);
        EnterpriseCacheSessionDAO sessionDAO = new EnterpriseCacheSessionDAO();
        SessionIdGenerator sessionIdGenerator = new SessionIdGenerator() {
            // 自定义cookie的值
            @Override
            public Serializable generateId(Session session) {
                String sessionOne = UUIDUtils.get32UUID();
                String sessionTwo = UUIDUtils.get24UUID();
                session.setAttribute("sessionA", sessionOne);
                session.setAttribute("sessionB", sessionTwo);
                String cookieValue = sessionOne + Constant.APPID + sessionTwo;
                return MD5Utils.GetMD5Code(cookieValue);
            }
        };
        sessionDAO.setSessionIdGenerator(sessionIdGenerator);
        shiroSessionManager.setSessionDAO(sessionDAO);
        shiroSessionManager.setGlobalSessionTimeout(expireTime * 60 * 1000);
        return shiroSessionManager;
    }

    /**
     * 自定义身份验证Realm （包含用户名密码校验，权限校验等）
     *
     * @return
     */
    @Bean(name = "myShiroRealm")
    public MyShiroRealm myShiroRealm() {
        MyShiroRealm myShiroRealm = new MyShiroRealm();
        return myShiroRealm;
    }

    /**
     * 安全管理器
     */
    @Bean
    public SecurityManager securityManager(MyShiroRealm myShiroRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //设置realm
        securityManager.setRealm(myShiroRealm);
        //自定义缓存实现
        /**securityManager.setCacheManager(ehCacheManager());*/
        securityManager.setSessionManager(sessionManager());
        SecurityUtils.setSecurityManager(securityManager);
        return securityManager;
    }

    /**
     * 开启Shiro注解(如@RequiresRoles,@RequiresPermissions),
     * 需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator和AuthorizationAttributeSourceAdvisor)
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    /**
     * 开启shiro aop 注解支持，不开启的话权限验证就会失效
     *
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }


    /**
     * thymeleaf模板引擎和shiro框架的整合
     */
    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

}
