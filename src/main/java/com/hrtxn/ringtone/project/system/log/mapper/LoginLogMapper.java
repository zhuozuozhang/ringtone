package com.hrtxn.ringtone.project.system.log.mapper;

import com.hrtxn.ringtone.project.system.log.domain.LoginLog;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-03 17:33
 * Description:登录日志数据访问层
 */
@Repository
public interface LoginLogMapper {

    /**
     * 添加登录记录
     * @param loginLog
     * @return
     */
    int insertLoginLog(LoginLog loginLog);

    List<LoginLog> findAllLoginLog() throws Exception;
}
