package com.hrtxn.ringtone.project.system.log.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Author:zcy
 * Date:2019-06-29 15:26
 * Description:日志实体类
 */
@Data
public class OperateLog implements Serializable {
    private static final long serialVersionUID = 1L;

    // 操作记录ID
    private Integer operateLogId;
    // 操作者
    private String operateLogUser;
    // IP地址
    private String ipAddress;
    // 业务类型（0.其它/1.新增/2.修改/3.删除）
    private Integer operateLogType;
    // 操作类型（0.其它/1.三网/2.号码认证/3.科大网站/4.公众号/5.管理端）
    private Integer operateLogClassify;
    // 操作url
    private String operateLogUrl;
    // 操作时间
    private Date operateLogTime;
    // 操作状态（0.正常/1.异常）
    private Integer operateLogStatus;
    // 模块标题
    private String operateLogTitle;
    // 方法名称
    private String operateLogMethod;
    // 操作地点
    private String operateLogLocation;
    // 请求参数
    private String operateLogParam;
    // 错误消息
    private String errorMsg;

}