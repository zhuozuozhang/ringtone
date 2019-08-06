package com.hrtxn.ringtone.project.system.ring.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsRing;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsRingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-24 10:36
 * Description:<描述>
 */
@Service
public class RingService {

    @Autowired
    private ThreenetsRingMapper threenetsRingMapper;

    public AjaxResult getRingList(Page page) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        if (!StringUtils.isNotNull(page)){
            return null;
        }
        List<ThreenetsRing> ring = threenetsRingMapper.getRing(page);
        int count = threenetsRingMapper.getCount(null);
        return AjaxResult.success(ring,"获取数据成功！",count);
    }
}
