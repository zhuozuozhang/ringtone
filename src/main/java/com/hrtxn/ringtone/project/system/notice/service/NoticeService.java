package com.hrtxn.ringtone.project.system.notice.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.DateUtils;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.system.notice.domain.Notice;
import com.hrtxn.ringtone.project.system.notice.domain.NoticeStatus;
import com.hrtxn.ringtone.project.system.notice.mapper.NoticeMapper;
import com.hrtxn.ringtone.project.system.notice.mapper.NoticeStatusMapper;
import com.hrtxn.ringtone.project.system.user.domain.User;
import com.hrtxn.ringtone.project.system.user.mapper.UserMapper;
import org.apache.ibatis.annotations.Param;
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
public class NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NoticeStatusMapper noticeStatusMapper;
    /**
     * 查询所有公告信息
     *
     * @return
     * @throws Exception
     */
    public AjaxResult findAllNoticeList(Page page) throws Exception {
        if (!StringUtils.isNotNull(page)) return null;
        page.setPage((page.getPage() - 1) * page.getPagesize());
        // 获取公告总数
        int count = noticeMapper.getNoticeCount(null);
        // 获取公告信息
        List<Notice> noticeList = noticeMapper.findAllNoticeList(page);
        if (noticeList.size() > 0) {
            return AjaxResult.success(noticeList, "获取数据成功！", count);
        }
        return AjaxResult.error("没有获取到数据！");
    }

    /**
     * 添加公告信息
     *
     * @param notice
     * @return
     * @throws Exception
     */
    public AjaxResult insertNotice(Notice notice) {
        if (StringUtils.isNotNull(notice)) {
            notice.setNoticeAuthor(ShiroUtils.getSysUser().getUserName());
            notice.setNoticeTime(new Date());
            Integer id = noticeMapper.getMaxId();
            if(id == null){
                id = 1;
            }else{
                id = id + 1;
            }
            notice.setNoticeId(id);
            noticeMapper.insertNotice(notice);

            List<User> userList = userMapper.findAllUser();
            for(User user : userList){
                NoticeStatus noticeStatus = new NoticeStatus();
                noticeStatus.setNoticeId(id);
                noticeStatus.setUserId(user.getId());
                noticeStatus.setStatus("1");
                noticeStatusMapper.insertNoticeStatus(noticeStatus);
            }
            return  AjaxResult.success("添加成功");
        }
        return AjaxResult.error("参数格式错误");
    }

    /**
     * 修改公告状态
     *
     * @param id
     * @return
     * @throws Exception
     */
    public AjaxResult updataNotiecStatus(Integer id, boolean noticeStatus) {
        Notice notice = new Notice();
        notice.setNoticeId(id);
        notice.setNoticeStatus(noticeStatus);
        int count = noticeMapper.updateNotice(notice);
        if (count > 0) {
            return AjaxResult.success(true, "修改公告状态成功");
        }
        return AjaxResult.error("修改公告状态出错");
    }

    /**
     * 根据ID查询公告信息
     *
     * @param id
     * @return
     * @throws Exception
     */
    public Notice findNoticeById(Integer id) throws Exception {
        if (id != null && id != 0) {
            return noticeMapper.findNoticeById(id);
        }
        return null;
    }

    /**
     * 修改公告信息
     *
     * @param notice
     * @return
     */
    public AjaxResult updateNotice(Notice notice) {
        int count = noticeMapper.updateNotice(notice);
        if (count > 0) {
            return AjaxResult.success(true, "修改成功");
        }
        return AjaxResult.error("修改公告信息失败");
    }

    /**
     * 删除公告信息
     *
     * @param id
     * @return
     */
    public AjaxResult deleteNotice(Integer id) {
        if (id != null && id != 0) {
            int count = noticeMapper.deleteNotice(id);
            if (count > 0) {
                return AjaxResult.success(true, "删除成功");
            }
        }
        return null;
    }

    /**
     * 根据模块获取公告
     *
     * @param modul
     * @return
     */
    public List<Notice> findNoticeListByModul(String modul) throws Exception {
        return noticeMapper.findNoticeListByModul(modul);
    }

    /**
     * 获取疑难杂单类型公告
     *
     * @param page
     * @param notice
     * @return
     */
    public AjaxResult getNoticeList(Page page, Notice notice) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        // 获取公告列表
        List<Notice> noticeList = noticeMapper.getNoticeList(page, notice);
        // 获取公告数量
        int count = noticeMapper.getNoticeCount(notice);
        if (StringUtils.isNotNull(noticeList)) {
            return AjaxResult.success(noticeList, "获取数据成功！", count);
        }
        return AjaxResult.error("无数据！");
    }

    /**
     * 分页查询公告
     * @param page
     * @param notice
     * @return
     * @throws Exception
     */
    public List<Notice> PageNoticeList(Page page, Notice notice) throws Exception {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        // 获取公告列表
        List<Notice> noticeList = noticeMapper.pageNoticeList(page, notice);
        for(Notice ne : noticeList){
            ne.setTimeStr(DateUtils.format(ne.getNoticeTime(),"yyyy-MM-dd HH:mm:ss"));
        }
        return noticeList;
    }

    public int pageNoticeCount( Notice notice){
        return noticeMapper.pageNoticeCount(notice);
    }
}
