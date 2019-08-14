package com.hrtxn.ringtone.project.threenets.kedas.kedasites.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaChildOrder;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.mapper.KedaChildOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.domain.PlotBarPhone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-08-14 10:17
 * Description:疑难杂单业务处理层
 */
@Service
public class KedaChildOrderService {

    @Autowired
    private KedaChildOrderMapper kedaChildOrderMapper;

    /**
     * 获取近5日信息
     *
     * @return
     */
    public List<PlotBarPhone> getFiveData() {
        Integer id = ShiroUtils.getSysUser().getId();
        if (StringUtils.isNotNull(id)) {
            // 获取近5日信息
            List<PlotBarPhone> fiveData = kedaChildOrderMapper.getFiveData(id);
            BaseRequest baseRequest = new BaseRequest();
            baseRequest.setUserId(id);
            // 根据条件获取总数
            Integer count = kedaChildOrderMapper.getCount(baseRequest);
            for (PlotBarPhone plotBarPhone : fiveData) {
                plotBarPhone.setCumulativeUser(count);
                count = count - plotBarPhone.getAddUser();
            }
            return fiveData;
        }
        return null;
    }

    /**
     * 待办任务 等待铃音设置
     *
     * @param page
     * @param baseRequest
     * @return
     */
    public AjaxResult getKeDaChildOrderBacklogList(Page page, BaseRequest baseRequest) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        baseRequest.setUserId(ShiroUtils.getSysUser().getId());
        // 根据条件获取子订单列表
        List<KedaChildOrder> kedaChildOrders = kedaChildOrderMapper.getKeDaChildOrderBacklogList(page, baseRequest);
        // 获取数量
        Integer count = kedaChildOrderMapper.getCount(baseRequest);
        if (kedaChildOrders.size() > 0) {
            return AjaxResult.success(kedaChildOrders, "获取子订单数据成功！", count);
        }
        return AjaxResult.error("无数据!");
    }

    /**
     * 获取子订单列表
     *
     * @param page
     * @param baseRequest
     * @return
     */
    public AjaxResult getKedaChidList(Page page, BaseRequest baseRequest) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        // 根据条件获取子订单列表
        List<KedaChildOrder> keDaChildOrderBacklogList = kedaChildOrderMapper.getKeDaChildOrderBacklogList(page, baseRequest);
        // 获取总数
        Integer count = kedaChildOrderMapper.getCount(baseRequest);
        if (keDaChildOrderBacklogList.size() > 0) {
            return AjaxResult.success(keDaChildOrderBacklogList, "获取数据成功！", count);
        }
        return AjaxResult.error("无数据！");
    }
}
