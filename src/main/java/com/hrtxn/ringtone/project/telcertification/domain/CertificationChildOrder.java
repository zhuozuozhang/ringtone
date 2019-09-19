package com.hrtxn.ringtone.project.telcertification.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
/**
 * @author : yuanye
 * Date:2019/8/16 14:04
 * Description:号码认证子订单
 */
@Data
public class CertificationChildOrder implements Serializable {
    /**
     * 号码认证子订单ID
     */
    private Integer id;
    /**
     * 号码认证子订单编号
     */
    private String telChildOrderNum;
    /**
     * 成员号码
     */
    private String telChildOrderPhone;
    /**
     * 年份
     */
    private Integer years;
    /**
     * 总价格
     */
    private Float price;
    /**
     * 号码认证子订单状态（1.开通中/2.开通成功/3.开通失败/4.续费中/5.续费成功/6.续费失败）
     */
    private Integer telChildOrderStatus;
    /**
     * 业务反馈
     */
    private String businessFeedback;
    /**
     * 子订单提交时间
     */
    private Date telChildOrderCtime;
    /**
     * 开通时间
     */
    private Date telChildOrderOpenTime;
    /**
     *
     */
    //到期时间
    private Date telChildOrderExpireTime;
    /**
     * 父级订单ID
     */
    private Integer parentOrderId;
    /**
     * 号码认证消费记录ID
     */
    private Integer consumeLogId;

    /**************************VO**********************************/
    /**
     * 子订单的备注
     */
    private String remark;
    /**
     * 状态名称
     */
    private String statusName;
    /**
     * 批量添加的号码
     */
    private String[] phoneNumberArray;
    /**
     * 订购的业务
     */
    private String product;

}