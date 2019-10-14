package com.hrtxn.ringtone.project.system.notice.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.system.notice.domain.Notice;
import com.hrtxn.ringtone.project.system.notice.domain.NoticeStatus;
import com.hrtxn.ringtone.project.system.notice.mapper.NoticeMapper;
import com.hrtxn.ringtone.project.system.notice.mapper.NoticeStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-09 16:29
 * Description:公告业务实现类
 */
@Service
public class NoticeStatusService {

    @Autowired
    private NoticeStatusMapper noticeStatusMapper;

    public List<Notice> findAllNoticeList(Integer noticeId){
        return noticeStatusMapper.findAllNoticeList(noticeId);
    }

    public void insertNoticeStatus(NoticeStatus noticeStatus){
        insertNoticeStatus(noticeStatus);
    }

    public void updateNoticeStatus(Integer noticeId,Integer userId){
        noticeStatusMapper.updateNoticeStatus(noticeId,userId);
    }


    public void deleteNoticeStatusByNoticeId(Integer noticeId){
        deleteNoticeStatusByNoticeId(noticeId);
    }

}
