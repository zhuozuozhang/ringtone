package com.hrtxn.ringtone.project.threenets.threenet.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.common.utils.juhe.JuhePhoneUtils;
import com.hrtxn.ringtone.project.system.json.JuhePhone;
import com.hrtxn.ringtone.project.system.json.JuhePhoneResult;
import com.hrtxn.ringtone.project.system.user.domain.User;
import com.hrtxn.ringtone.project.system.user.mapper.UserMapper;
import com.hrtxn.ringtone.project.threenets.threenet.domain.PlotBarPhone;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsChildOrder;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsChildOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Author:lile
 * Date:2019-07-11 10:28
 * Description:三网业务层
 */
@Service
@Slf4j
public class ThreeNetsService {

    @Autowired
    private ThreenetsChildOrderMapper threenetsChildOrderMapper;
    @Autowired
    private UserMapper userMapper;

    public AjaxResult enterSubUser(Integer id, HttpSession session) throws Exception {
        if (StringUtils.isNotNull(id) && id != 0) {
            // 判断当前登陆者是否是管理员
            User sysUser = (User) ShiroUtils.getSysUser();
            //if (sysUser.getUserRole() == 1){// 是管理员
            // 根据ID获取账号信息
            User user = userMapper.findUserById(id);
            Subject subject = ShiroUtils.getSubject();
            //if (subject.isAuthenticated()){
            //    subject.logout();
            //}
            if (StringUtils.isNotNull(user)) {
                subject.login(new UsernamePasswordToken(user.getUserName(), user.getUserPassword()));
                log.info("新登录用户名" + ShiroUtils.getSysUser().getUserName());
                session.setAttribute("username", ShiroUtils.getSysUser().getUserName());
                return AjaxResult.success(true, "成功！");
            }
            //}else{
            //    return AjaxResult.error("您非管理员！");
            //}
        }
        return AjaxResult.error("参数格式错误！");
    }

    /**
     * 获取某月数据统计
     *
     * @param request
     * @return
     */
    public AjaxResult getMonthData(BaseRequest request) {
        Integer pid = null;
        if (request.getUserId() == null) {
            request.setUserId(ShiroUtils.getSysUser().getId());
        }
        if (request.getUserId().equals(0)) {
            request.setUserId(null);
            request.setParentId(ShiroUtils.getSysUser().getId());
        }
        List<PlotBarPhone> list = threenetsChildOrderMapper.getMonthData(request);
        list = formatterDate(list, request);
        return AjaxResult.success(list, "获取成功");
    }

    /**
     * 获取年份数据统计
     *
     * @param request
     * @return
     */
    public AjaxResult getYearData(BaseRequest request) {
        Integer pid = null;
        if (request.getUserId() == null) {
            request.setUserId(ShiroUtils.getSysUser().getId());
        }
        if (request.getUserId().equals(0)) {
            request.setUserId(null);
            request.setParentId(ShiroUtils.getSysUser().getId());
        }
        List<PlotBarPhone> list = threenetsChildOrderMapper.getYearData(request);
        list = formatterDate(list, request);
        return AjaxResult.success(list, "获取成功");
    }

    /**
     * 格式化数据
     *
     * @param list
     * @param request
     * @return
     */
    private List<PlotBarPhone> formatterDate(List<PlotBarPhone> list, BaseRequest request) {
        List<PlotBarPhone> newList = new ArrayList<>();
        Map<String, List<PlotBarPhone>> collect = list.stream().collect(Collectors.groupingBy(PlotBarPhone::getDateTimes));
        for (String time : collect.keySet()) {
            PlotBarPhone plotBarPhone = new PlotBarPhone();
            List<PlotBarPhone> plotBarPhones = collect.get(time);
            List<PlotBarPhone> monthly = plotBarPhones.stream().filter(plot -> plot.getIsMonthly().equals(2)).collect(Collectors.toList());
            List<PlotBarPhone> unsubscribe = plotBarPhones.stream().filter(plot -> plot.getIsMonthly().equals(3)).collect(Collectors.toList());
            plotBarPhone.setDateTimes(time);
            plotBarPhone.setAddUser(monthly.size() > 0 ? monthly.get(0).getAllNumber() : 0);
            plotBarPhone.setUnsubscribeUser(unsubscribe.size() > 0 ? unsubscribe.get(0).getAllNumber() : 0);
            if (plotBarPhone.getAddUser() == 0 && plotBarPhone.getUnsubscribeUser() == 0) {
                continue;
            }
            newList.add(plotBarPhone);
        }
        newList.sort((o1, o2) -> o2.getDateTimes().compareTo(o1.getDateTimes()));
        ThreenetsChildOrder threenetsChildOrder = new ThreenetsChildOrder();
        threenetsChildOrder.setUserId(request.getUserId());
        threenetsChildOrder.setOperator(request.getOperate());
        threenetsChildOrder.setIsMonthly(2);
        Integer count = threenetsChildOrderMapper.getCount(threenetsChildOrder);
        for (PlotBarPhone plotBarPhone : newList) {
            plotBarPhone.setCumulativeUser(count);
            count = count - plotBarPhone.getAddUser();
        }
        return newList;
    }

    /**
     * 匹配运营商
     *
     * @param phones
     * @return
     */
    public AjaxResult matchingOperate(String phones)throws Exception {
        String regR = "\n\r";
        String regN = "\n";
        phones = phones.replace(regR,"br").replace(regN,"br");
        String[] phone = phones.split("br");
        int yidong = 0;
        int dianxin = 0;
        int liantong = 0;
        int sum = 0;
        for (String p: phone) {
            boolean mobileNO = isMobileNO(p);
            if (!mobileNO){
                return AjaxResult.error("#号码"+p+"不正确！");
            }
            sum++;
            JuhePhone juhePhone = JuhePhoneUtils.getPhone(p);
            JuhePhoneResult result = (JuhePhoneResult)juhePhone.getResult();
            if (result.getCompany().equals("移动")){
                yidong++;
            }else if(result.getCompany().equals("联通")){
                liantong++;
            }else{
                dianxin++;
            }
        }
        Integer[] count = {yidong,dianxin,liantong,sum};
        return AjaxResult.success(count,"匹配成功");
    }

    /**
     * 验证手机号是否正确
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

}
