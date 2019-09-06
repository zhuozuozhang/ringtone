package com.hrtxn.ringtone.project.threenets.threenet.service;

import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.OrderRequest;
import com.hrtxn.ringtone.common.exception.NoLoginException;
import com.hrtxn.ringtone.common.utils.ConfigUtil;
import com.hrtxn.ringtone.common.utils.Const;
import com.hrtxn.ringtone.common.utils.DateUtils;
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
import com.hrtxn.ringtone.project.threenets.threenet.json.swxl.SwxlBaseBackMessage;
import com.hrtxn.ringtone.project.threenets.threenet.json.swxl.SwxlGroupResponse;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreeNetsOrderAttachedMapper;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsChildOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsRingMapper;
import com.hrtxn.ringtone.project.threenets.threenet.utils.ApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author:lile
 * Date:2019-08-24 10:52
 * Description:
 */
@Slf4j
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
    private ThreeNetsOrderAttachedMapper threeNetsOrderAttachedMapper;

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
                createMobileMerchant(order, attached, collect.get(Const.OPERATORS_MOBILE));
            }
            //联通
            if (collect.get(Const.OPERATORS_UNICOM) != null) {
                order.setMianduan("0");
                if (StringUtils.isNotEmpty(orderRequest.getMianduan()) && orderRequest.getMianduan().equals("是")) {
                    order.setMianduan("1");
                }
                createUnicomMerchant(order, attached, collect.get(Const.OPERATORS_UNICOM));
            }
            //电信
            if (collect.get(Const.OPERATORS_TELECOM) != null) {
                createTelecomMerchant(order,attached,orderRequest,collect.get(Const.OPERATORS_TELECOM));
            }
            //保存订单附表
            threeNetsOrderAttachedMapper.updateByPrimaryKeySelective(attached);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存移动
     *
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
            attached.setMiguStatus(Const.REVIEWED);
            //成员
            for (int i = 0; i < childOrders.size(); i++) {
                ThreenetsChildOrder childOrder = childOrders.get(i);
                childOrder.setOperateId(attached.getMiguId());
                childOrder.setStatus(attached.getMiguPrice() <= 5 ? Const.SUCCESSFUL_REVIEW : Const.PENDING_REVIEW);
                childOrders.set(i, childOrder);
                //高资费成员没有直接上传
                //if (attached.getMiguPrice() <= 5) {
                if (!order.getLinkmanTel().equals(childOrder.getLinkmanTel())) {
                    MiguAddPhoneRespone respone = utils.addPhoneByYd(childOrder, attached.getMiguId());
                    childOrder.setRemark(respone.getMessage());
                }
                //}
                threenetsChildOrderMapper.updateThreeNetsChidOrder(childOrder);
            }
            if (attached.getMiguPrice() <= 5) {
                attached.setMiguStatus(Const.REVIEWED);
            } else {
                attached.setMiguStatus(Const.UNREVIEWED);
            }
            //铃音
            ThreenetsRing ring = threenetsRingMapper.selectByPrimaryKey(childOrders.get(0).getRingId());
            ring.setOperateId(miguAddGroupRespone.getCircleId());
            ring.setFile(new File(RingtoneConfig.getProfile() + ring.getRingWay()));
            MiguAddRingRespone ringRespone = utils.saveMiguRing(ring);
            if (ringRespone.isSuccess()) {
                ring.setOperateRingId(ringRespone.getRingId());
                fileService.updateStatus(ring.getRingWay());
            }
            ring.setRemark(ringRespone.getMsg());
            threenetsRingMapper.updateByPrimaryKeySelective(ring);
        } else { //建立商户失败
            creationFailed(childOrders, childOrders.get(0).getRingId(), "移动商户—" + miguAddGroupRespone.getMsg());
        }
        threeNetsOrderAttachedMapper.updateByPrimaryKeySelective(attached);
    }

    /**
     * 保存联通
     *
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
                if (childOrder.getLinkmanTel().equals(order.getLinkmanTel())) {
                    childOrder.setRemark("成功");
                    childOrder.setStatus(Const.SUCCESSFUL_REVIEW);
                } else {
                    SwxlBaseBackMessage result = utils.addPhoneByLt(childOrder, attached.getSwxlId());
                    childOrder.setStatus(result.getRecode().equals("000000") ? Const.SUCCESSFUL_REVIEW : Const.FAILURE_REVIEW);
                    childOrder.setRemark(result.getMessage());
                }
                threenetsChildOrderMapper.updateThreeNetsChidOrder(childOrder);
            }
            attached.setSwxlStatus(Const.REVIEWED);
            threenetsRingMapper.updateByPrimaryKeySelective(ring);
        } else {
            creationFailed(childOrders, childOrders.get(0).getRingId(), "联通商户—" + swxlGroupResponse.getRemark());
        }
        threeNetsOrderAttachedMapper.updateByPrimaryKeySelective(attached);
    }

    /**
     * 保存电信
     *
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
        for (int i = 0; i < childOrders.size(); i++) {
            ThreenetsChildOrder childOrder = childOrders.get(i);
            boolean flag = ConfigUtil.getAreaArray("unable_to_open_area", childOrder.getProvince());
            if (flag) {
                childOrder.setRemark("电信当前不提供" + childOrder.getProvince() + "地区的服务");
                childOrder.setStatus(Const.FAILURE_REVIEW);
            } else {
                childOrder.setStatus(Const.PENDING_REVIEW);
                order.setLinkmanTel(childOrders.get(i).getLinkmanTel());
                order.setProvince(childOrders.get(i).getProvince());
                order.setCity(childOrders.get(i).getCity());
                break;
            }
        }
        if (StringUtils.isEmpty(order.getLinkmanTel())) {
            order.setLinkmanTel(childOrders.get(0).getLinkmanTel());
            order.setProvince(childOrders.get(0).getProvince());
            order.setCity(childOrders.get(0).getCity());
        }
        try {
            McardAddGroupRespone groupRespone = utils.addOrderByDx(order, attached);
            ThreenetsRing ring = threenetsRingMapper.selectByPrimaryKey(childOrders.get(0).getRingId());
            if (groupRespone.getCode().equals(Const.ILLEFAL_AREA)) {
                for (int i = 0; i < childOrders.size(); i++) {
                    ThreenetsChildOrder childOrder = childOrders.get(i);
                    childOrder.setRemark("审核失败：电信商户—" + groupRespone.getMessage());
                    childOrder.setStatus("审核失败");
                    childOrders.set(i, childOrder);
                }
                ring.setRemark("审核失败：电信商户—" + groupRespone.getMessage());
            }
            attached.setMcardId(groupRespone.getAuserId());
            ring.setOperateId(groupRespone.getAuserId());
            threenetsRingMapper.updateByPrimaryKeySelective(ring);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //本地保存铃音和子表
        for (int i = 0; i < childOrders.size(); i++) {
            ThreenetsChildOrder childOrder = childOrders.get(i);
            childOrder.setOperateId(attached.getMcardId());
            childOrder.setStatus(Const.PENDING_REVIEW);
            childOrder.setRemark(Const.UNDER_REVIEW);
            childOrders.set(i, childOrder);
            threenetsChildOrderMapper.updateThreeNetsChidOrder(childOrder);
        }
        attached.setMcardStatus(Const.UNREVIEWED);
        threeNetsOrderAttachedMapper.updateByPrimaryKeySelective(attached);
    }

    /**
     * 创建商户失败时
     *
     * @param childOrders
     * @param ringId
     * @param msg
     */
    public void creationFailed(List<ThreenetsChildOrder> childOrders, Integer ringId, String msg) {
        try {
            String prompt = "审核失败：" + msg;
            for (int i = 0; i < childOrders.size(); i++) {
                ThreenetsChildOrder childOrder = childOrders.get(i);
                childOrder.setRemark(prompt);
                childOrder.setStatus(Const.FAILURE_REVIEW);
                threenetsChildOrderMapper.updateThreeNetsChidOrder(childOrder);
            }
            ThreenetsRing ring = threenetsRingMapper.selectByPrimaryKey(ringId);
            ring.setRemark(prompt);
            threenetsRingMapper.updateByPrimaryKeySelective(ring);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            childOrder.setStatus(Const.PENDING_REVIEW);
            List<ThreenetsChildOrder> childOrders = threenetsChildOrderMapper.listByParamNoPage(childOrder);

            Map<Integer, List<ThreenetsChildOrder>> map = childOrders.stream().collect(Collectors.groupingBy(ThreenetsChildOrder::getOperator));
            for (Integer operator : map.keySet()) {
                List<ThreenetsChildOrder> list = map.get(operator);
                if (operator == 1) {
                    ThreenetsRing threenetsRing = rings.get(0);
                    if (ringMap.get(1) == null) {
                        String path = fileService.cloneFile(threenetsRing);
                        threenetsRing.setOperateId(attached.getMiguId());
                        threenetsRing.setRingWay(path);
                        threenetsRing.setOperate(1);
                        threenetsRing.setRingStatus(2);
                        threenetsRing.setRingName(path.substring(path.lastIndexOf("\\") + 1));
                        threenetsRing.setRemark("");
                        threenetsRingMapper.insertThreeNetsRing(threenetsRing);
                    }
                    //order.setUpLoadAgreement(new File(RingtoneConfig.getProfile() + threenetsRing.getRingWay()));
                    list = addMembersByYd(order, attached, list, threenetsRing);
                    batchChindOrder(list, threenetsRing);
                }
                if (operator == 2) {
                    ThreenetsRing threenetsRing = rings.get(0);
                    if (ringMap.get(2) == null) {
                        String path = fileService.cloneFile(threenetsRing);
                        threenetsRing.setOperateId(attached.getMcardId());
                        threenetsRing.setRingWay(path);
                        threenetsRing.setOperate(2);
                        threenetsRing.setRingStatus(2);
                        threenetsRing.setRemark("");
                        threenetsRing.setRingName(path.substring(path.lastIndexOf("\\") + 1));
                        threenetsRingMapper.insertThreeNetsRing(threenetsRing);
                    }
                    List<ThreenetsChildOrder> orders = addMcardByDx(order, attached, list, request, threenetsRing);
                    batchChindOrder(orders, threenetsRing);
                }
                if (operator == 3) {
                    order.setMianduan("0");
                    if (StringUtils.isNotEmpty(request.getMianduan()) && request.getMianduan().equals("是")) {
                        order.setMianduan("1");
                    }
                    ThreenetsRing threenetsRing = rings.get(0);
                    if (ringMap.get(3) == null) {
                        String path = fileService.cloneFile(threenetsRing);
                        threenetsRing.setOperateId(attached.getSwxlId());
                        threenetsRing.setRingWay(path);
                        threenetsRing.setOperate(3);
                        threenetsRing.setRingStatus(2);
                        threenetsRing.setRemark("");
                        threenetsRing.setRingName(path.substring(path.lastIndexOf("\\") + 1));
                        threenetsRingMapper.insertThreeNetsRing(threenetsRing);
                    }
                    list = addMemberByLt(order, attached, list,threenetsRing);
                    batchChindOrder(list, threenetsRing);
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
        Integer sum = 0;
        for (int i = 0; i < orders.size(); i++) {
            ThreenetsChildOrder childOrder = orders.get(i);
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
    private List<ThreenetsChildOrder> addMembersByYd(ThreenetsOrder order, ThreeNetsOrderAttached attached, List<ThreenetsChildOrder> list, ThreenetsRing ring) throws IOException, NoLoginException {
        ApiUtils apiUtils = new ApiUtils();
        //无集团id则先进行集团新增
        if (StringUtils.isEmpty(attached.getMiguId())) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setRingId(ring.getId());
            }
            createMobileMerchant(order,attached,list);
            return list;
        }
        MiguAddPhoneRespone addPhoneRespone = new MiguAddPhoneRespone();
        for (int i = 0; i < list.size(); i++) {
            ThreenetsChildOrder childOrder = list.get(i);
            childOrder.setOperateId(attached.getMiguId());
            childOrder.setOperateOrderId(addPhoneRespone.getLeftMemberAddNum());
            addPhoneRespone = apiUtils.addPhoneByYd(childOrder, attached.getMiguId());
            if (addPhoneRespone.getSuccessCount().equals("0")) {
                childOrder.setRemark(addPhoneRespone.getMessage());
                childOrder.setStatus(Const.FAILURE_REVIEW);
            } else {
                childOrder.setRemark("添加成功");
                childOrder.setStatus(Const.SUCCESSFUL_REVIEW);
            }
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
    private List<ThreenetsChildOrder> addMemberByLt(ThreenetsOrder order, ThreeNetsOrderAttached attached, List<ThreenetsChildOrder> list,ThreenetsRing ring) throws IOException, NoLoginException {
        ApiUtils apiUtils = new ApiUtils();
        if (StringUtils.isEmpty(attached.getSwxlId())) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setRingId(ring.getId());
            }
            createUnicomMerchant(order,attached,list);
            return list;
        }
        for (int i = 0; i < list.size(); i++) {
            ThreenetsChildOrder childOrder = list.get(i);
            childOrder.setOperateId(attached.getSwxlId());
            SwxlBaseBackMessage result = apiUtils.addPhoneByLt(childOrder, attached.getSwxlId());
            childOrder.setRemark(result.getMessage());
            childOrder.setStatus(Const.SUCCESSFUL_REVIEW);
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
    public List<ThreenetsChildOrder> addMcardByDx(ThreenetsOrder order, ThreeNetsOrderAttached attached, List<ThreenetsChildOrder> list, BaseRequest request, ThreenetsRing ring) {
        ApiUtils apiUtils = new ApiUtils();
        String remark = "请等待电信商户审核完成！";
        String status = "待审核";
        if (StringUtils.isEmpty(attached.getMcardId())) {
            OrderRequest request1 = new OrderRequest();
            //新增电信商户需要先上传审核文件
            if (StringUtils.isNotEmpty(request.getCompanyUrl())) {
                request1.setCompanyUrl(request.getCompanyUrl());
            }
            if (StringUtils.isNotEmpty(request.getClientUrl())) {
                request1.setClientUrl(request.getClientUrl());
            }
            if (StringUtils.isNotEmpty(request.getMainUrl())) {
                request1.setMainUrl(request.getMainUrl());
            }
            createTelecomMerchant(order,attached,request1,list);
        }
        //特殊处理，需要先进入对应商户，然后进行成员添加
        if (attached.getMcardStatus() == 1) {
            list = apiUtils.addPhoneByDx(list, attached.getMcardId(), attached.getMcardDistributorId());
        }
        for (int i = 0; i < list.size(); i++) {
            ThreenetsChildOrder childOrder = list.get(i);
            childOrder.setOperateId(attached.getMiguId());
            if (childOrder.getLinkmanTel().equals(order.getLinkmanTel())) {
                childOrder.setStatus(status);
                childOrder.setRemark(remark);
            } else {
                childOrder.setStatus(StringUtils.isEmpty(childOrder.getStatus()) ? "待审核" : childOrder.getStatus());
            }
            list.set(i, childOrder);
        }
        ringToneReUpload(order, list);
        return list;
    }

    /**
     * 创建移动商户
     *
     * @param order
     * @param attached
     * @param childOrders
     */
    private void createMobileMerchant(ThreenetsOrder order, ThreeNetsOrderAttached attached, List<ThreenetsChildOrder> childOrders) {
        try {
            ApiUtils utils = new ApiUtils();
            //当前手机号是否存在于移动商户，如果存在则顺延下一个手机号
            ThreenetsChildOrder firstChildOrder = childOrders.get(0);
            order.setLinkmanTel(firstChildOrder.getLinkmanTel());
            MiguAddGroupRespone groupRespone = utils.addOrderByYd(order, attached);
            if (groupRespone != null && groupRespone.getMsg().equals("验证码输入错误")) {
                createMobileMerchant(order, attached, childOrders);
            }
            if (groupRespone != null && groupRespone.isSuccess()) {
                attached.setMiguId(groupRespone.getCircleId());
                attached.setMiguStatus(Const.REVIEWED);
                firstChildOrder.setOperateId(attached.getMiguId());
                firstChildOrder.setStatus(Const.SUCCESSFUL_REVIEW);
                firstChildOrder.setRemark("添加成功");
                threenetsChildOrderMapper.updateThreeNetsChidOrder(firstChildOrder);
                //成员上传
                for (int j = 1; j < childOrders.size(); j++) {
                    ThreenetsChildOrder childOrder = childOrders.get(j);
                    childOrder.setOperateId(attached.getMiguId());
                    //非高资费
                    if (attached.getMiguPrice() <= 5) {
                        MiguAddPhoneRespone respone = utils.addPhoneByYd(childOrder, attached.getMiguId());
                        childOrder.setStatus(Const.SUCCESSFUL_REVIEW);
                        childOrder.setRemark(respone.getMessage());
                        attached.setMiguStatus(Const.REVIEWED);
                    } else {
                        childOrder.setStatus(Const.PENDING_REVIEW);
                        attached.setMiguStatus(Const.UNREVIEWED);
                    }
                    threenetsChildOrderMapper.updateThreeNetsChidOrder(childOrder);
                }
                ThreenetsRing ring = threenetsRingMapper.selectByPrimaryKey(childOrders.get(0).getRingId());
                ring.setOperateId(groupRespone.getCircleId());
                ring.setFile(new File(RingtoneConfig.getProfile() + ring.getRingWay()));
                MiguAddRingRespone ringRespone = utils.saveMiguRing(ring);
                if (ringRespone.isSuccess()) {
                    ring.setOperateRingId(ringRespone.getRingId());
                    fileService.updateStatus(ring.getRingWay());
                }
                ring.setRemark(ringRespone.getMsg());
                threenetsRingMapper.updateByPrimaryKeySelective(ring);
                threeNetsOrderAttachedMapper.updateByPrimaryKeySelective(attached);
                return;
            } else {
                firstChildOrder.setRemark("移动商户:" + groupRespone.getMsg());
                firstChildOrder.setStatus(Const.FAILURE_REVIEW);
                threenetsChildOrderMapper.updateThreeNetsChidOrder(firstChildOrder);
                threeNetsOrderAttachedMapper.updateByPrimaryKeySelective(attached);
                if (childOrders.size() == 1) {
                    return;
                }
                childOrders.remove(0);
                createMobileMerchant(order, attached, childOrders);
            }
        } catch (Exception e) {
            log.info("移动商户建立失败=>" + e);
        }
    }

    /**
     * 创建联通商户
     *
     * @param order
     * @param attached
     * @param childOrders
     */
    private void createUnicomMerchant(ThreenetsOrder order, ThreeNetsOrderAttached attached, List<ThreenetsChildOrder> childOrders) {
        try{
            ApiUtils utils = new ApiUtils();
            ThreenetsChildOrder firstChildOrder = childOrders.get(0);
            //获取关联铃音
            ThreenetsRing ring = threenetsRingMapper.selectByPrimaryKey(firstChildOrder.getRingId());
            order.setRingName(ring.getRingName().substring(0, ring.getRingName().indexOf(".")));
            order.setUpLoadAgreement(new File(RingtoneConfig.getProfile() + ring.getRingWay()));
            //商户建立联系人手机号
            order.setLinkmanTel(childOrders.get(0).getLinkmanTel());
            SwxlGroupResponse swxlGroupResponse = utils.addOrderByLt(order, attached);
            if (swxlGroupResponse.getStatus() == 0) {
                firstChildOrder.setOperateId(swxlGroupResponse.getId());
                firstChildOrder.setStatus(Const.SUCCESSFUL_REVIEW);
                firstChildOrder.setRemark("添加成功");
                threenetsChildOrderMapper.updateThreeNetsChidOrder(firstChildOrder);
                attached.setSwxlId(swxlGroupResponse.getId());
                attached.setSwxlStatus(Const.REVIEWED);
                ring.setOperateId(swxlGroupResponse.getId());
                for (int i = 1; i < childOrders.size(); i++) {
                    ThreenetsChildOrder childOrder = childOrders.get(i);
                    childOrder.setOperateId(attached.getSwxlId());
                    SwxlBaseBackMessage result = utils.addPhoneByLt(childOrder, attached.getSwxlId());
                    childOrder.setStatus(result.getRecode().equals("000000") ? Const.SUCCESSFUL_REVIEW : Const.FAILURE_REVIEW);
                    childOrder.setRemark(result.getMessage());
                    threenetsChildOrderMapper.updateThreeNetsChidOrder(childOrder);
                }
                threenetsRingMapper.updateByPrimaryKeySelective(ring);
                threeNetsOrderAttachedMapper.updateByPrimaryKeySelective(attached);
            } else {
                firstChildOrder.setRemark("联通商户:" + swxlGroupResponse.getRemark());
                firstChildOrder.setStatus(Const.FAILURE_REVIEW);
                threenetsChildOrderMapper.updateThreeNetsChidOrder(firstChildOrder);
                threeNetsOrderAttachedMapper.updateByPrimaryKeySelective(attached);
                if (childOrders.size() == 1) {
                    return;
                }
                childOrders.remove(0);
                createUnicomMerchant(order, attached, childOrders);
            }
        }catch(Exception e) {
            log.info("联通商户建立失败=>" + e);
        }
    }

    /**
     * 创建电信商户
     *
     * @param order
     * @param attached
     * @param orderRequest
     * @param childOrders
     */
    private void createTelecomMerchant(ThreenetsOrder order, ThreeNetsOrderAttached attached, OrderRequest orderRequest, List<ThreenetsChildOrder> childOrders) {
        try {
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
            for (int i = 0; i < childOrders.size(); i++) {
                ThreenetsChildOrder childOrder = childOrders.get(i);
                boolean flag = ConfigUtil.getAreaArray("unable_to_open_area", childOrder.getProvince());
                if (flag) {
                    childOrder.setRemark("电信当前不提供" + childOrder.getProvince() + "地区的服务");
                    childOrder.setStatus(Const.FAILURE_REVIEW);
                    threenetsChildOrderMapper.updateThreeNetsChidOrder(childOrder);
                    childOrders.remove(i);
                }
            }
            if (childOrders.size() == 0){
                return;
            }
            ThreenetsChildOrder firstChildOrder = childOrders.get(0);
            order.setLinkmanTel(firstChildOrder.getLinkmanTel());
            order.setProvince(firstChildOrder.getProvince());
            order.setCity(firstChildOrder.getCity());
            //同步电信
            McardAddGroupRespone groupRespone = utils.addOrderByDx(order, attached);
            if (groupRespone.getCode().equals(Const.ILLEFAL_AREA)) {
                firstChildOrder.setRemark("审核失败：电信商户—" + groupRespone.getMessage());
                firstChildOrder.setStatus("审核失败");
                threenetsChildOrderMapper.updateThreeNetsChidOrder(firstChildOrder);
                childOrders.remove(0);
                createTelecomMerchant(order,attached,orderRequest,childOrders);
            }else{
                attached.setMcardStatus(Const.UNREVIEWED);
                threeNetsOrderAttachedMapper.updateByPrimaryKeySelective(attached);
                ThreenetsRing ring = threenetsRingMapper.selectByPrimaryKey(firstChildOrder.getRingId());
                attached.setMcardId(groupRespone.getAuserId());
                ring.setOperateId(groupRespone.getAuserId());
                threenetsRingMapper.updateByPrimaryKeySelective(ring);
                for (int i = 0; i < childOrders.size(); i++) {
                    ThreenetsChildOrder childOrder = childOrders.get(i);
                    childOrder.setOperateId(attached.getMcardId());
                    childOrder.setStatus(Const.PENDING_REVIEW);
                    childOrder.setRemark(Const.UNDER_REVIEW);
                    childOrders.set(i, childOrder);
                    threenetsChildOrderMapper.updateThreeNetsChidOrder(childOrder);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证电信铃音是否需要重新上传
     *
     * @param order
     * @param newChildOrders
     */
    public void ringToneReUpload(ThreenetsOrder order, List<ThreenetsChildOrder> newChildOrders) {
        ApiUtils apiUtils = new ApiUtils();
        try {
            int days = DateUtils.differentDaysByMillisecond(order.getCreateTime(), new Date());
            if (days <= 2) {
                return;
            }
            ThreenetsChildOrder childOrder = new ThreenetsChildOrder();
            childOrder.setParentOrderId(order.getId());
            List<ThreenetsChildOrder> oldChildOrders = threenetsChildOrderMapper.listByParamNoPage(childOrder);
            List<String> area = new ArrayList<>();
            for (int i = 0; i < oldChildOrders.size(); i++) {
                ThreenetsChildOrder child = oldChildOrders.get(i);
                area.add(child.getProvince());
            }
            boolean isReUpload = false;
            for (int i = 0; i < newChildOrders.size(); i++) {
                ThreenetsChildOrder child = newChildOrders.get(i);
                boolean contains = area.contains(child.getProvince());
                if (!contains) {
                    isReUpload = true;
                    break;
                }
            }
            if (isReUpload) {
                List<ThreenetsRing> rings = threenetsRingMapper.selectByOrderId(order.getId());
                ThreenetsRing newRing = null;
                for (int i = 0; i < rings.size(); i++) {
                    ThreenetsRing ring = rings.get(i);
                    if (ring.getOperate().equals(Const.OPERATORS_TELECOM)) {
                        newRing = ring;
                        break;
                    }
                }
                String path = fileService.cloneFile(newRing);
                newRing.setId(null);
                newRing.setRingWay(path);
                newRing.setRemark("");
                newRing.setRingName(path.substring(path.lastIndexOf("\\") + 1));
                threenetsRingMapper.insertThreeNetsRing(newRing);
                ThreeNetsOrderAttached attached = threeNetsOrderAttachedMapper.selectByParentOrderId(order.getId());
                apiUtils.addRingByDx(newRing, attached);
            }
        } catch (Exception e) {
            log.info("电信添加新地区铃音重新上传失败" + e);
        }
    }


    /**
     * 刷新电信商户信息，是否审核成功
     *
     * @param order
     */
    @Async
    public void refreshTelecomMerchantInfo(ThreenetsOrder order) {
        ApiUtils apiUtils = new ApiUtils();
        ThreeNetsOrderAttached attached = threeNetsOrderAttachedMapper.selectByParentOrderId(order.getId());
        if (attached == null) {
            return;
        }
        if (StringUtils.isEmpty(attached.getMcardId()) || attached.getMcardStatus().equals(Const.REVIEWED)) {
            return;
        }
        try {
            McardAddGroupRespone respone = apiUtils.normalBusinessInfo(order);
            if (respone.getCode().equals("0000")) {
                attached.setMcardStatus(Const.REVIEWED);
                ThreenetsChildOrder param = new ThreenetsChildOrder();
                param.setParentOrderId(order.getId());
                param.setOperator(Const.OPERATORS_TELECOM);
                param.setStatus("未审核");
                //保存成员
                List<ThreenetsChildOrder> childOrders = threenetsChildOrderMapper.listByParamNoPage(param);
                childOrders = apiUtils.addPhoneByDx(childOrders, attached.getMcardId(), attached.getMcardDistributorId());
                threeNetsOrderAttachedMapper.updateByPrimaryKeySelective(attached);
                //保存铃音
                List<ThreenetsRing> rings = threenetsRingMapper.selectByOrderId(order.getId());
                for (int i = 0; i < rings.size(); i++) {
                    if (rings.get(i).getOperate() != 2) {
                        continue;
                    }
                    ThreenetsRing ring = rings.get(i);
                    ring.setFile(new File(RingtoneConfig.getProfile() + ring.getRingWay()));
                    apiUtils.addRingByDx(rings.get(i), attached);
                }
                for (int i = 0; i < childOrders.size(); i++) {
                    threenetsChildOrderMapper.updateThreeNetsChidOrder(childOrders.get(i));
                }
            } else {
                ThreenetsChildOrder param = new ThreenetsChildOrder();
                param.setParentOrderId(order.getId());
                param.setOperator(Const.OPERATORS_TELECOM);
                List<ThreenetsChildOrder> childOrders = threenetsChildOrderMapper.listByParamNoPage(param);
                for (int i = 0; i < childOrders.size(); i++) {
                    ThreenetsChildOrder childOrder = childOrders.get(i);
                    childOrder.setRemark(respone.getMessage());
                    threenetsChildOrderMapper.updateThreeNetsChidOrder(childOrder);
                }
            }
        } catch (Exception e) {
            log.info("电信刷新信息上传用户失败" + e);
        }
    }


    /**
     * 移动上传铃音
     *
     * @param ring
     * @return
     */
    @Async
    public MiguAddRingRespone ringToneUploadByMobile(ThreenetsRing ring) {
        MiguAddRingRespone ringRespone = null;
        try {
            ApiUtils apiUtils = new ApiUtils();
            ring.setFile(new File(RingtoneConfig.getProfile() + ring.getRingWay()));
            ThreeNetsOrderAttached attached = threeNetsOrderAttachedMapper.selectByParentOrderId(ring.getOrderId());
            ring.setOperateId(attached.getMiguId());
            ringRespone = apiUtils.saveMiguRing(ring);
            if (ringRespone != null && ringRespone.isSuccess()) {
                //保存铃音
                ring.setOperateRingId(ringRespone.getRingId());
                fileService.updateStatus(ring.getRingWay());
            } else {
                threenetsRingMapper.deleteByPrimaryKey(ring.getId());
                fileService.deleteFile(ring.getRingWay());
            }
            ring.setRemark(ringRespone.getMsg());
            threenetsRingMapper.updateByPrimaryKeySelective(ring);
        } catch (IOException e) {
            log.info("移动铃音接口数据读写失败:" + e);
        } catch (NoLoginException e) {
            log.info("移动铃音接口登录移动商户失败:" + e);
        }
        return ringRespone;
    }

    /**
     * 联通上传铃音
     *
     * @param ring
     */
    @Async
    public void ringToneUploadByUnicom(ThreenetsRing ring) {
        try {
            ApiUtils apiUtils = new ApiUtils();
            ring.setFile(new File(RingtoneConfig.getProfile() + ring.getRingWay()));
            ThreeNetsOrderAttached attached = threeNetsOrderAttachedMapper.selectByParentOrderId(ring.getOrderId());
            ring.setOperateId(attached.getSwxlId());
            boolean ringByLt = apiUtils.addRingByLt(ring, attached.getSwxlId());
            if (ringByLt) {
                threenetsRingMapper.updateByPrimaryKeySelective(ring);
                fileService.updateStatus(ring.getRingWay());
            } else {
                threenetsRingMapper.deleteByPrimaryKey(ring.getId());
                fileService.deleteFile(ring.getRingWay());
            }
        } catch (IOException e) {
            log.info("联通铃音接口数据读写失败:" + e);
        } catch (NoLoginException e) {
            log.info("联通铃音接口登录移动商户失败:" + e);
        }
    }

    /**
     * 电信上传铃音
     *
     * @param ring
     */
    @Async
    public void ringToneUploadByTelecom(ThreenetsRing ring) {
        try {
            ApiUtils apiUtils = new ApiUtils();
            ring.setFile(new File(RingtoneConfig.getProfile() + ring.getRingWay()));
            ThreeNetsOrderAttached attached = threeNetsOrderAttachedMapper.selectByParentOrderId(ring.getOrderId());
            ring.setOperateId(attached.getMcardId());
            boolean flag = apiUtils.addRingByDx(ring, attached);
            if (flag) {
                threenetsRingMapper.updateByPrimaryKeySelective(ring);
                fileService.updateStatus(ring.getRingWay());
            } else {
                threenetsRingMapper.deleteByPrimaryKey(ring.getId());
                fileService.deleteFile(ring.getRingWay());
            }
        } catch (Exception e) {
            log.info("电信上传铃音失败:" + e);
        }
    }
}
