package com.hrtxn.ringtone.project.system.telAscription.mapper;


import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.system.telAscription.domain.TelAscription;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelAscriptionMapper {

    /**
     * 获取公告列表
     *
     * @return
     * @throws Exception
     */
    List<TelAscription> findAllTelAscriptionList(@Param("page") Page page, @Param("telAscription") TelAscription telAscription) throws Exception;


    /**
     * 获取公告数量
     *
     * @param telAscription
     * @return
     */
    int getTelAscriptionCount( @Param("telAscription") TelAscription telAscription);

    int insertTelAscription(TelAscription telAscription);

    public TelAscription getTelAscriptionById(Integer id);

    public int updateTelAscription(TelAscription telAscription);

    public int deleteTelAscription(Integer id);

    public TelAscription getTelAscriptionByTel(@Param("areaCode") String areaCode);
}