package com.hrtxn.ringtone.project.threenets.threenet.mapper;

import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreeNetsOrderAttached;
import org.springframework.stereotype.Repository;

/**
 * Author:zcy
 * Date:2019-07-29 13:41
 * Description:父级订单扩展表数据访问层
 */
@Repository
public interface ThreeNetsOrderAttachedMapper {

    /**
     *
     * @param id
     * @return
     */
    ThreeNetsOrderAttached selectByPrimaryKey(Integer id);

    /**
     *
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *
     * @param threeNetsOrderAttached
     * @return
     */
    int insertSelective(ThreeNetsOrderAttached threeNetsOrderAttached);

    /**
     *
     * @param threeNetsOrderAttached
     * @return
     */
    int updateByPrimaryKeySelective(ThreeNetsOrderAttached threeNetsOrderAttached);

    /**
     * 根据父级订单id获取附表数据
     *
     * @param id
     * @return
     */
    ThreeNetsOrderAttached selectByParentOrderId(Integer id);

    /**
     *
     * @param id
     * @return
     */

}
