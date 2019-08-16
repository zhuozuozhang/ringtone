package com.hrtxn.ringtone.project.threenets.threenet.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.OrderRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.juhe.JuhePhoneUtils;
import com.hrtxn.ringtone.freemark.config.systemConfig.RingtoneConfig;
import com.hrtxn.ringtone.project.system.File.domain.Uploadfile;
import com.hrtxn.ringtone.project.system.File.service.FileService;
import com.hrtxn.ringtone.project.system.json.JuhePhone;
import com.hrtxn.ringtone.project.system.json.JuhePhoneResult;
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
import lombok.Synchronized;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author:lile
 * Date:2019/7/11 10:35
 * Description:三网订单业务层
 */
@Service
public class ThreeNetsOrderService {

    @Autowired
    private ThreenetsOrderMapper threenetsOrderMapper;

    @Autowired
    private ThreeNetsChildOrderService threeNetsChildOrderService;
    @Autowired
    private ThreeNetsRingService threeNetsRingService;
    @Autowired
    private FileService fileService;
    @Autowired
    private ThreeNetsOrderAttachedService threeNetsOrderAttachedService;

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    public ThreenetsOrder getById(Integer id) {
        try {
            return threenetsOrderMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            return new ThreenetsOrder();
        }
    }

    /**
     * 修改
     *
     * @param order
     * @return
     */
    public AjaxResult update(ThreenetsOrder order) {
        int i = threenetsOrderMapper.updateByPrimaryKey(order);
        if (i > 0) {
            return AjaxResult.success(i, "修改成功");
        } else {
            return AjaxResult.error("修改失败");
        }
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @Transactional
    public AjaxResult delete(Integer id) {
        try {
            //删除铃音
            String ringRul = "";
            List<ThreenetsRing> list = threeNetsRingService.listByOrderId(id);
            for (int i = 0; i < list.size(); i++) {
                threeNetsRingService.delete(list.get(i).getId());
                ringRul = list.get(i).getRingWay();
            }
            fileService.deleteFile(ringRul);
            //删除子订单
            BaseRequest request = new BaseRequest();
            request.setId(id);
            List<ThreenetsChildOrder> childOrder = threeNetsChildOrderService.getChildOrder(request);
            for (int i = 0; i < childOrder.size(); i++) {
                threeNetsChildOrderService.delete(childOrder.get(i).getId());
            }
            //删除订单
            threenetsOrderMapper.deleteByPrimaryKey(id);
            //删除附表
            ThreeNetsOrderAttached attached = threeNetsOrderAttachedService.selectByParentOrderId(id);
            threeNetsOrderAttachedService.delete(attached.getId());
            return AjaxResult.success(true, "删除成功");
        } catch (Exception e) {
            return AjaxResult.error("删除失败");
        }
    }

    /**
     * 查询三网商户列表信息
     *
     * @return
     */
    public List<ThreenetsOrder> getAllorderList() {
        return threenetsOrderMapper.getAllorderList(new Page(), new BaseRequest());
    }

    /**
     * 查询三网商户列表信息
     *
     * @param page
     * @param request
     * @return
     */
    public List<ThreenetsOrder> getAllorderList(Page page, BaseRequest request) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        // 是否是管理员 是则统计所有数据
        Integer userRole = ShiroUtils.getSysUser().getUserRole();
        if (userRole != 1) {
            request.setUserId(ShiroUtils.getSysUser().getId());
        }
        List<ThreenetsOrder> allorderList = threenetsOrderMapper.getAllorderList(page, request);
        if (allorderList.size() > 0) {
            for (ThreenetsOrder threenetsOrder : allorderList) {
                threenetsOrder.setPeopleNum(threenetsOrder.getChildOrderQuantity() + "/" + threenetsOrder.getChildOrderQuantityByMonthly());
            }
        }
        return allorderList;
    }

    /**
     * 查询三网商户列表总数
     *
     * @return
     */
    public int getCount(BaseRequest request) {
        // 是否是管理员 是则统计所有数据
        Integer userRole = ShiroUtils.getSysUser().getUserRole();
        if (userRole != 1) {
            request.setUserId(ShiroUtils.getSysUser().getId());
        }
        return threenetsOrderMapper.getCount(request);
    }

    /**
     * 验证商户名称是否重复
     *
     * @param name
     * @return
     */
    public AjaxResult isRepetitionByName(String name) {
        List<ThreenetsOrder> orders = threenetsOrderMapper.isRepetitionByName(name);
        if (orders == null || orders.isEmpty()) {
            return AjaxResult.success("");
        } else {
            return AjaxResult.error("商户名称不允许重复！");
        }
    }

