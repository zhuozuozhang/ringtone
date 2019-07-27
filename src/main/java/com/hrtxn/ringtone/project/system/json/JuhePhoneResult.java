package com.hrtxn.ringtone.project.system.json;

import lombok.Data;

import java.io.Serializable;

/**
 * Author:zcy
 * Date:2019-07-20 10:33
 * Description:聚合获取号码信息结果集
 */
@Data
public class JuhePhoneResult implements Serializable {
    private String province;
    private String city;
    private String areacode;
    private String zip;
    private String company;
    private String card;
}
