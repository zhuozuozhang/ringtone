package com.hrtxn.ringtone.project.system.phonePlace.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.system.phonePlace.domain.PhonePlace;
import com.hrtxn.ringtone.project.system.phonePlace.mapper.PhonePlaceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-09 16:29
 * Description:公告业务实现类
 */
@Service
public class PhonePlaceService {

    @Autowired
    private PhonePlaceMapper PhonePlaceMapper;


    public AjaxResult findAllPhonePlaceList(Page page,PhonePlace PhonePlace) throws Exception {
        if (!StringUtils.isNotNull(page)) return null;
        page.setPage((page.getPage() - 1) * page.getPagesize());
        // 获取公告总数
        int count = PhonePlaceMapper.getPhonePlaceCount(PhonePlace);
        // 获取公告信息
        List<com.hrtxn.ringtone.project.system.phonePlace.domain.PhonePlace> noticeList = PhonePlaceMapper.findAllPhonePlaceList(page,PhonePlace);
        if (noticeList.size() > 0) {
            return AjaxResult.success(noticeList, "获取数据成功！", count);
        }
        return AjaxResult.success("没有获取到数据！");
    }

    public AjaxResult insertPhonePlace(PhonePlace PhonePlace){
        int flag = PhonePlaceMapper.insertPhonePlace(PhonePlace);
        if(flag > 0){
            return AjaxResult.success(true, "添加固话归属地");
        } else {
            return AjaxResult.error("添加固话归属地出错");
        }
    }

    public PhonePlace getPhonePlaceById(Integer id){
        return PhonePlaceMapper.getPhonePlaceById(id);
    }

    public AjaxResult updatePhonePlace(PhonePlace PhonePlace){
        int flag = PhonePlaceMapper.updatePhonePlace(PhonePlace);
        if(flag > 0){
            return AjaxResult.success(true, "修改固话归属地成功");
        } else {
            return AjaxResult.error("添加固话归属地出错");
        }
    }

    public AjaxResult deletePhonePlace(Integer id){
        if (id != null && id != 0) {
            int count = PhonePlaceMapper.deletePhonePlace(id);
            if (count > 0) {
                return AjaxResult.success(true, "删除成功");
            }
        }
        return null;
    }



}
