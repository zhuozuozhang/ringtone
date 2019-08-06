package com.hrtxn.ringtone.project.system.log.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.StringUtils;
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

    public AjaxResult findAllOperateLog(Page page) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<OperateLog> allOperateLog = operateLogMapper.findAllOperateLog(page);
        int count = operateLogMapper.getCount();
        if (StringUtils.isNotNull(allOperateLog) && allOperateLog.size() > 0){
            return AjaxResult.success(allOperateLog,"获取数据成功！",count);
        }
        return  AjaxResult.error("无数据！");
    }

    public OperateLog findOperateLogById(Integer operateLogId) {
        if (operateLogId != null && operateLogId != 0){
            return operateLogMapper.findOperateLogById(operateLogId);
        }
        return null;
    }
}
