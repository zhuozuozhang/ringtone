package com.hrtxn.ringtone.project.system.notice.mapper;

import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.system.notice.domain.Notice;
import com.hrtxn.ringtone.project.system.notice.domain.NoticeStatus;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-09 16:32
 * Description:公告数据方法实现
 */
@Repository
public interface NoticeStatusMapper {

    /**
     * 获取公告列表
     *
     * @return
     * @throws Exception
     */
    List<Notice> findAllNoticeList(Integer noticeId);

    void insertNoticeStatus(NoticeStatus noticeStatus);

    void updateNoticeStatus(@Param("noticeId") Integer noticeId,@Param("userId") Integer userId);

    void deleteNoticeStatusByNoticeId(Integer noticeId);

}
