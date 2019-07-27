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
public class Notice implements Serializable {
    // 公告ID
    private Integer noticeId;
    // 标题
    private String noticeTitle;
    // 权重
    private Integer noticeWeight;
    // 是否开放 （0.false/1.true）
    private Boolean noticeStatus;
    // 发布模块(0.三网/1.号码认证/2.400/3.视频制作/4.号卡/5.铃音录制/6.企业秀/7.来去电名片)
    private Integer noticeModule;
    // 创建时间
    private Date noticeTime;
    // 创建者
    private String noticeAuthor;
    // 正文
    private String noticeContent;
}
