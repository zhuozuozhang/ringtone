package com.hrtxn.ringtone.project.system.notice.domain;

import lombok.Data;

@Data
public class NoticeRecord {
    private Integer id;

    private Integer status;

    private Integer userId;

    private Integer noticeId;
}