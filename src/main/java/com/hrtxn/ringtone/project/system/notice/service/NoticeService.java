package com.hrtxn.ringtone.project.system.notice.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.system.notice.domain.Notice;
import com.hrtxn.ringtone.project.system.notice.mapper.NoticeMapper;
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

    /**
     * 查询所有公告信息
     * @return
     * @throws Exception
     */
    public List<Notice> findAllNoticeList() throws Exception {
        return noticeMapper.findAllNoticeList();
    }
    /**
     * 添加公告信息
     * @param notice
     * @return
     * @throws Exception
     */
    public AjaxResult insertNotice(Notice notice) {
        if (StringUtils.isNotNull(notice)){
            notice.setNoticeAuthor(ShiroUtils.getSysUser().getUserName());
            notice.setNoticeTime(new Date());
            int count = noticeMapper.insertNotice(notice);
            if (count > 0){
                return AjaxResult.success(true,"添加公告成功");
            }else {
                return AjaxResult.error("添加公告出错");
            }
        }
        return AjaxResult.error("参数格式错误");
    }
    /**
     * 修改公告状态
     * @param id
     * @return
     * @throws Exception
     */
    public AjaxResult updataNotiecStatus(Integer id, boolean noticeStatus) {
        Notice notice = new Notice();
        notice.setNoticeId(id);
        notice.setNoticeStatus(noticeStatus);
        int count = noticeMapper.updateNotice(notice);
        if (count > 0){
            return AjaxResult.success(true,"修改公告状态成功");
        }
        return AjaxResult.error("修改公告状态出错");
    }
    /**
     * 根据ID查询公告信息
     * @param id
     * @return
     * @throws Exception
     */
    public Notice findNoticeById(Integer id) throws Exception {
        if (id!=null && id !=0){
            return noticeMapper.findNoticeById(id);
        }
        return null;
    }
    /**
     * 修改公告信息
     * @param notice
     * @return
     */
    public AjaxResult updateNotice(Notice notice) {
        int count = noticeMapper.updateNotice(notice);
        if (count > 0){
            return AjaxResult.success(true,"修改成功");
        }
        return AjaxResult.error("修改公告信息失败");
    }

    /**
     * 删除公告信息
     * @param id
     * @return
     */
    public AjaxResult deleteNotice(Integer id) {
        if (id != null &&  id!= 0){
            int count = noticeMapper.deleteNotice(id);
            if (count > 0){
                return AjaxResult.success(true,"删除成功");
            }
        }
        return null;
    }

    /**
     * 根据模块获取公告
     * @param modul
     * @return
     */
    public List<Notice> findNoticeListByModul(String modul)throws Exception {
        return noticeMapper.findNoticeListByModul(modul);
    }
}
