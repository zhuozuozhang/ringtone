package com.hrtxn.ringtone.project.threenets.threenet.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrtxn.ringtone.common.api.MiguApi;
import com.hrtxn.ringtone.common.api.SwxlApi;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.exception.NoLoginException;
import com.hrtxn.ringtone.common.json.MiguAddGroupRespone;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.SpringUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.common.utils.json.JsonUtil;
import com.hrtxn.ringtone.project.system.user.domain.User;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsChildOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsRing;
import com.hrtxn.ringtone.project.threenets.threenet.json.migu.RefreshVbrtStatusResult;
import com.hrtxn.ringtone.project.threenets.threenet.json.swxl.SwxlPhoneInfoResult;
import com.hrtxn.ringtone.project.threenets.threenet.json.swxl.SwxlPubBackData;
import com.hrtxn.ringtone.project.threenets.threenet.json.swxl.SwxlQueryPubRespone;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsChildOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-22 14:19
 * Description:三网整合工具类
 */
@Slf4j
public class ApiUtils {

    private static MiguApi miguApi = new MiguApi();
    private static SwxlApi swxlApi = new SwxlApi();

    /**
     * 获取号码信息
     *
     * @param threenetsChildOrderList
     * @return
     */
    public AjaxResult getPhoneInfo(List<ThreenetsChildOrder> threenetsChildOrderList) throws Exception {
        Boolean f = false;
        String msg = "错误消息：";
        int failure=0;
        for (ThreenetsChildOrder threenetsChildOrder :threenetsChildOrderList) {
            // 判断运营商
            Integer operate = threenetsChildOrder.getOperate();
            if (operate == 1) {// 移动
                String result = miguApi.getPhoneInfo(threenetsChildOrder.getLinkmanTel(), threenetsChildOrder.getOperateId());
                if (StringUtils.isNotEmpty(result)) {
                    Document doc = Jsoup.parse(result);
                    Elements contents = doc.getElementsByClass("tbody_lis");
                    Elements datas = contents.get(0).getElementsByClass("tbody_lis");
                    Element ele = datas.get(0);
                    Elements trs = ele.getElementsByTag("tr");
                    if (trs.size() > 0) {// 没值的话，跳过
                        Elements tds = trs.get(0).getElementsByTag("td");
                        // 15280012834|a69ee792-623b-42db-a39b-4635f30767fd
                        // 获取子订单ID
                        String telNoOrId = tds.get(0).child(0).attr("value");
                        String mcardApersonId = telNoOrId.substring(telNoOrId.indexOf("|") + 1);
                        if (StringUtils.isNotEmpty(mcardApersonId)) {
                            threenetsChildOrder.setOperateOrderId(mcardApersonId);
                        }
                        String clgn = tds.get(2).text();// 彩铃功能
                        if ("彩铃用户".equals(clgn)) {
                            threenetsChildOrder.setIsRingtoneUser(true);
                        } else {
                            threenetsChildOrder.setIsRingtoneUser(false);
                        }
                        String vrbtStatus = tds.get(3).text();// 视频彩铃功能
                        if ("视频彩铃用户".equals(vrbtStatus)) {
                            threenetsChildOrder.setIsVideoUser(true);
                        } else {
                            threenetsChildOrder.setIsVideoUser(false);
                        }
                        String by = tds.get(8).text();// 包月
                        if ("包月".equals(by)) {
                            threenetsChildOrder.setIsMonthly(2);
                        } else if ("未包月".equals(by)) {
                            threenetsChildOrder.setIsMonthly(1);
                        } else {
                            threenetsChildOrder.setIsMonthly(3);
                        }
                        String ringName = tds.get(9).text();// 铃音名称
                        if (!"暂无".equals(ringName)) {
                            threenetsChildOrder.setRingName(ringName);
                        }
                        threenetsChildOrder.setRemark(tds.get(10).text());// 备注
                        f = true;
                    }else {
                        failure++;
                        msg += "[" + threenetsChildOrder.getLinkmanTel() + "：获取信息失败！]";
                    }
                }else {
                    failure++;
                    msg += "[" + threenetsChildOrder.getLinkmanTel() + "：获取信息失败！]";
                }
            } else if (operate == 2) {// 电信

            } else {// 联通
                String phoneInfo = swxlApi.getPhoneInfo(threenetsChildOrder.getLinkmanTel(), threenetsChildOrder.getOperateId());
                if (StringUtils.isNotEmpty(phoneInfo)) {
                    // 分发实体详情转换
                    ObjectMapper mapper = new ObjectMapper();
                    SwxlPubBackData<SwxlQueryPubRespone<SwxlPhoneInfoResult>> info = mapper.readValue(phoneInfo, new TypeReference<SwxlPubBackData<SwxlPhoneInfoResult>>() {});
                    if (StringUtils.isNotNull(info) && "000000".equals(info.getRecode())) {
                        SwxlQueryPubRespone<SwxlPhoneInfoResult> pageData = (SwxlQueryPubRespone) info.getData();
                        // 得到号码信息实体
                        for (SwxlPhoneInfoResult infoResult : pageData.getData()) {
                            // 判断与当前刷新号码是否一至
                            if (infoResult.getMsisdn().equals(threenetsChildOrder.getLinkmanTel().trim()) && infoResult.getGroupId().equals(threenetsChildOrder.getOperateId())) {
                                // 如为彩铃用户且已包月，则设置铃音名称
                                if (threenetsChildOrder.getIsRingtoneUser() && threenetsChildOrder.getIsMonthly() == 2) {
                                    log.info("联通  getPhoneInfo 备注 --->" + infoResult.getRemark());
                                    if (infoResult.getRemark().contains("[")) {
                                        String splitStr = infoResult.getRemark().split("\\[")[1];
                                        String ringName = splitStr.split("\\]")[0];
                                        if (threenetsChildOrder.getRingName() == null || threenetsChildOrder.getRingName() == "") {
                                            threenetsChildOrder.setRingName(ringName);
                                        }
                                    }
                                }
                                // 包月状态 0：已包月  1:未包月  2:未知（未包月）  3:已退订
                                // 对应状态 1.未包月/2.已包月/3.已退订
                                if (infoResult.getMonthStatus() == 0) {
                                    threenetsChildOrder.setIsMonthly(2);
                                } else if (infoResult.getMonthStatus() == 1) {
                                    threenetsChildOrder.setIsMonthly(1);
                                } else if (infoResult.getMonthStatus() == 2) {
                                    threenetsChildOrder.setIsMonthly(1);
                                } else if (infoResult.getMonthStatus() == 3) {
                                    threenetsChildOrder.setIsMonthly(3);
                                }
                                // 炫铃状态 0:已开通 1:未开通  2:未知
                                if (infoResult.getCrbtStatus() == 0) {
                                    threenetsChildOrder.setIsRingtoneUser(true);
                                } else if (infoResult.getCrbtStatus() == 1) {
                                    threenetsChildOrder.setIsRingtoneUser(false);
                                } else if (infoResult.getCrbtStatus() == 2) {
                                    threenetsChildOrder.setIsRingtoneUser(false);
                                }
                                // 设置备注
                                if (StringUtils.isNotEmpty(infoResult.getRemark())) {
                                    threenetsChildOrder.setRemark(infoResult.getRemark());
                                } else {
                                    threenetsChildOrder.setRemark("无");
                                }
                                f = true;
                                break;
                            }
                        }
                    } else {
                        failure++;
                        msg += "[" + threenetsChildOrder.getLinkmanTel() + ":" + info.getMessage() + "]";
                    }
                } else {
                    failure++;
                    msg += "[" + threenetsChildOrder.getLinkmanTel() + "：获取信息失败！]";
                }
            }
            if (f) {
                int i = SpringUtils.getBean(ThreenetsChildOrderMapper.class).updateThreeNetsChidOrder(threenetsChildOrder);
                if (i <= 0) {
                    failure++;
                    msg += "[" + threenetsChildOrder.getLinkmanTel() + "：更新信息失败！]";
                }
            }
        }
        if (failure > 0){
            return AjaxResult.success(false, msg);
        } else {
            return AjaxResult.success(true,"更新成功！");
        }
    }

