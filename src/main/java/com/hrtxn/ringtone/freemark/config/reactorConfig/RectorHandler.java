package com.hrtxn.ringtone.freemark.config.reactorConfig;

import com.hrtxn.ringtone.common.api.KedaApi;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.utils.Const;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaChildOrder;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaOrder;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.mapper.KedaChildOrderMapper;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.mapper.KedaOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.Reactor;
import reactor.event.Event;
import reactor.spring.annotation.Selector;

import java.io.IOException;
import java.util.List;

/**
 * Author:zcy
 * Date:2019-08-17 11:52
 * Description:<描述>
 */
@Component
@Slf4j
public class RectorHandler {

    @Autowired
    @Qualifier("createReactor")//通过name指定刚定义的库，并注入进来
            Reactor r;

    @Autowired
    private KedaChildOrderMapper kedaChildOrderMapper;

    @Autowired
    private KedaOrderMapper kedaOrderMapper;

    private KedaApi kedaApi = new KedaApi();

    /**
     * 创建疑难杂单父级订单
     *
     * @param data
     * @throws IOException
     */
    @Selector(value = "insertKedaorder", reactor = "@createReactor")
    public void insertKedaorder(Event<KedaChildOrder> data) throws IOException {
        KedaChildOrder kedaChildOrder = data.getData();
        KedaOrder order = kedaOrderMapper.getKedaOrder(kedaChildOrder.getOrderId());
        // 对接数据，创建父级订单
        AjaxResult add = kedaApi.add(kedaChildOrder, order);
        if ((int) add.get("code") == 200) {
            kedaChildOrder.setStatus(Const.SUCCESSFUL_REVIEW);
            kedaChildOrder.setRemark("添加成功！");
        } else {
            kedaChildOrder.setStatus(Const.FAILURE_REVIEW);
            kedaChildOrder.setRemark(add.get("msg").toString());
        }
        // 执行修改子级订单操作
        int count = kedaChildOrderMapper.updatKedaChildOrder(kedaChildOrder);
        log.info("异步任务--添加疑难杂单订单--->" + count);
    }


    /**
     * 创建疑难杂单父级订单
     *
     * @param data
     * @throws IOException
     */
    @Selector(value = "batchInsertKedaorder", reactor = "@createReactor")
    public void batchInsertKedaorder(Event<List<KedaChildOrder>> data) throws IOException {
        List<KedaChildOrder> list = data.getData();
        for (int i = 0; i < list.size(); i++) {
            KedaChildOrder kedaChildOrder = list.get(i);
            KedaOrder order = kedaOrderMapper.getKedaOrder(kedaChildOrder.getOrderId());
            // 对接数据，创建父级订单
            AjaxResult add = kedaApi.add(kedaChildOrder, order);
            if ((int) add.get("code") == 200) {
                kedaChildOrder.setStatus(Const.SUCCESSFUL_REVIEW);
                kedaChildOrder.setRemark("添加成功！");
            } else {
                kedaChildOrder.setStatus(Const.FAILURE_REVIEW);
                kedaChildOrder.setRemark(add.get("msg").toString());
            }
            // 执行修改子级订单操作
            int count = kedaChildOrderMapper.updatKedaChildOrder(kedaChildOrder);
            log.info("异步任务--添加疑难杂单订单--" + kedaChildOrder.getLinkTel() + "-->" + count);
        }
    }
}
