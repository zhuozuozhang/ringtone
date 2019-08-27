package com.hrtxn.ringtone.project.threenets.kedas.kedapublic.mapper;

import com.hrtxn.ringtone.project.threenets.kedas.kedapublic.domain.KedaPublicUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Author:zcy
 * @Date:2019-08-26 15:19
 * @Description:<描述>
 */
@Repository
public interface KedaPublicMapper {
    /**
     * @Author zcy
     * @Date 2019-8-26 17:32
     * @Description 添加用户信息
     */
    int insert(KedaPublicUser kedaPublicUser);
    /**
     * @Author zcy
     * @Date 2019-8-26 17:33
     * @Description 根据openid获取用户信息
     */
    KedaPublicUser selectByOpenId(@Param("openid") String openid2);
}
