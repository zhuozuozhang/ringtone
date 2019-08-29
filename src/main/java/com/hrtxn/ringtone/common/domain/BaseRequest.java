package com.hrtxn.ringtone.common.domain;

import lombok.Data;

import java.util.Date;

@Data
public class BaseRequest {

    private Integer id;

    private Integer userId;

    private Integer parentId;

    private String tel;

    private String name;

    private Integer operator;

    private Integer isMonthly;

    private Integer timeType;

    private Integer orderId;

    private Integer parentOrderId;

    private String companyName;

    private String month;

    private String year;
    // 用户状态
    private Integer userStatus;

    private String status;
    //企业资质
    private String companyUrl;
    //客户确认涵
    private String clientUrl;
    //主体证明
    private String mainUrl;
    //免短协议
    private String protocolUrl;

    //id集合  1，2，3，4
    private Integer[] arrayById;

    private String start;

    private String end;

    private String linkMan;

    private String mianduan;

    //号码认证订单编号
    private String telOrderNum;
    //商户名称/集团名称
    private String telCompanyName;
    //联系电话
    private String telLinkPhone;
    //提交时间/创建时间
    private Date telOrderTime;
    //成员电话号码
    private String phoneNum;
    //从前端获得这样的时间段
    //2019-08-14 - 2019-09-24
    private String rangetime;

    private String phoneNumberList;
}
