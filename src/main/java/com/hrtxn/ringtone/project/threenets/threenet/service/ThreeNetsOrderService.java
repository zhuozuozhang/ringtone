package com.hrtxn.ringtone.project.threenets.threenet.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.OrderRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.juhe.JuhePhoneUtils;
import com.hrtxn.ringtone.project.system.File.domain.Uploadfile;
import com.hrtxn.ringtone.project.system.File.service.FileService;
import com.hrtxn.ringtone.project.system.json.JuhePhone;
import com.hrtxn.ringtone.project.system.json.JuhePhoneResult;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsChildOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsRing;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.utils.ApiUtils;
import lombok.Synchronized;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        //删除铃音
        threeNetsRingService.delete(id);
        //删除子订单
        threeNetsChildOrderService.delete(id);
        //删除订单
        int sum = threenetsOrderMapper.deleteByPrimaryKey(id);
        if (sum > 0) {
            return AjaxResult.success(sum, "删除成功");
        } else {
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
     * 初始化订单
     *
     * @param order
     * @return
     */
    @Transactional
    public AjaxResult init(ThreenetsOrder order) throws Exception {
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
        //验证订单是否修改
        ThreenetsOrder threenetsOrder = threenetsOrderMapper.selectByPrimaryKey(order.getId());
        if (!threenetsOrder.getLinkmanTel().equals(order.getLinkmanTel())) {
            //如果更改手机号则更改手机号归属地
            threenetsOrder = JuhePhoneUtils.getPhone(threenetsOrder);
//            if (threenetsOrder.getOperator() == 1) {
//                threenetsOrder.setPaymentPrice(Integer.parseInt(order.getMobilePay() != null ? order.getMobilePay() : order.getSpecialPrice()));
//            } else if (threenetsOrder.getOperator() == 3) {
//                threenetsOrder.setPaymentPrice(Integer.parseInt(order.getUmicomPay()));
//            }
        }
        //保存本地
        BeanUtils.copyProperties(threenetsOrder, order);
        threenetsOrderMapper.updateByPrimaryKey(threenetsOrder);

        //查询铃音是否存在
        List<ThreenetsRing> rings = threeNetsRingService.listByOrderId(order.getId());
        if (order.getMemberTels().indexOf(order.getLinkmanTel()) < 0) {
            String tels = order.getMemberTels();
            tels = tels + '\n' + order.getLinkmanTel();
            order.setMemberTels(tels);
        }
        //子订单手机号验证
        List<ThreenetsChildOrder> childOrderList = threeNetsChildOrderService.formattedPhone(order.getMemberTels(), order.getId());
        for (int i = 0; i < childOrderList.size(); i++) {
            ThreenetsChildOrder childOrder = childOrderList.get(i);
            ThreenetsRing ring = getRingByOperate(rings, order, childOrder.getOperator());
            childOrder.setRingId(ring.getId());
            childOrder.setIsVideoUser(ring.getRingType().equals("视频") ? true : false);
            //保存子订单
            childOrderList.add(i, childOrder);
        }
        //批量保存
        threeNetsChildOrderService.batchChindOrder(childOrderList);
        return AjaxResult.success(threenetsOrder, "保存成功！");
    }

    /**
     * 转存铃音
     *
     * @param rings
     * @param order
     * @param operate
     * @return
     */
    private ThreenetsRing getRingByOperate(List<ThreenetsRing> rings, OrderRequest order, Integer operate) {
        if (rings.isEmpty()) {
            ThreenetsRing ring = initRing(order);
            rings.add(ring);
            return ring;
        } else if (rings.size() == 1) {
            if (rings.get(0).getOperate() == operate) {
                return rings.get(0);
            } else {
                ThreenetsRing ring = initRing(order);
                rings.add(ring);
                return ring;
            }
        } else {
            Map<Integer, List<ThreenetsRing>> collect = rings.stream().collect(Collectors.groupingBy(ThreenetsRing::getOperate));
            List<ThreenetsRing> threenetsRings = collect.get(operate);
            if (threenetsRings != null && !threenetsRings.isEmpty()) {
                return threenetsRings.get(0);
            } else {
                ThreenetsRing ring = initRing(order);
                rings.add(ring);
                return ring;
            }
        }
    }


    /**
     * 添加新铃音
     *
     * @param order
     * @return
     */
    private ThreenetsRing initRing(OrderRequest order) {
        ThreenetsRing ring = new ThreenetsRing();
        ring.setRingWay(order.getRingUrl());
        ring.setOperate(order.getOperate());
        ring.setOrderId(order.getId());
        ring.setRingContent(order.getRingContent());
        ring.setRingName(order.getRingName());
        //保存铃音
        return threeNetsRingService.save(ring);
    }

    /**
     * 三网保存订单
     *
     * @param order
     */
    @Synchronized
    private void saveOnlineOrder(ThreenetsOrder order,ThreenetsRing ring) {
        ApiUtils utils = new ApiUtils();
        switch (order.getOperator()) {
            //移动
            case 1:
                utils.saveOrderByYd(order);
                break;
            //电信
            case 2:
                break;
            //联通
            case 3:
                List<Uploadfile> list = fileService.listUploadfile(order.getId());
                utils.saveOrderByLt(order,list,ring);
                break;
            default:
                break;
        }
    }

    /**
     * 三网保存子订单
     *
     * @param childOrder
     */
    private void saveOnlineChildOrder(ThreenetsChildOrder childOrder) {

    }

}
