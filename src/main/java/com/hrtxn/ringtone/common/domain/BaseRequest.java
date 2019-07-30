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
}
