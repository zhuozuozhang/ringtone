package com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Author:zcy
 * Date:2019-08-13 13:24
 * Description:科大铃音
 */

@Data
public class KedaRing implements Serializable {
    // ID
    private Integer id;
    // 铃音名称
    private String ringName;
    // 铃音状态（1.待审核/2.激活中/3.激活成功/4.激活失败）
    private Integer ringStatus;
    // 创建时间
    private Date createTime;
    // 集团ID（运营商返回）
    private String opertateId;
    // 父级订单表ID
    private Integer orderId;
    // 铃音编号（运营商返回）
    private String ringNum;
    // 铃音路径（运营商返回）
    private String ringUrl;
    // 铃音本地路径
    private String ringPath;
    // 铃音内容
    private String ringContent;
    // 备注
    private String remark;

}