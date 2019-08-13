package com.hrtxn.ringtone.project.system.config.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * Author:zcy
 * Date:2019-08-12 17:38
 * Description: 配置实体类
 */
@Data
public class SystemConfig implements Serializable {
    private Integer id;
    private String type;
    private String info;
}
