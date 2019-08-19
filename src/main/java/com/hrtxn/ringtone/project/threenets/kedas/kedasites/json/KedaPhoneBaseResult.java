package com.hrtxn.ringtone.project.threenets.kedas.kedasites.json;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Author:zcy
 * Date:2019-08-19 10:29
 * Description:疑难杂单刷新信息基础类
 */

/**
 * {
 *     "retCode": "000000",
 *     "retMsg": "成功",
 *     "exDesc": null,
 *     "data": [
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
 *     ],
 *     "data2": {
 *         "528178656131931": "http://file.kuyinyun.com/group1/M00/AD/4A/rBBGdF1WG_qAScpMAAsY0Y0wyYY757.mp3"
 *     },
 *     "totalCount": 1
 * }
 */
@Data
public class KedaPhoneBaseResult<T> {
    private String retCode;
    private String retMsg;
    private String exDesc;
    private List<T> data;
    private Map<String, String> data2;
    private Integer totalCount;

}
