package com.hrtxn.ringtone.project.threenets.threenet.json.swxl;

import lombok.Data;

import java.io.Serializable;

/**
 * Author:zcy
 * Date:2019-07-25 10:50
 * Description:商务炫铃用户信息返回实体
 * {"recode":"000000","message":"成功",
 *      "data":{
 *          "data":[
 *              {"id":"93977b769be4445d9a75bd12322ea546",
 *              "msisdn":"15617391332",
 *              "groupId":"f1a40368c0434078b9ccee3132e6bd24",
 *              "crbtStatus":0,
 *              "monthStatus":0,
 *              "netType":"4",
 *              "status":0,
 *              "openTime":"2019-03-06 13:21:49",
 *              "ctime":"2019-03-06 08:52:26",
 *              "closeTime":null,
 *              "provinceId":76,
 *              "provinceName":"河南",
 *              "remark":"铃音[雪绒花1]设置成功",
 *              "syncStatus":0}
 *          ],
 *      "recordsTotal":1},
 * "success":true}
 */
@Data
public class SwxlPhoneInfoResult implements Serializable {

    private int monthStatus;//包月状态 0：已包月  1:未包月  2:未知  3:已退订
    private String groupId;//集团ID
    private int provinceId;//所属省份ID
    private String netType;//网络类型 2:2G 3:3G 4:4G
    private String ctime;//创建时间
    private String remark;//电话备注
    private String id;//电话ID
    private String msisdn;//开通电话
    private int crbtStatus;//炫铃状态 0:已开通 1:未开通  2:未知
    private int status;//用户状态: 0:添加成功  1:删除中 2：删除失败  3:未审核  4:未审核通过
    private String provinceName;//所属省份
    private int syncStatus;
    private boolean state;//执行状态
    private String openTime;//开通时间
    private String closeTime;//开通时间
    private String productName;
}
