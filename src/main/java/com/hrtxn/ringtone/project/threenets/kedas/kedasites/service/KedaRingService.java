package com.hrtxn.ringtone.project.threenets.kedas.kedasites.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaRing;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.mapper.KedaRingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-08-14 17:32
 * Description：疑难杂单铃音业务处理层
 */
@Service
public class KedaRingService {

    @Autowired
    private KedaRingMapper kedaRingMapper;

    public AjaxResult getKedaRingList(Page page, BaseRequest baseRequest) {

        page.setPage((page.getPage() - 1) * page.getPagesize());
        // 获取铃音列表
        List<KedaRing> kedaRingList = kedaRingMapper.getKedaRingList(page,baseRequest);
        // 获取铃音数量
        int count = kedaRingMapper.getCount(baseRequest);
        if (kedaRingList.size() > 0){
            return AjaxResult.success(kedaRingList,"",count);
        }
        return AjaxResult.error("无数据！");
    }
}
