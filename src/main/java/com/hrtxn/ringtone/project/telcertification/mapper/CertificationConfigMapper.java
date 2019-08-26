package com.hrtxn.ringtone.project.telcertification.mapper;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *  Author:yuanye
 *  Date:2019年8月24日13:31:28
 *  Description:号码认证配置数据访问层
 */
@Repository
public interface CertificationConfigMapper {
    List<CertificationConfig> getAllConfig(@Param("page") Page page);

    int getCount();

    /**
     * 根据id获取配置信息
     * @param id
     * @return
     */
    CertificationConfig selectByPrimaryKey(@Param("id") Integer id);

    /**
     * 修改配置信息
     * @param certificationConfig
     * @return
     */
    int updateTelCerConfig(CertificationConfig certificationConfig);

    int deleteByPrimaryKey(@Param("id") Integer id);
}
