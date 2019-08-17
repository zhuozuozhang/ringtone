package com.hrtxn.ringtone.project.threenets.kedas.kedasites.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.system.user.domain.UserVo;
import com.hrtxn.ringtone.project.system.user.mapper.UserMapper;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaChildOrder;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.mapper.KedaChildOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.domain.PlotBarPhone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Author:zcy
 * Date:2019-08-14 10:17
 * Description:疑难杂单业务处理层
 */
@Service
public class KedaChildOrderService {

    @Autowired
    private KedaChildOrderMapper kedaChildOrderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 获取近5日信息
     *
     * @return
     */
    public List<PlotBarPhone> getFiveData() {
        Integer id = ShiroUtils.getSysUser().getId();
        if (StringUtils.isNotNull(id)) {
            // 获取近5日信息
            List<PlotBarPhone> fiveData = kedaChildOrderMapper.getFiveData(id);
            BaseRequest baseRequest = new BaseRequest();
            baseRequest.setUserId(id);
            // 根据条件获取总数
            Integer count = kedaChildOrderMapper.getCount(baseRequest);
            for (PlotBarPhone plotBarPhone : fiveData) {
                plotBarPhone.setCumulativeUser(count);
                count = count - plotBarPhone.getAddUser();
            }
            return fiveData;
        }
        return null;
    }

