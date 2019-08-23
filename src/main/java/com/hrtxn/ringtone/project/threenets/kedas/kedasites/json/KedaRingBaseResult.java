package com.hrtxn.ringtone.project.threenets.kedas.kedasites.json;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * Author:zcy
 * Date:2019-08-17 14:04
 * Description:疑难杂单添加返回信息基础类
 * {"retCode":"020109","retMsg":"当前操作正在进行中","exDesc":null,"data":null,"data2":null}
 */

@Data
public class KedaRingBaseResult<T> implements Serializable {

    private String retCode;
    private String retMsg;
    private String exDesc;
    private Map<String,T> data;
    private Map<String, Object> data2;


}
