package com.hrtxn.ringtone.project.system.log.mapper;

import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.system.log.domain.LoginLog;
import org.apache.ibatis.annotations.Param;
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

    /**
     * 获取所有日志
     *
     * @param page
     * @return
     * @throws Exception
     */
    List<LoginLog> findAllLoginLog(@Param("page") Page page) throws Exception;

    /**
     * 获取日志总数
     *
     * @return
     */
    int getCount();

}
