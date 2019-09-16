package com.hrtxn.ringtone.project.numcertification.mapper;

import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.numcertification.domain.NumcertificationOrder;
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
public interface NumcertificationOrderMapper {
    int insert(NumcertificationOrder numcertificationOrder);

    List<NumcertificationOrder> selectOrder(@Param("page") Page page, @Param("param") BaseRequest b);

    int getCount(@Param("param") BaseRequest b);

}
