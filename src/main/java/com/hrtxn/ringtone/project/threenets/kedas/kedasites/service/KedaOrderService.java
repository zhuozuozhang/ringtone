package com.hrtxn.ringtone.project.threenets.kedas.kedasites.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.constant.Constant;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.*;
import com.hrtxn.ringtone.common.utils.juhe.JuhePhoneUtils;
import com.hrtxn.ringtone.freemark.config.systemConfig.RingtoneConfig;
import com.hrtxn.ringtone.project.system.json.JuhePhone;
import com.hrtxn.ringtone.project.system.json.JuhePhoneResult;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaChildOrder;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaOrder;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaRing;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.mapper.KedaChildOrderMapper;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.mapper.KedaOrderMapper;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.mapper.KedaRingMapper;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreeNetsOrderAttached;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.Reactor;
import reactor.event.Event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author:zcy
 * Date:2019-08-14 13:55
 * Description:疑难杂单 父级订单业务处理层
 */
@Service
@Slf4j
public class KedaOrderService {

    @Autowired
    private KedaOrderMapper kedaOrderMapper;
    @Autowired
    private KedaChildOrderMapper kedaChildOrderMapper;
    @Autowired
    private KedaRingMapper kedaRingMapper;
    @Autowired
    @Qualifier("createReactor")//同样指定并注入
            Reactor r;

