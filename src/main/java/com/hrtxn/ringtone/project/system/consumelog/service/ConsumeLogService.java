package com.hrtxn.ringtone.project.system.consumelog.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.system.consumelog.domain.ConsumeLog;
import com.hrtxn.ringtone.project.system.consumelog.mapper.ConsumeLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: yuanye
 * @Date: Created in 10:29 2019/8/30
 * @Description: 消费记录业务逻辑层
 * @Modified By:
 */
@Service
public class ConsumeLogService {
    @Autowired
    private ConsumeLogMapper consumeLogMapper;

    public AjaxResult getConsumeLogList(Page page) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<ConsumeLog> allConsume = consumeLogMapper.getConsumeLogList(page);
        if(allConsume.size() > 0 && allConsume != null){
            for (ConsumeLog c: allConsume) {
                if(c.getConsumeType() == 1){
                    c.setConsumeTypeName("号码认证");
                }else if(c.getConsumeType() == 2){
                    c.setConsumeTypeName("三网");
                }
            }
            int totalCount = consumeLogMapper.getCount();
            return AjaxResult.success(allConsume,"获取成功",totalCount);
        }
        return AjaxResult.success(false,"获取失败");
    }

    public List<ConsumeLog> findConsumeLogByUserId(Integer id) {
        if(StringUtils.isNotNull(id)){
            return consumeLogMapper.findConsumeLogByUserId(id);
        }
        return null;
    }
}
