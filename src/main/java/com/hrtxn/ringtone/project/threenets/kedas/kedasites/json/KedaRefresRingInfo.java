package com.hrtxn.ringtone.project.threenets.kedas.kedasites.json;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Author:zcy
 * Date:2019-08-22 9:30
 * Description:刷新铃音
 * {"retCode":"000000","retMsg":"成功","exDesc":null,
 *      "data":[
 *          {"id":599632203451329,"name":"huike001","status":2,"url":null,"auditDesc":"审核通过","type":3,"price":0,"activePrice":0}
 *          ,{"id":599408010656602,"name":"huike001","status":2,"url":null,"auditDesc":"审核通过","type":3,"price":0,"activePrice":0}
 *      ],
 *      "data2":
 *          {"599632203451329":"http://file.kuyinyun.com/group2/M00/BC/7B/rBBGeV1d61GAYx8UAAsY0Y0wyYY053.mp3"
 *          ,"599408010656602":"http://file.kuyinyun.com/group1/M00/AD/7D/rBBGdF1dFYSAM4ZEAAsY0Y0wyYY776.mp3"}
 * ,"totalCount":2}
 */
@Data
public class KedaRefresRingInfo implements Serializable {
    private BigInteger id;
    private String name;
    private Integer status;
    private String url;
    private String auditDesc;
    private Integer type;
    private Integer price;
    private Integer activePrice;
}
