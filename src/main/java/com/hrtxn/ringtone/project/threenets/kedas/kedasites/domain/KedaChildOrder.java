package com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Author:zcy
 * Date:2019-08-13 13:24
 * Description:科大子订单
 */
@Data
public class KedaChildOrder implements Serializable {
    // ID
    private Integer id;
    // 企业名称
    private String companyName;
    // 联系人
    private String linkMan;
    // 联系电话
    private String linkTel;
    // 运营商
    private Integer operate;
    // 省
    private String province;
    // 市
    private String city;
    // 创建时间
    private Date createTime;
    // 子订单状态
    private String status;
    // 集团ID（运营商返回）
    private String operateId;
    // 铃音ID
    private Integer ringId;
    // 铃音名称
    private String ringName;
    // 是否包月（1.未包月/2.已包月/3.已退订）
    private Integer isMonthiy;
    // 是否开通彩铃（1.未开通/2.已开通/3.开通失败）
    private Integer isRingtoneUser;
    // 用户ID
    private Integer userId;
    // 备注
    private String remark;
}