package com.hrtxn.ringtone.common.domain;

import lombok.Data;

/**
 * Author:lile
 * Date:2019/7/24 17:52
 * Description:
 */
@Data
public class OrderRequest {


    // 企业名称
    private String companyName;
    // 企业联系人
    private String companyLinkman;
    // 联系人电话
    private String linkmanTel;
    //成员电话
    private String memberTels;
    //铃音名称
    private String ringName;
    //铃音地址
    private String ringUrl;
    //铃音内容
    private String ringContent;

    //-------------------移动----------------
    //移动资费
    private String mobilePay;
    //移动高资费
    private String specialPrice;
    //-------------------电信----------------

    //企业资质
    private String companyUrl;
    //客户确认涵
    private String clientUrl;
    //主体证明
    private String mainUrl;

    //-------------------联通----------------
    //联通资费
    private String umicomPay;
    //是否免短
    private String mianduan;
    //免短协议
    private String protocolUrl;
    //支付方式
    private String paymentType;

    //随机id用于保存
    private Integer id;

    private Integer operate;

}
