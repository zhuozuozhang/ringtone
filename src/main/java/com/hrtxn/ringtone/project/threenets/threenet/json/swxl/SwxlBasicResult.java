package com.hrtxn.ringtone.project.threenets.threenet.json.swxl;

import lombok.Data;

/**
 * Author:zcy
 * Date:2019-07-26 17:58
 * Description:<描述>
 * {"recode":"000000","message":"成功","data":null,"success":true}
 */
@Data
public class SwxlBasicResult {

    private String recode;
    private String message;
    private boolean success;
    private String data;
}
