package com.hrtxn.ringtone.project.threenets.kedas.kedasites.mapper;

import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-08-13 13:36
 * Description:科大父级订单数据访问层
 */
@Repository
public interface KedaOrderMapper {

    /**
     * 获取父级订单列表
     *
     * @param page
     * @param baseRequest
     * @return
     */
    List<KedaOrder> getKeDaOrderList(@Param("page") Page page, @Param("param") BaseRequest baseRequest);

    /**
     * 获取总数
     *
     * @param baseRequest
     * @return
     */
    int getCount(@Param("param") BaseRequest baseRequest);

    /**
     * 修改父级订单信息
     *
     * @param kedaOrder
     * @return
     */
    int updateKedaOrder(KedaOrder kedaOrder);

    int insertKedaOrder(KedaOrder kedaOrder);

    /**
     * 疑难杂单父级订单删除
     *
     * @param id
     * @return
     */
    int deleteKedaOrder(@Param("id") Integer id);
}
