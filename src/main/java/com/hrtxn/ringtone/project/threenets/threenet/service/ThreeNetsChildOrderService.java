package com.hrtxn.ringtone.project.threenets.threenet.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.exception.NoLoginException;
import com.hrtxn.ringtone.common.utils.Const;
import com.hrtxn.ringtone.common.utils.DateUtils;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.common.utils.juhe.JuhePhoneUtils;
import com.hrtxn.ringtone.project.system.File.service.FileService;
import com.hrtxn.ringtone.project.system.json.JuhePhone;
import com.hrtxn.ringtone.project.system.json.JuhePhoneResult;
import com.hrtxn.ringtone.project.threenets.threenet.domain.*;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsChildOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsRingMapper;
import com.hrtxn.ringtone.project.threenets.threenet.utils.ApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

/**
 * Author:lile
 * Date:2019/7/11 15:45
 * Description:三网子订单业务层
 */
@Service
public class ThreeNetsChildOrderService {

    @Autowired
    private ThreenetsOrderMapper threenetsOrderMapper;

    @Autowired
    private ThreenetsChildOrderMapper threenetsChildOrderMapper;

    @Autowired
    private ThreenetsRingMapper threenetsRingMapper;

    @Autowired
    private ThreeNetsOrderAttachedService threeNetsOrderAttachedService;
    @Autowired
    private ThreeNetsAsyncService threeNetsAsyncService;

    @Autowired
    private FileService fileService;
    private ApiUtils apiUtils = new ApiUtils();

    /**
     * 根据订单id获取父级订单
     *
     * @param id
     * @return
     */
    public ThreenetsOrder getOrderById(Integer id) throws Exception {
        ThreenetsOrder order = threenetsOrderMapper.selectByPrimaryKey(id);
        threeNetsAsyncService.refreshTelecomMerchantInfo(order);
        return order;
    }

    /**
     * 获取总数
     *
     * @param request
     * @return
     */
    public Integer getCount(BaseRequest request) {
        return threenetsChildOrderMapper.getCount(getParameters(request));
    }

    /**
     * 修改子订单
     *
     * @param childOrder
     * @return
     */
    public Integer update(ThreenetsChildOrder childOrder) {
        return threenetsChildOrderMapper.updateThreeNetsChidOrder(childOrder);
    }

    /**
     * 获取代办列表
     *
     * @param page
     * @param request
     * @return
     * @throws Exception
     */
    public List<ThreenetsChildOrder> getChildOrderList(Page page, BaseRequest request) throws Exception {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        return threenetsChildOrderMapper.selectThreeNetsTaskList(page, getParameters(request));
    }

    /**
     * 获取无分页的代办列表
     *
     * @param request
     * @return
     * @throws Exception
     */
    public List<ThreenetsChildOrder> getChildOrder(BaseRequest request) throws Exception {
        return threenetsChildOrderMapper.selectThreeNetsTaskList(new Page(0, 100), getParameters(request));
    }

    /**
     * 参数格式化
     *
     * @param request
     * @return
     */
    private ThreenetsChildOrder getParameters(BaseRequest request) {
        ThreenetsChildOrder order = new ThreenetsChildOrder();
        if (StringUtils.isNotNull(request)) {
            if (request.getParentOrderId() != null) {
                order.setParentOrderId(request.getParentOrderId());
            } else {
                order.setParentOrderId(request.getId());
            }
            Integer userRole = ShiroUtils.getSysUser().getUserRole();
            if (userRole != 1) {
                order.setUserId(ShiroUtils.getSysUser().getId());
            }
            if (request.getTimeType() == null && request.getIsMonthly() == null) {
                return order;
            }
            if (!request.getTimeType().toString().equals("0")) {
                order.setEnd(DateUtils.formatNow());
                if ("1".equals(request.getTimeType().toString())) {//一天
                    order.setStart(DateUtils.getPastDate(1));
                } else if ("2".equals(request.getTimeType().toString())) {//一周
                    order.setStart(DateUtils.getPastDate(7));
                } else if ("3".equals(request.getTimeType().toString())) {//半个月
                    order.setStart(DateUtils.getPastDate(15));
                } else {//一个月
                    order.setStart(DateUtils.getPastDate(65));
                }
            }
            if (!request.getIsMonthly().toString().equals("0")) {
                order.setIsMonthly(request.getIsMonthly());
                if ("4".equals(request.getIsMonthly())) {
                    order.setIsMonthly(2);
                    order.setIsReplyMessage(true);
                }
            }
            if (StringUtils.isNotEmpty(request.getTelLinkPhone())) {
                order.setLinkmanTel(request.getTelLinkPhone());
            }
        }
        return order;
    }

