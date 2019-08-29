package com.hrtxn.ringtone.project.telcertification.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Author:yuanye
 * Date:2019/8/29 14:12
 * Description:充值记录
 */
@Data
public class CertificationRechargeLog implements Serializable {
    //号码认证充值记录id
    private Integer id;
    //用户名（默认为用户号码）
    private String userName;
    //充值号码
    private String userTel;
    //充值金额
    private Float price;
    //充值时间
    private Date rechargeTime;
    //操作者（当前登录的账户的id）
    private Integer operatorId;
    //备注
    private String remark;
}