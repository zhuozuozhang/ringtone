package com.hrtxn.ringtone.project.system.center.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.constant.Constant;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.common.utils.juhe.JuheMessageUtils;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.system.rechargelog.domain.RechargeLog;
import com.hrtxn.ringtone.project.system.rechargelog.service.RechargeLogService;
import com.hrtxn.ringtone.project.system.user.domain.User;
import com.hrtxn.ringtone.project.system.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-12 11:10
 * Description:客户端个人中心控制器
 */
@Controller
@Slf4j
public class CenterController {

    @Autowired
    private UserService userService;
    @Autowired
    private RechargeLogService rechargeLogService;

    @GetMapping("/system/toCenterPage")
    public String toCenterPage(ModelMap map){
        User user = ShiroUtils.getSysUser();
        map.put("user",user);
        List<RechargeLog> rechargeLogList = rechargeLogService.findRechargeLogByUserId(ShiroUtils.getSysUser().getId());
        map.put("rechargeLogList",rechargeLogList);
        return  "system/center";
    }

    /**
     * 发送短信验证码
     * @param phone 电话号码
     * @param type 验证码标识（1.修改密码）
     * @return
     */
    @ResponseBody
    @PostMapping("/system/sendMessage/{type}")
    @Log(title = "发送短信验证码",businessType = BusinessType.OTHER,operatorLogType = OperatorLogType.OTHER)
    public AjaxResult sendMessage(String phone , @PathVariable int type, HttpSession session) throws Exception {
        log.info("发送短信开始 --->");
        String tlpCode = null;
        if (type == 1){
            tlpCode = Constant.UPDATEPASSWORDID;
        }
        if (!StringUtils.isNotEmpty(phone) || !StringUtils.isNotEmpty(tlpCode)){
            return AjaxResult.error("参数格式不正确");
        }
        User user = userService.findUserByUserTel(phone);
        if (StringUtils.isNotNull(user)){
            return JuheMessageUtils.sendMessage(phone, tlpCode,session);
        }
        return AjaxResult.error("该["+phone+"]号码没有注册，请重新输入号码！");
    }

    /**
     * 个人中心--》修改密码
     * @param userPassword
     * @param code
     * @param session
     * @return
     */
    @ResponseBody
    @PutMapping("/system/updatePassword")
    public AjaxResult updatePassword(String userPassword,Integer code,HttpSession session){
        return userService.updatePassword(userPassword,code,session);
    }


}
