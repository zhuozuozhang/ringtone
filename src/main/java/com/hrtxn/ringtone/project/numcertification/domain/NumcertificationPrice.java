package com.hrtxn.ringtone.project.numcertification.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 价格管理
 *
 * @author zcy
 * @date 2019-8-31 16:21
 */
@Data
public class NumcertificationPrice implements Serializable {
    /**
     * id
     */
    private Integer id;
    /**
     * 类型别名
     */
    private String categoryAlias;
    /**
     * 类型
     */
    private String category;
    /**
     * 成本价
     */
    private Double costPrice;
    /**
     * 加价
     */
    private Double raisePrice;
    /**
     * 指导价
     */
    private Double guidePrice;
    /**
     * 创建时间
     */
    private Date createTime;
}