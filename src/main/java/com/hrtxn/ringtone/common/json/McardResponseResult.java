package com.hrtxn.ringtone.common.json;

import lombok.Data;

/**
 * Author:lile
 * Date:2019/7/26 15:36
 * Description:
 */
@Data
public class McardResponseResult {
    private String  executeTimeMs;//执行时间
    private String  description;//描述
    private String  returnCode;//返回编码
    private String  circleID;//商户唯一标示

    //铃音增加字段
    private String  audio_id;//铃音id



    //以下查询用户信息新增字段
    private int  ringState;
    private String  isRingUser;//是否已开通彩铃功能，1已开通0未开通2开通失败3开通中
    private String  isOpenImusic;  //是否已开通音乐名片业务，1已开通0未开通2开通失败3开通中（开通中首先确认是否回复开通业务的确认短信）
    private String  managerMsisdn;
    private String  circleName;
    private String  channelName;
    private String  ringStateDesc; //铃音设置描述

    private String  ringAudit;//铃音审核
}
