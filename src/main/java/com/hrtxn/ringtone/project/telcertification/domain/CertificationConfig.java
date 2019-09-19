package com.hrtxn.ringtone.project.telcertification.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yuanye
 * Date:2019/8/24 13:22
 * Description:号码认证配置
 */
@Data
public class CertificationConfig implements Serializable {
    /**
     * 号码认证配置id
     */
    private Integer id;
    /**
     * 产品类型
     */
    private Integer type;
    /**
     * 产品名称
     */
    private String typeName;
    /**
     * 产品标价
     */
    private Float price;

    /**************************VO**********************************/
    /**
     * 产品类型名称
     */
    private String name;
    /**
     * 产品开通有效时间
     */
    private String periodOfValidity;
    /**
     * 号码认证订单中订购业务中的费用
     */
    private Float cost;
    /**
     *  号码认证挂机短信每月上限条数
     */
    private Integer itemPerMonth;

}