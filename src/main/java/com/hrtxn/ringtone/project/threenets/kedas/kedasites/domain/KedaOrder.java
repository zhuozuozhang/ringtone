package com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author zcy
 * @Date 2019-08-13 13:24
 * @Description 科大订单
 */
@Data
public class KedaOrder implements Serializable {
    /**
     * ID
     */
    private Integer id;
    /**
     * 企业名称
     */
    private String companyName;
    /**
     * 联系人
     */
    private String linkMan;
    /**
     * 联系电话
     */
    private String linkTel;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 创建时间
     */
    private Date cerateTime;
    /**
     * 状态
     */
    private String status;
    /**
     * 用户ID
     */
    private Integer userId;

    /*********************************大大的分割线************************************/
    /**
     * 用户名
     */
    private String userName;
    /**
     * 子订单数
     */
    private Integer childOrderQuantity;
    /**
     * 包月子订单数
     */
    private Integer childOrderQuantityByMonthly;
    /**
     * 总数/包月
     */
    private String peopleNum;

    /**
     * 营业执照
     */
    private String creditFile;

    /**
     * 电信协议文件地址
     */
    private String protocol;

    /**
     * 科大商户id
     */
    private String kedaId;
}