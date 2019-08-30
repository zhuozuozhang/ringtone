package com.hrtxn.ringtone.project.system.consumelog.mapper;

import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.system.consumelog.domain.ConsumeLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: yuanye
 * @Date: Created in 10:29 2019/8/30
 * @Description: 消费记录数据访问层
 * @Modified By:
 */

@Repository
public interface ConsumeLogMapper {
    List<ConsumeLog> getConsumeLogList(@Param("page") Page page);

    int getCount();

    List<ConsumeLog> findConsumeLogByUserId(Integer id);
}
