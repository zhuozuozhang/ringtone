package com.hrtxn.ringtone.project.system.log.mapper;

import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.system.log.domain.OperateLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-06-26 11:57
 * Description:操作日志业务数据层
 */
@Repository
public interface OperateLogMapper {

    /**
     * 添加操作记录
     * @param operateLog
     * @return
     */
    int insertOperateLog(OperateLog operateLog);

    /**
     * 查询所有操作记录
     * @return
     */
    List<OperateLog> findAllOperateLog(@Param("page")Page page);

    OperateLog findOperateLogById(Integer operateLogId);

    int getCount();
}
