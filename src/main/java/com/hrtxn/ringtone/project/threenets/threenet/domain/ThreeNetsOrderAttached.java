package com.hrtxn.ringtone.project.threenets.threenet.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * Author:zcy
 * Date:2019-07-29 13:38
 * Description:三网整合工具类
 */
@Data
public class ThreeNetsOrderAttached implements Serializable {
    // id
    private Integer id;
    // 移动运营商ID
    private String miguId;
    // 电信运营商ID
    private String mcardId;
    // 联通运营商ID
    private String swxlId;
    // 移动支付价格
    private Integer miguPrice;
    // 电信支付价格
    private Integer mcardPrice;
    // 联通支付价格
    private Integer swxlPrice;
    // 父级订单ID
    private Integer parentOrderId;
    // 营业执照/企业资质路径（电信专属）
    private String businessLicense;
    // 客户确认函路径（电信专属）
    private String confirmLetter;
    // 主题证明路径（电信专属）
    private String subjectProve;
    // 免短协议路径（联通专属）
    private String avoidShortAgreement;
    //电信订单是否审核（0.待审核、1已审核）
    private Integer mcardStatus;
    //联通订单是否审核（0.待审核、1已审核）
    private Integer swxlStatus;
    //移动订单是否审核（0.待审核、1已审核）
    private Integer miguStatus;

}