package com.hrtxn.ringtone.project.threenets.kedas.kedasites.json;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Author:zcy
 * Date:2019-08-17 14:04
 * Description:<描述>
 */
@Data
public class KedaBaseResult<T> implements Serializable {

    private String retCode;
    private String retMsg;
    private String exDesc;
    private List<T> data;
    private Map<String, String> data2;


}
