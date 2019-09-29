package com.hrtxn.ringtone.project.threenets.threenet.domain;

import lombok.Data;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * Author:zcy
 * Date:2019-07-12 13:55
 * Description:三网铃音
 */
@Data
public class ThreenetsRing implements Serializable {
    // ID
    private Integer id;
    // 铃音名称
    private String ringName;
    // 铃音类型（音频/视频）
    private String ringType;
    // 铃音状态（1.待审核/2.激活中/3.激活成功/4.部分省份激活超时/5.部分省份激活成功/6.激活失败）
    private Integer ringStatus;
    // 运营商（1.移动/2.电信/3.联通）
    private Integer operate;
    // 三网父级订单ID
    private Integer orderId;
    // 运营商订单编号
    private String operateId;
    // 运营商返回的铃音编号
    private String operateRingId;
    // 铃音存放相对路径
    private String ringWay;
    // 铃音内容
    private String ringContent;
    // 铃音创建时间
    private Date createTime;
    // 备注
    private String remark;

    /***************VO******************/
    // 渠道商名称
    private String userName;
    // 商户名称
    private String companyName;
    // 铃音数量
    private Integer ringCount;
    //
    private File file;

    private String fileUrl;

}