package com.hrtxn.ringtone.project.system.user.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Author:zcy
 * Date:2019-06-29 15:26
 * Description:用户实体类
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    // 用户ID
    private Integer id;
    // 用户名称
    private String userName;
    //密码
    private String userPassword;
    // 邮箱
    private String userEmail;
    // 固话
    private String userGuhua;
    // 手机号
    private String userTel;
    // 登录拒绝次数
    private Integer userReject;
    // 创建时间
    private Date userTime;
    // 登录IP
    private String loginIp;
    // 最新登录时间
    private Date loginTime;
    // 父级ID
    private Integer parentId;
    // 用户角色（1.管理员/2.代理商）
    private Integer userRole;
    // 用户状态(1.true启用/0.false禁止)
    private Boolean userStatus;
    // 用户QQ
    private String userQq;
    // 号码认证账户余额
    private Float telcertificationAccount;
    // 用户所在省
    private String province;
    // 用户所在省
    private String city;
    // 省份证正面路径
    private String userCardZhen;
    // 省份证反面路径
    private String userCardFan;

}