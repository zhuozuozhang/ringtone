package com.hrtxn.ringtone.project.threenets.threenet.json.swxl;

import lombok.Data;

/**
 * Author:lile
 * Date:2019/7/27 15:35
 * Description:
 */
@Data
public class SwxlGroupResponse {
    private String id;//集团ID
    private String managerId;//管理员ID
    private String managerName;//管理员名称
    private String managerMsisdn;//管理员联系号码
    private String groupName;//集团名称
    private String productId;//产品ID
    private String ctime;//创建时间
    private int memberCount;//当前集团人数
    private int applyForSmsNotification;//免短信回复: 0:否 1:是
    private int status;//集团状态: 0 正常  1：异常
    private String platformId;
    private String remark;
    private String platformQdId;
    private String groupId;//集团ID
    private String payType;//付费类型
    private String expireTime;//执行时间
    private String productName;//执行时间
    private String productPrice;//执行时间
    private String tel;
    private String payWay;
    private String discount;
    private String days;
}
