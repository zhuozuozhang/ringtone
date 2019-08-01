package com.hrtxn.ringtone.project.threenets.threenet.mapper;

import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-12 14:08
 * Description:三网订单数据访问层
 */
@Repository
public interface ThreenetsOrderMapper {

    /**
     * 查询三网订单
     * @param id
     * @return
     */
    ThreenetsOrder selectByPrimaryKey(Integer id);

    /**
     * 删除
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 新增
     * @param ThreenetsOrder
     * @return
     */
    int insertThreenetsOrder(ThreenetsOrder ThreenetsOrder);

    /**
     * 修改
     * @param ThreenetsOrder
     * @return
     */
    int updateByPrimaryKey(ThreenetsOrder ThreenetsOrder);

    /**
     * 查询三网商户列表信息
     * @param page
     * @param request
     * @return
     */
    List<ThreenetsOrder> getAllorderList(@Param("page") Page page, @Param("param")BaseRequest request);

    /**
     * 查询三网商户列表总数
     * @return
     */
    int getCount(@Param("param")BaseRequest request);
}
