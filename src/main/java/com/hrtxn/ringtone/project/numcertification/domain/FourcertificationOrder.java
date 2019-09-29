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
public class FourcertificationOrder {
    private Integer id;
    /***/
    private Integer userId;
    private String userName;
    /***/
    private Date createTime;
    /** 400号码*/
    private String applyNumber;
    /** 运营商 */
    private String provider;
    /** 套餐模式 */
    private String comboPattern;
    /** 价格 */
    private Double price;
    /** 预占公司名称*/
    private String companyName;
    /** 充值年限 */
    private String ageLimit;
    /** 使用省份*/
    private String userProvince;
    /** 使用城市 */
    private String userCity;
    /** 联系手机 */
    private String otherLinkMobile;
    /** 联系固话 */
    private String otherLinkPhone;
    /** 联系邮箱 */
    private String otherLinkEmail;
    /** 绑定号码 */
    private String otherBindPhone;
    /** 验证手机 */
    private String otherValidPhone;
    /** 法人身份证文件地址 */
    private String legalCardUrl;
    /** 经办人身份证文件 */
    private String operatorCardUrl;
    /** 统一社会信用代码 */
    private String corpSocietyNo;
    /** 经营范围 */
    private String corpBusinessScope;
    /** 企业注册地省份主键 */
    private String corpCompanyProvince;
    /** 企业注册地城市主键 */
    private String corpCompanyCity;
    /** 企业注册地地址 */
    private String corpCompanyDetail;
    /** 实际办公地省份 */
    private String corpOfficeProvince;
    /** 实际办公地城市 */
    private String corpOfficeCity;
    /** 实际办公地地址 */
    private String corpOfficeDetail;
    /**号码用途*/
    private String corpNunmberUsage;
    /** 企业开户类型（公司账户/法人账户） */
    private String corpAccountType;
    /** 开户行名称 */
    private String corpBankName;
    /** 银行账号 */
    private String corpBankNo;
    /**法人名称  */
    private String legalName;
    /** 法人身份证 */
    private String legalIdentityId;
    /** 法人身份证有效期 */
    private String legalEffective;
    /** 法人身份证长期有效 */
    private String legalLongEffective;
    /** 经办人名称 */
    private String legalHandlerName;
    /** 经办人身份证 */
    private String legalHandlerIdentityId;
    /** 经办人有效期 */
    private String legalHandlerEffective;
    /** 经办人身份证长期有效 */
    private String legalHandlerLongEffective;
    /** 法人身份证地址 */
    private String legalAddress;
    /** 经办人身份证地址 */
    private String legalHandlerAddress;
    /** 营业执照文件 */
    private String yyzz;
    /** 开户许可证文件 */
    private String khxkz;
    /** 法人身份证文件 */
    private String frsfz;
    /** 法人授权证明文件 */
    private String frsq;
    /** 经办人身份证文件 */
    private String jbrsfz;
    /** 经办人授权证明文件 */
    private String jbrsq;
    /** 缴费发票文件 */
    private String jffp;
    /** 受理单文件 */
    private String sld;
    /** 服务协议文件 */
    private String fwxy;
    /** 安全承诺书文件 */
    private String aqcns;
    /** 手持身份证照片文件 */
    private String scsfz;
    /** 经营异常证明文件 */
    private String jyyczm;
    /** 手持营业执照文件 */
    private String ccyyzz;
    /** 低消协议文件 */
    private String dxxy;
    /** 工商网截图文件 */
    private String gswjt;
    /** 其他文件 */
    private String qt;
    /** 状态(1:预占申请、2：预占成功、3：资料模板审核、4：资料模板审核通过、5：资料提交成功) */
    private String status;
    /** 删除状态 */
    private String delFlag;
    /** 平台编号 */
    private String platform;
    /** 是否可用 */
    private String availability;
    /** 模板地址 */
    private String templateUrl;
    /** 任务编号 */
    private String taskId;
    /** 备注 */
    private String remarks;
}