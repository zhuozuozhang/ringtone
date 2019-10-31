package com.hrtxn.ringtone.project.threenets.threenet.service;

import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.OrderRequest;
import com.hrtxn.ringtone.common.exception.NoLoginException;
import com.hrtxn.ringtone.common.utils.*;
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
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.management.remote.rmi._RMIConnection_Stub;
import java.io.File;
import java.io.IOException;
import java.sql.Struct;
import java.util.*;
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
        log.info(DateUtils.getTime() + "创建三网订单开始：" + order.getCompanyName());
        try {
            ThreenetsChildOrder childOrder = new ThreenetsChildOrder();
            childOrder.setParentOrderId(order.getId());
            List<ThreenetsChildOrder> list = threenetsChildOrderMapper.listByParamNoPage(childOrder);
            Map<Integer, List<ThreenetsChildOrder>> collect = list.stream().collect(Collectors.groupingBy(ThreenetsChildOrder::getOperator));
            // 移动
            if (collect.get(Const.OPERATORS_MOBILE) != null) {
                log.info(DateUtils.getTime() + "创建三网移动订单开始：" + order.getCompanyName());
                createMobileMerchant(order, attached, collect.get(Const.OPERATORS_MOBILE));
            }
            //联通
            if (collect.get(Const.OPERATORS_UNICOM) != null) {
                log.info(DateUtils.getTime() + "创建三网联通订单开始：" + order.getCompanyName());
                List<ThreenetsChildOrder> childOrders = collect.get(Const.OPERATORS_UNICOM);
                order.setMianduan("0");
                if (StringUtils.isNotEmpty(orderRequest.getMianduan()) && orderRequest.getMianduan().equals("是")) {
                    order.setMianduan("1");
                    for (int i = 0; i < childOrders.size(); i++) {
                        childOrders.get(i).setIsExemptSms(Const.IS_EXEMPT_SMS_YES);
                    }
                } else {
                    //如果不是免短则不保存免短相关的文件
                    attached.setAvoidShortAgreement("");
                }
                order.setPaymentType("0");
                createUnicomMerchant(order, attached, childOrders);
            }
            //电信
            if (collect.get(Const.OPERATORS_TELECOM) != null) {
                log.info(DateUtils.getTime() + "创建三网电信订单开始：" + order.getCompanyName());
                createTelecomMerchant(order, attached, orderRequest, collect.get(Const.OPERATORS_TELECOM));
            }
            //保存订单附表
            threeNetsOrderAttachedMapper.updateByPrimaryKeySelective(attached);
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
        log.info(DateUtils.getTime() + "创建三网子订单开始：");
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
                    ThreenetsRing threenetsRing = new ThreenetsRing();
                    threenetsRing.setOrderId(attached.getParentOrderId());
                    if (rings.size()>0){
                        threenetsRing =  rings.get(0);
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
                    }
                    //order.setUpLoadAgreement(new File(RingtoneConfig.getProfile() + threenetsRing.getRingWay()));
                    log.info(DateUtils.getTime() + "创建三网移动子订单开始：" + threenetsRing.getRingName());
                    list = addMembersByYd(order, attached, list, threenetsRing);
                    batchChindOrder(list, threenetsRing, order.getUserId());
                }
                if (operator == 2) {
                    ThreenetsRing threenetsRing = new ThreenetsRing();
                    threenetsRing.setOrderId(attached.getParentOrderId());
                    if (rings.size()>0){
                        threenetsRing =  rings.get(0);
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
                    }
                    log.info(DateUtils.getTime() + "创建三网电信子订单开始：" + threenetsRing.getRingName());
                    List<ThreenetsChildOrder> orders = addMcardByDx(order, attached, list, request, threenetsRing);
                    batchChindOrder(orders, threenetsRing, order.getUserId());
                }
                if (operator == 3) {
                    order.setMianduan("0");
                    if ((StringUtils.isNotEmpty(request.getMianduan()) && request.getMianduan().equals("是"))||StringUtils.isNotEmpty(attached.getAvoidShortAgreement())) {
                        order.setMianduan("1");
                        for (int i = 0; i < list.size(); i++) {
                            list.get(i).setIsExemptSms(Const.IS_EXEMPT_SMS_YES);
                        }
                    }
                    //各付
                    order.setPaymentType("0");
                    ThreenetsRing threenetsRing = new ThreenetsRing();
                    threenetsRing.setOrderId(attached.getParentOrderId());
                    if (rings.size()>0){
                        threenetsRing =  rings.get(0);
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
                    }
                    log.info(DateUtils.getTime() + "创建三网联通子订单开始：" + threenetsRing.getRingName());
                    if(StringUtils.isNotEmpty(request.getProtocolUrl())){
                        attached.setAvoidShortAgreement(request.getProtocolUrl());
                    }
                    list = addMemberByLt(order, attached, list, threenetsRing);
                    batchChindOrder(list, threenetsRing, order.getUserId());
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
    public Integer batchChindOrder(List<ThreenetsChildOrder> orders, ThreenetsRing ring, Integer userId) {
        Integer sum = 0;
        for (int i = 0; i < orders.size(); i++) {
            ThreenetsChildOrder childOrder = orders.get(i);
            childOrder.setParentOrderId(ring.getOrderId());
            if (!userId.equals(childOrder.getUserId())) {
                childOrder.setUserId(userId);
            }
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
            createMobileMerchant(order, attached, list);
            return list;
        }
        MiguAddPhoneRespone addPhoneRespone = new MiguAddPhoneRespone();
        apiUtils.loginMobile();
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
        try {
            //apiUtils.getPhoneInfo(list);
            apiUtils.batchRefresh(list, order.getId());
        } catch (Exception e) {
            log.info("移动成员信息获取失败=>" + e);
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
    private List<ThreenetsChildOrder> addMemberByLt(ThreenetsOrder order, ThreeNetsOrderAttached attached, List<ThreenetsChildOrder> list, ThreenetsRing ring) throws IOException, NoLoginException {
        String phones = "";
        ApiUtils apiUtils = new ApiUtils();
        if (StringUtils.isEmpty(attached.getSwxlId())) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setRingId(ring.getId());
            }
            createUnicomMerchant(order, attached, list);
            return list;
        }
        //登录联通商户
        apiUtils.loginToUnicom();
        list = apiUtils.unicomCheckMobiles(list);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStatus().equals(Const.FAILURE_REVIEW)) {
                threenetsChildOrderMapper.updateThreeNetsChidOrder(list.get(i));
                list.remove(i);
            } else {
                phones = list.get(i).getLinkmanTel() + "," + phones;
            }
        }
        SwxlBaseBackMessage result = apiUtils.addPhoneByLt(phones, attached);
        for (int i = 0; i < list.size(); i++) {
            ThreenetsChildOrder childOrder = list.get(i);
            if (StringUtils.isNotEmpty(attached.getAvoidShortAgreement())) {
                childOrder.setIsExemptSms(Const.IS_EXEMPT_SMS_YES);
            }
            childOrder.setOperateId(attached.getSwxlId());
            childOrder.setRemark(result.getMessage());
            threenetsChildOrderMapper.updateThreeNetsChidOrder(childOrder);
        }
        try {
            //apiUtils.getPhoneInfo(list);
            apiUtils.batchRefresh(list, order.getId());
        } catch (Exception e) {
            log.info("联通成员信息获取失败=>" + e);
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
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setRingId(ring.getId());
            }
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
            createTelecomMerchant(order, attached, request1, list);
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
            utils.loginMobile();
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
                    if (attached.getMiguPrice() <= 15) {
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
                List<ThreenetsRing> rings = threenetsRingMapper.listByOrderIdAndOperator(order.getId(), Const.OPERATORS_MOBILE);
                if (rings != null && rings.size() > 0) {
                    ThreenetsRing ring = rings.get(0);
                    ring.setOperateId(groupRespone.getCircleId());
                    ring.setFile(new File(RingtoneConfig.getProfile() + ring.getRingWay()));
                    MiguAddRingRespone ringRespone = utils.saveMiguRing(ring);
                    if (ringRespone.isSuccess()) {
                        ring.setOperateRingId(ringRespone.getRingId());
                        fileService.updateStatus(ring.getRingWay());
                        if (ring.getRingType().equals("视频")) {
                            String extensionsName = ring.getRingName().substring(ring.getRingName().indexOf("."));
                            String ringName = ring.getRingName().substring(0, ring.getRingName().lastIndexOf("."));
                            String month = DateUtils.getMonth() < 10 ? "0" : "";
                            String time = "_" + DateUtils.getYear() + month + DateUtils.getMonth();
                            ring.setRingName(ringName + time + extensionsName);
                        }
                    }
                    ring.setRemark(ringRespone.getMsg());
                    threenetsRingMapper.updateByPrimaryKeySelective(ring);
                }
                threeNetsOrderAttachedMapper.updateByPrimaryKeySelective(attached);
                utils.batchRefresh(childOrders, order.getId());
                //utils.getPhoneInfo(childOrders);
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
                if (childOrders.size() > 0) {
                    createMobileMerchant(order, attached, childOrders);
                }
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
     * @param list
     */
    private void createUnicomMerchant(ThreenetsOrder order, ThreeNetsOrderAttached attached, List<ThreenetsChildOrder> list) {
        try {
            String phones = "";
            ApiUtils utils = new ApiUtils();
            //登录联通商户
            utils.loginToUnicom();
            //验证手机号是否可以添加
            List<ThreenetsChildOrder> childOrders = new ArrayList<>();
            list = utils.unicomCheckMobiles(list);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getStatus().equals(Const.FAILURE_REVIEW)) {
                    threenetsChildOrderMapper.updateThreeNetsChidOrder(list.get(i));
                } else {
                    phones = list.get(i).getLinkmanTel() + "," + phones;
                    childOrders.add(list.get(i));
                }
            }
            ThreenetsChildOrder firstChildOrder = childOrders.get(0);
            //获取关联铃音
            List<ThreenetsRing> rings = threenetsRingMapper.listByOrderIdAndOperator(order.getId(), Const.OPERATORS_UNICOM);
            if (rings != null && rings.size() > 0) {
                ThreenetsRing ring = rings.get(0);
                order.setRingName(ring.getRingName().substring(0, ring.getRingName().indexOf(".")));
                order.setUpLoadAgreement(new File(RingtoneConfig.getProfile() + ring.getRingWay()));
            }
            //商户建立联系人手机号
            order.setLinkmanTel(childOrders.get(0).getLinkmanTel());
            order.setPhones(phones);
            SwxlGroupResponse swxlGroupResponse = utils.addOrderByLt(order, attached);
            if (swxlGroupResponse.getStatus() == 0 || swxlGroupResponse.getStatus() == 1) {
                attached.setSwxlId(swxlGroupResponse.getId());
                attached.setSwxlStatus(Const.REVIEWED);
                threeNetsOrderAttachedMapper.updateByPrimaryKeySelective(attached);
                for (int i = 0; i < childOrders.size(); i++) {
                    ThreenetsChildOrder childOrder = childOrders.get(i);
                    childOrder.setOperateId(attached.getSwxlId());
                    if (swxlGroupResponse.getStatus() == 1) {
                        childOrder.setRemark("免短审核中，请稍后刷新查看！");
                    } else {
                        childOrder.setRemark("添加成功");
                    }
                    threenetsChildOrderMapper.updateThreeNetsChidOrder(childOrder);
                }
                for (int i = 0; i < rings.size(); i++) {
                    rings.get(i).setOperateId(swxlGroupResponse.getId());
                    threenetsRingMapper.updateByPrimaryKeySelective(rings.get(i));
                }
                utils.batchRefresh(childOrders, order.getId());
                //utils.getPhoneInfo(childOrders);
                return;
            } else {
                firstChildOrder.setRemark("联通商户:" + swxlGroupResponse.getRemark());
                firstChildOrder.setStatus(Const.FAILURE_REVIEW);
                threenetsChildOrderMapper.updateThreeNetsChidOrder(firstChildOrder);
                threeNetsOrderAttachedMapper.updateByPrimaryKeySelective(attached);
                if (childOrders.size() == 1) {
                    return;
                }
                childOrders.remove(0);
                if (childOrders.size() > 0) {
                    createUnicomMerchant(order, attached, childOrders);
                }
            }
        } catch (Exception e) {
            log.info("联通商户建立失败=>" + e);
        }
    }

    /**
     * 创建电信商户
     *
     * @param order
     * @param attached
     * @param orderRequest
     * @param childOrderList
     */
    private void createTelecomMerchant(ThreenetsOrder order, ThreeNetsOrderAttached attached, OrderRequest orderRequest, List<ThreenetsChildOrder> childOrderList) {
        try {
            ApiUtils utils = new ApiUtils();
            List<ThreenetsChildOrder> childOrders = new ArrayList<>();
            for (int i = 0; i < childOrderList.size(); i++) {
                ThreenetsChildOrder childOrder = childOrderList.get(i);
                boolean flag = ConfigUtil.getAreaArray("unable_to_open_area", childOrder.getProvince());
                if (flag) {
                    childOrder.setRemark(childOrder.getProvince() + "电信暂停业务");
                    childOrder.setStatus(Const.FAILURE_REVIEW);
                    threenetsChildOrderMapper.updateThreeNetsChidOrder(childOrder);
                }else{
                    childOrders.add(childOrder);
                }
            }
            if (childOrders.size() == 0) {
                return;
            }
            ThreenetsChildOrder firstChildOrder = childOrders.get(0);
            order.setLinkmanTel(firstChildOrder.getLinkmanTel());
            order.setProvince(firstChildOrder.getProvince());
            order.setCity(firstChildOrder.getCity());
            //验证是否为固定电话
            if (childOrders.size() == 1 && PhoneUtils.isFixedPhone(firstChildOrder.getLinkmanTel())){
                firstChildOrder.setRemark("请填写一个商户地区非固定电话的号码用以创建商户！");
                firstChildOrder.setStatus(Const.FAILURE_REVIEW);
                threenetsChildOrderMapper.updateThreeNetsChidOrder(firstChildOrder);
                return;
            }
            //文件上传
            if (order.getProvince().equals("河南")) {
                attached.setMcardDistributorId(Const.parent_Distributor_ID_177);
            } else {
                attached.setMcardDistributorId(Const.parent_Distributor_ID_188);
            }
            if (orderRequest.getCompanyUrl() != null && !orderRequest.getCompanyUrl().isEmpty()) {
                String path = utils.mcardUploadFile(new File(RingtoneConfig.getProfile() + orderRequest.getCompanyUrl()), attached.getMcardDistributorId());
                attached.setBusinessLicense(path);
            }
            if (orderRequest.getClientUrl() != null && !orderRequest.getClientUrl().isEmpty()) {
                String path = utils.mcardUploadFile(new File(RingtoneConfig.getProfile() + orderRequest.getClientUrl()), attached.getMcardDistributorId());
                attached.setConfirmLetter(path);
            }
            if (orderRequest.getMainUrl() != null && !orderRequest.getMainUrl().isEmpty()) {
                String path = utils.mcardUploadFile(new File(RingtoneConfig.getProfile() + orderRequest.getMainUrl()), attached.getMcardDistributorId());
                attached.setSubjectProve(path);
            }
            //同步电信
            McardAddGroupRespone groupRespone = utils.addOrderByDx(order, attached);
            if (groupRespone.getCode().equals(Const.ILLEFAL_AREA)) {
                firstChildOrder.setRemark("审核失败：电信商户—" + groupRespone.getMessage());
                firstChildOrder.setStatus("审核失败");
                threenetsChildOrderMapper.updateThreeNetsChidOrder(firstChildOrder);
                childOrders.remove(0);
                if (childOrders.size() > 0) {
                    createTelecomMerchant(order, attached, orderRequest, childOrders);
                }
            } else {
                //保存附表
                attached.setMcardStatus(Const.UNREVIEWED);
                attached.setMcardId(groupRespone.getAuserId());
                attached.setMcardPrice(attached.getMcardPrice() == 2 ? 10 : 20);
                threeNetsOrderAttachedMapper.updateByPrimaryKeySelective(attached);
                //保存铃音
                List<ThreenetsRing> rings = threenetsRingMapper.listByOrderIdAndOperator(order.getId(), Const.OPERATORS_TELECOM);
                for (int i = 0; i < rings.size(); i++) {
                    rings.get(i).setOperateId(groupRespone.getAuserId());
                    threenetsRingMapper.updateByPrimaryKeySelective(rings.get(i));
                }
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
    @Synchronized
    public void refreshTelecomMerchantInfo(ThreenetsOrder order) {
        ApiUtils apiUtils = new ApiUtils();
        ThreeNetsOrderAttached attached = threeNetsOrderAttachedMapper.selectByParentOrderId(order.getId());
        if (attached == null) {
            return;
        }
        if (StringUtils.isEmpty(attached.getMcardId()) && StringUtils.isNotEmpty(attached.getMcardDistributorId())) {
            String ID = apiUtils.normalBusinessID(order);
            attached.setMcardId(ID);
        }
        if (StringUtils.isEmpty(attached.getMcardId()) || attached.getMcardStatus().equals(Const.REVIEWED)) {
            return;
        }
        try {
            McardAddGroupRespone respone = apiUtils.normalBusinessInfo(order);
            log.info("电信获取审核状态---->" + respone.getCode());
            if (respone.getCode().equals("0000")) {
                ThreenetsChildOrder param = new ThreenetsChildOrder();
                param.setParentOrderId(order.getId());
                param.setOperator(Const.OPERATORS_TELECOM);
                param.setStatus(Const.PENDING_REVIEW);
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
                    if (StringUtils.isNotEmpty(rings.get(i).getOperateRingId())) {
                        continue;
                    }
                    ThreenetsRing ring = rings.get(i);
                    ring.setFile(new File(RingtoneConfig.getProfile() + ring.getRingWay()));
                    apiUtils.addRingByDx(rings.get(i), attached);
                }
                for (int i = 0; i < childOrders.size(); i++) {
                    threenetsChildOrderMapper.updateThreeNetsChidOrder(childOrders.get(i));
                }
                attached.setMcardStatus(Const.REVIEWED);
            } else {
                ThreenetsChildOrder param = new ThreenetsChildOrder();
                param.setParentOrderId(order.getId());
                param.setOperator(Const.OPERATORS_TELECOM);
                List<ThreenetsChildOrder> childOrders = threenetsChildOrderMapper.listByParamNoPage(param);
                for (int i = 0; i < childOrders.size(); i++) {
                    ThreenetsChildOrder childOrder = childOrders.get(i);
                    log.info("电信修改审核状态---->" + respone.getMessage());
                    if (respone.getCode().equals("1001")) {
                        childOrder.setRemark(respone.getMessage());
                    } else {
                        childOrder.setStatus(Const.PENDING_REVIEW);
                        childOrder.setRemark("电信商户正在审核中，请等待！");
                    }
                    threenetsChildOrderMapper.updateThreeNetsChidOrder(childOrder);
                }
            }
        } catch (Exception e) {
            log.info("电信刷新信息上传用户失败" + e);
        }
    }

    /**
     * 从新上传添加失败的用户
     *
     * @param order
     */
    @Async
    public void uploadFailedMember(ThreenetsOrder order) {
        ThreeNetsOrderAttached attached = threeNetsOrderAttachedMapper.selectByParentOrderId(order.getId());
        ThreenetsChildOrder childOrder = new ThreenetsChildOrder();
        childOrder.setParentOrderId(order.getId());
        childOrder.setStatus(Const.PENDING_REVIEW);
        try {
            List<ThreenetsChildOrder> oldlist = threenetsChildOrderMapper.listByParamNoPage(childOrder);
            List<ThreenetsChildOrder> list = new ArrayList<>();
            for (int i = 0; i < oldlist.size(); i++) {
                if (StringUtils.isEmpty(oldlist.get(i).getRemark())) {
                    list.add(oldlist.get(i));
                }
            }
            Map<Integer, List<ThreenetsChildOrder>> map = list.stream().collect(Collectors.groupingBy(ThreenetsChildOrder::getOperator));
            List<ThreenetsRing> threenetsRings = threenetsRingMapper.selectByOrderId(order.getId());
            Map<Integer, List<ThreenetsRing>> ringMap = threenetsRings.stream().collect(Collectors.groupingBy(ThreenetsRing::getOperate));
            for (Integer operate : map.keySet()) {
                if (operate.equals(Const.OPERATORS_MOBILE)) {
                    ThreenetsRing ring = ringMap.get(Const.OPERATORS_MOBILE).get(0);
                    addMembersByYd(order, attached, map.get(Const.OPERATORS_MOBILE), ring);
                }
                if (operate.equals(Const.OPERATORS_TELECOM)) {
                    ThreenetsRing ring = ringMap.get(Const.OPERATORS_TELECOM).get(0);
                    BaseRequest request = new BaseRequest();
                    if (StringUtils.isNotEmpty(attached.getBusinessLicense())) {
                        request.setCompanyUrl(attached.getBusinessLicense());
                    }
                    if (StringUtils.isNotEmpty(attached.getConfirmLetter())) {
                        request.setClientUrl(attached.getConfirmLetter());
                    }
                    if (StringUtils.isNotEmpty(attached.getSubjectProve())) {
                        request.setMainUrl(attached.getSubjectProve());
                    }
                    addMcardByDx(order, attached, map.get(Const.OPERATORS_TELECOM), request, ring);
                }
                if (operate.equals(Const.OPERATORS_UNICOM)) {
                    ThreenetsRing ring = ringMap.get(Const.OPERATORS_UNICOM).get(0);
                    order.setPaymentType("0");
                    if (StringUtils.isNotEmpty(attached.getAvoidShortAgreement())) {
                        order.setMianduan("1");
                    } else {
                        order.setMianduan("0");
                    }
                    addMemberByLt(order, attached, map.get(Const.OPERATORS_UNICOM), ring);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("重新上传添加失败用户" + e);
        }
    }

    public void refreshMobileMerchantInfo(ThreenetsOrder order) {
        try {
            ApiUtils apiUtils = new ApiUtils();
            ThreeNetsOrderAttached attached = threeNetsOrderAttachedMapper.selectByParentOrderId(order.getId());
            if (attached == null) {
                return;
            }
            if (StringUtils.isEmpty(attached.getMiguId()) || attached.getMcardStatus().equals(Const.REVIEWED)) {
                return;
            }
            ThreenetsChildOrder param = new ThreenetsChildOrder();
            param.setParentOrderId(order.getId());
            param.setStatus(Const.PENDING_REVIEW);
            List<ThreenetsChildOrder> list = threenetsChildOrderMapper.listByParamNoPage(param);
            addMembersByYd(order, attached, list, new ThreenetsRing());
        } catch (Exception e) {
            log.info("移动高资费添加成员失败=>" + e);
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
            ring.setOperate(Const.OPERATORS_MOBILE);
            ringRespone = apiUtils.saveMiguRing(ring);
            if (ringRespone != null && ringRespone.isSuccess()) {
                //保存铃音
                ring.setOperateRingId(ringRespone.getRingId());
                fileService.updateStatus(ring.getRingWay());
            }
            if (ring.getRingType().equals("视频")) {
                String extensionsName = ring.getRingName().substring(ring.getRingName().indexOf("."));
                String ringName = ring.getRingName().substring(0, ring.getRingName().lastIndexOf("."));
                String month = DateUtils.getMonth() < 10 ? "0" : "";
                String time = "_" + DateUtils.getYear() + month + DateUtils.getMonth();
                ring.setRingName(ringName + time + extensionsName);
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

    public static void main(String[] args) {
        String rn = "新宇装饰207739.mp4";
        String extensionsName = rn.substring(rn.indexOf("."));
        String ringName = rn.substring(0, rn.lastIndexOf("."));
        String month = DateUtils.getMonth() < 10 ? "0" : "";
        String time = "_" + DateUtils.getYear() + month + DateUtils.getMonth();
        String name = ringName + time + extensionsName;
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
            //登录联通商户
            apiUtils.loginToUnicom();
            ring.setFile(new File(RingtoneConfig.getProfile() + ring.getRingWay()));
            ThreeNetsOrderAttached attached = threeNetsOrderAttachedMapper.selectByParentOrderId(ring.getOrderId());
            ring.setOperateId(attached.getSwxlId());
            ring.setOperate(Const.OPERATORS_UNICOM);
            boolean ringByLt = apiUtils.addRingByLt(ring, attached.getSwxlId());
            if (ringByLt) {
                threenetsRingMapper.updateByPrimaryKeySelective(ring);
                fileService.updateStatus(ring.getRingWay());
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
            ring.setOperate(Const.OPERATORS_TELECOM);
            boolean flag = apiUtils.addRingByDx(ring, attached);
            if (flag) {
                threenetsRingMapper.updateByPrimaryKeySelective(ring);
                fileService.updateStatus(ring.getRingWay());
            }
        } catch (Exception e) {
            log.info("电信上传铃音失败:" + e);
        }
    }
}