    /**
     * 添加三网子订单
     *
     * @param threenetsChildOrder
     * @return
     */
    @Transactional
    public AjaxResult insterThreeNetsChildOrder(ThreenetsChildOrder threenetsChildOrder, BaseRequest request) throws Exception {
        if (!StringUtils.isNotNull(threenetsChildOrder)) {
            return AjaxResult.error("参数格式不正确");
        }
        List<ThreenetsChildOrder> list = formattedPhone(threenetsChildOrder.getMemberTels(), threenetsChildOrder.getParentOrderId());
        ThreeNetsOrderAttached attached = threeNetsOrderAttachedService.selectByParentOrderId(threenetsChildOrder.getParentOrderId());
        //克隆
        if (StringUtils.isEmpty(attached.getMiguId())) {
            attached.setMiguPrice(threenetsChildOrder.getMiguPrice() != null ? threenetsChildOrder.getMiguPrice() : threenetsChildOrder.getSpecialPrice());
        }
        if (StringUtils.isEmpty(attached.getSwxlId())) {
            attached.setSwxlPrice(threenetsChildOrder.getSwxlPrice());
        }
        threenetsChildOrderMapper.batchChindOrder(list);
        threeNetsAsyncService.saveThreenetsPhone(attached, request);
        return AjaxResult.success(true, "保存成功");
    }


    /**
     * 批量保存
     *
     * @param orders
     * @return
     */
    public Integer batchChindOrder(List<ThreenetsChildOrder> orders) {
        return threenetsChildOrderMapper.batchChindOrder(orders);
    }


    /**
     * 格式化手机号
     *
     * @param phones
     */
    public List<ThreenetsChildOrder> formattedPhone(String phones, Integer orderId) throws Exception {
        //换行符----
        String regNR = "\n\r";
        String regN = "\n";
        String regR = "\r";
        //将手机号转为数组
        phones = phones.replace(regNR, "br").replace(regR, "br").replace(regN, "br");
        String[] phoneArray = phones.split("br");

        List<ThreenetsChildOrder> list = new ArrayList<>();
        for (String tel : phoneArray) {
            tel = tel.replace(" ", "");
            ThreenetsChildOrder childOrder = new ThreenetsChildOrder();
            if (tel.isEmpty()) {
                continue;
            }
            childOrder.setLinkmanTel(tel);
            if (orderId != null) {
                childOrder.setParentOrderId(orderId);
            }
            childOrder = initChildOrder(childOrder);
            list.add(childOrder);
        }
        return list;
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
        //是否铃音用户
        childOrder.setIsRingtoneUser(false);
        //手机号验证
        JuhePhone juhePhone = JuhePhoneUtils.getPhone(childOrder.getLinkmanTel());
        JuhePhoneResult result = (JuhePhoneResult) juhePhone.getResult();
        //运营商
        childOrder.setOperator(JuhePhoneUtils.getOperate(result));
        childOrder.setProvince(result.getProvince());
        childOrder.setCity(result.getCity());
        //设置代理商
        childOrder.setUserId(ShiroUtils.getSysUser().getId());
        childOrder.setCreateDate(new Date());
        childOrder.setStatus(Const.PENDING_REVIEW);
        return childOrder;
    }

    /**
     * 获取近五日信息
     * 条件：当前登陆者下已包月子订单
     *
     * @return
     */
    public List<PlotBarPhone> getFiveData() {
        Integer id = ShiroUtils.getSysUser().getId();
        if (StringUtils.isNotNull(id)) {
            List<PlotBarPhone> fiveData = threenetsChildOrderMapper.getFiveData(id);
            ThreenetsChildOrder threenetsChildOrder = new ThreenetsChildOrder();
            threenetsChildOrder.setUserId(id);
            threenetsChildOrder.setIsMonthly(2);
            Integer count = threenetsChildOrderMapper.getCount(threenetsChildOrder);
            for (PlotBarPhone plotBarPhone : fiveData) {
                plotBarPhone.setCumulativeUser(count);
                count = count - plotBarPhone.getAddUser();
            }
            return fiveData;
        }
        return null;
    }

    /**
     * 删除子订单
     *
     * @param id
     * @return
     */
    @Transactional
    public AjaxResult delete(Integer id) {
        int sum = threenetsChildOrderMapper.deleteByPrimaryKey(id);
        if (sum > 0) {
            return AjaxResult.success(sum, "删除成功");
        } else {
            return AjaxResult.error("删除失败");
        }
    }

