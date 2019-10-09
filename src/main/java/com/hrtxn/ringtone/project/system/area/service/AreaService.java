package com.hrtxn.ringtone.project.system.area.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.system.area.mapper.AreaMapper;
import com.hrtxn.ringtone.project.system.area.domain.Area;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-09 16:29
 * Description:公告业务实现类
 */
@Service
public class AreaService {

    @Autowired
    private AreaMapper AreaMapper;


    public AjaxResult findAllAreaList(Page page, Area Area) throws Exception {
        if (!StringUtils.isNotNull(page)) return null;
        page.setPage((page.getPage() - 1) * page.getPagesize());
        // 获取公告总数
        int count = AreaMapper.getAreaCount(Area);
        // 获取公告信息
        List<Area> noticeList = AreaMapper.findAllAreaList(page,Area);
        if (noticeList.size() > 0) {
            return AjaxResult.success(noticeList, "获取数据成功！", count);
        }
        return AjaxResult.success("没有获取到数据！");
    }

    public AjaxResult insertArea(Area Area){
        int flag = AreaMapper.insertArea(Area);
        if(flag > 0){
            return AjaxResult.success(true, "添加固话归属地");
        } else {
            return AjaxResult.error("添加固话归属地出错");
        }
    }

    public Area getAreaById(Integer id){
        return AreaMapper.getAreaById(id);
    }

    public AjaxResult updateArea(Area Area){
        int flag = AreaMapper.updateArea(Area);
        if(flag > 0){
            return AjaxResult.success(true, "修改固话归属地成功");
        } else {
            return AjaxResult.error("添加固话归属地出错");
        }
    }

    public AjaxResult deleteArea(Integer id){
        if (id != null && id != 0) {
            int count = AreaMapper.deleteArea(id);
            if (count > 0) {
                return AjaxResult.success(true, "删除成功");
            }
        }
        return null;
    }


    public List<Area> queryAreaByCons(String type,String pid){
        return AreaMapper.queryAreaByCons(type,pid);
    }





}
