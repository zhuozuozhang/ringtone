package com.hrtxn.ringtone.project.threenets.kedas.kedapublic.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.threenets.kedas.kedapublic.domain.KedaPublicOnlineOrder;
import com.hrtxn.ringtone.project.threenets.kedas.kedapublic.mapper.KedaPublicOnlineOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author:zcy
 * @Date:2019-08-24 16:27
 * @Description: 商务彩铃在线办理订单
 */
@Service
public class KedaPublicOnlineOrderService {

    @Autowired
    private KedaPublicOnlineOrderMapper kedaPublicOnlineOrderMapper;

    /**
     * @Author zcy
     * @Date 2019-8-24 16:44
     * @Description 添加商务彩铃在线办理订单
     */
    public AjaxResult insertOnlieOrder(KedaPublicOnlineOrder kedaPublicOnlineOrder) {
        if (StringUtils.isNull(kedaPublicOnlineOrder)) {return AjaxResult.error();}
        if (StringUtils.isEmpty(kedaPublicOnlineOrder.getName())) {return AjaxResult.error();}
        if (StringUtils.isEmpty(kedaPublicOnlineOrder.getCompanyName())) {return AjaxResult.error();}
        if (StringUtils.isEmpty(kedaPublicOnlineOrder.getPhone()) || kedaPublicOnlineOrder.getPhone().length() != 11) {return AjaxResult.error();}
        //执行添加操作
        Boolean b = kedaPublicOnlineOrderMapper.insertOnlieOrder(kedaPublicOnlineOrder) > 0 ? true : false;
        if (b) {
            return AjaxResult.success("提交成功，稍后会有客服与您取得联系！");
        } else {
            return AjaxResult.error("提交失败！");
        }
    }
}
