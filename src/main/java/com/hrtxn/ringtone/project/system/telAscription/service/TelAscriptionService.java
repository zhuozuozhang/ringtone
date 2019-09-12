package com.hrtxn.ringtone.project.system.telAscription.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.system.notice.domain.Notice;
import com.hrtxn.ringtone.project.system.telAscription.domain.TelAscription;
import com.hrtxn.ringtone.project.system.telAscription.mapper.TelAscriptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-09 16:29
 * Description:公告业务实现类
 */
@Service
public class TelAscriptionService {

    @Autowired
    private TelAscriptionMapper telAscriptionMapper;


    public AjaxResult findAllTelAscriptionList(Page page,TelAscription telAscription) throws Exception {
        if (!StringUtils.isNotNull(page)) return null;
        page.setPage((page.getPage() - 1) * page.getPagesize());
        // 获取公告总数
        int count = telAscriptionMapper.getTelAscriptionCount(telAscription);
        // 获取公告信息
        List<TelAscription> noticeList = telAscriptionMapper.findAllTelAscriptionList(page,telAscription);
        if (noticeList.size() > 0) {
            return AjaxResult.success(noticeList, "获取数据成功！", count);
        }
        return AjaxResult.success("没有获取到数据！");
    }

    public AjaxResult insertTelAscription(TelAscription telAscription){
        int flag = telAscriptionMapper.insertTelAscription(telAscription);
        if(flag > 0){
            return AjaxResult.success(true, "添加固话归属地");
        } else {
            return AjaxResult.error("添加固话归属地出错");
        }
    }

    public TelAscription getTelAscriptionById(Integer id){
        return telAscriptionMapper.getTelAscriptionById(id);
    }

    public AjaxResult updateTelAscription(TelAscription telAscription){
        int flag = telAscriptionMapper.updateTelAscription(telAscription);
        if(flag > 0){
            return AjaxResult.success(true, "修改固话归属地成功");
        } else {
            return AjaxResult.error("添加固话归属地出错");
        }
    }

    public AjaxResult deleteTelAscription(Integer id){
        if (id != null && id != 0) {
            int count = telAscriptionMapper.deleteTelAscription(id);
            if (count > 0) {
                return AjaxResult.success(true, "删除成功");
            }
        }
        return null;
    }



}
