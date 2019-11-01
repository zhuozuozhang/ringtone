package com.hrtxn.ringtone.project.telcertification.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Author:yuanye
 * Date:2019/8/15 16:47
 * Description:号码认证订单
 */
@Data
public class CertificationOrder implements Serializable {

    //号码认证订单ID
    private Integer id;
    //号码认证订单编号
    private String telOrderNum;
    //集团名称
    private String telCompanyName;
    //联系人姓名
    private String telLinkName;
    //联系人电话
    private String telLinkPhone;
    //认证展示内容
    private String telContent;
    //营业执照路径
    private String businessLicense;
    //法人身份证正面路径
    private String legalPersonCardZhen;
    //法人身份证反面路径
    private String legalPersonCardFan;
    //logo路径
    private String logo;
    //授权书路径
    private String authorization;
    //号码证明
    private String numberProve;
    //备注
    private String remark;
    //单价
    private Float unitPrice;
    //用户ID
    private Integer userId;
    //号码认证订单状态（1.审核中/2.开通中/3.审核失败/4.开通成功/5.续费成功/6.续费失败）
    private Integer telOrderStatus;
    //订单创建时间
    private Date telOrderTime;
    //产品业务名称
    private String productName;

    /**************************VO**********************************/
    //成员数
    private int memberNum;
    //状态名(回显用)
    private String statusName;
    //成员电话号
    private String phoneNum;

    private String userName;

    private String statusStr;
}