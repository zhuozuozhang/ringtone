package com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * @Author zcy
 * @Date 2019-08-13 13:24
 * @Description 科大子订单
 */
@Data
public class KedaChildOrder implements Serializable {
    /**ID*/
    private Integer id;
    /**联系人*/
    private String linkMan;
    /**联系电话*/
    private String linkTel;
    /**运营商*/
    private Integer operate;
    /**省*/
    private String province;
    /**市*/
    private String city;
    /**创建时间*/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
    /**子订单状态*/
    private String status;
    /**集团ID（运营商返回）*/
    private String operateId;
    /**员工ID(运营商返回）*/
    private BigInteger employeeId;
    /**铃音ID*/
    private Integer ringId;
    /**铃音名称*/
    private String ringName;
    /**是否包月（0.未开通/1.开通成功/2.开通失败/3.已退订/4.开通中/5.删除中/6.删除失败）*/
    private Integer isMonthly;
    /**是否开通彩铃（1.未开通/2.已开通/3.开通失败）*/
    private Integer isRingtoneUser;
    /**用户ID*/
    private Integer userId;
    /**备注*/
    private String remark;
    /**父级订单ID*/
    private Integer orderId;
    /** 成员手机号*/
    private String tels;
    /** 10元资质*/
    private String protocolTelecom10;
    /** 20元资质*/
    private String protocolTelecom20;
}