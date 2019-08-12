package com.hrtxn.ringtone.project.threenets.threenet.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Author:zcy
 * Date:2019-07-12 14:02
 * Description:三网子订单
 */
@Data
public class ThreenetsChildOrder implements Serializable {
    // 子订单
    private Integer id;
    // 联系人
    private String linkman;
    // 联系人电话
    private String linkmanTel;
    // 状态（审核通过）
    private String status;
    // 创建日期
    private Date createDate;
    // 铃音名称
    private String ringName;
    // 铃音ID
    private Integer ringId;
    // 父级订单ID
    private Integer parentOrderId;
    // 运营商订单编号
    private String operateId;
    // 运营商子订单ID（移动专属）
    private String operateOrderId;
    // 是否包月（1.未包月/2.已包月/3.已退订）
    private Integer isMonthly;
    // 支付方式（0.个付/1.统付）
    private Integer paymentType;
    // 是否是视频彩铃用户（移动专属 0.否/1.是）
    private Boolean isVideoUser;
    // 是否是彩铃用户（0.否/1.是）
    private Boolean isRingtoneUser;
    // 是否回复短信（0.否/1.是）
    private Boolean isReplyMessage;
    // 所在省
    private String province;
    // 所在市
    private String city;
    // 运营商（1.移动/2.电信/3.联通）
    private Integer operator;
    // 用户ID
    private Integer userId;
    // 运营商返回的备注信息
    private String remark;

    //----
    private String companyLinkman;

    private String start;

    private String end;

    private String memberTels;
    //移动支付价格
    private Integer miguPrice;
    //移动高资费
    private Integer specialPrice;
    // 联通支付价格
    private Integer swxlPrice;

    //
    private String month;

    private String year;

}