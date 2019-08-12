package com.hrtxn.ringtone.project.system.charts.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.system.charts.mapper.ChartsMapper;
import com.hrtxn.ringtone.project.threenets.threenet.domain.PlotBarPhone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Author:zcy
 * Date:2019-08-12 10:40
 * Description:管理端 数据统计业务处理层
 */
@Service
public class ChartsService {

    @Autowired
    private ChartsMapper chartsMapper;

    public AjaxResult echartsData(String start, Integer operate, Integer type) throws ParseException {
        // 判断时间是否为空，为空则获取当前时间
        if (!StringUtils.isNotEmpty(start)) {
            if (type == 1) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
                start = simpleDateFormat.format(new Date());
            } else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
                start = simpleDateFormat.format(new Date());
            }
        }
        List<PlotBarPhone> plotBarPhoneList = new ArrayList<>();
        List<PlotBarPhone> plotBarUnsubscribePhoneList = new ArrayList<>();
        if (type == 1){
            // 根据日期和运营商获取已包月子账号数据
            plotBarPhoneList = chartsMapper.getEchartsData(start, operate);
            // 根据日期和运营商获取退订子账号数据
            plotBarUnsubscribePhoneList = chartsMapper.getUnsubscribeData(start, operate);
        } else {
            // 根据日期和运营商获取已包月子账号数据
            plotBarPhoneList = chartsMapper.getYearEchartsData(start, operate);
            // 根据日期和运营商获取退订子账号数据
            plotBarUnsubscribePhoneList = chartsMapper.getYearUnsubscribeData(start, operate);
        }
        plotBarPhoneList = dataFormate(plotBarPhoneList, plotBarUnsubscribePhoneList, type);
        // 获取总数
        int count = 0;
        if (type == 1) {
            count = chartsMapper.getCount(start, operate);
        } else {
            count = chartsMapper.getYearCount(start, operate);
        }
        for (int i = plotBarPhoneList.size() - 1; i >= 0; i--) {
            plotBarPhoneList.get(i).setCumulativeUser(count);
            count = count - plotBarPhoneList.get(i).getAddUser();
        }
        String[] dataTime = new String[plotBarPhoneList.size()];
        int[] addUser = new int[plotBarPhoneList.size()];
        int[] cumulativeUser = new int[plotBarPhoneList.size()];
        int[] unsubscribeUser = new int[plotBarPhoneList.size()];
        if (StringUtils.isNotNull(plotBarPhoneList)) {
            for (int i = 0; i < plotBarPhoneList.size(); i++) {
                dataTime[i] = plotBarPhoneList.get(i).getDateTimes();
                addUser[i] = plotBarPhoneList.get(i).getAddUser();
                cumulativeUser[i] = plotBarPhoneList.get(i).getCumulativeUser();
                unsubscribeUser[i] = plotBarPhoneList.get(i).getUnsubscribeUser();
            }
        }

        HashMap map = new HashMap();
        map.put("dataTime", dataTime);
        map.put("addUser", addUser);
        map.put("cumulativeUser", cumulativeUser);
        map.put("unsubscribeUser", unsubscribeUser);
        return AjaxResult.success(map, "获取数据成功！");
    }


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
