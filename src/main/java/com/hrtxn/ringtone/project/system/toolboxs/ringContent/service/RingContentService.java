package com.hrtxn.ringtone.project.system.toolboxs.ringContent.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.system.toolboxs.ringContent.domain.RingContent;
import com.hrtxn.ringtone.project.system.toolboxs.ringContent.mapper.RingContentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @param
 * @Author zcy
 * @Date 2019-09-04 13:23
 * @return
 */
@Service
public class RingContentService {

    @Autowired
    private RingContentMapper ringContentMapper;

    public AjaxResult selectRingContent(Page page,String ringcontent) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        // 执行获取l铃音内容列表
        BaseRequest b = new BaseRequest();
        b.setName(ringcontent);
        List<RingContent> ringContentList = ringContentMapper.selectRingContent(page, b);
        int count = ringContentMapper.getCount(b);
        return AjaxResult.success(ringContentList, "获取成功！", count);
    }

    public AjaxResult deleteRingContent(Integer id) {
        if (StringUtils.isNull(id) || id <= 0) {
            return AjaxResult.error();
        }
        // 执行删除操作
        int i = ringContentMapper.deleteRingContent(id);
        if (i > 0) {
            return AjaxResult.success(true, "删除成功！");
        }
        return AjaxResult.error("删除失败！");
    }

    public AjaxResult insertRingContent(RingContent ringContent) {
        if (StringUtils.isNull(ringContent)) {
            return AjaxResult.error();
        }
        if (StringUtils.isEmpty(ringContent.getRingContent())) {
            return AjaxResult.error();
        }

        // 执行添加操作
        ringContent.setCreateTime(new Date());
        int count = ringContentMapper.insertRingContent(ringContent);

        if (count > 0) {
            return AjaxResult.success(true, "添加成功！");
        }
        return AjaxResult.error("添加失败！");
    }

    public RingContent selectById(Integer id) {
        if (StringUtils.isNotNull(id)) {
            BaseRequest b = new BaseRequest();
            b.setId(id);
            List<RingContent> ringContentList = ringContentMapper.selectRingContent(null, b);
            if (StringUtils.isNotNull(ringContentList) && ringContentList.size() > 0) {
                return ringContentList.get(0);
            }
        }
        return null;
    }

    public AjaxResult updateRingContent(RingContent ringContent) {
        if (StringUtils.isNull(ringContent)) {
            return AjaxResult.error();
        }
        if (StringUtils.isEmpty(ringContent.getRingContent())) {
            return AjaxResult.error();
        }
        if (StringUtils.isNull(ringContent.getId())) {
            return AjaxResult.error();
        }
        // 执行修改操作
        int i = ringContentMapper.updateRingContent(ringContent);
        if (i > 0) {
            return AjaxResult.success(true, "修改成功！");
        }
        return AjaxResult.error("修改失败！");
    }
}