    /**
     * 移动刷新视频彩铃功能
     *
     * @param threenetsChildOrder
     * @return
     */
    public AjaxResult refreshVbrtStatus(ThreenetsChildOrder threenetsChildOrder) throws IOException, NoLoginException {
        Boolean f = false;
        String msg = "刷新失败！";
        if (threenetsChildOrder.getOperate() == 1 && StringUtils.isNotEmpty(threenetsChildOrder.getLinkmanTel())) {
            String result = miguApi.refreshVbrtStatus(threenetsChildOrder.getLinkmanTel());
            if (StringUtils.isNotEmpty(result)) {
                // 解析出数据
                RefreshVbrtStatusResult vbrt = (RefreshVbrtStatusResult) JsonUtil.getObject4JsonString(result, RefreshVbrtStatusResult.class);
                // 0是、1不是
                if ("0".equals(vbrt.getVrbtStatus())) {
                    threenetsChildOrder.setIsVideoUser(true);
                } else {
                    threenetsChildOrder.setIsVideoUser(false);
                }
                f = true;
            }
        } else {
            msg = "该运营商不支持视频彩铃功能";
        }
        if (f) {
            int i = SpringUtils.getBean(ThreenetsChildOrderMapper.class).updateThreeNetsChidOrder(threenetsChildOrder);
            if (i > 0) {
                return AjaxResult.success(true, "刷新成功！");
            }
        }
        return AjaxResult.success(false, msg);
    }

