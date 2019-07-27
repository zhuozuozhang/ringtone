package com.hrtxn.ringtone.project.system.File.domain;

import lombok.Data;

import java.util.Date;

/**
 * Author:zcy
 * Date:2019-07-23 16:50
 * Description:文件上传
 */
@Data
public class Uploadfile {
    // ID
    private Integer id;
    // 文件原名称
    private String filename;
    // 文件相对路径
    private String path;
    // 状态（1.上传成功/2.已使用）
    private Integer status;
    // 上传时间
    private Date createtime;
}