    /**
     * 初始化订单
     *
     * @param order
     * @return
     */
    @Transactional
    public AjaxResult init(ThreenetsOrder order) throws Exception {
        if (order.getCompanyName() == null || order.getCompanyName().isEmpty()) {
            return AjaxResult.error("商户名称不能为空！");
        }
        JuhePhone phone = JuhePhoneUtils.getPhone(order.getLinkmanTel());
        if (!phone.getResultcode().equals("200")) {
            return AjaxResult.error("获取归属地失败，请填写正确的手机号码！");
        }
        JuhePhoneResult result = (JuhePhoneResult) phone.getResult();
        order.setOperator(JuhePhoneUtils.getOperate(result));
        order.setProvince(result.getProvince());
        order.setCity(result.getCity());
        order.setUserId(ShiroUtils.getSysUser().getId());
        order.setCreateTime(new Date());
        order.setStatus("审核通过");
        threenetsOrderMapper.insertThreenetsOrder(order);
        return AjaxResult.success(order.getId(), "保存成功");
    }


    /**
     * 保存订单
     *
     * @param order
     */
    @Transactional
    public AjaxResult save(OrderRequest order) throws Exception {
        if (order.getId() == null){
            return AjaxResult.error("添加失败！");
        }
        //验证订单是否修改
        ThreenetsOrder threenetsOrder = threenetsOrderMapper.selectByPrimaryKey(order.getId());
        threenetsOrder.setCompanyName(order.getCompanyName());
        threenetsOrder.setCompanyLinkman(order.getCompanyLinkman());
        if (!threenetsOrder.getLinkmanTel().equals(order.getLinkmanTel())) {
            //如果更改手机号则更改手机号归属地
            threenetsOrder = JuhePhoneUtils.getPhone(threenetsOrder);
            BeanUtils.copyProperties(threenetsOrder, order);
            threenetsOrderMapper.updateByPrimaryKey(threenetsOrder);
        }
        //订单附表初始化
        ThreeNetsOrderAttached attached = new ThreeNetsOrderAttached();
        if (order.getMobilePay() != null || order.getSpecialPrice() != null) {
            attached.setMiguPrice(Integer.parseInt(order.getMobilePay() != null ? order.getMobilePay() : order.getSpecialPrice()));
        }
        if (order.getUmicomPay() != null) {
            attached.setSwxlPrice(Integer.parseInt(order.getUmicomPay()));
        }
        attached.setParentOrderId(order.getId());
        attached.setBusinessLicense(order.getCompanyUrl());//企业资质
        attached.setConfirmLetter(order.getClientUrl());//客户确认函
        attached.setSubjectProve(order.getMainUrl());//主体证明
        attached.setAvoidShortAgreement(order.getProtocolUrl());//免短协议
        //保存订单附表
        threeNetsOrderAttachedService.save(attached);
        //子订单手机号验证,以及子订单数据初始化
        List<ThreenetsChildOrder> childOrderList = threeNetsChildOrderService.formattedPhone(order.getMemberTels(), order.getId());
        List<ThreenetsChildOrder> childOrders = new ArrayList<>();
        //将子订单数据按照网段区分
        Map<Integer, List<ThreenetsChildOrder>> collect = childOrderList.stream().collect(Collectors.groupingBy(ThreenetsChildOrder::getOperator));
        int num = 1;
        for (Integer operator : collect.keySet()) {
            ThreenetsRing ring = new ThreenetsRing();
            ring.setRingWay(order.getRingUrl());
            ring.setOperate(order.getOperate());
            ring.setOrderId(order.getId());
            ring.setRingContent(order.getRingContent());
            ring.setRingName(order.getRingName());
            ring.setOperate(operator);
            ring.setRingStatus(1);
            if (num > 1) {
                String file = fileService.cloneFile(ring);
                ring.setRingWay(file);
            }
            threeNetsRingService.save(ring);
            List<ThreenetsChildOrder> list = collect.get(operator);
            for (int i = 0; i < list.size(); i++) {
                ThreenetsChildOrder childOrder = list.get(i);
                childOrder.setRingId(ring.getId());
                childOrder.setRingName(ring.getRingName());
                childOrder.setIsVideoUser(ring.getRingType().equals("视频") ? true : false);
                childOrder.setIsRingtoneUser(ring.getRingType().equals("视频") ? false : true);
                childOrder.setPaymentType(Integer.parseInt(order.getPaymentType()));
                childOrders.add(childOrder);
            }
            num++;
        }
        //保存线上
        saveOnlineOrder(threenetsOrder, attached, childOrders, order);
        return AjaxResult.success(threenetsOrder, "保存成功！");
    }

