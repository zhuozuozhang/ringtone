package com.hrtxn.ringtone.project.numcertification.domain;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 400号码认证订单
 *
 * @author zcy
 * @date 2019-8-30 11:00
 */
@Data
public class NumcertificationOrder implements Serializable {
    private Integer id;
    /***/
    private Integer userId;
    /***/
    private Date createTime;
    /**号码*/
    private String phoneNum;
    /**运营商*/
    private String provider;
    /**价格*/
    private BigDecimal price;
    /**套餐模式*/
    private String comboPattern;
    /**充值年限*/
    private Integer ageLimit;
    /**绑定号码-*/
    private String bindPhone;
    /**号码使用地-省*/
    private String province;
    /**号码使用地-市*/
    private String city;
    /**公司名*/
    private String companyName;
    /**法人姓名*/
    private String legalPersionName;
    /**法人电话*/
    private String legalPersionPhone;
    /**法人身份证号码*/
    private String legalPersionCardNum;
    /**公司注册地址*/
    private String companyAddress;
    /**电子邮箱*/
    private String email;
    /**经营许可证号*/
    private String businessCertificateNum;
    /**行业*/
    private String business;
    /**法人身份证地址*/
    private String legalPersionCardAddress;
    /**固定电话*/
    private String fixPhone;
    /**开户银行*/
    private String openAccountBank;
    /**公司账户 银行账户*/
    private String bankAccount;
    /**营业执照*/
    private String businessLicenseUrl;
    /**受理单*/
    private String acceptanceFormUrl;
    /**服务协议*/
    private String serviceAgreementUrl;
    /**安全责任承诺书*/
    private String securityResponsibilityCommitmentUrl;
    /**法人身份证（正面）*/
    private String legalPersionCardZUrl;
    /**法人身份证（反面）*/
    private String legalPersionCardFUrl;
    /**法人手持身份证拍照*/
    private String legalPersonCardHoldsUrl;
    /**目的码实名制承诺书*/
    private String realNameCommitmentUrl;
    /**缴费凭证*/
    private String paymentVoucherUrl;
    /**经办人身份证（正面）*/
    private String handlerPersionCardZUrl;
    /**经办人身份证（反面）*/
    private String handlerPersionCardFUrl;
    /**经办人手持身份证拍照*/
    private String handlerPersionCardHoldsUrl;
    /**经办人授权证明*/
    private String handlerPersionCertificateUrl;
    /**开户行许可证*/
    private String bankOpeningPermitUrl;
    /**套餐类型*/
    private String comboType;
    /**套餐金额*/
    private Float comboMoney;
    /**套餐周期*/
    private Integer comboPeriod;
    /**扣费周期*/
    private Integer deductionPeriod;
    /**费率*/
    private Float rate;
    /**最低消费金额*/
    private Float minConsumeMoney;
    /**签订合同日期*/
    private String dateSigning;
    /**合同附件*/
    private String contractAttachUrl;
    /**经营范围*/
    private String businessScope;
    /**备注信息*/
    private String remark;

    /*****************VO****************/
    /**用户名称*/
    private String userName;

    /**
     * 接口返回唯一标识
     */
    private String taskId;

    /**
     * 统一社会信用代码
     */
    private String corpSocietyNo;
    /** 号码用途 */
    private String corpNunmberUsage;
    /** 开户类型*/
    private String corpAccountType;
    /** 法人身份证有效期 */
    private String legalEffective;
    /** 法人身份证有效期是否永久有效 */
    private String legalLongEffective;
    /** 经办人名称 */
    private String legalHandlerName;
    /** 经办人身份证 */
    private String legalHandlerIdentityId;
    /** 经办人身份证有效期 */
    private String legalHandlerEffective;
    /** 经办人身份证有效期类型 */
    private String legalHandlerLongEffective;
    /** 法人身份证地址 */
    private String legalAddress;
    /** 经办人身份证地址 */
    private String legalHandlerAddress;

    /** 订单预占申请状态 */
    private String  auditStatus;
    /** 订单状态 */
    private String status;

    /** 订单更新时间 */
    private String updateDate;

    /** 平台编号 */
    private String platform;

    /** 资料审核状态 */
    private String approveStatus;
}