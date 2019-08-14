package com.hrtxn.ringtone.project.threenets.kedas.kedasites.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaOrder;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.mapper.KedaOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-08-14 13:55
 * Description:疑难杂单 父级订单业务处理层
 */
@Service
public class KedaOrderService {

    @Autowired
    private KedaOrderMapper kedaOrderMapper;

    /**
     * 获取父级订单列表
     *
     * @param page
     * @param baseRequest
     * @return
     */
    public AjaxResult getKeDaOrderList(Page page, BaseRequest baseRequest) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        // 获取数据
        baseRequest.setUserId(ShiroUtils.getSysUser().getId());
        List<KedaOrder> kedaOrderList = kedaOrderMapper.getKeDaOrderList(page, baseRequest);
        // 获取数量
        int count = kedaOrderMapper.getCount(baseRequest);
        if (kedaOrderList.size() > 0) {
            for (KedaOrder kedaOrder : kedaOrderList) {
                kedaOrder.setPeopleNum(kedaOrder.getChildOrderQuantity() + "/" + kedaOrder.getChildOrderQuantityByMonthly());
            }
            return AjaxResult.success(kedaOrderList, "获取数据成功！", count);
        }
        return AjaxResult.error("无数据！");
    }

    /**
     * 修改父级订单信息
     *
     * @param kedaOrder
     * @return
     */
    public AjaxResult updateCompanyName(KedaOrder kedaOrder) {
        if (StringUtils.isNotNull(kedaOrder) && StringUtils.isNotNull(kedaOrder.getId()) && StringUtils.isNotEmpty(kedaOrder.getCompanyName())) {
            // 根据商户名称查找父级订单信息
            BaseRequest baseRequest = new BaseRequest();
            baseRequest.setCompanyName(kedaOrder.getCompanyName());
            List<KedaOrder> kedaOrderList = kedaOrderMapper.getKeDaOrderList(null,baseRequest);
            if (kedaOrderList.size() > 0){
                return AjaxResult.error("商户名称重复！");
            }
            // 执行修改商户名操作
            int count = kedaOrderMapper.updateKedaOrder(kedaOrder);
            if (count > 0) {
                return AjaxResult.success(true, "修改成功！");
            } else {
                return AjaxResult.error("修改失败！");
            }
        }
        return AjaxResult.error("参数格式错误！");
    }
}
