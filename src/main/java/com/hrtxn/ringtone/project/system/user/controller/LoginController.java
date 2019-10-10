package com.hrtxn.ringtone.project.system.user.controller;

import com.google.code.kaptcha.Constants;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.utils.AddressUtils;
import com.hrtxn.ringtone.common.utils.Const;
import com.hrtxn.ringtone.common.utils.MD5Utils;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.freemark.config.asyncConfig.AsyncConfig;
import com.hrtxn.ringtone.project.system.log.domain.LoginLog;
import com.hrtxn.ringtone.project.system.user.domain.LoginParam;
import com.hrtxn.ringtone.project.system.user.domain.User;
import com.hrtxn.ringtone.project.system.user.service.UserService;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsChildOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.service.ThreeNetsRingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
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

    @Autowired
    private UserService userService;
    @Autowired
    private ThreenetsChildOrderMapper threenetsChildOrderMapper;
    @Autowired
    private ThreeNetsRingService threeNetsRingService;

    /**
     * 执行登陆操作
     *
     * @param loginParam
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public String login(LoginParam loginParam, Map<String, Object> map, HttpSession session) {
        long startTime = System.currentTimeMillis();//获取当前时间
        String msg = "";
        // 构造登录记录实体类
        LoginLog loginLog = new LoginLog();
        loginLog.setLoginLogTime(new Date());
        loginLog.setLoginLogUsername(loginParam.getUsername());
        String ip = ShiroUtils.getIp();
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        loginLog.setIpAdress(ip);
        log.info("保存登录IP，共耗时 -- >" + (System.currentTimeMillis() - startTime) + "ms");
        try {
            // 1、检验验证码
            if (loginParam.getCaptchaCode() != null) {
                String inputCode = loginParam.getCaptchaCode();
                System.out.println("=========================================================================================");
                System.out.println(session.getAttribute(Const.SESSION_VERIFICATION_CODE));
                System.out.println(session.getAttribute(Const.SESSION_VERIFICATION_CODE));
                System.out.println(session.getAttribute(Const.SESSION_VERIFICATION_CODE));
                System.out.println(session.getAttribute(Const.SESSION_VERIFICATION_CODE));
                System.out.println(session.getAttribute(Const.SESSION_VERIFICATION_CODE));
                System.out.println(session.getAttribute(Const.SESSION_VERIFICATION_CODE));
                System.out.println(session.getAttribute(Const.SESSION_VERIFICATION_CODE));
                System.out.println(session.getAttribute(Const.SESSION_VERIFICATION_CODE));
                System.out.println(session.getAttribute(Const.SESSION_VERIFICATION_CODE));
                System.out.println("=========================================================================================");
                if (session.getAttribute(Const.SESSION_VERIFICATION_CODE) == null) {//有时候取值是null，真是奇怪，没找到原因
                    map.put("msg", "验证码失效!");
                    // 异步操作，执行修改登录时间以及添加登录日志
                    loginLog.setLoginLogStatus("验证码失效");
                    AsyncConfig.ac().loginLogTask(ShiroUtils.getSysUser(), loginLog);
                    return "system/login";
                }
                String captchaSession = session.getAttribute(Const.SESSION_VERIFICATION_CODE).toString();
                log.info("获取验证码，共耗时 -- >" + (System.currentTimeMillis() - startTime) + "ms");
                if (captchaSession != null) {//如果缓存中没有验证码，则直接不验证验证码
                    if (!Objects.equals(inputCode, captchaSession)) {
                        log.info("验证码错误，用户输入：{}, 正确验证码：{}", inputCode, captchaSession);
                        map.put("msg", "验证码不正确!");
                        // 异步操作，执行修改登录时间以及添加登录日志
                        loginLog.setLoginLogStatus("验证码错误");
                        AsyncConfig.ac().loginLogTask(ShiroUtils.getSysUser(), loginLog);
                        return "system/login";
                    }
                    log.info("验证码验证，共耗时 -- >" + (System.currentTimeMillis() - startTime) + "ms");
                }
            }
            // 2、执行认证、授权操作
            Subject subject = SecurityUtils.getSubject();
            log.info("执行授权操作，共耗时 -- >" + (System.currentTimeMillis() - startTime) + "ms");
            UsernamePasswordToken token = new UsernamePasswordToken(loginParam.getUsername(), MD5Utils.GetMD5Code(loginParam.getPassword()), false);
            // 执行登录操作
            subject.login(token);
            log.info("执行登录操作，共耗时 -- >" + (System.currentTimeMillis() - startTime) + "ms");
            Boolean status = ShiroUtils.getSysUser().getUserStatus();
            if (status) {
                // 将用户名存到session中
                session.setAttribute("username", ShiroUtils.getSysUser().getUserName());
                // 异步操作，执行修改登录时间以及添加登录日志
                loginLog.setLoginLogStatus("登录成功");
                AsyncConfig.ac().loginLogTask(ShiroUtils.getSysUser(), loginLog);
                log.info("用户登录成功，共耗时 -- >" + (System.currentTimeMillis() - startTime) + "ms");
                return "redirect:/system/index";
            } else {
                ShiroUtils.getSubject().logout();
                msg = "您已被禁止登录，请联系管理员解除";
            }
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
        map.put("msg", msg);
        // 异步操作，执行修改登录时间以及添加登录日志
        loginLog.setLoginLogStatus(msg);
        AsyncConfig.ac().loginLogTask(ShiroUtils.getSysUser(), loginLog);
        return "system/login";
    }

    /**
     * 登出操作
     *
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/loginout")
    public String logout(HttpSession session, Model model) {
        session.removeAttribute("username");
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        model.addAttribute("msg", "安全退出！");
        return "system/login";
    }

    /**
     * 验证是否登录
     *
     * @return
     */
    @GetMapping("/index")
    public String index() {
        User user = ShiroUtils.getSysUser();
        if (user != null) {
            return "redirect:/system/index";
        }
        return "system/login";
    }


    /**
     * 跳转到管理端首页
     *
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/admin/index")
    public String toAdminIndexPage() {
        return "admin/index";
    }

    /**
     * 跳转管理端欢迎页
     *
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/admin/welcome")
    public String welcome(ModelMap map) {
        // 数据统计
        // 1、用户（总用户数、正常用户）
        // 总数
        int userCount = userService.getCount(null);
        map.put("userCount", userCount);
        // 正常
        int userSuccCount = userService.getCount(1);
        map.put("userSuccCount", userSuccCount);

        // 2、号码（总号码数、移动总数/已包月数、电信总数/已包月数、联通总数/已包月数）
        // 总数
        Integer phoneCount = threenetsChildOrderMapper.getPhoneCount(null, null);
        map.put("phoneCount", phoneCount);

        // 移动
        // 移动总数
        Integer miguPhoneCount = threenetsChildOrderMapper.getPhoneCount(1, null);
        // 移动已包月数
        Integer miguPhoneSuccCount = threenetsChildOrderMapper.getPhoneCount(1, 2);
        map.put("miguPhoneCount", miguPhoneCount + "/" + miguPhoneSuccCount);

        // 电信
        // 电信总数
        Integer mcardPhoneCount = threenetsChildOrderMapper.getPhoneCount(2, null);
        // 电信已包月数
        Integer mcardPhoneSuccCount = threenetsChildOrderMapper.getPhoneCount(2, 2);
        map.put("mcardPhoneCount", mcardPhoneCount + "/" + mcardPhoneSuccCount);

        // 联通
        // 联通总数
        Integer swxlPhoneCount = threenetsChildOrderMapper.getPhoneCount(3, null);
        // 联通已包月数
        Integer swxlPhoneSuccCount = threenetsChildOrderMapper.getPhoneCount(3, 2);
        map.put("swxlPhoneCount", swxlPhoneCount + "/" + swxlPhoneSuccCount);

        // 3、铃音（总铃音数、移动总数/已包月数、电信总数/已包月数、联通总数/已包月数）
        // 总数
        int ringCount = threeNetsRingService.getCount(null);
        map.put("ringCount", ringCount);
        // 移动总数
        BaseRequest miguBaseRequest = new BaseRequest();
        miguBaseRequest.setOperator(1);
        int miguRingCount = threeNetsRingService.getCount(miguBaseRequest);
        // 移动激活成功数量
        BaseRequest miguSuccBaseRequest = new BaseRequest();
        miguSuccBaseRequest.setIsMonthly(3);
        miguSuccBaseRequest.setOperator(1);
        int miguRingSuccCount = threeNetsRingService.getCount(miguSuccBaseRequest);
        map.put("miguRingCount", miguRingCount + "/" + miguRingSuccCount);

        // 电信总数
        BaseRequest mcardBaseRequest = new BaseRequest();
        mcardBaseRequest.setOperator(2);
        int mcardRingCount = threeNetsRingService.getCount(mcardBaseRequest);
        // 电信激活成功数量
        BaseRequest mcardSuccBaseRequest = new BaseRequest();
        mcardSuccBaseRequest.setIsMonthly(3);
        mcardSuccBaseRequest.setOperator(2);
        int mcardRingSuccCount = threeNetsRingService.getCount(mcardSuccBaseRequest);
        map.put("mcardRingCount", mcardRingCount + "/" + mcardRingSuccCount);

        // 联通总数
        BaseRequest swxlBaseRequest = new BaseRequest();
        swxlBaseRequest.setOperator(3);
        int swxlRingCount = threeNetsRingService.getCount(swxlBaseRequest);
        // 联通激活成功数量
        BaseRequest swxlSuccBaseRequest = new BaseRequest();
        swxlSuccBaseRequest.setIsMonthly(3);
        swxlSuccBaseRequest.setOperator(3);
        int swxlRingSuccCount = threeNetsRingService.getCount(swxlSuccBaseRequest);
        map.put("swxlRingCount", swxlRingCount + "/" + swxlRingSuccCount);

        return "admin/welcome";
    }
}
