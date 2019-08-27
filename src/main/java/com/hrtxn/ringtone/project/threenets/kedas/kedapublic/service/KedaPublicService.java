package com.hrtxn.ringtone.project.threenets.kedas.kedapublic.service;

import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.threenets.kedas.kedapublic.domain.KedaPublicUser;
import com.hrtxn.ringtone.project.threenets.kedas.kedapublic.mapper.KedaPublicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author:zcy
 * @Date:2019-08-26 15:19
 * @Description:<描述>
 */
@Service
public class KedaPublicService {

    @Autowired
    private KedaPublicMapper kedaPublicMapper;

    /**
     * @Author zcy
     * @Date 2019-8-26 17:32
     * @Description 添加用户信息
     */
    public int insert(KedaPublicUser kedaPublicUser) {
        return kedaPublicMapper.insert(kedaPublicUser);
    }

    /**
     * @Author zcy
     * @Date 2019-8-26 17:32
     * @Description 根据openid获取用户信息
     */
    public KedaPublicUser selectByOpenId(String openid2) {
        if (StringUtils.isEmpty(openid2)) {
            return null;
        }
        return kedaPublicMapper.selectByOpenId(openid2);
    }
}
