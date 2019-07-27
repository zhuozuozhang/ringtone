package com.hrtxn.ringtone.project.threenets.threenet.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * Author:zcy
 * Date:2019-07-19 16:30
 * Description:号码开通情况（待办数据统计、折线图）
 */
@Data
public class PlotBarPhone implements Serializable {
    private String dateTimes;//日期，月份
    private int cumulativeUser;//累计用户
    private int addUser;//新增用户
    private int unsubscribeUser;// 退订用户
    private Integer isMonthly;//是否包月
    private Integer allNumber;//统计个数
}
