package com.hrtxn.ringtone.project.system.consumelog.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Author:yuanye
 * Date:2019-08-30 10:24
 * Description:消费记录实体类
 */
@Data
public class ConsumeLog implements Serializable {
    // 消费记录id
    private Integer id;
    // 消费金额
    private Float consumePrice;
    // 消费后金额
    private Float consumeMoney;
    // 消费时间
    private Date consumeTime;
    // 消费类型（1.号码认证）
    private Integer consumeType;
    // 操作者
    private String consumeOperator;
    // 消费账户（默认为账户号码）
    private String userName;
    // 消费账户号码
    private String userTel;
    // 号码认证消费记录类型（1.首次/2.续费）
    private Integer telConsumeLogType;
    // 号码认证消费记录状态（1.开通中/2.开通成功/3.开通失败/4.续费中/5.续费成功/6.续费失败）
    private Integer telConsumeLogStatus;
    // 号码认证开通时间
    private Date telConsumeLogOpenTime;
    // 号码认证到期时间
    private Date telConsumeLogExpireTime;
    // 消费记录账户ID
    private Integer userId;
    // 备注
    private String consumeRemark;

    /*************************VO***************************/
    // 消费记录类型名称
    private String consumeTypeName;
    // 号码认证消费记录类型名称
    private String telConsumeLogTypeName;
    // 号码认证消费记录状态名称
    private String telConsumeLogStatusName;
}