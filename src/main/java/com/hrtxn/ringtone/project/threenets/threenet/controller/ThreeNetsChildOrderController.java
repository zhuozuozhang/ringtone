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
    @PutMapping("/threenets/chidSetRing")
    @ResponseBody
    @Log(title = "号码管理设置铃音", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult chidSetRing(Integer orderId, Integer operate, Integer ringId, Integer childOrderId) throws Exception {
        return threeNetsChildOrderService.chidSetRing(orderId, operate, ringId, childOrderId);
    }

    /**
     * 号码管理---跳转到设置铃音页面
     *
     * @param id
     * @param operator
     * @param companyName
     * @param orderId
     * @param map
     * @return
     */
    @GetMapping("/threenets/toUserSetingRing/{id}/{operator}/{companyName}/{orderId}")
    public String toUserSetingRing(@PathVariable("id") Integer id,
                                   @PathVariable("operator") Integer operator,
                                   @PathVariable("companyName") String companyName,
                                   @PathVariable("orderId") Integer orderId, ModelMap map) {
        map.put("companyName", companyName);
        map.put("orderId", orderId);
        map.put("operate", operator);
        map.put("id", id);// 子订单ID
        // 根据ID获取子订单信息
        try {
            ThreenetsChildOrder threenetsChildOrder = threeNetsChildOrderService.selectByPrimaryKey(id);
            map.put("threenetsChildOrder", threenetsChildOrder);
        } catch (Exception e) {
            log.error("获取子订单信息出错" + e);
        }
        return "threenets/threenet/merchants/number_list_set";
    }


    /**
     * 获取设置铃音子订单数据
     *
     * @param page
     * @param orderId
     * @param operate
     * @return
     */
    @PostMapping("/threenets/getThreeNetsChidOrderSetingList")
    @ResponseBody
    public AjaxResult getThreeNetsChidOrderSetingList(Page page, Integer orderId, Integer operate) {
        try {
            return threeNetsChildOrderService.findChildOrderByOrderId(page, orderId, operate);
        } catch (Exception e) {
            log.error("获取设置铃音子订单数据 方法：getThreeNetsChidOrderSetingList 错误信息：", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 进入号码管理
     *
     * @return
     */
    @GetMapping("/threenets/toMerchantsPhonePage/{parentOrderId}")
    public String toMerchantsPhonePage(ModelMap map, @PathVariable Integer parentOrderId) {
        try {
            ThreenetsOrder order = threeNetsChildOrderService.getOrderById(parentOrderId);
            map.put("parentOrderId", parentOrderId);
            if (order.getCompanyName().length() <= 6) {
                map.put("companyName", order.getCompanyName());
            } else {
                boolean result = order.getCompanyName().substring(order.getCompanyName().length() - 6).matches("[0-9]+");
                map.put("companyName", result ? order.getCompanyName().substring(0, order.getCompanyName().length() - 6) : order.getCompanyName());
            }
        } catch (Exception e) {
            log.error("获取集团名称失败 方法：toMerchantsPhonePage 错误信息：", e);
        }
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
    public AjaxResult insterThreeNetsChildOrder(ThreenetsChildOrder childOrder, BaseRequest request) {
        long startTime = System.currentTimeMillis();//获取当前时间
        try {
            return threeNetsChildOrderService.insterThreeNetsChildOrder(childOrder, request);
        } catch (Exception e) {
            log.error("批量保存 方法：insterThreeNetsChildOrder 错误信息", e);
            return AjaxResult.error("保存失败");
        } finally {
            log.info("保存三网子订单，共耗时 -- >" + (System.currentTimeMillis() - startTime) + "ms");
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
     * @param type 标识是否是批量操作 1、批量操作/2、单个操作
     * @param data 数据 type为1时，data为父级订单ID；type为2时，data为子订单ID
     * @return
     */
    @PutMapping("/threenets/getPhoneInfo")
    @ResponseBody
    @Log(title = "获取号码信息功能", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult getPhoneInfo(Integer type, Integer data) {
        long startTime = System.currentTimeMillis();//获取当前时间
        try {
            return threeNetsChildOrderService.getPhoneInfo(type, data);
        } catch (Exception e) {
            log.error("获取号码信息 方法：getPhoneInfo 错误信息", e);
            return AjaxResult.error(e.getMessage());
        } finally {
            log.info("刷新三网成员列表，共耗时 -- >" + (System.currentTimeMillis() - startTime) + "ms");
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
        long startTime = System.currentTimeMillis();//获取当前时间
        try {
            return threeNetsChildOrderService.refreshVbrtStatus(id);
        } catch (Exception e) {
            log.error("刷新视频彩铃功能 方法：refreshIsMonthly 错误信息", e);
            return AjaxResult.error(e.getMessage());
        } finally {
            log.info("刷新三网铃音列表，共耗时 -- >" + (System.currentTimeMillis() - startTime) + "ms");
        }
    }

    /**
     * 发送短信
     *
     * @param type
     * @param flag
     * @param data
     * @return
     */
    @PutMapping("/threenets/sendMessage")
    @ResponseBody
    @Log(title = "发送短信", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult sendMessage(Integer type, Integer flag, Integer data) {
        try {
            return threeNetsChildOrderService.sendMessage(type, flag, data);
        } catch (Exception e) {
            log.error("发送短信功能 方法：sendMessage 错误信息", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 移动工具箱-->用户信息
     *
     * @param ringMsisdn
     * @return
     */
    @PutMapping("/threenets/getUserInfoByRingMsisdn/{ringMsisdn}")
    @ResponseBody
    @Log(title = "移动工具箱-->获取用户信息", operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult getUserInfo(String ringMsisdn) {
        try {
            return threeNetsChildOrderService.getUserInfoByRingMsisdn(ringMsisdn);
        } catch (Exception e) {
            log.error("移动工具箱-->获取用户信息 方法：getUserInfo 错误信息", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 移动工具箱-->删除铃音-->搜索
     *
     * @param msisdn
     * @return
     */
    @PutMapping("/threenets/findRingInfoByMsisdn/{msisdn}")
    @ResponseBody
    @Log(title = "移动工具箱-->删除铃音-->搜索", operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult findRingInfoByMsisdn(@PathVariable String msisdn) {
        try {
            return threeNetsChildOrderService.findRingInfoByMsisdn(msisdn);
        } catch (Exception e) {
            log.error("移动工具箱-->删除铃音-->搜索 方法：findRingInfoByMsisdn 错误信息", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 移动工具箱-->删除铃音-->删除个人铃音设置
     *
     * @param msisdn
     * @param settingID
     * @param toneID
     * @param type
     * @return
     */
    @PutMapping("/threenets/singleDeleteRingSet/{msisdn}")
    @ResponseBody
    @Log(title = "通过移动手机号、设置ID、铃音ID、铃音类型删除某条个人铃音设置", businessType = BusinessType.DELETE, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult singleDeleteRingSet(@PathVariable String msisdn, String settingID, String toneID, String type) {
        try {
            return threeNetsChildOrderService.singleDeleteRingSet(msisdn, settingID, toneID, type);
        } catch (Exception e) {
            log.error("删除某条个人铃音设置 方法：singleDeleteRingSet 错误信息", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 移动工具箱-->删除铃音-->删除某条个人铃音库
     *
     * @param msisdn
     * @param toneIds
     * @param type
     * @return
     */
    @PutMapping("/threenets/singleDeleteRing/{msisdn}")
    @ResponseBody
    @Log(title = "删除某条个人铃音库", businessType = BusinessType.DELETE, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult singleDeleteRing(@PathVariable String msisdn, String toneIds, String type) {
        try {
            return threeNetsChildOrderService.singleDeleteRing(msisdn, toneIds, type);
        } catch (Exception e) {
            log.error("通过移动手机号、铃音ID、铃音类型删除某条个人铃音库 方法：singleDeleteRing 错误信息", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 批量删除个人铃音设置
     *
     * @param msisdn
     * @param vals
     * @return
     */
    @PutMapping("/threenets/batchDeleteRingSet")
    @ResponseBody
    @Log(title = "批量删除个人铃音设置", businessType = BusinessType.DELETE, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult batchDeleteRingSet(String msisdn, String vals) {
        try {
            return threeNetsChildOrderService.batchDeleteRingSet(msisdn, vals);
        } catch (Exception e) {
            log.error("批量删除个人铃音设置 方法：batchDeleteRingSet 错误信息", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 批量删除个人铃音库
     *
     * @param msisdn
     * @param vals
     * @return
     */
    @PutMapping("/threenets/batchDeleteRing")
    @ResponseBody
    @Log(title = "批量删除个人铃音库", businessType = BusinessType.DELETE, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult batchDeleteRing(String msisdn, String vals) {
        try {
            return threeNetsChildOrderService.batchDeleteRing(msisdn, vals);
        } catch (Exception e) {
            log.error("批量删除个人铃音库 方法：batchDeleteRing 错误信息", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 联通工具箱-->用户信息
     *
     * @param phoneNumber
     * @return
     */
    @PutMapping("/threenets/getUnicomUserInfoByPhoneNumber/{phoneNumber}")
    @ResponseBody
    @Log(title = "联通工具箱-->用户信息", operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult getUnicomUserInfoByPhoneNumber(@PathVariable String phoneNumber) {
        try {
            return threeNetsChildOrderService.getUnicomUserInfoByPhoneNumber(phoneNumber);
        } catch (Exception e) {
            log.error("联通工具箱-->用户信息 方法：getUnicomUserInfoByPhoneNumber 错误信息", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 联通工具箱-->用户信息-->删除某条用户信息
     *
     * @param msisdn
     * @return
     */
    @PutMapping("/threenets/deleteSilentMemberByMsisdn/{msisdn}")
    @ResponseBody
    @Log(title = "删除联通某条用户信息", businessType = BusinessType.DELETE, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult deleteSilentMemberByMsisdn(@PathVariable String msisdn) {
        try {
            return threeNetsChildOrderService.deleteSilentMemberByMsisdn(msisdn);
        } catch (Exception e) {
            log.error("联通工具箱-->用户信息-->删除某条用户信息 方法：deleteSilentMemberByMsisdn 错误信息", e);
            return AjaxResult.error(e.getMessage());
        }
    }
}
