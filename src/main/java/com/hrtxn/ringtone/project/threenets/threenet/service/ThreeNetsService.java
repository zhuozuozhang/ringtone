package com.hrtxn.ringtone.project.threenets.threenet.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.DateUtils;
import com.hrtxn.ringtone.common.utils.PhoneUtils;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.common.utils.juhe.JuhePhoneUtils;
import com.hrtxn.ringtone.project.system.json.JuhePhone;
import com.hrtxn.ringtone.project.system.json.JuhePhoneResult;
import com.hrtxn.ringtone.project.system.user.domain.User;
import com.hrtxn.ringtone.project.system.user.domain.UserVo;
import com.hrtxn.ringtone.project.system.user.mapper.UserMapper;
import com.hrtxn.ringtone.project.threenets.threenet.domain.PlotBarPhone;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreeNetsOrderAttached;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsChildOrder;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreeNetsOrderAttachedMapper;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsChildOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsOrderMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.hrtxn.ringtone.common.utils.PhoneUtils.isFixedPhone;

/**
 * Author:lile
 * Date:2019-07-11 10:28
 * Description:三网业务层
 */
@Service
@Slf4j
public class ThreeNetsService {

    @Autowired
    private ThreenetsOrderMapper threenetsOrderMapper;

    @Autowired
    private ThreeNetsOrderAttachedMapper threeNetsOrderAttachedMapper;

    @Autowired
    private ThreenetsChildOrderMapper threenetsChildOrderMapper;
    @Autowired
    private UserMapper userMapper;


    /**
     * 根据父级id获取附表
     *
     * @param id
     * @return
     */
    public ThreeNetsOrderAttached getOrderAttached(Integer id) {
        return threeNetsOrderAttachedMapper.selectByParentOrderId(id);
    }

    /**
     * @param id
     * @param session
     * @return
     * @throws Exception
     */
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
    public AjaxResult getMonthData(BaseRequest request) throws Exception {
        if (request.getUserId() == null) {
            request.setUserId(ShiroUtils.getSysUser().getId());
        }
        if (request.getUserId().equals(0)) {
            request.setUserId(ShiroUtils.getSysUser().getUserRole().equals(1) ? null : ShiroUtils.getSysUser().getId());
            request.setArrayById(getChildUserList());
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
    public AjaxResult getYearData(BaseRequest request) throws Exception {
        if (request.getUserId() == null) {
            request.setUserId(ShiroUtils.getSysUser().getId());
        }
        if (request.getUserId().equals(0)) {
            request.setUserId(ShiroUtils.getSysUser().getUserRole().equals(1) ? null : ShiroUtils.getSysUser().getId());
            request.setArrayById(getChildUserList());
        }
        List<PlotBarPhone> list = threenetsChildOrderMapper.getYearData(request);
        list = formatterDate(list, request);
        return AjaxResult.success(list, "获取成功");
    }

    public Integer[] getChildUserList() throws Exception {
        Page page = new Page(0, 9999);
        Integer id = ShiroUtils.getSysUser().getId();
        List<UserVo> list = userMapper.getUserList(null, null);
        Map<Integer, List<UserVo>> map = list.stream().collect(Collectors.groupingBy(User::getParentId));
        List<User> userList = new ArrayList<>();
        recursion(map.get(id), map, userList);
        Integer[] ids = new Integer[userList.size() + 1];
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            ids[i] = user.getId();
        }
        ids[userList.size()] = id;
        return ids;
    }

    //递归
    private void recursion(List<UserVo> list, Map<Integer, List<UserVo>> map, List<User> userList) {
        if (list == null || list.isEmpty()) {
            return;
        }
        userList.addAll(list);
        for (int i = 0; i < list.size(); i++) {
            UserVo vo = list.get(i);
            List<UserVo> vos = map.get(vo.getId());
            recursion(vos, map, userList);
        }
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
        threenetsChildOrder.setOperator(request.getOperator());
        threenetsChildOrder.setIsMonthly(2);
        threenetsChildOrder.setMonth(request.getMonth());
        threenetsChildOrder.setYear(request.getYear());
        Integer count = threenetsChildOrderMapper.getCount(threenetsChildOrder);
        for (PlotBarPhone plotBarPhone : newList) {
            plotBarPhone.setCumulativeUser(count);
            count = count - plotBarPhone.getAddUser();
            count = count + plotBarPhone.getUnsubscribeUser();
        }
        if (newList.isEmpty()) {
            String month = DateUtils.getMonth() < 10 ? "0" + DateUtils.getMonth() : DateUtils.getMonth() + "";
            String day = DateUtils.getDay() < 10 ? "0" + DateUtils.getDay() : DateUtils.getDay() + "";
            String time = DateUtils.getYear() + "-" + month + (StringUtils.isEmpty(request.getYear()) ? "-" + day : "");
            PlotBarPhone plotBarPhone = new PlotBarPhone();
            plotBarPhone.setDateTimes(time);
            plotBarPhone.setAddUser(0);
            plotBarPhone.setUnsubscribeUser(0);
            plotBarPhone.setCumulativeUser(count);
            newList.add(plotBarPhone);
        }
        return newList;
    }

    /**
     * 匹配运营商
     *
     * @param phones
     * @return
     */
    public AjaxResult matchingOperate(String phones, Integer parentOrderId) throws Exception {
        String regR = "\n\r";
        String regN = "\n";
        phones = phones.replace(regR, "br").replace(regN, "br");
        String[] phone = phones.split("br");
        int yidong = 0;
        int dianxin = 0;
        int liantong = 0;
        int sum = 0;
        for (String p : phone) {
            p = p.replace(" ", "");
            //首先验证是固定电话还是手机号
            boolean mobileNO = PhoneUtils.isMobileNO(p);
            boolean fixedPhone = PhoneUtils.isFixedPhone(p);
            if (!mobileNO && !fixedPhone) {
                return AjaxResult.error("#号码" + p + "不正确！");
            }
            sum++;
            JuhePhone juhePhone = JuhePhoneUtils.getPhone(p);
            JuhePhoneResult result = (JuhePhoneResult) juhePhone.getResult();
            if (result.getCompany().equals("移动")) {
                yidong++;
            } else if (result.getCompany().equals("联通")) {
                liantong++;
            } else {
                dianxin++;
            }
        }
        ThreeNetsOrderAttached attached = threeNetsOrderAttachedMapper.selectByParentOrderId(parentOrderId);
        JSONObject outData = new JSONObject();
        outData.put("mobile", yidong);
        outData.put("telecom", dianxin);
        outData.put("unicom", liantong);
        outData.put("total", sum);
        if (attached != null) {
            outData.put("mobileStatus", StringUtils.isEmpty(attached.getMiguId()));
            outData.put("telecomStatus", StringUtils.isEmpty(attached.getMcardId()));
            outData.put("unicomStatus", StringUtils.isEmpty(attached.getSwxlId()));
        }
        return AjaxResult.success(outData, "匹配成功");
    }
}
