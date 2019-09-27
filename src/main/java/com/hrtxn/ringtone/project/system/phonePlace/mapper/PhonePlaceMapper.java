package com.hrtxn.ringtone.project.system.phonePlace.mapper;


import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.system.phonePlace.domain.PhonePlace;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhonePlaceMapper {

    /**
     * 获取公告列表
     *
     * @return
     * @throws Exception
     */
    List<PhonePlace> findAllPhonePlaceList(@Param("page") Page page, @Param("phonePlace") PhonePlace phonePlace) throws Exception;


    /**
     * 获取公告数量
     *
     * @param phonePlace
     * @return
     */
    int getPhonePlaceCount(@Param("phonePlace") PhonePlace phonePlace);

    int insertPhonePlace(PhonePlace phonePlace);

    PhonePlace getPhonePlaceById(Integer id);

    int updatePhonePlace(PhonePlace phonePlace);

    int deletePhonePlace(Integer id);

    PhonePlace getPhonePlaceByPhone(String phone);
}