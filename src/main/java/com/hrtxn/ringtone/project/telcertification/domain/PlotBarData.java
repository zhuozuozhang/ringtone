package com.hrtxn.ringtone.project.telcertification.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: yuanye
 * @Date: Created in 9:45 2019/10/18
 * @Description: 号码认证 订购统计 折线图
 * @Modified By:
 */
@Data
public class PlotBarData implements Serializable {
    private String dateTimes;
    private int cumulativeUser;//累计用户
    private int addUser;//新增用户
    private Integer allNumber;//统计个数
}
