package com.hrtxn.ringtone.project.threenets.threenet.domain;

import lombok.Data;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * Author:zcy
 * Date:2019-07-12 13:59
 * Description:三网订单
 */
@Data
public class ThreenetsOrder implements Serializable {
    // 订单
    private Integer id;
    // 运营商（1.移动/2.电信/3.联通）
    private Integer operator;
    // 企业名称
    private String companyName;
    // 企业联系人
    private String companyLinkman;
    // 联系人电话
    private String linkmanTel;
    // 所在省
    private String province;
    // 所在市
    private String city;
    // 创建时间
    private Date createTime;
    // 状态（审核通过）
    private String status;
    // 用户ID
    private Integer userId;

    /**************************VO**********************************/
    //----子订单数
    private Integer childOrderQuantity;
    //----包月子订单数
    private Integer childOrderQuantityByMonthly;
    // 总数/包月
    private String peopleNum;
    // 用户名称
    private String userName;


    //-------------------------------提交三网文件--------------------------------------
    private File serviceProtocolFile;

    private File businesslicenseFile;

    private File upLoadAgreement;

    private String mcardId;//音乐名片系统id   和咪咕id
    private String verifycode;//音乐名片 扣费验证码
    private String feePhone;//音乐名片 扣费手机号
    private String makeFee;//音乐名片制作费
}