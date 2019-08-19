package com.hrtxn.ringtone.project.threenets.kedas.kedasites.json;

import lombok.Data;

import java.io.Serializable;

/**
 * Author:zcy
 * Date:2019-08-19 10:31
 * Description:<描述>
 */

/**
 * "data": [
 *         {
 *             "businessType": null,
 *             "businessEmpId": 528178656131931,
 *             "businessEmpName": "15050840350",
 *             "businessEmpPhone": "15050840350",
 *             "businessEmpState": 1,
 *             "businessId": 581,
 *             "feeType": 1,
 *             "businessState": 0,
 *             "businessStartTime": "2019-05-31 16:54:15",
 *             "businessUpdateTime": "2019-08-17 16:22:26",
 *             "businessEndTime": "1970-01-01 00:00:00",
 *             "bizCreateTime": "2019-05-31 16:54:14",
 *             "unsubTime": null,
 *             "businessSettingStatus": "3",
 *             "jjexpiration": false,
 *             "orderContent": "ring_cr",
 *             "setContent": "597535758896078",
 *             "setStatus": "3",
 *             "setContentText": " ",
 *             "masterBusiness": "ring_cr",
 *             "orderStatus": "0",
 *             "groupId": 441104513194111,
 *             "groupName": "徐州盛博通信科技有限公司开放平台商户",
 *             "agentAccount": "bojietongxin",
 *             "agentUserName": "王坡",
 *             "groupAdminInfo": "()",
 *             "businessRetDesc": "",
 *             "setRetDesc": "设置请求已提交后台",
 *             "businessName": null,
 *             "groupAdmin": null,
 *             "groupAdminPhone": null,
 *             "agentName": null,
 *             "dueTime": null,
 *             "contentName": "慧科",
 *             "contentUrl": null,
 *             "province": "江苏",
 *             "city": "徐州",
 *             "carriersCode": 1,
 *             "ringStatus": 0,
 *             "ringRetDesc": "",
 *             "ext": "",
 *             "feeStatus": 0,
 *             "circleFlag": 0,
 *             "expiration": false
 *         }
 *     ]
 */
@Data
public class KedaPhoneResult implements Serializable {

    private String businessType;
    private Integer businessEmpId;
    private String businessEmpName;
    private String businessEmpPhone;
    // 用户状态 1.待员工确认/0.待发送确认/2.已确认
    private Integer businessEmpState;
    private Integer businessId;
    private Integer feeType;
    // 商彩开通状态 0.未开通/1.开通成功/2.开通失败/3.已退订/4.开通中/5.删除中/6.删除失败
    private Integer businessState;
    private String businessStartTime;
    private String businessUpdateTime;
    private String businessEndTime;
    private String bizCreateTime;
    private String unsubTime;
    private String businessSettingStatus;
    private Boolean jjexpiration;
    private String orderContent;
    private String setContent;
    // 设置状态 0.未设置/1.设置成功/2.设置失败/3.设置中/4.审核驳回
    private String setStatus;
    private String setContentText;
    private String masterBusiness;
    private String orderStatus;
    private Integer groupId;
    private String groupName;
    private String agentAccount;
    private String agentUserName;
    private String groupAdminInfo;
    private String businessRetDesc;
    private String setRetDesc;
    private String businessName;
    private String groupAdmin;
    private String groupAdminPhone;
    private String agentName;
    private String dueTime;
    private String contentName;
    private String contentUrl;
    private String province;
    private String city;
    private Integer carriersCode;
    private Integer ringStatus;
    private String ringRetDesc;
    private String ext;
    private Integer feeStatus;
    private Integer circleFlag;
    private Boolean expiration;


}
