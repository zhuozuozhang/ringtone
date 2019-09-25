package com.hrtxn.ringtone.project.system.notice.service;

import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.project.system.notice.domain.NoticeRecord;
import com.hrtxn.ringtone.project.system.notice.mapper.NoticeRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author:lile
 * Date:2019-09-24 16:37
 * Description:
 */
@Service
public class NoticeRecordService {

    @Autowired
    private NoticeRecordMapper mapper;

    /**
     * 按照id删除
     *
     * @param id
     * @return
     */
    public int deleteByPrimaryKey(Integer id) {
        return mapper.deleteByPrimaryKey(id);
    }

    /**
     * 新增
     *
     * @param record
     * @return
     */
    public int insert(NoticeRecord record) {
        return mapper.insert(record);
    }

    /**
     * 新增
     *
     * @param record
     * @return
     */
    public int insertSelective(NoticeRecord record) {
        return mapper.insertSelective(record);
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    public NoticeRecord selectByPrimaryKey(Integer id) {
        return mapper.selectByPrimaryKey(id);
    }

    /**
     * 修改
     *
     * @param record
     * @return
     */
    public int updateByPrimaryKeySelective(NoticeRecord record) {
        return mapper.updateByPrimaryKeySelective(record);
    }

    /**
     * 修改
     *
     * @param record
     * @return
     */
    public int updateByPrimaryKey(NoticeRecord record) {
        return mapper.updateByPrimaryKey(record);
    }


    public void recordInformation(NoticeRecord record) {
        //首先查询当前是否保存了用户查看记录
        List<NoticeRecord> noticeRecords = mapper.selectByParam(record);
        //删除记录
        for (int i = 0; i < noticeRecords.size(); i++) {
            mapper.deleteByPrimaryKey(noticeRecords.get(i).getId());
        }
        //添加记录
        record.setUserId(ShiroUtils.getSysUser().getId());
        mapper.insertSelective(record);
    }
}
