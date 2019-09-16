package com.hrtxn.ringtone.project.numcertification.json;

import lombok.Data;

import java.io.Serializable;

/**
 * 400结果返回基本分装
 *
 * @author zcy
 * @date 2019-8-29 11:04
 */
@Data
public class NumBaseResult<T> implements Serializable {
    private String msg;
    private Integer code;
    private T data;
}
