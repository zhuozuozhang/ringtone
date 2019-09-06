package com.hrtxn.ringtone.project.system.toolboxs.ringContent.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 工具箱--铃音内容
 *
 * @author zcy
 * @date 2019-9-4 13:26
 */
@Data
public class RingContent implements Serializable {

    private Integer id;

    private Date createTime;

    private String ringContent;
}