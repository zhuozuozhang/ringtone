package com.hrtxn.ringtone.project.numcertification.mapper;

import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.numcertification.domain.NumcertificationPrice;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author zcy
 * @Date 2019-08-31 17:25
 */
@Repository
public interface NumCertificateionPriceMapper {
    /**
     * 获取价格配置列表
     *
     * @author zcy
     * @date 2019-9-2 9:53
     */
    List<NumcertificationPrice> getNumPriceList(@Param("page") Page page, @Param("param") BaseRequest baseRequest);

    /**
     * 获取总数
     *
     * @author zcy
     * @date 2019-9-2 9:53
     */
    int getCount();

    /**
     * 修改价格配置信息
     *
     * @author zcy
     * @date 2019-9-2 13:29
     */
    int updateNumcertificationPrice(NumcertificationPrice numcertificationPrice);
}
