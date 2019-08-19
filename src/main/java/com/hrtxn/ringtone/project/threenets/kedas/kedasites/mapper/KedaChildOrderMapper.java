package com.hrtxn.ringtone.project.threenets.kedas.kedasites.mapper;

import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaChildOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.PlotBarPhone;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-08-13 13:32
 * Description:科大子订单数据访问层
 */
@Repository
public interface KedaChildOrderMapper {
    /**
     * 获取近5日信息
     *
     * @param id
     * @return
     */
    List<PlotBarPhone> getFiveData(@Param("id") Integer id);

    /**
     * 根据条件获取总数
     *
     * @param baseRequest
     * @return
     */
    Integer getCount(@Param("param") BaseRequest baseRequest);

    /**
     * 待办任务 等待铃音设置
     *
     * @param page
     * @param baseRequest
     * @return
     */
    List<KedaChildOrder> getKeDaChildOrderBacklogList(@Param("page") Page page, @Param("param") BaseRequest baseRequest);

    /**
     * 根据条件查询子订单已包月数据
     *
     * @param type
     * @param baseRequest
     * @return
     */
    List<PlotBarPhone> getIsMonthly(@Param("type") Integer type, @Param("param") BaseRequest baseRequest);

    /**
     * 根据条件查询子订单已退订数据
     *
     * @param type
     * @param baseRequest
     * @return
     */
    List<PlotBarPhone> getUnsubscribe(@Param("type") Integer type, @Param("param") BaseRequest baseRequest);

    /**
     * 根据条件获取业务发展总数
     *
     * @param type
     * @param baseRequest
     * @return
     */
    int getBussinessCount(@Param("type") Integer type, @Param("param") BaseRequest baseRequest);

    /**
     * 添加自己订单
     *
     * @param kedaChildOrder
     * @return
     */
    int insertKedaChildOrder(KedaChildOrder kedaChildOrder);

    /**
     * 批量刪除子級訂單
     *
     * @param keDaChildOrderIds
     * @return
     */
    int deletePlKedaChilOrder(@Param("keDaChildOrderIds") int[] keDaChildOrderIds);

    /**
     * 修改子级订单信息
     *
     * @param kedaChildOrder
     * @return
     */
    int updatKedaChildOrder(KedaChildOrder kedaChildOrder);

    /**
     * 疑难杂单子订单删除
     *
     * @param id
     * @return
     */
    int deleteKedaChilOrder(@Param("id") Integer id);
}
