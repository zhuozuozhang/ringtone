package com.hrtxn.ringtone.project.threenets.threenet.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.OrderRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsChildOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsOrder;
import com.hrtxn.ringtone.project.threenets.threenet.service.ThreeNetsOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author:lile
 * Date:2019/7/11 10:34
 * Description:三网订单控制器
 */
@Slf4j
@Controller
public class ThreeNetsOrderController {

    @Autowired
    private ThreeNetsOrderService threeNetsOrderService;

    /**
     * 跳轉到商戶列表，帶有ID
     *
     * @param map
     * @param id
     * @return
     */
    @GetMapping("/threenets/toMerchantsPage")
    public String toMerchantsPhonePage(ModelMap map, Integer id) {
        map.put("id", id);
        return "threenets/threenet/merchants/merchants";
    }

    /**
     * 获取三网商户列表数据
     *
     * @param page
     * @param request
     * @return
     */
    @PostMapping("/threenets/getThressNetsOrderList")
    @ResponseBody
    public AjaxResult getThressNetsOrderList(Page page, BaseRequest request) {
        List<ThreenetsOrder> list = null;
        try {
            // 获取订单总数
            int totalCount = threeNetsOrderService.getCount(request);
            list = threeNetsOrderService.getAllorderList(page, request);
            return AjaxResult.success(list, "查询成功", totalCount);
        } catch (Exception e) {
            log.error("获取三网商户列表数据 方法：getThressNetsOrderList 错误信息", e);
        }
        return null;
    }

    /**
     * 修改商户名称
     *
     * @param order
     * @return
     */
    @PostMapping("/threenets/updateThreeNetsOrder")
    @ResponseBody
    @Log(title = "修改三网订单", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult updateThreeNetsOrder(ThreenetsOrder order) {
        return threeNetsOrderService.update(order);
    }

    /**
     * 初始化三网订单
     *
     * @return
     */
    @PostMapping("/threenets/insterThreeNetsOrder")
    @ResponseBody
    @Log(title = "初始化三网订单", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult insterThreeNetsOrder(ThreenetsOrder order) {
        try{
            return threeNetsOrderService.init(order);
        }catch (Exception e){
            log.error("初始化三网订单失败 方法：insterThreeNetsOrder 错误信息", e);
            return AjaxResult.error("保存失败");
        }
    }

    /**
     * 添加三网订单
     *
     * @return
     */
    @PostMapping("/threenets/saveThreeNetsOrder")
    @ResponseBody
    @Log(title = "添加三网订单", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult saveThreeNetsOrder(OrderRequest order) {
        try {
            return threeNetsOrderService.save(order);
        }catch (Exception e){
            log.error("保存三网订单失败 方法：saveThreeNetsOrder 错误信息", e);
            return AjaxResult.error("添加失败");
        }
    }

    /**
     * 删除三网订单
     *
     * @param id
     * @return
     */
    @ResponseBody
    @DeleteMapping("/threenets/deleteThreeNetsOrder")
    @Log(title = "删除三网订单", businessType = BusinessType.DELETE, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult deleteThreeNetsOrder(Integer id) {
        return threeNetsOrderService.delete(id);
    }


}
