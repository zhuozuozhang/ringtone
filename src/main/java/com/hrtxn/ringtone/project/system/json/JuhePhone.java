package com.hrtxn.ringtone.project.system.json;

import lombok.Data;

import java.io.Serializable;

/**
 * Author:zcy
 * Date:2019-07-20 10:30
 * Description:聚合获取号码信息
 * {"resultcode":"200","reason":"Return Successd!","result":{"province":"江苏","city":"徐州","areacode":"0516","zip":"221000","company":"移动","card":""},"error_code":0}
 */
@Data
public class JuhePhone<JuhePhoneResult> implements Serializable {
    private String resultcode;
    private Integer error_code;
    private String reason;
    private JuhePhoneResult result;
}
