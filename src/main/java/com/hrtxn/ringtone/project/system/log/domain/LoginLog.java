package com.hrtxn.ringtone.project.system.log.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Author:zcy
 * Date:2019-07-04 09:18
 * Description:登录记录实体类
 */
@Data
public class LoginLog implements Serializable {

    // 登录记录ID
    private Integer loginLogId;
    // 登录名
    private String loginLogUsername;
    // IP地址
    private String ipAdress;
    // 登录地点
    private String loginLocation;
    // 登录时间
    private Date loginLogTime;
    // 登陆状态
    private String loginLogStatus;
}