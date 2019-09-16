package com.hrtxn.ringtone.project.numcertification.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 条件搜索
 *
 * @Author zcy
 * @Date 2019-08-29 13:48
 */
@Data
public class NumOrder implements Serializable {
    /**
     * 运营商
     */
    private Integer provider;
    /**
     * 类型
     */
    private Integer category;
    /**
     * 不包含数字，多个数字以英文逗号
     */
    private String notInclude;
    /**
     * 400 号码
     */
    private String phoneNum;



    /**
     * 成本价
     */
    private Double costPrice;
    /**
     * 指导价
     */
    private Double guidePrice;


}