    /**
     * 获取号码信息
     *
     * @param type 标识是否是批量操作 1、批量操作/2、单个操作
     * @param data 数据 type为1时，data为父级订单ID；type为2时，data为子订单ID
     * @return
     */
    public AjaxResult getPhoneInfo(Integer type, Integer data) throws Exception {
        if (StringUtils.isNotNull(type) && StringUtils.isNotNull(data) && data > 0) {
            List<ThreenetsChildOrder> threenetsChildOrderList = new ArrayList<>();
            if (type == 1) { // 批量刷新操作 根据父级ID获取子订单
                ThreenetsChildOrder threenetsChildOrder = new ThreenetsChildOrder();
                threenetsChildOrder.setParentOrderId(data);
                threenetsChildOrderList = threenetsChildOrderMapper.selectThreeNetsTaskList(null, threenetsChildOrder);
            } else {
                // 根据ID查询子订单信息
                ThreenetsChildOrder threenetsChildOrder = threenetsChildOrderMapper.selectByPrimaryKey(data);
                threenetsChildOrderList.add(threenetsChildOrder);
            }
            if (threenetsChildOrderList.size() > 0) {
                return apiUtils.getPhoneInfo(threenetsChildOrderList);
            } else {
                return AjaxResult.success(false, "查询不到数据！");
            }
        }
        return AjaxResult.success(false, "请F5刷新页面后进项数据刷新！");
    }

    /**
     * 刷新视频彩铃
     *
     * @param id
     * @return
     * @throws Exception
     */
    public AjaxResult refreshVbrtStatus(Integer id) throws Exception {
        if (StringUtils.isNotNull(id) && id > 0) {
            // 根据ID查询子订单信息
            ThreenetsChildOrder threenetsChildOrder = threenetsChildOrderMapper.selectByPrimaryKey(id);
            if (StringUtils.isNotNull(threenetsChildOrder)) {
                return apiUtils.refreshVbrtStatus(threenetsChildOrder);
            } else {
                return AjaxResult.success(false, "查询不到数据！");
            }
        }
        return AjaxResult.success(false, "参数不正确！");
    }

    /**
     * @param type 标识是否是批量操作 1、批量操作/2、单个操作
     * @param flag 标识是否是下发链接短信 1、普通短信/2、链接短信
     * @param data 数据 type为1时，data为父级订单ID；type为2时，data为子订单ID
     * @return
     */
    public AjaxResult sendMessage(Integer type, Integer flag, Integer data) throws Exception {
        if (StringUtils.isNotNull(type) && StringUtils.isNotNull(flag) && StringUtils.isNotNull(data)) {
            // 根据type获取数据内容
            List<ThreenetsChildOrder> threenetsChildOrderList = new ArrayList<>();
            if (type == 1) {// 批量操作，应获取父级ID下所有子订单
                // 根据父级订单ID获取企业彩铃为未包月状态子订单
                ThreenetsChildOrder threenetsChildOrder = new ThreenetsChildOrder();
                threenetsChildOrder.setParentOrderId(data);
                threenetsChildOrder.setIsMonthly(1);
                threenetsChildOrderList = threenetsChildOrderMapper.selectThreeNetsTaskList(null, threenetsChildOrder);
            } else {
                // 单个操作，获取子订单信息
                ThreenetsChildOrder threenetsChildOrder = threenetsChildOrderMapper.selectByPrimaryKey(data);
                threenetsChildOrderList.add(threenetsChildOrder);
            }
            // 判断是否有数据
            if (threenetsChildOrderList.size() > 0) {
                return apiUtils.sendMessage(threenetsChildOrderList, flag);
            } else {
                return AjaxResult.success(false, "无数据！");
            }
        }
        return AjaxResult.success(false, "参数不正确！");
    }

