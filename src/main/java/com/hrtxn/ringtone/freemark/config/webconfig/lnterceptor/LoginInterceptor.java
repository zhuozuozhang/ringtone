package com.hrtxn.ringtone.freemark.config.webconfig.lnterceptor;

import com.hrtxn.ringtone.common.constant.Constant;
import com.hrtxn.ringtone.common.utils.MD5Utils;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author:zcy
 * Date:2019-07-03 12:15
 * Description:实现登录拦截器
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info(" 开始执行拦截器 preHandle -- >");
        Session session = ShiroUtils.getSession();
        Object sessionA = session.getAttribute("sessionA");
        Object sessionB = session.getAttribute("sessionB");
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie:cookies) {
            String cookieName = cookie.getName();
            String cookieValue = cookie.getValue();
            if ("AUTHENTICATION-TOKEN".equals(cookieName)){
                String newCookieValue = MD5Utils.GetMD5Code(sessionA + Constant.APPID + sessionB);
                if (newCookieValue.equals(cookieValue)){
                    return true;
                }
            }
        }
        response.sendRedirect("/");
        log.info("被拦截 -->");
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
