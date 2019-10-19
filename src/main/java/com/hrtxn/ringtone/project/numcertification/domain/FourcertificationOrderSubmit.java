package com.hrtxn.ringtone.project.numcertification.domain;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 400号码认证订单
 *
 * @author zcy
 * @date 2019-8-30 11:00
 */
@Data
public class FourcertificationOrderSubmit {

    private String numberCode;
    private String corpSocietyNo;
    private String corpBusinessScope;
    private String corpCompanyProvince;
    private String corpCompanyCity;
    private String corpCompanyDetail;
    private String corpOfficeProvince;
    private String corpOfficeCity;
    private String corpOfficeDetail;
    private String corpNunmberUsage;
    private String corpAccountType;
    private String corpBankName;

    private String corpBankNo;
    private String legalName;
    private String legalIdentityId;
    private String legalEffective;
    private String legalLongEffective;
    private String legalHandlerName;
    private String legalHandlerIdentityId;
    private String legalHandlerEffective;
    private String legalHandlerLongEffective;
    private String legalAddress;
    private String legalHandlerAddress;
    private String otherLinkMobile;
    private String otherLinkPhone;
    private String otherLinkEmail;
    private String otherBindPhone;
    private String otherValidPhone;

    private List<OrderFileBean> yyzz;
    private List<OrderFileBean> khxkz;
    private List<OrderFileBean> frsfz;
    private List<OrderFileBean> frsq;
    private List<OrderFileBean> jbrsfz;
    private List<OrderFileBean> jbrsq;
    private List<OrderFileBean> jffp;
    private List<OrderFileBean> sld;
    private List<OrderFileBean> fwxy;
    private List<OrderFileBean> aqcns;
    private List<OrderFileBean> scsfz;
    private List<OrderFileBean> jyyczm;
    private List<OrderFileBean> ccyyzz;
    private List<OrderFileBean> dxxy;
    private List<OrderFileBean> gswjt;
    private List<OrderFileBean> qt;

}