    /**
     * 获取设置铃音子订单数据
     *
     * @param page
     * @param orderId
     * @param operate
     * @return
     * @throws Exception
     */
    public AjaxResult findChildOrderByOrderId(Page page, Integer orderId, Integer operate) throws Exception {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        if (StringUtils.isNotNull(page) && StringUtils.isNotNull(orderId) && StringUtils.isNotNull(operate)) {
            // 获取已包月子订单
            ThreenetsChildOrder threenetsChildOrder = new ThreenetsChildOrder();
            threenetsChildOrder.setParentOrderId(orderId);
            threenetsChildOrder.setIsMonthly(2);
            threenetsChildOrder.setOperator(operate);
            List<ThreenetsChildOrder> threenetsChildOrderList = threenetsChildOrderMapper.selectThreeNetsTaskList(page, threenetsChildOrder);
            // 获取总数
            Integer count = threenetsChildOrderMapper.getCount(threenetsChildOrder);
            if (threenetsChildOrderList.size() > 0) {
                return AjaxResult.success(threenetsChildOrderList, "获取数据成功！", count);
            } else {
                return AjaxResult.error("无已包月数据！");
            }
        }
        return AjaxResult.error("参数不正确！");
    }

    /**
     * 根据主键获取子订单信息
     *
     * @param id
     * @return
     * @throws Exception
     */
    public ThreenetsChildOrder selectByPrimaryKey(Integer id) throws Exception {
        return threenetsChildOrderMapper.selectByPrimaryKey(id);
    }

    /**
     * 号码管理设置铃音
     *
     * @param orderId
     * @param operate
     * @param ringId
     * @param childOrderId
     * @return
     * @throws Exception
     */
    public AjaxResult chidSetRing(Integer orderId, Integer operate, Integer ringId, Integer childOrderId) throws Exception {
        if (StringUtils.isNotNull(orderId) && StringUtils.isNotNull(ringId) && StringUtils.isNotNull(childOrderId)) {
            // 根据子订单ID获取子订单信息
            ThreenetsChildOrder threenetsChildOrder = threenetsChildOrderMapper.selectByPrimaryKey(childOrderId);
            // 根据铃音ID获取铃音信息
            ThreenetsRing threenetsRing = threenetsRingMapper.selectByPrimaryKey(ringId);
            if (StringUtils.isNotNull(threenetsChildOrder) && StringUtils.isNotNull(threenetsRing)) {
                return apiUtils.setRing(threenetsChildOrder.getLinkmanTel(), threenetsRing, operate, orderId);
            }
            return AjaxResult.error("获取数据出错！");
        }
        return AjaxResult.error("参数格式不正确！");
    }

    /**
     * 移动工具箱-->用户信息
     *
     * @param ringMsisdn
     * @return
     */
    public AjaxResult getUserInfoByRingMsisdn(String ringMsisdn) throws Exception {
        if (StringUtils.isNotNull(ringMsisdn)) {
            return apiUtils.getUserInfoByRingMsisdn(ringMsisdn);
        }
        return AjaxResult.success(false, "参数不正确！");
    }

    /**
     * 移动工具箱-->删除铃音-->搜索
     *
     * @param msisdn
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public AjaxResult findRingInfoByMsisdn(String msisdn) throws NoLoginException, IOException {
        if (StringUtils.isNotNull(msisdn)) {
            return apiUtils.findRingInfoByMsisdn(msisdn);
        }
        return AjaxResult.success(false, "参数不正确");
    }

    /**
     * 移动工具箱-->删除铃音-->删除个人铃音设置
     *
     * @param msisdn
     * @param settingID
     * @param toneID
     * @param type
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public AjaxResult singleDeleteRingSet(String msisdn, String settingID, String toneID, String type) throws NoLoginException, IOException {
        if (StringUtils.isNotNull(msisdn) && StringUtils.isNotNull(settingID) &&
                StringUtils.isNotNull(toneID) && StringUtils.isNotNull(type)) {
            return apiUtils.singleDeleteRingSet(msisdn, settingID, toneID, type);
        }
        return AjaxResult.success(false, "参数不正确");
    }

    /**
     * 移动工具箱-->删除铃音-->删除个人铃音库
     *
     * @param msisdn
     * @param toneIds
     * @param type
     * @return
     */
    public AjaxResult singleDeleteRing(String msisdn, String toneIds, String type) throws NoLoginException, IOException {
        if (StringUtils.isNotNull(msisdn) && StringUtils.isNotNull(toneIds) && StringUtils.isNotNull(type)) {
            return apiUtils.singleDeleteRing(msisdn, toneIds, type);
        }
        return AjaxResult.success(false, "参数不正确！");
    }