    /**
     * 获取父级订单列表
     *
     * @param page
     * @param baseRequest
     * @return
     */
    public AjaxResult getKeDaOrderList(Page page, BaseRequest baseRequest) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        //是否为管理员
        Integer userRole = ShiroUtils.getSysUser().getUserRole();
        if (userRole != 1) {
            baseRequest.setUserId(ShiroUtils.getSysUser().getId());
        }
        // 获取数据
        List<KedaOrder> kedaOrderList = kedaOrderMapper.getKeDaOrderList(page, baseRequest);
        // 获取数量
        int count = kedaOrderMapper.getCount(baseRequest);
        if (kedaOrderList.size() > 0) {
            for (KedaOrder kedaOrder : kedaOrderList) {
                kedaOrder.setPeopleNum(kedaOrder.getChildOrderQuantity() + "/" + kedaOrder.getChildOrderQuantityByMonthly());
            }
            return AjaxResult.success(kedaOrderList, "获取数据成功！", count);
        }
        return AjaxResult.success(false, "无数据！");
    }

    /**
     * 修改父级订单信息
     *
     * @param kedaOrder
     * @return
     */
    public AjaxResult updateCompanyName(KedaOrder kedaOrder) {
        if (StringUtils.isNotNull(kedaOrder) && StringUtils.isNotNull(kedaOrder.getId()) && StringUtils.isNotEmpty(kedaOrder.getCompanyName())) {
            // 根据商户名称查找父级订单信息
            BaseRequest baseRequest = new BaseRequest();
            baseRequest.setCompanyName(kedaOrder.getCompanyName());
            List<KedaOrder> kedaOrderList = kedaOrderMapper.getKeDaOrderList(null, baseRequest);
            if (kedaOrderList.size() > 0) {
                return AjaxResult.error("商户名称重复！");
            }
            // 执行修改商户名操作
            int count = kedaOrderMapper.updateKedaOrder(kedaOrder);
            if (count > 0) {
                return AjaxResult.success(true, "修改成功！");
            } else {
                return AjaxResult.error("修改失败！");
            }
        }
        return AjaxResult.error("参数格式错误！");
    }

    /**
     * 添加父級訂單
     *
     * @param kedaOrder
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult addKedaOrder(KedaOrder kedaOrder) throws Exception {
        if (!StringUtils.isNotNull(kedaOrder)) return AjaxResult.error("参数格式不正确!");
        if (!StringUtils.isNotEmpty(kedaOrder.getCompanyName())) return AjaxResult.error("参数格式不正确!");
        if (!StringUtils.isNotEmpty(kedaOrder.getLinkMan())) return AjaxResult.error("参数格式不正确!");
        if (!StringUtils.isNotEmpty(kedaOrder.getLinkTel())) return AjaxResult.error("参数格式不正确!");

        // 查重
        BaseRequest baseRequest = new BaseRequest();

        baseRequest.setCompanyName(kedaOrder.getCompanyName());
        List<KedaOrder> companyName = kedaOrderMapper.getKeDaOrderList(null, baseRequest);
        if (companyName.size() > 0) return AjaxResult.error("集团名称已被使用！");

        baseRequest.setCompanyName(null);
        baseRequest.setTel(kedaOrder.getLinkTel());
        List<KedaOrder> linkTel = kedaOrderMapper.getKeDaOrderList(null, baseRequest);
        if (linkTel.size() > 0) return AjaxResult.error("联系电话已被使用！");

        // 添加父级订单
        kedaOrder.setCerateTime(new Date());
        kedaOrder.setUserId(ShiroUtils.getSysUser().getId());
        // 根据电话号码获取省市
        JuhePhone<JuhePhoneResult> phone = JuhePhoneUtils.getPhone(kedaOrder.getLinkTel());
        KedaChildOrder kedaChildOrder = new KedaChildOrder();
        if ("200".equals(phone.getResultcode())) {
            JuhePhoneResult result = phone.getResult();
            kedaOrder.setProvince(result.getProvince());
            kedaOrder.setCity(result.getCity());
            if ("移动".equals(result.getCompany())) {
                kedaChildOrder.setOperate(1);
            } else if ("电信".equals(result.getCompany())) {
                kedaChildOrder.setOperate(2);
            } else {
                kedaChildOrder.setOperate(3);
            }
        }
        int count = kedaOrderMapper.insertKedaOrder(kedaOrder);
        log.info("疑难杂单创建父级订单---->" + count);
        // 添加子级订单
        kedaChildOrder.setProvince(kedaOrder.getProvince());
        kedaChildOrder.setCity(kedaOrder.getCity());
        kedaChildOrder.setCreateTime(new Date());
        kedaChildOrder.setIsMonthly(0);
        kedaChildOrder.setIsRingtoneUser(1);
        kedaChildOrder.setLinkMan(kedaOrder.getLinkMan());
        kedaChildOrder.setLinkTel(kedaOrder.getLinkTel());
        kedaChildOrder.setOrderId(kedaOrder.getId());
        kedaChildOrder.setUserId(ShiroUtils.getSysUser().getId());
        kedaChildOrder.setOperateId(Constant.OPERATEID);
        kedaChildOrder.setStatus(Const.KEDA_UNDER_REVIEW);
        int insert = kedaChildOrderMapper.insertKedaChildOrder(kedaChildOrder);
        log.info("添加疑难杂单子级订单---->" + insert);
        // 同步数据
        r.notify("insertKedaorder", Event.wrap(kedaChildOrder));
        return AjaxResult.success("添加成功！");
    }


    /**
     * 添加父級訂單
     *
     * @param kedaOrder
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult addKedaOrderNew(KedaOrder kedaOrder) {
        if (!StringUtils.isNotNull(kedaOrder)) return AjaxResult.error("参数格式不正确!");
        if (!StringUtils.isNotEmpty(kedaOrder.getCompanyName())) return AjaxResult.error("参数格式不正确!");
        if (!StringUtils.isNotEmpty(kedaOrder.getLinkMan())) return AjaxResult.error("参数格式不正确!");
        String[] phone = kedaOrder.getTels().split(",");
        List<KedaChildOrder> childOrders = new ArrayList<>();
        for (String p : phone) {
            p = p.replace(" ", "");
            //首先验证是固定电话还是手机号
            JuhePhone<JuhePhoneResult> juhePhone = JuhePhoneUtils.getPhone(p);
            KedaChildOrder kedaChildOrder = new KedaChildOrder();
            if ("200".equals(juhePhone.getResultcode())) {
                JuhePhoneResult result = juhePhone.getResult();
                kedaChildOrder.setProvince(result.getProvince());
                kedaChildOrder.setCity(result.getCity());
                if ("移动".equals(result.getCompany())) {
                    kedaChildOrder.setOperate(1);
                } else if ("电信".equals(result.getCompany())) {
                    kedaChildOrder.setOperate(2);
                } else {
                    kedaChildOrder.setOperate(3);
                }
            }
            kedaChildOrder.setLinkTel(p);
            kedaChildOrder.setLinkMan(p);
            childOrders.add(kedaChildOrder);
        }
        // 添加父级订单
        kedaOrder.setCerateTime(new Date());
        kedaOrder.setUserId(ShiroUtils.getSysUser().getId());
        kedaOrder.setLinkTel(childOrders.get(0).getLinkTel());
        kedaOrder.setProvince(childOrders.get(0).getProvince());
        kedaOrder.setCity(childOrders.get(0).getCity());
        int count = kedaOrderMapper.insertKedaOrder(kedaOrder);
        initChildOrder(childOrders, kedaOrder.getId());
        log.info("疑难杂单创建父级订单---->" + count);
        // 同步数据
        r.notify("addOrderInfo", Event.wrap(kedaOrder));
        return AjaxResult.success("添加成功！");
    }

    /**
     * 疑难杂单父级订单删除
     *
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteKedaOrder(Integer id) {
        if (!StringUtils.isNotNull(id) || id <= 0) return AjaxResult.error("参数格式不正确！");
        // 批量删除铃音
        // 根据父级订单查询铃音
        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setOrderId(id);
        List<KedaRing> kedaRingList = kedaRingMapper.getKedaRingList(null, baseRequest);
        if (kedaRingList.size() > 0) {
            int[] ringIds = new int[kedaRingList.size()];
            for (int i = 0; i < kedaRingList.size(); i++) {
                // 删除铃音文件
                FileUtil.deleteFile(RingtoneConfig.getProfile() + kedaRingList.get(i).getRingPath());
                // 获取需要删除的铃音ID
                ringIds[i] = kedaRingList.get(i).getId();
            }
            // 执行批量删除铃音操作
            int kedaRing = kedaRingMapper.deletePlKedaRing(ringIds);
            log.info("刪除疑难杂单铃音---->" + kedaRing);
        }
        // 批量删除子级订单
        List<KedaChildOrder> keDaChildOrderBacklogList = kedaChildOrderMapper.getKeDaChildOrderBacklogList(null, baseRequest);
        if (keDaChildOrderBacklogList.size() > 0) {
            int[] keDaChildOrderIds = new int[keDaChildOrderBacklogList.size()];
            for (int i = 0; i < keDaChildOrderBacklogList.size(); i++) {
                keDaChildOrderIds[i] = keDaChildOrderBacklogList.get(i).getId();
            }
            // 执行批量删除子级订单操作
            int kedaChilOrder = kedaChildOrderMapper.deletePlKedaChilOrder(keDaChildOrderIds);
            log.info("刪除疑难杂单子级订单---->" + kedaChilOrder);
        }
        // 删除父级订单
        int count = kedaOrderMapper.deleteKedaOrder(id);
        log.info("删除疑难杂单父级订单---->" + count);
        return AjaxResult.success(true, "删除成功！");
    }

    /**
     * 验证商户名称是否存在
     *
     * @param companyName
     * @return
     */
    public Boolean isItRedundantByName(String companyName) {
        KedaOrder order = new KedaOrder();
        order.setCompanyName(companyName);
        List<KedaOrder> kedaOrders = kedaOrderMapper.liseOrderNoPage(order);
        return kedaOrders.size() > 0 ? true : false;
    }

    /**
     * 格式化手机号
     *
     * @param phones
     * @return
     * @throws Exception
     */
    public AjaxResult formatPhoneNumber(String phones, Integer orderId) {
        String[] phone = phones.split(",");
        int numberOfTele = 0;
        for (String p : phone) {
            p = p.replace(" ", "");
            //首先验证是固定电话还是手机号
            boolean mobileNO = PhoneUtils.isMobileNO(p);
            boolean fixedPhone = PhoneUtils.isFixedPhone(p);
            if (!mobileNO && !fixedPhone) {
                return AjaxResult.error("#号码" + p + "不正确！");
            }
            BaseRequest param = new BaseRequest();
            param.setTel(p);
            Integer count = kedaChildOrderMapper.getCount(param);
            if (count > 0) {
                return AjaxResult.error("#号码" + p + "重复！");
            }
            JuhePhone juhePhone = JuhePhoneUtils.getPhone(p);
            JuhePhoneResult result = (JuhePhoneResult) juhePhone.getResult();
            if (result.getCompany().equals("电信")) {
                numberOfTele = numberOfTele + 1;
            }
        }
        if (orderId != null && numberOfTele > 0) {
            KedaOrder kedaOrder = kedaOrderMapper.getKedaOrder(orderId);
            if (StringUtils.isNotEmpty(kedaOrder.getProtocolTelecom10()) || StringUtils.isNotEmpty(kedaOrder.getProtocolTelecom20())) {
                numberOfTele = 0;
            }
        }
        return AjaxResult.success(numberOfTele, "匹配成功");
    }

    public void initChildOrder(List<KedaChildOrder> list, Integer oderId) {
        for (int i = 0; i < list.size(); i++) {
            KedaChildOrder kedaChildOrder = list.get(i);
            kedaChildOrder.setCreateTime(new Date());
            kedaChildOrder.setIsMonthly(9);
            kedaChildOrder.setIsRingtoneUser(1);
            kedaChildOrder.setOrderId(oderId);
            kedaChildOrder.setUserId(ShiroUtils.getSysUser().getId());
            kedaChildOrder.setOperateId(Constant.OPERATEID);
            kedaChildOrder.setStatus(Const.KEDA_UNDER_REVIEW);
            kedaChildOrderMapper.insertKedaChildOrder(kedaChildOrder);
        }
    }

    /**
     * 更新id和状态
     *
     * @param id
     * @param name
     */
    public void updateBusinessStatus(Integer id, String name) {
        KedaOrder kedaOrder = new KedaOrder();
        kedaOrder.setId(id);
        kedaOrder.setCompanyName(name);
        r.notify("uploadOrderInfo", Event.wrap(kedaOrder));
    }

    /**
     * 修改商户信息
     *
     * @param order
     * @return
     */
    public AjaxResult updateKedaOrderInfo(KedaOrder order) {
        r.notify("updateOrderInfo", Event.wrap(order));
        return AjaxResult.error();
    }
}

