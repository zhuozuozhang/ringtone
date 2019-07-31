package com.hrtxn.ringtone.project.threenets.threenet.json.migu;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Author:zcy
 * Date:2019-07-25 15:25
 * Description:移动号码铃音查询结果
 * {"msg":"验证码输入错误","success":false}
 */
public class RingSetResult implements Serializable {
    @Getter
    @Setter
    private String msg;//查询结果，文字说明
    @Getter
    @Setter
    private boolean success;//是否查询成功

}
