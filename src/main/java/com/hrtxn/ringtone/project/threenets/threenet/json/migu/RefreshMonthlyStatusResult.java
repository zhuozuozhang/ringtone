package com.hrtxn.ringtone.project.threenets.threenet.json.migu;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Author:zcy
 * Date:2019-07-23 10:48
 * Description:查询移动号码包月返回结果
 * {"unsubscribedStatus":null,"msg":"更新企业彩铃包月状态状态成功","success":true,"freezeStatus":"1"}
 */
public class RefreshMonthlyStatusResult  implements Serializable {
    public static final String BAOYUE = "0";
    public static final String WEIBAOYUE = "1";

    @Getter @Setter private String unsubscribedStatus;//是否退订 1：已退订，null：未退订
    @Getter @Setter private String msg;//信息
    @Getter @Setter private boolean success;// 是否成功
    @Getter @Setter private String freezeStatus;//开通包月情况，0：已包月，1：未包月
}
