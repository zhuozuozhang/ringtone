package com.hrtxn.ringtone.project.system.notice.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Author:zcy
 * Date:2019-07-09 16:30
 * Description:公告实体类
 */
@Data
public class NoticeStatus implements Serializable {
    private Integer id;
    private Integer noticeId;
    private Integer userId;
    private String status;

}
