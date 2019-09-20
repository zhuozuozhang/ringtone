package com.hrtxn.ringtone.project.threenets.kedas.kedasites.time;

import com.hrtxn.ringtone.common.api.KedaApi;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.utils.SpringUtils;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaChildOrder;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaOrder;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.mapper.KedaChildOrderMapper;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.mapper.KedaOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Author:zcy
 * Date:2019-08-23 10:26
 * Description:疑难杂单定时任务
 */
@Component
@Slf4j
public class KedaTimeTask {

    private KedaApi kedaApi = new KedaApi();

    /**
     * 更新除已包月子订单数据
     * 每天12:30
     * 0 30 12 ? * *
     */
    @Scheduled(cron = "0 30 12 ? * *")
    public void updatew() throws IOException {
        // 获取除已包月子订单数据（0,2,3,4）
        List<KedaChildOrder> kedaChildOrders = SpringUtils.getBean(KedaChildOrderMapper.class).selectKedaChildorder(1);
        if (kedaChildOrders.size() > 0) {
            for (int i = 0; i < kedaChildOrders.size(); i++) {
                AjaxResult msg = kedaApi.getMsg(kedaChildOrders.get(i).getLinkTel(), kedaChildOrders.get(i).getId());
                log.info("疑难杂单定时器更新除已包月子订单数据：{}", msg);
            }
        }
    }

    /**
     * 更新已包月子订单数据
     * 每月15号00:00
     * 0 00 00 15 * ?
     */
    @Scheduled(cron = "0 57 10 ? * *")
    public void updatey() throws IOException {
        // 获取所有无效文件
        List<KedaChildOrder> kedaChildOrders = SpringUtils.getBean(KedaChildOrderMapper.class).selectKedaChildorder(2);
        if (kedaChildOrders.size() > 0) {
            for (int i = 0; i < kedaChildOrders.size(); i++) {
                AjaxResult msg = kedaApi.getMsg(kedaChildOrders.get(i).getLinkTel(), kedaChildOrders.get(i).getId());
                log.info("疑难杂单定时器更新已包月子订单数据：{}", msg);
            }
        }
    }

    /**
     * 同步失败的子订单
     *
     * @throws IOException
     */
    @Scheduled(cron = "0 23 23 ? * *")
    public void synchronizationFailureData() throws IOException {
        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setRemark("参数校验失败");
        List<KedaChildOrder> list = SpringUtils.getBean(KedaChildOrderMapper.class).selectByParam(baseRequest);
        for (int i = 0; i < list.size(); i++) {
            KedaChildOrder kedaChildOrder = list.get(i);
            KedaOrder order = SpringUtils.getBean(KedaOrderMapper.class).getKedaOrder(kedaChildOrder.getOrderId());
            AjaxResult add = kedaApi.add(kedaChildOrder,order);
            if ((int) add.get("code") == 200) {
                kedaChildOrder.setStatus("审核通过");
                kedaChildOrder.setRemark("添加成功！");
            } else {
                kedaChildOrder.setRemark(add.get("msg").toString());
            }
            SpringUtils.getBean(KedaChildOrderMapper.class).updatKedaChildOrder(kedaChildOrder);
        }
    }
}
