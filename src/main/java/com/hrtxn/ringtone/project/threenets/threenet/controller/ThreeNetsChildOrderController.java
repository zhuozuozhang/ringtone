package com.hrtxn.ringtone.project.threenets.threenet.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsChildOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsOrder;
import com.hrtxn.ringtone.project.threenets.threenet.service.ThreeNetsChildOrderService;
import com.hrtxn.ringtone.project.threenets.threenet.service.ThreeNetsOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author:lile
 * Date:2019/7/11 15:44
 * Description:子订单控制器
 */
@Slf4j
@Controller
public class ThreeNetsChildOrderController {

    @Autowired
    private ThreeNetsChildOrderService threeNetsChildOrderService;
    @Autowired
    private ThreeNetsOrderService threeNetsOrderService;

    /**
     * 进入号码管理
     *
     * @return
     */
    @GetMapping("/threenets/toMerchantsPhonePage/{parentOrderId}")
    public String toMerchantsPhonePage(ModelMap map, @PathVariable Integer parentOrderId) {
        ThreenetsOrder order = threeNetsOrderService.getById(parentOrderId);
        map.put("parentOrderId", parentOrderId);
        map.put("companyName", order.getCompanyName());
        return "threenets/threenet/merchants/number_list";
    }

    /**
     * 查询子订单
     *
     * @param page
     * @param request
     * @return
     */
    @PostMapping("/threenets/getThreeNetsTaskList")
    @ResponseBody
    public AjaxResult getThreeNetsTaskList(Page page, BaseRequest request) {
        try {
            List<ThreenetsChildOrder> list = threeNetsChildOrderService.getChildOrderList(page, request);
            int totalCount = threeNetsChildOrderService.getCount(request);
            return AjaxResult.success(list, "查询成功", totalCount);
        } catch (Exception e) {
            log.error("获取三网商户列表数据 方法：getThressNetsOrderList 错误信息", e);
        }
        return null;
    }


    /**
     * 添加子订单
     *
     * @return
     */
    @PostMapping("/threenets/insterThreeNetsChildOrder")
    @ResponseBody
    @Log(title = "添加子订单", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult insterThreeNetsChildOrder(ThreenetsChildOrder threenetsChildOrder) {
        try {
            return threeNetsChildOrderService.insterThreeNetsChildOrder(threenetsChildOrder);
        }catch (Exception e){
            log.error("批量保存 方法：insterThreeNetsChildOrder 错误信息", e);
            return AjaxResult.error("保存失败");
        }
    }


    /**
     * 删除子订单
     *
     * @param id
     * @return
     */
    @ResponseBody
    @DeleteMapping("/threenets/deleteThreeNetsChildOrder")
    @Log(title = "删除子订单", businessType = BusinessType.DELETE, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult deleteNotice(Integer id) {
        return threeNetsChildOrderService.delete(id);
    }

    /**
     * 获取号码信息
     *
     * @param id
     * @return
     */
    @PutMapping("/threenets/getPhoneInfo/{id}")
    @ResponseBody
    @Log(title = "获取号码信息功能", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult getPhoneInfo(@PathVariable Integer id) {
        try {
            return threeNetsChildOrderService.getPhoneInfo(id);
        } catch (Exception e) {
            log.error("获取号码信息 方法：getPhoneInfo 错误信息", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 刷新视频彩铃
     *
     * @param id
     * @return
     */
    @PutMapping("/threenets/refreshVbrtStatus/{id}")
    @ResponseBody
    @Log(title = "刷新视频彩铃功能", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult refreshVbrtStatus(@PathVariable Integer id) {
        try {
            return threeNetsChildOrderService.refreshVbrtStatus(id);
        } catch (Exception e) {
            log.error("刷新视频彩铃功能 方法：refreshIsMonthly 错误信息", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 发送短信
     * @param type
     * @param flag
     * @param data
     * @return
     */
    @PutMapping("/threenets/sendMessage")
    @ResponseBody
    @Log(title = "发送短信", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult sendMessage(Integer type, Integer flag, Integer data){
        try {
            return threeNetsChildOrderService.sendMessage(type,flag,data);
        } catch (Exception e) {
            log.error("发送短信功能 方法：sendMessage 错误信息", e);
            return AjaxResult.error(e.getMessage());
        }
    }

}
