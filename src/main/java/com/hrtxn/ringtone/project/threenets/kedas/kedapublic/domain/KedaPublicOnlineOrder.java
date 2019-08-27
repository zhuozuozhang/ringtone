package com.hrtxn.ringtone.project.threenets.kedas.kedapublic.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author:zcy
 * @Date:2019-08-24 16:23
 * @Description:商务彩铃在线办理
 */
@Data
public class KedaPublicOnlineOrder implements Serializable {
    /**公众号在线办理订单表*/
    private Integer id;
    /**姓名*/
    private String name;
    /**手机号*/
    private String phone;
    /**公司名*/
    private String companyName;
    /**创建时间*/
    private Date createTime;
}
