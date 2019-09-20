package com.hrtxn.ringtone.project.system.dict.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * Author:lxp
 * Date:2019-06-29 15:26
 * Description:字典管理
 */
@Data
public class Dict implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String code;

    private String name;

    private String type;

    private String remarks;


}