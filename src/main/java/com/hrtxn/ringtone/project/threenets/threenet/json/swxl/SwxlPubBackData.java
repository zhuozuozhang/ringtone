package com.hrtxn.ringtone.project.threenets.threenet.json.swxl;

import lombok.Data;

import java.io.Serializable;

/**
 * Author:zcy
 * Date:2019-07-25 10:50
 * Description:顶级信息封装
 */
@Data
public class SwxlPubBackData<T> implements Serializable {
    private static final long serialVersionUID = -5893793992171290021L;
    private String recode;
    private String message;
    private boolean success;
    private SwxlQueryPubRespone<T> data;


}
