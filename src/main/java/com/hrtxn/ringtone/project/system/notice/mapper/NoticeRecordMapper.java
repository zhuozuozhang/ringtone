package com.hrtxn.ringtone.project.system.notice.mapper;

import com.hrtxn.ringtone.project.system.notice.domain.NoticeRecord;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(NoticeRecord record);

    int insertSelective(NoticeRecord record);

    NoticeRecord selectByPrimaryKey(Integer id);

    List<NoticeRecord> selectByParam(NoticeRecord record);

    int updateByPrimaryKeySelective(NoticeRecord record);

    int updateByPrimaryKey(NoticeRecord record);
}