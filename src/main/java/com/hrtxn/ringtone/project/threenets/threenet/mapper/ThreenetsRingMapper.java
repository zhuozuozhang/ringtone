package com.hrtxn.ringtone.project.threenets.threenet.mapper;

import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsRing;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-12 14:09
 * Description:三网铃音数据访问层
 */
@Repository
public interface ThreenetsRingMapper {

    List<ThreenetsRing> getRing(@Param("page") Page page);

    /**
     * 添加三网铃音信息
     *
     * @param threenetsRing
     * @return
     */
    int insertThreeNetsRing(ThreenetsRing threenetsRing);

    /**
     * 根据id获取铃音
     *
     * @param id
     * @return
     * @throws Exception
     */
    ThreenetsRing selectByPrimaryKey(Integer id) throws Exception;

    /**
     * 根据父级订单id获取铃音
     *
     * @param id
     * @return
     * @throws Exception
     */
    List<ThreenetsRing> selectByOrderId(Integer id) throws Exception;

    /**
     * 根据id删除铃音
     *
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 修改铃音
     *
     * @param threenetsRing
     * @return
     */
    int updateByPrimaryKeySelective(ThreenetsRing threenetsRing);

    /**
     * 获取铃音
     *
     * @param page
     * @param request
     * @return
     */
    List<ThreenetsRing> getRingList(@Param("page") Page page, @Param("param") BaseRequest request);

    /**
     * 获取铃音个数
     *
     * @param request
     * @return
     */
    int getCount(@Param("param") BaseRequest request);

    /**
     * 根据父级id进行删除
     *
     * @param id
     * @return
     */
    int deleteByParadeOrderId(Integer id);

    /**
     * 获取设置铃音激活成功铃音数据数据
     *
     * @param page
     * @param orderId
     * @param operate
     * @return
     */
    List<ThreenetsRing> getSetRingList(@Param("page") Page page, @Param("orderId") Integer orderId, @Param("operate") Integer operate) throws Exception;

    /**
     * 获取设置铃音激活成功铃音总数
     *
     * @param orderId
     * @param operate
     * @return
     */
    int getSetRingCount(@Param("orderId") Integer orderId, @Param("operate") Integer operate) throws Exception;

    /**
     * 获取待审核、激活中的铃音
     *
     *@param userId
     * @return
     */
    List<ThreenetsRing> findRingIsNotSuccess(@Param("userId") Integer userId);

    /**
     * 根据商户id和运营商查询铃音
     *
     * @param orderId
     * @param operate
     * @return
     */
    List<ThreenetsRing> listByOrderIdAndOperator(@Param("orderId") Integer orderId,@Param("operate") Integer operate);
}
