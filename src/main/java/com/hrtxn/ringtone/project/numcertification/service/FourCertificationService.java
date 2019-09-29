package com.hrtxn.ringtone.project.numcertification.service;

import com.hrtxn.ringtone.common.api.NumApi;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.Const;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.numcertification.domain.FourcertificationOrder;
import com.hrtxn.ringtone.project.numcertification.domain.NumOrder;
import com.hrtxn.ringtone.project.numcertification.domain.NumcertificationOrder;
import com.hrtxn.ringtone.project.numcertification.domain.NumcertificationPrice;
import com.hrtxn.ringtone.project.numcertification.json.NumDataResult;
import com.hrtxn.ringtone.project.numcertification.mapper.FourcertificationOrderMapper;
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
public class FourCertificationService {

    @Autowired
    private NumcertificationOrderMapper numcertificationOrderMapper;
    @Autowired
    private NumCertificateionPriceMapper numCertificateionPriceMapper;

    @Autowired
    private FourcertificationOrderMapper fourcertificationOrderMapper;

    private NumApi numApi = new NumApi();

    /**
     * 预占
     * @return
     */
    public AjaxResult preoccupation(FourcertificationOrder fourcertificationOrder){
        if (StringUtils.isNull(fourcertificationOrder)) {
            return AjaxResult.error();
        }
        String result =  numApi.preoccupation(fourcertificationOrder);
        if(!"0".equals(result)){
            return AjaxResult.error("预占申请失败！");
        }
        //预占申请中
        fourcertificationOrder.setStatus(Const.FOUR_ORDER_PREOCCUPATION_NEW);
        fourcertificationOrder.setDelFlag("0");
        fourcertificationOrderMapper.insert(fourcertificationOrder);
        return AjaxResult.error("预占申请成功！");
    }

    public AjaxResult ApplyTemplate(FourcertificationOrder fourcertificationOrder){
        try {
            String result = numApi.downloadTemplate(fourcertificationOrder);
            if(!"0".equals(result)){
                return AjaxResult.error("模板申请失败！");
            }
            fourcertificationOrder.setStatus(Const.FOUR_ORDER_TEMPLATE_NEW);
            fourcertificationOrderMapper.update(fourcertificationOrder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return AjaxResult.error("模板申请成功！");
    }


    public FourcertificationOrder getOrderByApplyNumber(String applyNumber){
        return fourcertificationOrderMapper.getOrderByApplyNumber(applyNumber);
    }

    public void update(FourcertificationOrder fourcertificationOrder){
        fourcertificationOrderMapper.update(fourcertificationOrder);
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
        List<FourcertificationOrder> fourcertificationOrders = fourcertificationOrderMapper.queryOrderPage(page, b);
        for (int i = 0; i < fourcertificationOrders.size(); i++) {
            fourcertificationOrders.get(i).setUserName(ShiroUtils.getSysUser().getUserName());
        }
        // 获取数量
        int count = fourcertificationOrderMapper.getCount(b);
        return AjaxResult.success(fourcertificationOrders, "", count);
    }


}