    /**
     * 三网保存订单
     *
     * @param list
     */
    @Synchronized
    private void saveOnlineOrder(ThreenetsOrder order, ThreeNetsOrderAttached attached, List<ThreenetsChildOrder> list, OrderRequest orderRequest) {
        Map<Integer, List<ThreenetsChildOrder>> collect = list.stream().collect(Collectors.groupingBy(ThreenetsChildOrder::getOperator));
        ApiUtils utils = new ApiUtils();
        try {
            // 移动
            if (collect.get(1) != null) {
                order.setLinkmanTel(collect.get(1).get(0).getLinkmanTel());
                MiguAddGroupRespone miguAddGroupRespone = utils.addOrderByYd(order, attached);
                if (miguAddGroupRespone.isSuccess()) {
                    List<ThreenetsChildOrder> childOrders = collect.get(1);
                    ThreenetsRing ring = threeNetsRingService.getRing(childOrders.get(0).getRingId());
                    attached.setMiguId(miguAddGroupRespone.getCircleId());
                    ring.setOperateId(miguAddGroupRespone.getCircleId());
                    MiguAddRingRespone ringRespone = utils.saveMiguRing(ring, attached.getMiguId(), order.getCompanyName());
                    if (ringRespone.isSuccess()) {
                        ring.setOperateRingId(ringRespone.getRingId());
                        threeNetsRingService.update(ring);
                    } else {
                        threeNetsRingService.delete(ring.getId());
                        fileService.deleteFile(ring.getRingWay());
                    }
                    for (int i = 0; i < childOrders.size(); i++) {
                        ThreenetsChildOrder childOrder = childOrders.get(i);
                        childOrder.setOperateId(attached.getMiguId());
                        childOrders.set(i, childOrder);
                    }
                    if (attached.getMiguPrice()<=5){
                        utils.addPhoneByYd(childOrders,attached.getMiguId());
                        attached.setMiguStatus(1);
                    }else{
                        attached.setMiguStatus(0);
                    }
                    threeNetsChildOrderService.batchChindOrder(childOrders);
                }
            }
            //联通
            if (collect.get(3) != null) {
                List<ThreenetsChildOrder> childOrders = collect.get(3);
                ThreenetsRing ring = threeNetsRingService.getRing(childOrders.get(0).getRingId());
                order.setUpLoadAgreement(new File(RingtoneConfig.getProfile() + ring.getRingWay()));
                SwxlGroupResponse swxlGroupResponse = utils.addOrderByLt(order, attached);
                if (swxlGroupResponse.getStatus() == 0) {
                    attached.setSwxlId(swxlGroupResponse.getGroupId());
                    ring.setOperateId(swxlGroupResponse.getGroupId());
                    boolean addRingByLt = utils.addRingByLt(ring, attached.getSwxlId());
                    if (!addRingByLt) {
                        threeNetsRingService.delete(ring.getId());
                        fileService.deleteFile(ring.getRingWay());
                    }
                    for (int i = 0; i < childOrders.size(); i++) {
                        ThreenetsChildOrder childOrder = childOrders.get(i);
                        childOrder.setOperateId(attached.getSwxlId());
                        childOrders.set(i, childOrder);
                    }
                    utils.addPhoneByLt(childOrders,attached.getSwxlId());
                    attached.setSwxlStatus(1);
                    threeNetsChildOrderService.batchChindOrder(childOrders);
                    threeNetsRingService.update(ring);
                } else {
                    return;
                }
            }
            //电信
            if (collect.get(2) != null) {
                //文件上传
                if (orderRequest.getCompanyUrl() != null && !orderRequest.getCompanyUrl().isEmpty()) {
                    String path = utils.mcardUploadFile(new File(RingtoneConfig.getProfile() + orderRequest.getCompanyUrl()));
//                    String path = "/pic.diy.v1/nets/mcard/DiyFile/image/2019/08/15/fe1185ad-dc80-4ed6-9d08-fc039e4d94c7.jpg";
                    attached.setBusinessLicense(path);
                }
                if (orderRequest.getClientUrl() != null && !orderRequest.getClientUrl().isEmpty() ) {
                    String path = utils.mcardUploadFile(new File(RingtoneConfig.getProfile() + orderRequest.getClientUrl()));
//                    String path = "/pic.diy.v1/nets/mcard/DiyFile/image/2019/08/15/93b4d9f6-3980-455f-911d-02039b6709aa.jpg";
                    attached.setConfirmLetter(path);
                }
                if (orderRequest.getMainUrl() != null && !orderRequest.getMainUrl().isEmpty()) {
                    String path = utils.mcardUploadFile(new File(RingtoneConfig.getProfile() + orderRequest.getMainUrl()));
                    attached.setSubjectProve(path);
                }
                List<ThreenetsChildOrder> childOrders = collect.get(2);
                order.setLinkmanTel(childOrders.get(0).getLinkmanTel());
                McardAddGroupRespone groupRespone = utils.addOrderByDx(order, attached);
                attached.setMcardId(groupRespone.getAuserId());
                //本地保存铃音和子表
                for (int i = 0; i < childOrders.size(); i++) {
                    ThreenetsChildOrder childOrder = childOrders.get(i);
                    childOrder.setOperateId(attached.getMcardId());
                    childOrder.setStatus("待审核");
                    childOrders.set(i, childOrder);
                }
                threeNetsChildOrderService.batchChindOrder(childOrders);
                attached.setMcardStatus(0);
            }
            //保存订单附表
            threeNetsOrderAttachedService.update(attached);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
