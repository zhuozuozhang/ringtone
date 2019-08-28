package com.hrtxn.ringtone.project.threenets.threenet.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.OrderRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.exception.NoLoginException;
import com.hrtxn.ringtone.common.utils.*;
import com.hrtxn.ringtone.common.utils.juhe.JuhePhoneUtils;
import com.hrtxn.ringtone.freemark.config.systemConfig.RingtoneConfig;
import com.hrtxn.ringtone.project.system.File.domain.Uploadfile;
import com.hrtxn.ringtone.project.system.File.service.FileService;
import com.hrtxn.ringtone.project.system.config.domain.SystemConfig;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
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

    @Autowired
    private ThreeNetsAsyncService threeNetsAsyncService;
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
    public AjaxResult delete(Integer id) throws Exception {
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
        if (attached != null && attached.getId() != null) {
            threeNetsOrderAttachedService.delete(attached.getId());
        }
        return AjaxResult.success("删除成功");
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
        name = name + DateUtils.getTimeRadom();
        List<ThreenetsOrder> orders = threenetsOrderMapper.isRepetitionByName(name);
        if (orders == null || orders.isEmpty()) {
            ThreenetsOrder order= new ThreenetsOrder();
            order.setCompanyName(name);
            threenetsOrderMapper.insertThreenetsOrder(order);
            return AjaxResult.success(order,"");
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
    public ThreenetsOrder init(ThreenetsOrder order) throws Exception {
        JuhePhone phone = JuhePhoneUtils.getPhone(order.getLinkmanTel());
        if (phone.getResultcode().equals("200")) {
            JuhePhoneResult result = (JuhePhoneResult) phone.getResult();
            order.setOperator(JuhePhoneUtils.getOperate(result));
            order.setProvince(result.getProvince());
            order.setCity(result.getCity());
        }
        order.setUserId(ShiroUtils.getSysUser().getId());
        order.setCreateTime(new Date());
        order.setStatus("审核通过");
        return  order;
    }


    /**
     * 保存订单
     *
     * @param order
     */
    @Transactional
    public AjaxResult save(OrderRequest order) throws Exception {
        if (order.getId() == null) {
            return AjaxResult.error("添加失败！");
        }
        //验证订单是否修改
        ThreenetsOrder threenetsOrder = threenetsOrderMapper.selectByPrimaryKey(order.getId());
        threenetsOrder.setCompanyName(order.getCompanyName());
        threenetsOrder.setCompanyLinkman(order.getCompanyLinkman());
        threenetsOrder.setLinkmanTel(order.getLinkmanTel());
        threenetsOrder = init(threenetsOrder);
        threenetsOrderMapper.updateByPrimaryKey(threenetsOrder);
        //订单附表初始化
        ThreeNetsOrderAttached attached = new ThreeNetsOrderAttached();
        attached.setParentOrderId(order.getId());
        if (StringUtils.isNotEmpty(order.getCompanyUrl())) {
            attached.setBusinessLicense(order.getCompanyUrl());//企业资质
        }
        if (StringUtils.isNotEmpty(order.getClientUrl())) {
            attached.setConfirmLetter(order.getClientUrl());//客户确认函
        }
        if (StringUtils.isNotEmpty(order.getMainUrl())) {
            attached.setSubjectProve(order.getMainUrl());//主体证明
        }
        if (StringUtils.isNotEmpty(order.getProtocolUrl())) {
            attached.setAvoidShortAgreement(order.getProtocolUrl());//免短协议
        }
        //子订单手机号验证,以及子订单数据初始化
        List<ThreenetsChildOrder> childOrderList = threeNetsChildOrderService.formattedPhone(order.getMemberTels(), order.getId());
        List<ThreenetsChildOrder> childOrders = new ArrayList<>();
        //将子订单数据按照网段区分
        Map<Integer, List<ThreenetsChildOrder>> collect = childOrderList.stream().collect(Collectors.groupingBy(ThreenetsChildOrder::getOperator));
        int num = 1;
        for (Integer operator : collect.keySet()) {
            if (operator.equals(Const.OPERATORS_MOBILE)) {
                attached.setMiguPrice(Integer.parseInt(order.getMobilePay() != null ? order.getMobilePay() : order.getSpecialPrice()));
            }
            if (operator.equals(Const.OPERATORS_UNICOM)) {
                attached.setSwxlPrice(Integer.parseInt(order.getUmicomPay()));
            }
            ThreenetsRing ring = new ThreenetsRing();
            ring.setRingWay(order.getRingUrl());
            ring.setOperate(order.getOperate());
            ring.setOrderId(order.getId());
            ring.setRingContent(order.getRingContent());
            ring.setRingName(order.getRingName() + DateUtils.getTimeRadom());
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
                boolean flag = false;
                if (operator.equals(Const.OPERATORS_TELECOM)) {
                    flag = ConfigUtil.getAreaArray("unable_to_open_area",childOrder.getProvince());
                }
                if (flag){
                    childOrder.setRemark("电信当前不提供"+childOrder.getProvince()+"地区的服务");
                    return AjaxResult.error("电信当前不提供"+childOrder.getProvince()+"地区的服务");
                }
                childOrder.setRingId(ring.getId());
                childOrder.setRingName(ring.getRingName());
                childOrder.setIsVideoUser(ring.getRingType().equals("视频") ? true : false);
                childOrder.setIsRingtoneUser(ring.getRingType().equals("视频") ? false : true);
                childOrder.setPaymentType(Integer.parseInt(order.getPaymentType()));
                childOrders.add(childOrder);
            }
            num++;
        }
        //保存订单附表
        if (attached.getId() == null){
            threeNetsOrderAttachedService.save(attached);
        }
        threeNetsChildOrderService.batchChindOrder(childOrders);
        //保存线上
        threeNetsAsyncService.saveOnlineOrder(threenetsOrder, attached, order);
        return AjaxResult.success("保存成功！");
    }
}
