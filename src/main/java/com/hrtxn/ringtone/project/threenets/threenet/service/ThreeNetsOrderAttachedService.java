package com.hrtxn.ringtone.project.threenets.threenet.service;

import com.hrtxn.ringtone.common.api.MiguApi;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.exception.NoLoginException;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreeNetsOrderAttached;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsOrder;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreeNetsOrderAttachedMapper;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.utils.ApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-29 13:44
 * Description:父级订单扩展表业务处理层
 */
@Service
public class ThreeNetsOrderAttachedService {

    @Autowired
    private ThreeNetsOrderAttachedMapper threeNetsOrderAttachedMapper;
    @Autowired
    private ThreenetsOrderMapper threenetsOrderMapper;

    private ApiUtils apiUtils = new ApiUtils();

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

    /**
     * 根据父级id查询
     *
     * @param id
     * @return
     */
    public ThreeNetsOrderAttached selectByParentOrderId(Integer id){
        return threeNetsOrderAttachedMapper.selectByParentOrderId(id);
    }

    /**
     * 删除附表
     *
     * @param id
     * @return
     */
    public Integer delete(Integer id){
        return threeNetsOrderAttachedMapper.deleteByPrimaryKey(id);
    }


    /**
     * 商户列表-->信息处理
     * @param com_id
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public AjaxResult findCricleMsgList(String com_id) throws IOException, NoLoginException {
        if(StringUtils.isNotNull(com_id)){
            Integer id = Integer.parseInt(com_id);
            ThreeNetsOrderAttached threeNetsOrderAttached = threeNetsOrderAttachedMapper.selectByParentOrderId(id);
            int count = 0;
            if(StringUtils.isNotNull(threeNetsOrderAttached)){
                String migu_id = threeNetsOrderAttached.getMiguId();
                if(StringUtils.isNotNull(migu_id)){
                    count = threenetsOrderMapper.updateMessageIsNotNullById(id); //order表中的message设置为2，意味着有消息
                    return apiUtils.findCricleMsgList(migu_id);
                }
                count = threenetsOrderMapper.updateMessageById(id); //order表中的message设置为1，意味着无消息
                return AjaxResult.success(count,"没有获取到该商户的migu_id,因为不是移动用户");
            }
            count = threenetsOrderMapper.updateMessageById(id); //order表中的message设置为1
            return AjaxResult.success(count,"在父表的附属表中，不含有外键父级订单id：\"+com_id+\"所属的订单");
        }
        return AjaxResult.success(false,"参数不正确");
    }

}
