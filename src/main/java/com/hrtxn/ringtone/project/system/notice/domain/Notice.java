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
    // 发布模块(0.企业彩铃/视频彩铃/1.电话认证/挂机短信/彩印/2.400电话/3.企业秀/4.网站建设/万词霸屏/快排优化/5.微信/百度/抖音小程序/6.铃音录制/视频制作/7.流量卡/号卡/8.疑难杂单)
    private Integer noticeModule;
    // 创建时间
    private Date noticeTime;
    // 创建者
    private String noticeAuthor;
    // 正文
    private String noticeContent;

    /***************************************大大的分割线***********************************************/
    // 开始日期
    private String start;
    // 结束日期
    private String end;

}
