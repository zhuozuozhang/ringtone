package com.hrtxn.ringtone.project.numcertification.mapper;

import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.numcertification.domain.FourcertificationOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @param
 * @Author zcy
 * @Date 2019-08-30 11:05
 * @return
 */
@Repository
public interface FourcertificationOrderMapper {
    int insert(FourcertificationOrder fourcertificationOrder);

    List<FourcertificationOrder> queryOrderPage(@Param("page") Page page, @Param("param") BaseRequest b);

    int getCount(@Param("param") BaseRequest b);

    FourcertificationOrder getOrderByphoneFour(@Param("phoneFour") String phoneFour);

    void update(FourcertificationOrder FourcertificationOrder);

    int delete(int id);

    FourcertificationOrder getOrderByApplyNumber(String applyNumber);

    void updateAvailability(FourcertificationOrder FourcertificationOrder);

    FourcertificationOrder selectByPrimaryKey(@Param("id") Long id);
}
