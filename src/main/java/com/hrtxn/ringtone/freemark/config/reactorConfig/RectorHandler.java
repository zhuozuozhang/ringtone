package com.hrtxn.ringtone.freemark.config.reactorConfig;

import com.hrtxn.ringtone.common.api.KedaApi;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaChildOrder;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.mapper.KedaChildOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.Reactor;
import reactor.event.Event;
import reactor.spring.annotation.Selector;

import java.io.IOException;

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

    private KedaApi kedaApi = new KedaApi();

    @Selector(value = "insertKedaorder", reactor = "@createReactor")
    public void insertKedaorder(Event<KedaChildOrder> data) throws IOException {
        KedaChildOrder kedaChildOrder = data.getData();
        AjaxResult add = kedaApi.add(kedaChildOrder);
        if ((int)add.get("code") == 200){
            kedaChildOrder.setRemark("添加成功！");
        } else {
            kedaChildOrder.setRemark(add.get("msg").toString());
        }
        // 执行修改子级订单操作
        int count = kedaChildOrderMapper.updatKedaChildOrder(kedaChildOrder);
        log.info("异步任务--添加疑难杂单订单--->"+count);
    }

}
