package com.hrtxn.ringtone.project.system.log.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
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

    /**
     * 添加登录日志
     *
     * @param loginLog
     * @return
     */
    public boolean insertLoginLog(LoginLog loginLog){
        return loginLogMapper.insertLoginLog(loginLog) > 0 ? true : false;
    }

    /**
     * 获取所有登录日志
     *
     * @param page
     * @return
     * @throws Exception
     */
    public AjaxResult findAllLoginLog(Page page) throws Exception {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<LoginLog> allLoginLog = loginLogMapper.findAllLoginLog(page);
        int count = loginLogMapper.getCount();
        if (allLoginLog.size() > 0){
            return AjaxResult.success(allLoginLog,"",count);
        }
        return AjaxResult.error("无数据！");
    }
}
