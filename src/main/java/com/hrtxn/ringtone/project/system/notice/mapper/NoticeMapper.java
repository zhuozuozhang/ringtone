package com.hrtxn.ringtone.project.system.notice.mapper;

import com.hrtxn.ringtone.project.system.notice.domain.Notice;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-09 16:32
 * Description:公告数据方法实现
 */
@Repository
public interface NoticeMapper {

    /**
     *获取公告列表
     * @return
     * @throws Exception
     */
    List<Notice> findAllNoticeList() throws Exception;

    /**
     * 添加公告
     * @param notice
     * @return
     */
    int insertNotice(Notice notice);

    /**
     * 修改公告信息
     * @param notice
     * @return
     */
    int updateNotice(Notice notice);

    /**
     * 根据ID查询公告信息
     * @param id
     * @return
     * @throws Exception
     */
    Notice findNoticeById(Integer id) throws Exception;

    /**
     * 删除公告信息
     * @param id
     * @return
     */
    int deleteNotice(Integer id);

    /**
     * 根据类型获取公告列表
     * @param modul
     * @return
     */
    List<Notice> findNoticeListByModul(String modul) throws Exception;
}
