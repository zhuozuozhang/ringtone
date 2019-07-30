package com.hrtxn.ringtone.project.threenets.threenet.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.DateUtils;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.common.utils.juhe.JuhePhoneUtils;
import com.hrtxn.ringtone.project.system.json.JuhePhone;
import com.hrtxn.ringtone.project.system.json.JuhePhoneResult;
import com.hrtxn.ringtone.project.threenets.threenet.domain.PlotBarPhone;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsChildOrder;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsChildOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.utils.ApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author:lile
 * Date:2019/7/11 15:45
 * Description:三网子订单业务层
 */
@Service
public class ThreeNetsChildOrderService {

    @Autowired
    private ThreenetsChildOrderMapper threenetsChildOrderMapper;
    private ApiUtils apiUtils = new ApiUtils();

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
     * 参数格式化
     *
     * @param request
     * @return
     */
    private ThreenetsChildOrder getParameters(BaseRequest request) {
        ThreenetsChildOrder order = new ThreenetsChildOrder();
        order.setParentOrderId(request.getId());
        Integer userRole = ShiroUtils.getSysUser().getUserRole();
        if (userRole != 1) {
            order.setUserId(ShiroUtils.getSysUser().getId());
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
        return order;
    }

    /**
     * 添加三网子订单
     *
     * @param threenetsChildOrder
     * @return
     */
    @Transactional
    public AjaxResult insterThreeNetsChildOrder(ThreenetsChildOrder threenetsChildOrder)throws Exception {
        if (!StringUtils.isNotNull(threenetsChildOrder)) {
            return AjaxResult.error("参数格式不正确");
        }
        List<ThreenetsChildOrder> list = formattedPhone(threenetsChildOrder.getMemberTels(),null);
        Integer count = batchChindOrder(list);
        //int count = threenetsChildOrderMapper.insertThreeNetsChildOrder(threenetsChildOrder);
        if (count > 0) {
            return AjaxResult.success(true, "添加成功");
        }
        return AjaxResult.error("执行出错");
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
    public List<ThreenetsChildOrder> formattedPhone(String phones,Integer orderId) throws Exception {
        //换行符----
        String regR = "\n\r";
        String regN = "\n";
        //将手机号转为数组
        phones = phones.replace(regR, "br").replace(regN, "br");
        String[] phoneArray = phones.split("br");

        List<ThreenetsChildOrder> list = new ArrayList<>();
        for (String tel : phoneArray) {
            ThreenetsChildOrder childOrder = new ThreenetsChildOrder();
            childOrder.setLinkmanTel(tel);
            if (orderId != null || orderId != 0){
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
        //childOrder.setStatus("审核通过");
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
    public AjaxResult getPhoneInfo(Integer type,Integer data) throws Exception {
        if ( StringUtils.isNotNull(type) && StringUtils.isNotNull(data) && data > 0) {
            List<ThreenetsChildOrder> threenetsChildOrderList = new ArrayList<>();
            if (type == 1){ // 批量刷新操作 根据父级ID获取子订单
                ThreenetsChildOrder threenetsChildOrder = new ThreenetsChildOrder();
                threenetsChildOrder.setParentOrderId(data);
                threenetsChildOrderList = threenetsChildOrderMapper.selectThreeNetsTaskList(null,threenetsChildOrder);
            }else {
                // 根据ID查询子订单信息
                ThreenetsChildOrder threenetsChildOrder = threenetsChildOrderMapper.selectByPrimaryKey(data);
                threenetsChildOrderList.add(threenetsChildOrder);
            }
            if (threenetsChildOrderList.size() > 0){
                return apiUtils.getPhoneInfo(threenetsChildOrderList);
            }else {
                return AjaxResult.success(false,"查询不到数据！");
            }
        }
        return AjaxResult.success(false, "参数不正确！");
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
        if (StringUtils.isNotNull(type) && StringUtils.isNotNull(flag) && StringUtils.isNotNull(data)){
            // 根据type获取数据内容
            List<ThreenetsChildOrder> threenetsChildOrderList = new ArrayList<>();
            if (type == 1){// 批量操作，应获取父级ID下所有子订单
                // 根据父级订单ID获取企业彩铃为未包月状态子订单
                ThreenetsChildOrder threenetsChildOrder = new ThreenetsChildOrder();
                threenetsChildOrder.setParentOrderId(data);
                threenetsChildOrder.setIsMonthly(1);
                threenetsChildOrderList = threenetsChildOrderMapper.selectThreeNetsTaskList(null,threenetsChildOrder);
            }else{
                // 单个操作，获取子订单信息
                ThreenetsChildOrder threenetsChildOrder = threenetsChildOrderMapper.selectByPrimaryKey(data);
                threenetsChildOrderList.add(threenetsChildOrder);
            }
            // 判断是否有数据
            if (threenetsChildOrderList.size() > 0){
                return apiUtils.sendMessage(threenetsChildOrderList, flag);
            }else {
                return AjaxResult.success(false,"无数据！");
            }
        }
        return AjaxResult.success(false,"参数不正确！");
    }
}
