package com.hrtxn.ringtone.project.threenets.threenet.service;

import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreeNetsOrderAttached;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreeNetsOrderAttachedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author:zcy
 * Date:2019-07-29 13:44
 * Description:父级订单扩展表业务处理层
 */
@Service
public class ThreeNetsOrderAttachedService {

    @Autowired
    private ThreeNetsOrderAttachedMapper threeNetsOrderAttachedMapper;

    /**
     * 添加附表
     *
     * @param attached
     */
    public void save(ThreeNetsOrderAttached attached) {
        threeNetsOrderAttachedMapper.insertSelective(attached);
    }

    /**
     * 修改
     *
     * @param attached
     */
    public void update(ThreeNetsOrderAttached attached){
        threeNetsOrderAttachedMapper.updateByPrimaryKeySelective(attached);
    }
}
