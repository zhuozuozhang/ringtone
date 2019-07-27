package com.hrtxn.ringtone.project.system.rechargelog.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Author:zcy
 * Date:2019-07-08 14:28
 * Description:充值记录实体类
 */
@Data
public class RechargeLog implements Serializable {
    // 充值记录ID
    private Integer id;
    // 充值金额
    private Float rechargePrice;
    // 充值后金额
    private Float rechargeMoney;
    // 充值时间
    private Date rechargeTime;
    // 充值类型（1.号码认证）
    private Integer rechargeType;
    // 充值账户
    private String userName;
    // 操作者
    private String rechargeOperator;
    // 充值账户ID
    private Integer userId;
    // 备注
    private String rechargeRemark;
}