    /**
     * 发送短信
     *
     * @param threenetsChildOrderList
     * @param flag 标识是否是下发链接短信 1、普通短信/2、链接短信
     * @return
     */
    public AjaxResult sendMessage(List<ThreenetsChildOrder> threenetsChildOrderList, Integer flag) throws IOException, NoLoginException {
        String msg = "发送成功！";
        String msg2 = "错误信息：";
        int failure = 0; // 发送失败数量
        for (ThreenetsChildOrder t : threenetsChildOrderList) {
            if (flag == 1) {
                if (t.getOperate() == 1) { // 移动普通短信
                    miguApi.remindOrderCrbtAndMonth(t.getLinkmanTel(), t.getOperateOrderId(), t.getOperateId(), false);
                } else if (t.getOperate() == 2) { // 电信普通短信

                } else { // 联通普通短信
                    String result = swxlApi.remindOrderCrbtAndMonth(t.getLinkmanTel(), t.getOperateId(), false);
                    if (StringUtils.isNotEmpty(result)){
                        SwxlPubBackData info = (SwxlPubBackData) JsonUtil.getObject4JsonString(result, SwxlPubBackData.class);
                        if (!"000000".equals(info.getRecode()) || !info.isSuccess()) {
                            msg2 += "["+t.getLinkmanTel()+":"+info.getMessage()+"]";
                            failure++;
                        }
                    }
                }
            } else {
                if (t.getOperate() == 3) { // 链接短信，联通专属
                    String res = swxlApi.swxlSendSMSByCRBTFail(t.getLinkmanTel());
                    if (StringUtils.isNotEmpty(res)){
                        SwxlPubBackData info = (SwxlPubBackData) JsonUtil.getObject4JsonString(res, SwxlPubBackData.class);
                        if (!"000000".equals(info.getRecode()) || !info.isSuccess()) {
                            msg2 += "["+t.getLinkmanTel()+":"+info.getMessage()+"]";
                            failure++;
                        }
                    }
                }
            }
        }
        if (failure == 0){
            return AjaxResult.success(true, msg);
        }else{
            return AjaxResult.success(false, msg2);
        }
    }

    /**
     * 保存移动订单
     *
     * @param order
     * @param order
     * @return
     */
    public AjaxResult saveOrderByYd(ThreenetsOrder order){
        try{
            //登陆
            miguApi.loginAuto();

           int sendCount = 3;// 系统同步远程系统3次。解决网络慢的问题
            // 添加到咪咕平台
            // 1.先进行增加到本地数据库
                // 2.在进行同步到服务器，同步3次。
                MiguAddGroupRespone addGroupResponse = null;
                for (int i = 0; i < sendCount; i++) {// 重试添加3次
                    addGroupResponse = miguApi.add(order);
                    if (addGroupResponse != null) {
                        break;
                    }
                }
                if (addGroupResponse.isSuccess()) {
                    // 设置集团id
                    order.setMcardId(addGroupResponse.getCircleId());
                    // 2.增加订单表
                    if (order.getPaymentPrice() == 3 || order.getPaymentPrice() == 5) {
                        order.setStatus("审核通过");
                    } else {
                        order.setStatus("待审核");
                    }
                }
            return AjaxResult.success(addGroupResponse,addGroupResponse.getMsg());
        }catch (Exception e){
            log.error("对接移动 方法：saveOrderByYd  错误信息", e);
            return AjaxResult.error("保存失败");
        }
    }
}
