package com.hrtxn.ringtone.project.threenets.threenet.service;

import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.OrderRequest;
import com.hrtxn.ringtone.common.exception.NoLoginException;
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
import com.hrtxn.ringtone.project.threenets.threenet.json.migu.MiguAddPhoneRespone;
import com.hrtxn.ringtone.project.threenets.threenet.json.migu.MiguAddRingRespone;
import com.hrtxn.ringtone.project.threenets.threenet.json.swxl.SwxlGroupResponse;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsChildOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsRingMapper;
import com.hrtxn.ringtone.project.threenets.threenet.utils.ApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    private ThreenetsOrderMapper threenetsOrderMapper;
    @Autowired
    private ThreenetsChildOrderMapper threenetsChildOrderMapper;
    @Autowired
    private ThreenetsRingMapper threenetsRingMapper;
    @Autowired
    private FileService fileService;
    @Autowired
    private ThreeNetsOrderAttachedService threeNetsOrderAttachedService;

    private ApiUtils apiUtils = new ApiUtils();
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
            ThreenetsChildOrder childOrder = new ThreenetsChildOrder();
            childOrder.setParentOrderId(order.getId());
            List<ThreenetsChildOrder> list = threenetsChildOrderMapper.listByParamNoPage(childOrder);
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
                threenetsChildOrderMapper.updateThreeNetsChidOrder(childOrder);
            }
            if (attached.getMiguPrice() <= 5) {
                utils.addPhoneByYd(childOrders, attached.getMiguId());
                attached.setMiguStatus(Const.REVIEWED);
            } else {
                attached.setMiguStatus(Const.UNREVIEWED);
            }
            //铃音
            ThreenetsRing ring = threenetsRingMapper.selectByPrimaryKey(childOrders.get(0).getRingId());
            ring.setOperateId(miguAddGroupRespone.getCircleId());
            MiguAddRingRespone ringRespone = utils.saveMiguRing(ring, attached.getMiguId(), order.getCompanyName());
            if (ringRespone.isSuccess()) {
                ring.setOperateRingId(ringRespone.getRingId());
                threenetsRingMapper.updateByPrimaryKeySelective(ring);
            } else {
                threenetsRingMapper.deleteByPrimaryKey(ring.getId());
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
        ThreenetsRing ring = threenetsRingMapper.selectByPrimaryKey(childOrders.get(0).getRingId());
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
                threenetsChildOrderMapper.updateThreeNetsChidOrder(childOrder);
            }
            utils.addPhoneByLt(childOrders, attached.getSwxlId());
            attached.setSwxlStatus(Const.REVIEWED);
            threenetsRingMapper.updateByPrimaryKeySelective(ring);
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
            threenetsChildOrderMapper.updateThreeNetsChidOrder(childOrder);
        }
        attached.setMcardStatus(Const.UNREVIEWED);
        threeNetsOrderAttachedService.update(attached);
    }

    /**
     * 保存子订单
     *
     * @param attached
     * @param request
     */
    @Async
    public void saveThreenetsPhone(ThreeNetsOrderAttached attached, BaseRequest request) {
        try {
            List<ThreenetsRing> rings = threenetsRingMapper.selectByOrderId(attached.getParentOrderId());
            Map<Integer, List<ThreenetsRing>> ringMap = rings.stream().collect(Collectors.groupingBy(ThreenetsRing::getOperate));
            ThreenetsOrder order = threenetsOrderMapper.selectByPrimaryKey(attached.getParentOrderId());

            ThreenetsChildOrder childOrder = new ThreenetsChildOrder();
            childOrder.setParentOrderId(order.getId());
            List<ThreenetsChildOrder> childOrders = threenetsChildOrderMapper.listByParamNoPage(childOrder);

            Map<Integer, List<ThreenetsChildOrder>> map = childOrders.stream().collect(Collectors.groupingBy(ThreenetsChildOrder::getOperator));
            for (Integer operator : map.keySet()) {
                List<ThreenetsChildOrder> list = map.get(operator);
                if (operator == 1) {
                    ThreenetsRing threenetsRing = rings.get(0);
                    if (ringMap.get(1) == null) {
                        String path = fileService.cloneFile(threenetsRing);
                        threenetsRing.setRingWay(path);
                        threenetsRing.setOperate(1);
                        threenetsRing.setRingStatus(2);
                        threenetsRing.setRingName(path.substring(path.lastIndexOf("\\")));
                        threenetsRingMapper.insertThreeNetsRing(threenetsRing);
                    }
                    order.setUpLoadAgreement(new File(RingtoneConfig.getProfile() + threenetsRing.getRingWay()));
                    list = addMembersByYd(order, attached, list);
                    batchChindOrder(list,threenetsRing);
                }
                if (operator == 2) {
                    ThreenetsRing threenetsRing = rings.get(0);
                    if (ringMap.get(2) == null) {
                        String path = fileService.cloneFile(threenetsRing);
                        threenetsRing.setRingWay(path);
                        threenetsRing.setOperate(2);
                        threenetsRing.setRingStatus(2);
                        threenetsRingMapper.insertThreeNetsRing(threenetsRing);
                    }
                    List<ThreenetsChildOrder> orders = addMcardByDx(order, attached, list, request);
                    batchChindOrder(orders, threenetsRing);
                }
                if (operator == 3) {
                    order.setMianduan("0");
                    if (StringUtils.isNotEmpty(request.getMianduan()) && request.getMianduan().equals("是")){
                        order.setMianduan("1");
                    }
                    ThreenetsRing threenetsRing = rings.get(0);
                    if (ringMap.get(3) == null) {
                        String path = fileService.cloneFile(threenetsRing);
                        threenetsRing.setRingWay(path);
                        threenetsRing.setOperate(3);
                        threenetsRing.setRingStatus(2);
                        threenetsRingMapper.insertThreeNetsRing(threenetsRing);
                    }
                    list = addMemberByLt(order, attached, list);
                    batchChindOrder(list,threenetsRing);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量保存
     *
     * @param orders
     * @return
     */
    public Integer batchChindOrder(List<ThreenetsChildOrder> orders, ThreenetsRing ring) {
        Integer sum = 0 ;
        for (int i = 0; i < orders.size(); i++) {
            ThreenetsChildOrder childOrder = orders.get(i);
            childOrder.setRingName(ring.getRingName());
            childOrder.setRingId(ring.getId());
            childOrder.setIsVideoUser(ring.getRingType().equals("视频") ? true : false);
            childOrder.setIsRingtoneUser(ring.getRingType().equals("视频") ? false : true);
            childOrder.setParentOrderId(ring.getOrderId());
            orders.set(i, childOrder);
            sum = sum + threenetsChildOrderMapper.updateThreeNetsChidOrder(childOrder);
        }
        return sum;
    }

    /**
     * 新增移动成员
     *
     * @param attached
     * @param list
     * @throws IOException
     * @throws NoLoginException
     */
    private List<ThreenetsChildOrder> addMembersByYd(ThreenetsOrder order, ThreeNetsOrderAttached attached, List<ThreenetsChildOrder> list) throws IOException, NoLoginException {
        //无集团id则先进行集团新增
        if (attached.getMiguId() == null) {
            MiguAddGroupRespone miguAddGroupRespone = apiUtils.addOrderByYd(order, attached);
            if (miguAddGroupRespone.isSuccess()) {
                attached.setMiguId(miguAddGroupRespone.getCircleId());
                threeNetsOrderAttachedService.update(attached);
            } else {
                return new ArrayList<>();
            }
        }
        MiguAddPhoneRespone addPhoneRespone = new MiguAddPhoneRespone();
        if (attached.getMiguStatus() == 1) {
            addPhoneRespone = apiUtils.addPhoneByYd(list, attached.getMiguId());
        }
        if (addPhoneRespone.getSuccessCount().equals("0")){
            return new ArrayList<>();
        }
        for (int i = 0; i < list.size(); i++) {
            ThreenetsChildOrder childOrder = list.get(i);
            childOrder.setOperateId(attached.getMiguId());
            childOrder.setOperateOrderId(addPhoneRespone.getLeftMemberAddNum());
            list.set(i, childOrder);
        }
        return list;
    }

    /**
     * 新增联通成员
     *
     * @param attached
     * @param list
     * @throws IOException
     * @throws NoLoginException
     */
    private List<ThreenetsChildOrder> addMemberByLt(ThreenetsOrder order, ThreeNetsOrderAttached attached, List<ThreenetsChildOrder> list) throws IOException, NoLoginException {
        if (attached.getSwxlId() == null) {
            List<ThreenetsRing> rings = new ArrayList<>();
            try {
                rings = threenetsRingMapper.selectByOrderId(attached.getParentOrderId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!rings.isEmpty()) {
                ThreenetsRing threenetsRing = rings.get(0);
                order.setUpLoadAgreement(new File(RingtoneConfig.getProfile() + threenetsRing.getRingWay()));
            }
            order.setLinkmanTel(list.get(0).getLinkmanTel());
            for (int i = 0; i < rings.size(); i++) {
                if (rings.get(i).getOperate() == 3) {
                    String ringName = rings.get(i).getRingName();
                    order.setRingName(ringName.substring(0, ringName.indexOf(".")));
                }
            }
            SwxlGroupResponse swxlGroupResponse = apiUtils.addOrderByLt(order, attached);
            if (swxlGroupResponse.getStatus() == 0) {
                attached.setSwxlId(swxlGroupResponse.getId());
                threeNetsOrderAttachedService.update(attached);
            } else {
                return new ArrayList<>();
            }
        }
        apiUtils.addPhoneByLt(list, attached.getSwxlId());
        for (int i = 0; i < list.size(); i++) {
            ThreenetsChildOrder childOrder = list.get(i);
            childOrder.setOperateId(attached.getMiguId());
            list.set(i, childOrder);
        }
        return list;
    }

    /**
     * 电信添加用户
     *
     * @param order
     * @param attached
     * @param list
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public List<ThreenetsChildOrder> addMcardByDx(ThreenetsOrder order, ThreeNetsOrderAttached attached, List<ThreenetsChildOrder> list, BaseRequest request) throws IOException, NoLoginException {
        if (attached.getMcardId() == null) {
            //新增电信商户需要先上传审核文件
            if (request.getCompanyUrl() != null) {
                String path = apiUtils.mcardUploadFile(new File(request.getCompanyUrl()));
                attached.setBusinessLicense(path);
            }
            if (request.getClientUrl() != null) {
                String path = apiUtils.mcardUploadFile(new File(request.getClientUrl()));
                attached.setConfirmLetter(path);
            }
            if (request.getMainUrl() != null) {
                String path = apiUtils.mcardUploadFile(new File(request.getMainUrl()));
                attached.setSubjectProve(path);
            }
            //没有电信商户，先新增商户
            McardAddGroupRespone respone = apiUtils.addOrderByDx(order, attached);
            if (respone.getCode().equals("0000")) {
                attached.setMcardId(respone.getAuserId());
                threeNetsOrderAttachedService.update(attached);
            } else {
                return new ArrayList<>();
            }
        }
        //特殊处理，需要先进入对应商户，然后进行成员添加
        if (attached.getMcardStatus() == 1) {
            list = apiUtils.addPhoneByDx(list, attached.getMcardId(),attached.getMcardDistributorId());
        }
        for (int i = 0; i < list.size(); i++) {
            ThreenetsChildOrder childOrder = list.get(i);
            childOrder.setOperateId(attached.getMiguId());
            childOrder.setStatus("待审核");
            list.set(i, childOrder);
        }
        return list;
    }
}
