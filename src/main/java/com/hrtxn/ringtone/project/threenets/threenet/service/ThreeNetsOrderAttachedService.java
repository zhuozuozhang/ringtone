package com.hrtxn.ringtone.project.threenets.threenet.service;

import com.hrtxn.ringtone.common.api.MiguApi;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.exception.NoLoginException;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreeNetsOrderAttached;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreeNetsOrderAttachedMapper;
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



    public AjaxResult findCricleMsgList(String com_id) throws IOException, NoLoginException {
        System.out.println("service"+com_id);
        if(StringUtils.isNotNull(com_id)){
            Integer id = Integer.parseInt(com_id);
            ThreeNetsOrderAttached threeNetsOrderAttached = threeNetsOrderAttachedMapper.selectByParentOrderId(id);
            System.out.println(threeNetsOrderAttached);
            return apiUtils.findCricleMsgList(threeNetsOrderAttached.getMiguId());
        }
        return AjaxResult.success(false,"参数不正确！");

    }

//    public ThreeNetsOrderAttached findCricleMsgListById(String com_id) {
//        Integer id = Integer.parseInt(com_id);
//        return threeNetsOrderAttachedMapper.selectByParentOrderId(id);
//    }
}
