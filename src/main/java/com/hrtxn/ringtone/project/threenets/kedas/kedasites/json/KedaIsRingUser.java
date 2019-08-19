package com.hrtxn.ringtone.project.threenets.kedas.kedasites.json;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * Author:zcy
 * Date:2019-08-19 12:10
 * Description:疑难杂单刷新是否是彩铃用户
 */
@Data
public class KedaIsRingUser implements Serializable {
    private String retCode;
    private String retMsg;
    private String exDesc;
    private String data;
    private Map<String, String> data2;
}
