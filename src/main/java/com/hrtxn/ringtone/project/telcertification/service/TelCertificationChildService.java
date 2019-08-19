package com.hrtxn.ringtone.project.telcertification.service;

import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.DateUtils;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationChildOrder;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationConsumeLog;
import com.hrtxn.ringtone.project.telcertification.mapper.CertificationChildOrderMapper;
import com.hrtxn.ringtone.project.telcertification.mapper.CertificationConsumeLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author:lile
 * Date:2019-07-11 09:07
 * Description:电话认证成员业务层
 */
@Service
public class TelCertificationChildService {
    @Autowired
    private CertificationChildOrderMapper certificationChildOrderMapper;
    @Autowired
    private CertificationConsumeLogMapper certificationConsumeLogMapper;

    public int getCount() {
        return certificationChildOrderMapper.getCount();
    }

    public List<CertificationChildOrder> findAllChildOrder(Page page,BaseRequest request) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<CertificationChildOrder> allChildOrderList = certificationChildOrderMapper.findAllChildOrder(page,request);

        List<CertificationChildOrder> dueList = new ArrayList<CertificationChildOrder>();
        if(allChildOrderList.size() > 0){
            return allChildOrderList;
        }
        return null;
    }

    public int getConsumeLogCount() {
        return certificationConsumeLogMapper.getConsumeLogCount();
    }

    public List<CertificationConsumeLog> findAllConsumeLog() {
        return certificationConsumeLogMapper.findAllConsumeLog();
    }

//    public int getFallDueCount() {
//        return certificationChildOrderMapper.getFallDueCount();
//    }

    //可能不需要的代码
    public List<CertificationChildOrder> getFallDueList(Page page, BaseRequest request) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<CertificationChildOrder> allChildOrderList = certificationChildOrderMapper.findAllChildOrder(page,request);
        List<CertificationChildOrder> fallDueList = new ArrayList<CertificationChildOrder>();
        if(allChildOrderList.size() > 0){
            for(CertificationChildOrder c : allChildOrderList){
                Timestamp nowTS = new Timestamp(System.currentTimeMillis());
                long now = nowTS.getTime();
                long extime = DateUtils.getNow(c.getTelChildOrderExpireTime());
                long result = (extime-now)/24/60/60/1000;
                if(result < 60 && result >= 0){
                    fallDueList.add(c);
                    c.setDue(2);
                }
            }
            return fallDueList;
        }

        return null;
    }


    public List<CertificationChildOrder> getDueList(Page page, BaseRequest request) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<CertificationChildOrder> allChildOrderList = certificationChildOrderMapper.findAllChildOrder(page,request);
        List<CertificationChildOrder> dueList = new ArrayList<CertificationChildOrder>();
        if(allChildOrderList.size() > 0){
            for(CertificationChildOrder c : allChildOrderList){
                Timestamp nowTS = new Timestamp(System.currentTimeMillis());
                long now = nowTS.getTime();
                long extime = DateUtils.getNow(c.getTelChildOrderExpireTime());
                long result = (extime-now)/24/60/60/1000;
                if(result < 0){
                    dueList.add(c);
                    c.setDue(1);
                }
            }
            return dueList;
        }
        return null;
    }
}
