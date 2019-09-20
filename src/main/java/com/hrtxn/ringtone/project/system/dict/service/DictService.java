package com.hrtxn.ringtone.project.system.dict.service;


import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.system.dict.domain.Dict;
import com.hrtxn.ringtone.project.system.dict.mapper.DictMapper;
import com.hrtxn.ringtone.project.system.telAscription.domain.TelAscription;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictService {

    @Autowired
    DictMapper mapper;


    public AjaxResult insert(Dict dict){
        int flag =  mapper.insert(dict);
        if(flag > 0){
            return AjaxResult.success(true, "添加成功");
        } else {
            return AjaxResult.error("添加失败");
        }
    }

    public AjaxResult update(Dict dict){
        int flag = mapper.update(dict);
        if(flag > 0){
            return AjaxResult.success(true, "修改成功");
        } else {
            return AjaxResult.error("修改失败");
        }
    }

    public AjaxResult pageDictList(@Param("page") Page page, @Param("dict") Dict dict){

        if (!StringUtils.isNotNull(page)) return null;
        page.setPage((page.getPage() - 1) * page.getPagesize());
        // 获取公告总数
        int count = mapper.getDictCount(dict);
        // 获取公告信息
        List<Dict> list =  mapper.pageDictList(page,dict);
        if (list.size() > 0) {
            return AjaxResult.success(list, "获取数据成功！", count);
        }
        return AjaxResult.success("没有获取到数据！");

    }

    public List<Dict> queryDictByType(String type){
        return mapper.queryDictByType(type);
    }

    public Dict getDcitByTypeAndCode(String type,String code){
        return mapper.getDcitByTypeAndCode(type,code);
    }

    public Dict getDictByTypeAndName(String type,String name){
        return mapper.getDictByTypeAndName(type,name);
    }

    public int getDictCount(Dict dict){
        return mapper.getDictCount(dict);
    }

    public AjaxResult delete(Integer id){
        int flag = mapper.delete(id);
        if(flag > 0){
            return AjaxResult.success(true, "删除成功");
        } else {
            return AjaxResult.error("删除失败");
        }
    }

    public Dict getDictById(Integer id){
        return mapper.getDictById(id);
    }

}
