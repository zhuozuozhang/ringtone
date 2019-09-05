package com.hrtxn.ringtone.project.telcertification.mapper;

import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationOrder;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationRequest;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author:yuanye
 * Date:2019/8/15 17:26
 * Description:号码认证订单数据访问层
 */
@Repository
public interface CertificationOrderMapper {

    /**
     * 根据条件获取全部商户信息
     * @param page
     * @param request
     * @return
     */
    List<CertificationOrder> findAllTelCertification(@Param("page") Page page, @Param("param") BaseRequest request);

    /**
     * 根据条件获取商户信息总数
     * @param request
     * @return
     */
    int getCount(@Param("param")BaseRequest request);

    /**
     * 根据id获取商户信息
     * @param id
     * @return
     */
    CertificationOrder getTelCerOrderById(Integer id);

    /**
     * 根据id删除商户信息
     * @param id
     * @return
     */
    int deleteByPrimaryKey(@Param("id") Integer id);

    /**
     * 根据子订单中的号码获取全部父订单
     * @param userTel
     * @return
     */
    CertificationOrder getTelCerOrderByChildOrder(String userTel);

    /**
     * 获取全部商户信息（无参数）
     * @return
     */
    List<CertificationOrder> findAllTelCer();

    /**
     * 根据id修改商户信息
     * @param certificationOrder
     * @return
     */
    int updateByPrimaryKey(CertificationOrder certificationOrder);

    /**
     * 添加商户信息
     * @param certificationRequest
     * @return
     */
    int insertTelCertifyOrder(CertificationRequest certificationRequest);

    /**
     * 获取最新添加的id
     * @return
     */
    int getLastInsertId();

    /**
     * 查询集团名称是否重复
     * @param telCompanyName
     * @return
     */
    List<CertificationOrder> isRepetitionByName(@Param("telCompanyName")String telCompanyName);
}
