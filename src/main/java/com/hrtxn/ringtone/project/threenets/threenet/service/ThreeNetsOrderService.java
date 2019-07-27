package com.hrtxn.ringtone.project.threenets.threenet.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.OrderRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.juhe.JuhePhoneUtils;
import com.hrtxn.ringtone.project.system.json.JuhePhone;
import com.hrtxn.ringtone.project.system.json.JuhePhoneResult;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsChildOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsRing;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsChildOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsOrderMapper;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private ThreenetsChildOrderMapper threenetsChildOrderMapper;
    @Autowired
    private ThreeNetsRingService threeNetsRingService;

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
        threenetsChildOrderMapper.deleteByParadeOrderId(id);
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
    public AjaxResult save(OrderRequest order) throws Exception {
        //验证订单是否修改
        ThreenetsOrder threenetsOrder = threenetsOrderMapper.selectByPrimaryKey(order.getId());
        if (!threenetsOrder.getLinkmanTel().equals(order.getLinkmanTel())) {
            //如果更改手机号则更改手机号归属地
        }
        //保存本地
        BeanUtils.copyProperties(threenetsOrder, order);
        threenetsOrderMapper.updateByPrimaryKey(threenetsOrder);

        //查询铃音是否存在
        List<ThreenetsRing> rings = threeNetsRingService.listByOrderId(order.getId());

        //子订单手机号验证
        List<ThreenetsChildOrder> childOrderList = new ArrayList<>();
        String regR = "\n\r";
        String regN = "\n";
        String phones = order.getMemberTels().replace(regR, "br").replace(regN, "br");
        String[] phone = phones.split("br");
        //保存子订单
        for (String tel : phone) {
            ThreenetsChildOrder childOrder = new ThreenetsChildOrder();
            childOrder.setLinkmanTel(tel);
            childOrder.setParentOrderId(order.getId());
            childOrder = initChildOrder(childOrder);
            order.setOperate(childOrder.getOperate());
            //如果铃音为空则添加新铃音，如果不为空则添加对应铃音，文件指向同一个位置
            ThreenetsRing ring = getRingByOperate(rings, order, childOrder.getOperate());
            childOrder.setRingId(ring.getId());
            childOrder.setIsVideoUser(ring.getRingType().equals("视频") ? true : false);
            //保存子订单
            threenetsChildOrderMapper.insertThreeNetsChildOrder(childOrder);
            childOrderList.add(childOrder);
        }
        return AjaxResult.success(threenetsOrder, "保存成功！");
    }

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
     * 初始化子订单
     *
     * @param childOrder
     * @return
     * @throws Exception
     */
    private ThreenetsChildOrder initChildOrder(ThreenetsChildOrder childOrder) throws Exception {
        //子订单用户名
        childOrder.setLinkman(childOrder.getLinkmanTel());
        //未包月
        childOrder.setIsMonthly(1);
        //未回复短信
        childOrder.setIsReplyMessage(false);
        //手机号验证
        JuhePhone juhePhone = JuhePhoneUtils.getPhone(childOrder.getLinkmanTel());
        JuhePhoneResult result = (JuhePhoneResult) juhePhone.getResult();
        //运营商
        childOrder.setOperate(JuhePhoneUtils.getOperate(result));
        childOrder.setProvince(result.getProvince());
        childOrder.setCity(result.getCity());
        //设置代理商
        childOrder.setUserId(ShiroUtils.getSysUser().getId());
        childOrder.setCreateDate(new Date());
        childOrder.setStatus("审核通过");
        return childOrder;
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
    private void saveOnlineOrder(ThreenetsOrder order) {

    }

    /**
     * 三网保存子订单
     *
     * @param childOrder
     */
    private void saveOnlineChildOrder(ThreenetsChildOrder childOrder) {

    }

}
