package com.hrtxn.ringtone.project.threenets.kedas.kedasites.service;

import com.hrtxn.ringtone.common.api.KedaApi;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaRing;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.mapper.KedaRingMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Author:lile
 * Date:2019-09-20 10:16
 * Description:
 */
@Slf4j
@Service
public class KedaAsyncService {

    @Autowired
    private KedaRingMapper ringMapper;

    @Async
    public void updateRingtoneInformation(List<KedaRing> list){
        log.info("疑难杂单数据同步开始 ----------->");
        try{
            KedaApi kedaApi = new KedaApi();
            list = kedaApi.refreshRingInfo(list);
            for (int i = 0; i < list.size(); i++) {
                ringMapper.updateKedaRing(list.get(i));
            }
        }catch(IOException e) {
            log.info("疑难杂单同步铃音数据错误 ----------->",e);
        }
    }
}
