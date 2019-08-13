package com.hrtxn.ringtone.project.system.config.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.system.config.domain.SystemConfig;
import com.hrtxn.ringtone.project.system.config.mapper.SystemConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-08-12 17:41
 * Description:系统配置业务处理层
 */
@Service
public class SystemConfigService {

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    public AjaxResult getConfigList(Page page) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        // 获取配置列表
        List<SystemConfig> systemConfigList = systemConfigMapper.getConfigList(page);
        // 获取总数
        int count = systemConfigMapper.getCount();
        if (systemConfigList.size() > 0){
            return AjaxResult.success(systemConfigList,"获取数据成功！",count);
        }
        return AjaxResult.error("无数据！");
    }
}
