package com.hrtxn.ringtone.project.threenets.threenet.json.migu;

import lombok.Data;

/**
 * Author:zcy
 * Date:2019-07-25 15:25
 * Description:查询移动是否是视频用户结果
 */
@Data
public class RefreshVbrtStatusResult {

    private String msg;//信息
    private boolean success;// 是否成功
    private String vrbtStatus;//0是、1不是
}
