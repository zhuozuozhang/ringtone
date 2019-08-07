package com.hrtxn.ringtone.common.domain;

import lombok.Data;

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

    private String companyName;

    private String month;

    private String year;
    // 用户状态
    private Integer userStatus;


    //企业资质
    private String companyUrl;
    //客户确认涵
    private String clientUrl;
    //主体证明
    private String mainUrl;
    //免短协议
    private String protocolUrl;
}
