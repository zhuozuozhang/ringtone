package com.hrtxn.ringtone.project.system.log.service;

import com.hrtxn.ringtone.project.system.log.domain.OperateLog;
import com.hrtxn.ringtone.project.system.log.mapper.OperateLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-06-26 11:55
 * Description:操作日志业务实现类
 */
@Service
public class OperateLogService {

    @Autowired
    private OperateLogMapper operateLogMapper;

    /**
     * 添加操作记录
     * @param operateLog
     * @return
     */
    public boolean insertOperateLog(OperateLog operateLog) {
        boolean b = operateLogMapper.insertOperateLog(operateLog) > 0 ? true : false;
        return b;
    }

    public List<OperateLog> findAllOperateLog() {
        return operateLogMapper.findAllOperateLog();
    }

    public OperateLog findOperateLogById(Integer operateLogId) {
        if (operateLogId != null && operateLogId != 0){
            return operateLogMapper.findOperateLogById(operateLogId);
        }
        return null;
    }
}
