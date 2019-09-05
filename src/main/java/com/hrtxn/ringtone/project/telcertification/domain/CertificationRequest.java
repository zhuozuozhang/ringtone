package com.hrtxn.ringtone.project.telcertification.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yuanye
 * @Date: Created in 14:44 2019/9/3
 * @Description:
 * @Modified By:
 */
@Data
public class CertificationRequest implements Serializable{

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
    //号码认证订单状态（1.开通中/2.开通成功/3.开通失败/4.续费中/5.续费成功/6.续费失败）
    private Integer telOrderStatus;
    //订单创建时间
    private Date telOrderTime;
    //产品业务名称
    private String productName;


    // 泰迪熊
    private String teddy;
    // 泰迪熊年份
    private Integer year;
    // 电话邦
    private String telBond;
    // 电话邦年份
    private Integer telBondYear;
    // 彩印
    private String colorPrint;
    // 挂机短信
    private String hangUpMessage;
    // 挂机短信每月条数
    private Integer itemPerMonth;
    // 挂机短信价格
    private Float hangUpMessagePrice;

    //添加号码
    private String[] phoneNumberArray;
    //父id
    private Integer parentOrderId;
}
