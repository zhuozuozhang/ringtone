package com.hrtxn.ringtone.project.system.log.service;

import com.hrtxn.ringtone.project.system.log.domain.LoginLog;
import com.hrtxn.ringtone.project.system.log.mapper.LoginLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-04 10:03
 * Description:登陆记录业务实现类
 */
@Service
public class LoginLogService {

    @Autowired
    private LoginLogMapper loginLogMapper;

    public boolean insertLoginLog(LoginLog loginLog){
        return loginLogMapper.insertLoginLog(loginLog) > 0 ? true : false;
    }

    public List<LoginLog> findAllLoginLog() throws Exception {
        return loginLogMapper.findAllLoginLog();
    }
}
