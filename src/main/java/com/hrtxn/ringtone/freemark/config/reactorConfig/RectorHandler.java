package com.hrtxn.ringtone.freemark.config.reactorConfig;

import com.hrtxn.ringtone.common.api.KedaApi;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.utils.Const;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.freemark.config.systemConfig.RingtoneConfig;
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

import java.io.File;
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

    /**
     * 创建疑难杂单父级订单
     *
     * @param data
     * @throws IOException
     */
    @Selector(value = "addOrderInfo", reactor = "@createReactor")
    public void addOrderInfo(Event<KedaOrder> data) {
        KedaOrder kedaOrder = data.getData();
        if (StringUtils.isNotEmpty(kedaOrder.getCreditFile())){
            kedaOrder.setCreditFile(kedaApi.uploadFile(new File(RingtoneConfig.getProfile() + kedaOrder.getCreditFile())));
        }
        if (StringUtils.isNotEmpty(kedaOrder.getProtocolTelecom10())){
            kedaOrder.setProtocolTelecom10(kedaApi.uploadFile(new File(RingtoneConfig.getProfile() + kedaOrder.getProtocolTelecom10())));
        }
        if (StringUtils.isNotEmpty(kedaOrder.getProtocolTelecom20())){
            kedaOrder.setProtocolTelecom20(kedaApi.uploadFile(new File(RingtoneConfig.getProfile() + kedaOrder.getProtocolTelecom20())));
        }
        String s = kedaApi.addGroup(kedaOrder);
        if (s.equals("success")){
            String id = kedaApi.getOrderIdByName(kedaOrder.getCompanyName());
            kedaOrder.setKedaId(id);
            kedaOrderMapper.updateKedaOrder(kedaOrder);
        }
        log.info("异步任务--添加疑难杂单订单--"+s);
    }

    /**
     * 更新疑难杂单id和状态
     *
     * @param data
     */
    @Selector(value = "uploadOrderInfo", reactor = "@createReactor")
    public void uploadOrderInfo(Event<KedaOrder> data) {
        try{
            KedaOrder kedaOrder = data.getData();
            BaseRequest request = new BaseRequest();
            request.setOrderId(kedaOrder.getId());
            request.setIsMonthly(9);
            List<KedaChildOrder> list = kedaChildOrderMapper.selectByParam(request);
            String status = kedaApi.getOrderStatusByName(kedaOrder.getCompanyName());
            String id = kedaApi.getOrderIdByName(kedaOrder.getCompanyName());
            kedaOrder.setKedaId(id);
            if (status.equals("1")){
                kedaOrder.setStatus("审核通过");
                for (int i = 0; i < list.size(); i++) {
                    KedaChildOrder kedaChildOrder = list.get(i);
                    AjaxResult add = kedaApi.addPhone(kedaChildOrder, kedaOrder);
                    if ((int) add.get("code") == 200) {
                        kedaChildOrder.setIsMonthly(0);
                        kedaChildOrder.setStatus("审核通过");
                        kedaChildOrder.setStatus(Const.SUCCESSFUL_REVIEW);
                        kedaChildOrder.setRemark("添加成功！");
                    } else {
                        kedaChildOrder.setIsMonthly(2);
                        kedaChildOrder.setStatus("审核失败");
                        kedaChildOrder.setStatus(Const.FAILURE_REVIEW);
                        kedaChildOrder.setRemark(add.get("msg").toString());
                    }
                    // 执行修改子级订单操作
                    kedaChildOrderMapper.updatKedaChildOrder(kedaChildOrder);
                }
            }else if (status.equals("2")){
                kedaOrder.setStatus("待审核");
                for (int i = 0; i < list.size(); i++) {
                    KedaChildOrder kedaChildOrder = list.get(i);
                    kedaChildOrder.setRemark("商户审核中，请稍后查询！");
                }
            }else{
                kedaOrder.setStatus("审核驳回");
                for (int i = 0; i < list.size(); i++) {
                    KedaChildOrder kedaChildOrder = list.get(i);
                    kedaChildOrder.setRemark("审核驳回");
                }
            }
            kedaOrderMapper.updateKedaOrder(kedaOrder);
            log.info("异步任务--添加疑难杂单订单--"+status);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新疑难杂单id和状态
     *
     * @param data
     */
    @Selector(value = "updateOrderInfo", reactor = "@createReactor")
    public void updateOrderInfo(Event<KedaOrder> data) {
        KedaOrder kedaOrder = data.getData();
        KedaOrder order = kedaOrderMapper.getKedaOrder(kedaOrder.getId());
        if (StringUtils.isNotEmpty(kedaOrder.getProtocolTelecom10())){
            order.setProtocolTelecom10(kedaApi.uploadFile(new File(RingtoneConfig.getProfile() + kedaOrder.getProtocolTelecom10())));
        }
        if (StringUtils.isNotEmpty(kedaOrder.getProtocolTelecom20())){
            order.setProtocolTelecom20(kedaApi.uploadFile(new File(RingtoneConfig.getProfile() + kedaOrder.getProtocolTelecom20())));
        }
        if (order.getKedaId().equals("") || order.getKedaId().equals("")){
            kedaOrderMapper.updateKedaOrder(order);
        }else{
            String s = kedaApi.editGroup(order);
            kedaOrderMapper.updateKedaOrder(order);
            log.info("异步任务--添加疑难杂单订单--"+s);
        }
    }
}
