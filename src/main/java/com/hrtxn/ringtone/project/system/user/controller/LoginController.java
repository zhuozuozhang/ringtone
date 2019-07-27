package com.hrtxn.ringtone.project.system.user.controller;

import com.google.code.kaptcha.Constants;
import com.hrtxn.ringtone.common.utils.AddressUtils;
import com.hrtxn.ringtone.common.utils.MD5Utils;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.freemark.config.AsyncConfig.AsyncConfig;
import com.hrtxn.ringtone.project.system.log.domain.LoginLog;
import com.hrtxn.ringtone.project.system.user.domain.LoginParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * Author:zcy
 * Date:2019-07-01 16:28
 * Description:登录控制器
 */
@Slf4j
@Controller
public class LoginController {

    /**
     * 执行登陆操作
     * @param loginParam
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public String login(LoginParam loginParam, Map<String ,Object> map, HttpSession session){
        String msg = "";
        // 构造登录记录实体类
        LoginLog loginLog = new LoginLog();
        loginLog.setLoginLogTime(new Date());
        loginLog.setLoginLogUsername(loginParam.getUsername());
        String ip = ShiroUtils.getIp();
        if ("0:0:0:0:0:0:0:1".equals(ip)){
            ip = "127.0.0.1";
        }
        loginLog.setIpAdress(ip);
        loginLog.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
        try {
            // 1、检验验证码
            if (loginParam.getCaptchaCode() != null) {
                String inputCode = loginParam.getCaptchaCode();
                String captchaSession = (String) session.getAttribute(Constants.KAPTCHA_SESSION_KEY);
                if (!Objects.equals(inputCode, captchaSession)) {
                    log.info("验证码错误，用户输入：{}, 正确验证码：{}", inputCode, captchaSession);
                    map.put("msg", "验证码不正确!");
                    // 异步操作，执行修改登录时间以及添加登录日志
                    loginLog.setLoginLogStatus("验证码错误");
                    AsyncConfig.ac().loginLogTask(ShiroUtils.getSysUser(),loginLog);
                    return "system/login";
                }
            }
            // 2、执行认证、授权操作
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(loginParam.getUsername(),MD5Utils.GetMD5Code(loginParam.getPassword()),false);
            // 执行登录操作
            subject.login(token);
            // 将用户名存到session中
            session.setAttribute("username",ShiroUtils.getSysUser().getUserName());
            // 异步操作，执行修改登录时间以及添加登录日志
            loginLog.setLoginLogStatus("登录成功");
            AsyncConfig.ac().loginLogTask(ShiroUtils.getSysUser(),loginLog);
            return "redirect:/system/index";
        } catch (UnknownAccountException e) {
            log.info("UnknownAccountException -- > 账号不正确");
            msg = "账号不正确";
        } catch (IncorrectCredentialsException e) {
            // 密码不正确
            log.info("IncorrectCredentialsException -- > 密码不正确");
            msg = "密码不正确";
        } catch (LockedAccountException e) {
            log.info("LockedAccountException -- > 账号被锁定");
            msg = "账号被锁定!";
        } catch (Exception e) {
            msg = "出现其他异常，请尝试重新登录";
            log.info(e.getMessage());
        }
        map.put("msg",msg);
        // 异步操作，执行修改登录时间以及添加登录日志
        loginLog.setLoginLogStatus(msg);
        AsyncConfig.ac().loginLogTask(ShiroUtils.getSysUser(),loginLog);
        return "system/login";
    }

    /**
     * 登出操作
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/loginout")
    public String logout(HttpSession session, Model model) {
        session.removeAttribute("username");
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        model.addAttribute("msg","安全退出！");
        return "redirect:/";
    }
    /**
     * 跳转到管理端首页
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/admin/index")
    public String toAdminIndexPage(){
        return "admin/index";
    }

    /**
     * 跳转管理端欢迎页
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/admin/welcome")
    public String welcome(){
        return "admin/welcome";
    }

    /**
     * 客户端首页
     * @return
     */
    @GetMapping("/system/index")
    public String toClientHome(){
        return "system/home";
    }

}
