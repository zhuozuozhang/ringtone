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
     * 订购统计渠道商信息id
     */
    private Integer id;
    /**
     * 渠道商id
     */
    private Integer distributorId;
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
     * 今日新增开通数
     */
    private Integer todayTotal;
    /**
     * 泰迪熊开通个数
     */
    private Integer teddyNum;
    /**
     * 电话邦开通个数
     */
    private Integer telBondNum;
    /**
     * 彩印开通个数
     */
    private Integer colorPrintNum;
    /**
     * 挂机短信开通个数
     */
    private Integer hangupMessageNum;
    /**
     * 泰迪熊和电话邦的总价
     */
    private Float teddyAndTelBond;
    /**
     * 挂机短信（用户输入的）
     */
    private Float hangUpMessage;
}