    /**
     * 待办任务 等待铃音设置
     *
     * @param page
     * @param baseRequest
     * @return
     */
    public AjaxResult getKeDaChildOrderBacklogList(Page page, BaseRequest baseRequest) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        baseRequest.setUserId(ShiroUtils.getSysUser().getId());
        // 根据条件获取子订单列表
        List<KedaChildOrder> kedaChildOrders = kedaChildOrderMapper.getKeDaChildOrderBacklogList(page, baseRequest);
        // 获取数量
        Integer count = kedaChildOrderMapper.getCount(baseRequest);
        if (kedaChildOrders.size() > 0) {
            return AjaxResult.success(kedaChildOrders, "获取子订单数据成功！", count);
        }
        return AjaxResult.error("无数据!");
    }

    /**
     * 获取子订单列表
     *
     * @param page
     * @param baseRequest
     * @return
     */
    public AjaxResult getKedaChidList(Page page, BaseRequest baseRequest) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        // 根据条件获取子订单列表
        List<KedaChildOrder> keDaChildOrderBacklogList = kedaChildOrderMapper.getKeDaChildOrderBacklogList(page, baseRequest);
        // 获取总数
        Integer count = kedaChildOrderMapper.getCount(baseRequest);
        if (keDaChildOrderBacklogList.size() > 0) {
            return AjaxResult.success(keDaChildOrderBacklogList, "获取数据成功！", count);
        }
        return AjaxResult.error("无数据！");
    }

    /**
     * 获取业务发展数据
     *
     * @param type
     * @param baseRequest
     * @return
     * @throws Exception
     */
    public AjaxResult getBusinessData(Integer type, BaseRequest baseRequest) throws Exception {
        // 判断是否是当前用户
        if (!StringUtils.isNotNull(baseRequest.getUserId())) {
            baseRequest.setUserId(ShiroUtils.getSysUser().getId());
        }
        // 选中“全部汇总”
        if (baseRequest.getUserId().equals(0)) {
            baseRequest.setUserId(null);
            // 递归查询当前登陆者下的所有子代理商
            List<UserVo> userVoList = userMapper.findUserByparentId(null, null, ShiroUtils.getSysUser().getId());
            List<UserVo> node = getNode(userVoList);
            // 获取当前登陆者下的所有子代理商ID
            Integer[] id = new Integer[node.size()];
            for (int i = 0; i < node.size(); i++) {
                id[i] = node.get(i).getId();
            }
            baseRequest.setArrayById(id);
        }
        // 判断月份是否为空
        if (!StringUtils.isNotEmpty(baseRequest.getMonth())) {
            if (type == 1) {
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM");
                baseRequest.setMonth(f.format(new Date()));
            } else {
                SimpleDateFormat f = new SimpleDateFormat("yyyy");
                baseRequest.setMonth(f.format(new Date()));
            }
        }
        // 根据条件查询子订单已包月数据
        List<PlotBarPhone> plotBarPhoneList = kedaChildOrderMapper.getIsMonthly(type, baseRequest);
        // 根据条件查询子订单已退订数据
        List<PlotBarPhone> unsubscribeList = kedaChildOrderMapper.getUnsubscribe(type, baseRequest);
        plotBarPhoneList = dataFormate(plotBarPhoneList, unsubscribeList, 1);
        // 根据条件获取总数
        int count = kedaChildOrderMapper.getBussinessCount(type, baseRequest);
        for (int i = 0; i < plotBarPhoneList.size(); i++) {
            plotBarPhoneList.get(i).setCumulativeUser(count);
            count = count - plotBarPhoneList.get(i).getAddUser();
        }
        return AjaxResult.success(plotBarPhoneList, "获取数据成功！");
    }

    /**
     * 递归获取子账号
     *
     * @param users
     * @return
     * @throws Exception
     */
    public List<UserVo> getNode(List<UserVo> users) throws Exception {
        if (users != null && users.size() != 0) {
            for (int i = 0; i < users.size(); i++) {
                List<UserVo> list = userMapper.findUserByparentId(null, null, users.get(i).getId());
                if (list != null && list.size() != 0) {
                    for (UserVo user : list) {
                        users.add(user);
                    }
                    getNode(list);
                }
            }
        }
        return users;
    }

    /**
     * 数据格式化
     *
     * @param plotBarPhoneList
     * @param plotBarUnsubscribePhoneList
     * @param type
     * @return
     * @throws ParseException
     */
    private List<PlotBarPhone> dataFormate(List<PlotBarPhone> plotBarPhoneList, List<PlotBarPhone> plotBarUnsubscribePhoneList, Integer type) throws ParseException {
        DateFormat dateFormat;
        if (type == 1) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            dateFormat = new SimpleDateFormat("yyyy");
        }

        // 比较增加人数和退订人数的大小
        if (plotBarPhoneList.size() >= plotBarUnsubscribePhoneList.size()) {
            for (int i = 0; i < plotBarPhoneList.size(); i++) {
                for (int j = 0; j < plotBarUnsubscribePhoneList.size(); j++) {
                    if ((plotBarPhoneList.get(i).getDateTimes()).equals(plotBarUnsubscribePhoneList.get(j).getDateTimes())) {
                        plotBarPhoneList.get(i).setUnsubscribeUser(plotBarUnsubscribePhoneList.get(j).getUnsubscribeUser());
                        plotBarUnsubscribePhoneList.remove(j);
                        break;
                    } else {
                        plotBarPhoneList.get(i).setUnsubscribeUser(0);
                    }
                }
            }
            if (plotBarUnsubscribePhoneList.size() > 0) {
                for (int i = 0; i < plotBarUnsubscribePhoneList.size(); i++) {
                    plotBarPhoneList.add(plotBarUnsubscribePhoneList.get(i));
                }
                for (int i = 0; i < plotBarPhoneList.size(); i++) {
                    for (int j = i; j < plotBarPhoneList.size(); j++) {
                        if (dateFormat.parse(plotBarPhoneList.get(i).getDateTimes()).getTime() < dateFormat.parse(plotBarPhoneList.get(j).getDateTimes()).getTime()) {
                            PlotBarPhone plotBarPhone = plotBarPhoneList.get(i);
                            plotBarPhoneList.set(i, plotBarPhoneList.get(j));
                            plotBarPhoneList.set(j, plotBarPhone);
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < plotBarUnsubscribePhoneList.size(); i++) {
                for (int j = 0; j < plotBarPhoneList.size(); j++) {
                    if ((plotBarUnsubscribePhoneList.get(i).getDateTimes()).equals(plotBarPhoneList.get(j).getDateTimes())) {
                        plotBarUnsubscribePhoneList.get(i).setAddUser(plotBarPhoneList.get(j).getAddUser());
                        plotBarPhoneList.remove(j);
                        break;
                    } else {
                        plotBarUnsubscribePhoneList.get(i).setAddUser(0);
                    }
                }
            }
            if (plotBarPhoneList.size() > 0) {
                for (int i = 0; i < plotBarPhoneList.size(); i++) {
                    plotBarUnsubscribePhoneList.add(plotBarPhoneList.get(i));
                }
                for (int i = 0; i < plotBarUnsubscribePhoneList.size(); i++) {
                    for (int j = i; j < plotBarUnsubscribePhoneList.size(); j++) {
                        if (dateFormat.parse(plotBarUnsubscribePhoneList.get(i).getDateTimes()).getTime() < dateFormat.parse(plotBarUnsubscribePhoneList.get(j).getDateTimes()).getTime()) {
                            PlotBarPhone plotBarPhone = plotBarUnsubscribePhoneList.get(i);
                            plotBarUnsubscribePhoneList.set(i, plotBarUnsubscribePhoneList.get(j));
                            plotBarUnsubscribePhoneList.set(j, plotBarPhone);
                        }
                    }
                }
            }
            plotBarPhoneList = plotBarUnsubscribePhoneList;
        }
        return plotBarPhoneList;
    }
}
