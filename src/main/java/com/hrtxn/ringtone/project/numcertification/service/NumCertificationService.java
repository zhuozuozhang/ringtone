package com.hrtxn.ringtone.project.numcertification.service;

import com.hrtxn.ringtone.common.api.NumApi;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.numcertification.domain.NumOrder;
import com.hrtxn.ringtone.project.numcertification.domain.NumcertificationOrder;
import com.hrtxn.ringtone.project.numcertification.domain.NumcertificationPrice;
import com.hrtxn.ringtone.project.numcertification.json.NumDataResult;
import com.hrtxn.ringtone.project.numcertification.mapper.NumCertificateionPriceMapper;
import com.hrtxn.ringtone.project.numcertification.mapper.NumcertificationOrderMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @param
 * @Author zcy
 * @Date 2019-08-29 14:51
 * @return
 */
@Service
public class NumCertificationService {

    @Autowired
    private NumcertificationOrderMapper numcertificationOrderMapper;
    @Autowired
    private NumCertificateionPriceMapper numCertificateionPriceMapper;

    private NumApi numApi = new NumApi();

    /**
     * 获取数据
     *
     * @author zcy
     * @date 2019-9-2 14:04
     */
    public AjaxResult selectData(Page page, NumOrder numOrder) throws IOException {
        if (StringUtils.isNull(page)) {
            return AjaxResult.error();
        }
        AjaxResult data = numApi.getData(page, numOrder);
        if ((int) data.get("code") == 200) {
            Object obj = data.get("data");
            JSONObject jsonObject = JSONObject.fromObject(obj);
            Integer dataCount = jsonObject.getInt("dataCount");
            JSONArray dataList = jsonObject.getJSONArray("dataList");
            List<NumDataResult> numDataResultList = new ArrayList<>();
            for (Object obje : dataList) {
                JSONObject jsonObj = JSONObject.fromObject(obje);
                NumDataResult numDataResult = (NumDataResult) JSONObject.toBean(jsonObj, NumDataResult.class);
                numDataResultList.add(numDataResult);
            }
            List<NumOrder> numOrderList = new ArrayList<>();
            for (int i = 0; i < numDataResultList.size(); i++) {
                // 根据分类名称获取成本价以及指导价
                BaseRequest b = new BaseRequest();
                b.setName(numDataResultList.get(i).getCategory());
                List<NumcertificationPrice> numPriceList = numCertificateionPriceMapper.getNumPriceList(null, b);
                if (StringUtils.isNotNull(numPriceList) && numPriceList.size() > 0) {
                    NumOrder numOrder1 = new NumOrder();
                    numOrder1.setPhoneNum(numDataResultList.get(i).getPhoneNum());
                    numOrder1.setCategory(numDataResultList.get(i).getCategory());
                    numOrder1.setCostPrice(numDataResultList.get(i).getAgentCost());
                    numOrder1.setGuidePrice(getGuidePrice(numDataResultList.get(i).getCategory(),numDataResultList.get(i).getAgentCost()));
//                    numOrder1.setGuidePrice(numPriceList.get(0).getGuidePrice());
                    numOrderList.add(numOrder1);
                }
            }
            return AjaxResult.success(numOrderList, "获取数据成功！", dataCount);
        }
        return data;
    }

    /**
     * 价格
     * @param category
     * @param agentCost
     * @return
     */
    public Double getGuidePrice(String category,Double agentCost){
        Double result = 0d;
        if("C类".equals(category) || "D类".equals(category)){
            result = 1200d;
        }else if("B类".equals(category)){
            result=1500d;
        }else if("A类".equals(category)){
            result = 1800d;
        }else{
            result = agentCost * 2d;
        }

        return result;
    }

    /**
     * 生成订单
     *
     * @author zcy
     * @date 2019-9-2 14:04
     */
    public AjaxResult order(NumcertificationOrder numcertificationOrder) {
        if (StringUtils.isNull(numcertificationOrder)) {
            return AjaxResult.error();
        }
        if (StringUtils.isEmpty(numcertificationOrder.getPhoneNum())) {
            return AjaxResult.error();
        }
        if (StringUtils.isNull(numcertificationOrder.getAgeLimit())) {
            return AjaxResult.error();
        }
        if (StringUtils.isEmpty(numcertificationOrder.getCompanyName())) {
            return AjaxResult.error();
        }
        if (StringUtils.isEmpty(numcertificationOrder.getProvince())) {
            return AjaxResult.error();
        }
        if (StringUtils.isEmpty(numcertificationOrder.getCity())) {
            return AjaxResult.error();
        }
        // 1、执行预占
        try {
//            String result =  numApi.cgiOccupyAdd(numcertificationOrder);
//            if(!"0".equals(result)){
//                return AjaxResult.error("预占失败！");
//            }
            numcertificationOrder.setUserId(ShiroUtils.getSysUser().getId());
            numcertificationOrder.setCreateTime(new Date());
            int count = numcertificationOrderMapper.insert(numcertificationOrder);
            if (count > 0) {
                // 3、返回订单ID
                return AjaxResult.success(numcertificationOrder.getId(), "创建成功！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 2、预占成功生成订单

        return AjaxResult.error("订单创建失败！");
    }

    /**
     * 根据ID获取订单信息
     *
     * @author zcy
     * @date 2019-9-2 14:06
     */
    public NumcertificationOrder selectById(Integer orderId) {
        BaseRequest b = new BaseRequest();
        b.setId(orderId);
        List<NumcertificationOrder> numcertificationOrders = numcertificationOrderMapper.selectOrder(null, b);
        if (numcertificationOrders.size() > 0) {
            return numcertificationOrders.get(0);
        }
        return null;
    }

    /**
     * 获取订单列表
     *
     * @author zcy
     * @date 2019-9-2 14:07
     */
    public AjaxResult selectOrder(Page page) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        // 获取订单
        BaseRequest b = new BaseRequest();
        b.setUserId(ShiroUtils.getSysUser().getId());
        List<NumcertificationOrder> numcertificationOrders = numcertificationOrderMapper.selectOrder(page, b);
        for (int i = 0; i < numcertificationOrders.size(); i++) {
            numcertificationOrders.get(i).setUserName(ShiroUtils.getSysUser().getUserName());
        }
        // 获取数量
        int count = numcertificationOrderMapper.getCount(b);
        return AjaxResult.success(numcertificationOrders, "", count);
    }

    /**
     * 根据400号码查询订单
     * @param phoneNum
     * @return
     */
    public NumcertificationOrder getOrderByphoneNum(String phoneNum){
        return  numcertificationOrderMapper.getOrderByphoneNum(phoneNum);
    }

    /**
     * 修改订单
     * @param numcertificationOrder
     */
    public void update(NumcertificationOrder numcertificationOrder){
        numcertificationOrderMapper.update(numcertificationOrder);
    }

}
