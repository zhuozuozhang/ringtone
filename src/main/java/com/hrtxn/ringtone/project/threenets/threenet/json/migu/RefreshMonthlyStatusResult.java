package com.hrtxn.ringtone.project.threenets.threenet.json.migu;

import lombok.Data;

/**
 * Author:zcy
 * Date:2019-07-23 10:48
 * Description:查询移动号码包月返回结果
 * {"unsubscribedStatus":null,"msg":"更新企业彩铃包月状态状态成功","success":true,"freezeStatus":"1"}
 */
@Data
public class RefreshMonthlyStatusResult {
    public static final String BAOYUE = "0";
    public static final String WEIBAOYUE = "1";

    private String unsubscribedStatus;//是否退订 1：已退订，null：未退订
    private String msg;//信息
    private boolean success;// 是否成功
    private String freezeStatus;//开通包月情况，0：已包月，1：未包月


}
