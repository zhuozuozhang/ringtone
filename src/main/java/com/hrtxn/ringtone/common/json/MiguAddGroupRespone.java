package com.hrtxn.ringtone.common.json;

import lombok.Data;

/**
 * Author:lile
 * Date:2019/7/26 12:02
 * Description:咪咕创建成功后，返回对象
 * http://211.137.107.18:8888/cm/groupInfo!addGroup.action
 * {"circleId":"af3f7bd6-f0f8-45cb-8d17-0f11a833edee","msg":"创建集团成功","success":true}
 */
@Data
public class MiguAddGroupRespone {
    private String circleId;
    private String msg;
    private boolean success;
}
