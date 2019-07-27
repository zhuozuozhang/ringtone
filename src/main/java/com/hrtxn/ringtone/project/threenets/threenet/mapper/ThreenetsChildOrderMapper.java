package com.hrtxn.ringtone.project.threenets.threenet.mapper;

import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.threenets.threenet.domain.PlotBarPhone;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsChildOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-12 14:06
 * Description:三网子订单数据
 */
@Repository
public interface ThreenetsChildOrderMapper {

    /**
     * 查询三网订单
     *
     * @param id
     * @return
     * @throws Exception
     */
    ThreenetsChildOrder selectByPrimaryKey(Integer id) throws Exception;

    /**
     * 删除
     *
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 新增
     *
     * @param ThreenetsChildOrder
     * @return
     */
    int insertThreeNetsChildOrder(ThreenetsChildOrder ThreenetsChildOrder);

    /**
     * 修改
     *
     * @param ThreenetsChildOrder
     * @return
     */
    int updateThreeNetsChidOrder(ThreenetsChildOrder ThreenetsChildOrder);

    /**
     * 根据条件查询列表
     *
     * @param page
     * @param param
     * @return
     * @throws Exception
     */
    List<ThreenetsChildOrder> selectThreeNetsTaskList(@Param("page") Page page, @Param("param") ThreenetsChildOrder param) throws Exception;

    /**
     * 获取总条数
     *
     * @param param
     * @return
     */
    Integer getCount(@Param("param") ThreenetsChildOrder param);

    /**
     * 获取近5日信息
     *
     * @param id
     * @return
     */
    List<PlotBarPhone> getFiveData(Integer id);

    /**
     * 查询某月数据统计
     *
     * @param param
     * @return
     */
    List<PlotBarPhone> getMonthData(@Param("param") BaseRequest param);

    /**
     * 查询某年数据统计
     *
     * @param param
     * @return
     */
    List<PlotBarPhone> getYearData(@Param("param") BaseRequest param);


    /**
     * 根据父级订单id删除
     *
     * @param id
     * @return
     */
    int deleteByParadeOrderId(Integer id);

    /**
     * 批量保存
     *
     * @param list
     * @return
     */
    int batchChindOrder(@Param("list") List<ThreenetsChildOrder> list);
}
