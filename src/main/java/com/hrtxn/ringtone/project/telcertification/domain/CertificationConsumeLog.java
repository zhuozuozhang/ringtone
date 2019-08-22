package com.hrtxn.ringtone.project.telcertification.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CertificationConsumeLog implements Serializable {
    //号码认证消费记录ID
    private Integer id;
    //用户名
    private String userName;
    //用户号码
    private String userTel;
    //号码认证消费记录类型（1.首次/2.续费）
    private Integer telConsumeLogType;
    //号码认证消费记录状态（1.开通中/2.开通成功/3.开通失败/4.续费中/5.续费成功/6.续费失败）
    private Integer telConsumeLogStatus;
    //号码认证消费记录资费
    private Float telconsumeLogPrice;
    //提交时间
    private Date telConsumeLogCtime;
    //开通时间
    private Date telConsumeLogOpenTime;
    //到期时间
    private Date telConsumeLogExpireTime;
    //用户ID
    private Integer userId;
}