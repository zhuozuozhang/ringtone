package com.hrtxn.ringtone.project.threenets.kedas.kedasites.mapper;

import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaRing;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-08-13 13:37
 * Description:科大铃音数据访问层
 */
@Repository
public interface KedaRingMapper {

    /**
     * 获取铃音列表
     *
     * @param page
     * @param baseRequest
     * @return
     */
    List<KedaRing> getKedaRingList(@Param("page") Page page, @Param("param") BaseRequest baseRequest);

    /**
     * 获取铃音数量
     *
     * @param baseRequest
     * @return
     */
    int getCount(@Param("param") BaseRequest baseRequest);

    /**
     * 批量删除铃音
     *
     * @param ids
     * @return
     */
    int deletePlKedaRing(@Param("ids") int[] ids);

    /**
     * 添加鈴音
     *
     * @param kedaRing
     * @return
     */
    int isertKedaRingOrder(KedaRing kedaRing);

    /**
     * 删除铃音
     *
     * @param id
     * @return
     */
    int deleteRing(@Param("id") Integer id);

    /**
     * 修改铃音信息
     *
     * @param kedaRing
     * @return
     */
    int updateKedaRing(KedaRing kedaRing);
}
