package com.hrtxn.ringtone.project.telcertification.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationOrder;
import com.hrtxn.ringtone.project.telcertification.mapper.CertificationChildOrderMapper;
import com.hrtxn.ringtone.project.telcertification.mapper.CertificationOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author:lile
 * Date:2019-07-10 16:07
 * Description:电话认证业务层
 */
@Service
public class TelCertificationService {

    @Autowired
    private CertificationOrderMapper certificationOrderMapper;
    @Autowired
    private CertificationChildOrderMapper certificationChildOrderMapper;


    public AjaxResult insertTelCertifyOrder(CertificationOrder certificationOrder) {
        if(!StringUtils.isNotNull(certificationOrder)){
            return AjaxResult.error("参数不正确！");
        }
        int count = certificationOrderMapper.insertTelCertifyOrder(certificationOrder);
        return null;
    }

    public int getCount() {
        return certificationOrderMapper.getCount();
    }

    public List<CertificationOrder> findAllTelCertification(Page page, BaseRequest request) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<CertificationOrder> allTelCer = certificationOrderMapper.findAllTelCertification(page,request);
        if(allTelCer.size() > 0){
            for (CertificationOrder c: allTelCer) {
                int member = certificationChildOrderMapper.getMemberCountByParentId(c.getId());
                c.setMemberNum(member);
            }
        }
        if(allTelCer != null){
            return allTelCer;
        }
        return null;
    }


}
