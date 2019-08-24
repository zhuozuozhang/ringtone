package com.hrtxn.ringtone.project.threenets.threenet.service;

import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.OrderRequest;
import com.hrtxn.ringtone.common.utils.Const;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.freemark.config.systemConfig.RingtoneConfig;
import com.hrtxn.ringtone.project.system.File.service.FileService;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreeNetsOrderAttached;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsChildOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsRing;
import com.hrtxn.ringtone.project.threenets.threenet.json.mcard.McardAddGroupRespone;
import com.hrtxn.ringtone.project.threenets.threenet.json.migu.MiguAddGroupRespone;
import com.hrtxn.ringtone.project.threenets.threenet.json.migu.MiguAddRingRespone;
import com.hrtxn.ringtone.project.threenets.threenet.json.swxl.SwxlGroupResponse;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.utils.ApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author:lile
 * Date:2019-08-24 10:52
 * Description:
 */
@Service
public class ThreeNetsAsyncService {

    @Autowired
    private ThreeNetsChildOrderService threeNetsChildOrderService;
    @Autowired
    private ThreeNetsRingService threeNetsRingService;
    @Autowired
    private FileService fileService;
    @Autowired
    private ThreeNetsOrderAttachedService threeNetsOrderAttachedService;

    /**
     * 异步保存三网订单
     *
     * @param order
     * @param attached
     * @param orderRequest
     */
    @Async
    public void saveOnlineOrder(ThreenetsOrder order, ThreeNetsOrderAttached attached, OrderRequest orderRequest) {
        try {
            BaseRequest baseRequest = new BaseRequest();
            baseRequest.setParentOrderId(order.getId());
            List<ThreenetsChildOrder> list = threeNetsChildOrderService.getChildOrder(baseRequest);
            Map<Integer, List<ThreenetsChildOrder>> collect = list.stream().collect(Collectors.groupingBy(ThreenetsChildOrder::getOperator));
            // 移动
            if (collect.get(Const.OPERATORS_MOBILE) != null) {
                saveOrderByYd(order, attached, collect.get(Const.OPERATORS_MOBILE));
            }
            //联通
            if (collect.get(Const.OPERATORS_UNICOM) != null) {
                order.setMianduan("0");
                if (StringUtils.isNotEmpty(orderRequest.getMianduan()) && orderRequest.getMianduan().equals("是")){
                    order.setMianduan("1");
                }
                saveOrderByLt(order, attached, collect.get(Const.OPERATORS_UNICOM));
            }
            //电信
            if (collect.get(Const.OPERATORS_TELECOM) != null) {
                saveOrderByDx(order, attached, orderRequest, collect.get(Const.OPERATORS_TELECOM));
            }
            //保存订单附表
            threeNetsOrderAttachedService.update(attached);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存移动
     * @param order
     * @param attached
     * @param childOrders
     * @throws Exception
     */
    private void saveOrderByYd(ThreenetsOrder order, ThreeNetsOrderAttached attached, List<ThreenetsChildOrder> childOrders) throws Exception {
        ApiUtils utils = new ApiUtils();
        order.setLinkmanTel(childOrders.get(0).getLinkmanTel());
        MiguAddGroupRespone miguAddGroupRespone = utils.addOrderByYd(order, attached);
        if (miguAddGroupRespone != null && miguAddGroupRespone.isSuccess()) {
            attached.setMiguId(miguAddGroupRespone.getCircleId());
            //成员
            for (int i = 0; i < childOrders.size(); i++) {
                ThreenetsChildOrder childOrder = childOrders.get(i);
                childOrder.setOperateId(attached.getMiguId());
                childOrder.setStatus(attached.getMiguPrice() <= 5 ? "审核通过" : "未审核");
                childOrders.set(i, childOrder);
                threeNetsChildOrderService.update(childOrder);
            }
            if (attached.getMiguPrice() <= 5) {
                utils.addPhoneByYd(childOrders, attached.getMiguId());
                attached.setMiguStatus(Const.REVIEWED);
            } else {
                attached.setMiguStatus(Const.UNREVIEWED);
            }
            //铃音
            ThreenetsRing ring = threeNetsRingService.getRing(childOrders.get(0).getRingId());
            ring.setOperateId(miguAddGroupRespone.getCircleId());
            MiguAddRingRespone ringRespone = utils.saveMiguRing(ring, attached.getMiguId(), order.getCompanyName());
            if (ringRespone.isSuccess()) {
                ring.setOperateRingId(ringRespone.getRingId());
                threeNetsRingService.update(ring);
            } else {
                threeNetsRingService.delete(ring.getId());
                fileService.deleteFile(ring.getRingWay());
            }
        }
        threeNetsOrderAttachedService.update(attached);
    }

    /**
     * 保存联通
     * @param order
     * @param attached
     * @param childOrders
     * @throws Exception
     */
    private void saveOrderByLt(ThreenetsOrder order, ThreeNetsOrderAttached attached, List<ThreenetsChildOrder> childOrders) throws Exception {
        ApiUtils utils = new ApiUtils();
        ThreenetsRing ring = threeNetsRingService.getRing(childOrders.get(0).getRingId());
        order.setLinkmanTel(childOrders.get(0).getLinkmanTel());
        order.setRingName(ring.getRingName().substring(0, ring.getRingName().indexOf(".")));
        order.setUpLoadAgreement(new File(RingtoneConfig.getProfile() + ring.getRingWay()));
        SwxlGroupResponse swxlGroupResponse = utils.addOrderByLt(order, attached);
        if (swxlGroupResponse.getStatus() == 0) {
            attached.setSwxlId(swxlGroupResponse.getId());
            ring.setOperateId(swxlGroupResponse.getId());
            //保存铃音，联通在建立商户时进行了铃音上传，无需单独上传铃音
            //ring.setFile(new File(RingtoneConfig.getProfile() + ring.getRingWay()));
            //utils.addRingByLt(ring, attached.getSwxlId());
            for (int i = 0; i < childOrders.size(); i++) {
                ThreenetsChildOrder childOrder = childOrders.get(i);
                childOrder.setOperateId(attached.getSwxlId());
                childOrders.set(i, childOrder);
                threeNetsChildOrderService.update(childOrder);
            }
            utils.addPhoneByLt(childOrders, attached.getSwxlId());
            attached.setSwxlStatus(Const.REVIEWED);
            threeNetsRingService.update(ring);
        }
        threeNetsOrderAttachedService.update(attached);
    }

    /**
     * 保存电信
     * @param order
     * @param attached
     * @param orderRequest
     * @param childOrders
     */
    private void saveOrderByDx(ThreenetsOrder order, ThreeNetsOrderAttached attached, OrderRequest orderRequest, List<ThreenetsChildOrder> childOrders) {
        ApiUtils utils = new ApiUtils();
        //文件上传
        if (orderRequest.getCompanyUrl() != null && !orderRequest.getCompanyUrl().isEmpty()) {
            String path = utils.mcardUploadFile(new File(RingtoneConfig.getProfile() + orderRequest.getCompanyUrl()));
            attached.setBusinessLicense(path);
        }
        if (orderRequest.getClientUrl() != null && !orderRequest.getClientUrl().isEmpty()) {
            String path = utils.mcardUploadFile(new File(RingtoneConfig.getProfile() + orderRequest.getClientUrl()));
            attached.setConfirmLetter(path);
        }
        if (orderRequest.getMainUrl() != null && !orderRequest.getMainUrl().isEmpty()) {
            String path = utils.mcardUploadFile(new File(RingtoneConfig.getProfile() + orderRequest.getMainUrl()));
            attached.setSubjectProve(path);
        }
        order.setLinkmanTel(childOrders.get(0).getLinkmanTel());
        McardAddGroupRespone groupRespone = utils.addOrderByDx(order, attached);
        if (groupRespone.getCode().equals(Const.ILLEFAL_AREA)) {
            return;
        }
        attached.setMcardId(groupRespone.getAuserId());
        //本地保存铃音和子表
        for (int i = 0; i < childOrders.size(); i++) {
            ThreenetsChildOrder childOrder = childOrders.get(i);
            childOrder.setOperateId(attached.getMcardId());
            childOrder.setStatus("待审核");
            childOrders.set(i, childOrder);
            threeNetsChildOrderService.update(childOrder);
        }
        attached.setMcardStatus(Const.UNREVIEWED);
        threeNetsOrderAttachedService.update(attached);
    }
}
