package com.hrtxn.ringtone.project.telcertification.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: yuanye
 * @Date: Created in 15:03 2019/9/18
 * @Description:
 * @Modified By:
 */
@Data
public class TelCerDistributor implements Serializable {
    /**
     * 渠道商名称
     */
    private String distributorName;
    /**
     * 级别
     */
    private String stage;
    /**
     * 业务开通总数
     */
    private Integer total;
    /**
     * 上月累计开通数
     */
    private Integer lastMonthTotal;
    /**
     * 当月新增开通数
     */
    private Integer theMonthTotal;
    /**
     * 昨日新增开通数
     */
    private Integer yesterdayTotal;
}
