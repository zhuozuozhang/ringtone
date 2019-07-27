package com.hrtxn.ringtone.project.threenets.threenet.json.swxl;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Author:zcy
 * Date:2019-07-25 10:50
 * Description:顶级信息封装
 */
public class SwxlPubBackData<T> implements Serializable {
    @Getter @Setter private String recode;
    @Getter @Setter private String message;
    @Getter @Setter private boolean success;
    @Getter @Setter private SwxlQueryPubRespone<T> data;


}