    /**
     * 批量删除个人铃音设置
     *
     * @param msisdn
     * @param vals
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public AjaxResult batchDeleteRingSet(String msisdn, String vals) throws NoLoginException, IOException {
        if (StringUtils.isNotNull(msisdn) && StringUtils.isNotNull(vals)) {
            return apiUtils.batchDeleteRingSet(msisdn, vals);
        }
        return AjaxResult.success(false, "参数不正确");
    }

    /**
     * 批量删除个人铃音库
     *
     * @param msisdn
     * @param vals
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public AjaxResult batchDeleteRing(String msisdn, String vals) throws NoLoginException, IOException {
        if (StringUtils.isNotNull(msisdn) && StringUtils.isNotNull(vals)) {
            return apiUtils.batchDeleteRing(msisdn, vals);
        }
        return AjaxResult.success(false, "参数不正确");
    }

    /**
     * 联通工具箱-->用户信息
     *
     * @param phoneNumber
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public AjaxResult getUnicomUserInfoByPhoneNumber(String phoneNumber) throws NoLoginException, IOException {
        if (StringUtils.isNotNull(phoneNumber)) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("silentMemberByMsisdn", apiUtils.getSilentMemberByMsisdn(phoneNumber));
            map.put("systemLogListByMsisdn", apiUtils.getSystemLogListByMsisdn(phoneNumber));
            return AjaxResult.success(map, "查找到了");
        }
        return AjaxResult.success(false, "参数不正确");
    }

    /**
     * 联通工具箱-->用户信息-->删除某条用户信息
     *
     * @param msisdn
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public AjaxResult deleteSilentMemberByMsisdn(String msisdn) throws NoLoginException, IOException {
        if (StringUtils.isNotNull(msisdn)) {
            return apiUtils.deleteSilentMemberByMsisdn(msisdn);
        }
        return AjaxResult.success(false, "参数不正确");
    }

    /**
     * 管理端首页--根据条件获取子账号数量
     *
     * @param operate
     * @param isMonthly
     * @return
     */
    public Integer getPhoneCount(Integer operate, Integer isMonthly) {
        return threenetsChildOrderMapper.getPhoneCount(operate, isMonthly);
    }

    /**
     * 刷新彩铃开通状态
     *
     * @param id
     * @return
     * @throws Exception
     */
    public AjaxResult refreshUserStatus(String type, Integer id) throws Exception {
        if (StringUtils.isNull(id) || id == 0) {
            return AjaxResult.error("查询不到数据！");
        }
        if (type.equals(Const.UPDATE_STATUS_ALL)) {
            ThreenetsChildOrder threenetsChildOrder = new ThreenetsChildOrder();
            threenetsChildOrder.setParentOrderId(id);
            List<ThreenetsChildOrder> childOrders = threenetsChildOrderMapper.listByParamNoPage(threenetsChildOrder);
            for (int i = 0; i < childOrders.size(); i++) {
                ThreenetsChildOrder childOrder = childOrders.get(i);
                childOrder = apiUtils.refreshAloneStatus(childOrder);
                threenetsChildOrderMapper.updateThreeNetsChidOrder(childOrder);
            }
            return AjaxResult.success("更新完成！");
        } else {
            //查询单独
            ThreenetsChildOrder threenetsChildOrder = threenetsChildOrderMapper.selectByPrimaryKey(id);
            if (threenetsChildOrder == null){
                return AjaxResult.error("查询不到数据！");
            }
            if (type.equals(Const.UPDATE_STATUS_RING)) {
                threenetsChildOrder = apiUtils.refreshRingStatus(threenetsChildOrder);
            }
            if (type.equals(Const.UPDATE_STATUS_VIDEO_RING)) {
                threenetsChildOrder = apiUtils.refreshVideoRingStatus(threenetsChildOrder);
            }
            if (type.equals(Const.UPDATE_STATUS_MONTHLY)) {
                threenetsChildOrder = apiUtils.refreshMonthlyStatus(threenetsChildOrder);
            }
            if (type.equals(Const.UPDATE_STATUS_ALONE)) {
                threenetsChildOrder = apiUtils.refreshAloneStatus(threenetsChildOrder);
            }
            threenetsChildOrderMapper.updateThreeNetsChidOrder(threenetsChildOrder);
            return AjaxResult.success("更新完成！");
        }
    }

    /**
     * 免短商户开通业务
     *
     * @param id
     * @return
     * @throws Exception
     */
    public AjaxResult openingBusiness(Integer id) throws Exception{
        if (StringUtils.isNull(id) || id == 0) {
            return AjaxResult.error("查询不到数据！");
        }
        ThreenetsChildOrder threenetsChildOrder = threenetsChildOrderMapper.selectByPrimaryKey(id);
        if (threenetsChildOrder == null){
            return AjaxResult.error("查询不到数据！");
        }
        return apiUtils.openingBusiness(threenetsChildOrder);
    }
}
