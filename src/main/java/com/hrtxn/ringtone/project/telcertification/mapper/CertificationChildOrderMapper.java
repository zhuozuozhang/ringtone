package com.hrtxn.ringtone.project.telcertification.mapper;

import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationChildOrder;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author:yuanye
 * Date:2019/8/16 14:14
 * Description:号码认证子订单数据访问层
 */
@Repository
public interface CertificationChildOrderMapper {

    int getCount(@Param("param") BaseRequest request);

    /**
     * 根据成员号码查找
     * @param page
     * @param request
     * @return
     */
    List<CertificationChildOrder> findTheChildOrder(@Param("page") Page page, @Param("param") BaseRequest request);

    int getMemberCountByParentId(int i);

    CertificationChildOrder getTelCerChildById(Integer id);

    int deleteByPrimaryKey(@Param("id") Integer id);

    int editFeedBackById(CertificationChildOrder certificationChildOrder);

    int getTelcerChildParentIdByPhoneNum(String phoneNum);

    int getTelcerChildParentIdById(String id);

    CertificationChildOrder getTelcerChildByPhoneNum(String userTel);

    int insert(CertificationChildOrder certificationChildOrder);

    int batchInsertChildOrder(@Param("list") List<CertificationChildOrder> list);

    int editChildOrderIfStatusChanged(CertificationChildOrder certificationChildOrder);

    List<CertificationChildOrder> getFallDueList(@Param("page") Page page, @Param("param") BaseRequest request);

    int getFallDueListCount(@Param("page") Page page,@Param("param") BaseRequest request);

    List<CertificationChildOrder> getDueList(@Param("page") Page page, @Param("param") BaseRequest request);

    int getDueListCount(@Param("page") Page page,@Param("param") BaseRequest request);

    List<CertificationChildOrder> isRepetitionByChildNum(@Param("phoneNum") String phoneNum);

    void updateExamine(CertificationChildOrder certificationChildOrder);
    void updateExamineById(CertificationChildOrder certificationChildOrder);

    Float queryPriceByPid(@Param("pid") Integer pid);
}
