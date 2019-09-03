package com.hrtxn.ringtone.project.threenets.threenet.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrtxn.ringtone.common.api.McardApi;
import com.hrtxn.ringtone.common.api.MiguApi;
import com.hrtxn.ringtone.common.api.SwxlApi;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.exception.NoLoginException;
import com.hrtxn.ringtone.common.utils.ConfigUtil;
import com.hrtxn.ringtone.common.utils.Const;
import com.hrtxn.ringtone.common.utils.SpringUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.common.utils.json.JsonUtil;
import com.hrtxn.ringtone.project.system.File.service.FileService;
import com.hrtxn.ringtone.project.system.user.domain.User;
import com.hrtxn.ringtone.project.system.user.mapper.UserMapper;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreeNetsOrderAttached;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsChildOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsRing;
import com.hrtxn.ringtone.project.threenets.threenet.json.mcard.McardAddGroupRespone;
import com.hrtxn.ringtone.project.threenets.threenet.json.mcard.McardAddPhoneRespone;
import com.hrtxn.ringtone.project.threenets.threenet.json.migu.*;
import com.hrtxn.ringtone.project.threenets.threenet.json.swxl.*;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreeNetsOrderAttachedMapper;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsChildOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsRingMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Author:zcy
 * Date:2019-07-22 14:19
 * Description:三网整合工具类
 */
@Slf4j
public class ApiUtils {

    private static MiguApi miguApi = new MiguApi();
    private static SwxlApi swxlApi = new SwxlApi();
    private static McardApi mcardApi = new McardApi();
    private static ConfigUtil configUtil = new ConfigUtil();

    /**
     * 获取号码信息
     *
     * @param threenetsChildOrderList
     * @return
     */
    public AjaxResult getPhoneInfo(List<ThreenetsChildOrder> threenetsChildOrderList) throws Exception {
        Boolean f = false;
        String msg = "错误消息：";
        int failure = 0;
        for (ThreenetsChildOrder threenetsChildOrder : threenetsChildOrderList) {
            // 判断运营商
            Integer operate = threenetsChildOrder.getOperator();
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
                    } else {
                        failure++;
                        msg += "[" + threenetsChildOrder.getLinkmanTel() + "：获取信息失败！]";
                    }
                } else {
                    failure++;
                    msg += "[" + threenetsChildOrder.getLinkmanTel() + "：获取信息失败！]";
                }
            } else if (operate == 2) {// 电信
                ThreeNetsOrderAttached attached = SpringUtils.getBean(ThreeNetsOrderAttachedMapper.class).selectByParentOrderId(threenetsChildOrder.getParentOrderId());
                if (StringUtils.isEmpty(attached.getMcardId()) || StringUtils.isEmpty(attached.getMcardDistributorId())) {
                    break;
                }
                mcardApi.toUserList(threenetsChildOrder.getOperateId(), attached.getMcardDistributorId());
                String result = mcardApi.getUserInfo(attached.getMcardDistributorId());
                Document doc = Jsoup.parse(result);
                Elements contents = doc.getElementsByTag("tbody");
                Elements trs = contents.get(0).getElementsByTag("tr");
                for (int i = 0; i < trs.size(); i++) {
                    Elements tds = trs.get(i).getElementsByTag("td");
                    if (tds.size() <= 0) {
                        continue;
                    }
                    if (tds.get(1).text().equals(threenetsChildOrder.getLinkmanTel())) {
                        //threenetsChildOrder.setRingName(tds.get(5).text());
                        threenetsChildOrder.setStatus(tds.get(6).text());
                        String value = tds.get(12).childNode(0).attr("value");
                        threenetsChildOrder.setOperateOrderId(value);
                        //是否彩铃用户
                        if (tds.get(7).text().equals("已开通")) {
                            threenetsChildOrder.setIsRingtoneUser(true);
                        } else {
                            threenetsChildOrder.setIsRingtoneUser(false);
                        }
                        //是否包月
                        if (tds.get(8).text().equals("未开通")) {
                            threenetsChildOrder.setIsMonthly(1);
                        } else {
                            threenetsChildOrder.setIsMonthly(2);
                        }
                        threenetsChildOrder.setRemark(tds.get(9).text());
                        f = true;
                    }
                }
            } else {// 联通
                String phoneInfo = swxlApi.getPhoneInfo(threenetsChildOrder.getLinkmanTel(), threenetsChildOrder.getOperateId());
                if (StringUtils.isNotEmpty(phoneInfo)) {
                    // 分发实体详情转换
                    SwxlPubBackData<SwxlQueryPubRespone<SwxlPhoneInfoResult>> info = SpringUtils.getBean(ObjectMapper.class).readValue(phoneInfo, SwxlPubBackData.class);
                    if (StringUtils.isNotNull(info) && "000000".equals(info.getRecode())) {
                        SwxlQueryPubRespone<SwxlPhoneInfoResult> pageData = (SwxlQueryPubRespone) info.getData();
                        List<SwxlPhoneInfoResult> data = pageData.getData();
                        List<SwxlPhoneInfoResult> swxlPhoneInfoResultList = new ArrayList<>();
                        for (Object obj : data) {
                            JSONObject jsonObject = JSONObject.fromObject(obj); // 将数据转成json字符串
                            SwxlPhoneInfoResult swxlPhoneInfoResult = (SwxlPhoneInfoResult) JSONObject.toBean(jsonObject, SwxlPhoneInfoResult.class); //将json转成需要的对象
                            swxlPhoneInfoResultList.add(swxlPhoneInfoResult);
                        }
                        // 得到号码信息实体
                        for (SwxlPhoneInfoResult infoResult : swxlPhoneInfoResultList) {
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
        if (failure > 0) {
            return AjaxResult.success(false, msg);
        } else {
            return AjaxResult.success(true, "更新成功！");
        }
    }

    /**
     * 定时器 刷新号码信息
     *
     * @param threenetsChildOrderList
     * @return
     * @throws Exception
     */
    public void getPhoneInfoTimeTask(List<ThreenetsChildOrder> threenetsChildOrderList, String userName) throws Exception {
        MiguApi miguApi1 = new MiguApi();
        boolean f1 = miguApi1.loginAutoParam(userName);
        Boolean f = false;
        for (ThreenetsChildOrder threenetsChildOrder : threenetsChildOrderList) {
            // 判断运营商
            Integer operate = threenetsChildOrder.getOperator();
            if (operate == 1) {// 移动
                if (f1) {
                    String result = miguApi1.getPhoneInfo(threenetsChildOrder.getLinkmanTel(), threenetsChildOrder.getOperateId());
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
                        } else {
                            log.info("[" + threenetsChildOrder.getLinkmanTel() + "：获取信息失败！]");
                        }
                    } else {
                        log.info("[" + threenetsChildOrder.getLinkmanTel() + "：获取信息失败！]");
                    }
                }
            } else if (operate == 2) {// 电信
                ThreeNetsOrderAttached attached = SpringUtils.getBean(ThreeNetsOrderAttachedMapper.class).selectByParentOrderId(threenetsChildOrder.getParentOrderId());
                mcardApi.toUserList(threenetsChildOrder.getOperateId(), attached.getMcardDistributorId());
                String result = mcardApi.getUserInfo(attached.getMcardDistributorId());
                Document doc = Jsoup.parse(result);
                Elements contents = doc.getElementsByTag("tbody");
                Elements trs = contents.get(0).getElementsByTag("tr");
                for (int i = 0; i < trs.size(); i++) {
                    Elements tds = trs.get(i).getElementsByTag("td");
                    if (tds.size() <= 0) {
                        continue;
                    }
                    if (tds.get(1).text().equals(threenetsChildOrder.getLinkmanTel())) {
                        //threenetsChildOrder.setRingName(tds.get(5).text());
                        threenetsChildOrder.setStatus(tds.get(6).text());
                        String value = tds.get(12).childNode(0).attr("value");
                        threenetsChildOrder.setOperateOrderId(value);
                        //是否彩铃用户
                        if (tds.get(7).text().equals("已开通")) {
                            threenetsChildOrder.setIsRingtoneUser(true);
                        } else {
                            threenetsChildOrder.setIsRingtoneUser(false);
                        }
                        //是否包月
                        if (tds.get(8).text().equals("未开通")) {
                            threenetsChildOrder.setIsMonthly(1);
                        } else {
                            threenetsChildOrder.setIsMonthly(2);
                        }
                        threenetsChildOrder.setRemark(tds.get(9).text());
                        f = true;
                    }
                }
            } else {// 联通
                String phoneInfo = swxlApi.getPhoneInfo(threenetsChildOrder.getLinkmanTel(), threenetsChildOrder.getOperateId());
                if (StringUtils.isNotEmpty(phoneInfo)) {
                    // 分发实体详情转换
                    SwxlPubBackData<SwxlQueryPubRespone<SwxlPhoneInfoResult>> info = SpringUtils.getBean(ObjectMapper.class).readValue(phoneInfo, SwxlPubBackData.class);
                    if (StringUtils.isNotNull(info) && "000000".equals(info.getRecode())) {
                        SwxlQueryPubRespone<SwxlPhoneInfoResult> pageData = (SwxlQueryPubRespone) info.getData();
                        List<SwxlPhoneInfoResult> data = pageData.getData();
                        List<SwxlPhoneInfoResult> swxlPhoneInfoResultList = new ArrayList<>();
                        for (Object obj : data) {
                            JSONObject jsonObject = JSONObject.fromObject(obj); // 将数据转成json字符串
                            SwxlPhoneInfoResult swxlPhoneInfoResult = (SwxlPhoneInfoResult) JSONObject.toBean(jsonObject, SwxlPhoneInfoResult.class); //将json转成需要的对象
                            swxlPhoneInfoResultList.add(swxlPhoneInfoResult);
                        }
                        // 得到号码信息实体
                        for (SwxlPhoneInfoResult infoResult : swxlPhoneInfoResultList) {
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
                        log.info("[" + threenetsChildOrder.getLinkmanTel() + ":" + info.getMessage() + "]");
                    }
                } else {
                    log.info("[" + threenetsChildOrder.getLinkmanTel() + "：获取信息失败！]");
                }
            }
            if (f) {
                int i = SpringUtils.getBean(ThreenetsChildOrderMapper.class).updateThreeNetsChidOrder(threenetsChildOrder);
                if (i <= 0) {
                    log.info("[" + threenetsChildOrder.getLinkmanTel() + "：更新信息失败！]");
                }
            }
        }
    }

    /**
     * 验证电信商户是否审核通过
     *
     * @param order
     * @return
     */
    public McardAddGroupRespone normalBusinessInfo(ThreenetsOrder order) {
        McardAddGroupRespone respone = new McardAddGroupRespone();
        ThreeNetsOrderAttached attached = SpringUtils.getBean(ThreeNetsOrderAttachedMapper.class).selectByParentOrderId(order.getId());
        String result = mcardApi.refreshBusinessInfo(order, attached.getMcardDistributorId());
        if (StringUtils.isNotEmpty(result)) {
            Document doc = Jsoup.parse(result);
            Elements contents = doc.getElementsByTag("tbody");
            if (contents.size() <= 0) {
                respone.setCode("1001");
                return respone;
            }
            Elements trs = contents.get(0).getElementsByTag("tr");
            for (int i = 0; i < trs.size(); i++) {
                Elements tds = trs.get(i).getElementsByTag("td");
                if (tds.get(1).text().equals(order.getCompanyName())) {
                    Element el2 = tds.get(6);
                    String ringCheckmsg = el2.attr("title");
                    String remark = el2.text();
                    respone.setMessage(remark+"-"+ringCheckmsg);
                    if (remark.indexOf("未通过") >= 0) {
                        respone.setCode("1001");
                    } else {
                        respone.setCode("0000");
                    }
                    break;
                }
            }
        }
        return respone;
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
        if (threenetsChildOrder.getOperator() == 1 && StringUtils.isNotEmpty(threenetsChildOrder.getLinkmanTel())) {
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
     * @param flag                    标识是否是下发链接短信 1、普通短信/2、链接短信
     * @return
     */
    public AjaxResult sendMessage(List<ThreenetsChildOrder> threenetsChildOrderList, Integer flag) throws IOException, NoLoginException {
        String msg = "发送成功！";
        String msg2 = "错误信息：";
        int failure = 0; // 发送失败数量
        for (ThreenetsChildOrder t : threenetsChildOrderList) {
            if (flag == 1) {
                if (t.getOperator() == 1) { // 移动普通短信
                    miguApi.remindOrderCrbtAndMonth(t.getLinkmanTel(), t.getOperateOrderId(), t.getOperateId(), false);
                } else if (t.getOperator() == 2) { // 电信普通短信

                } else { // 联通普通短信
                    String result = swxlApi.remindOrderCrbtAndMonth(t.getLinkmanTel(), t.getOperateId(), false);
                    if (StringUtils.isNotEmpty(result)) {
                        SwxlPubBackData info = (SwxlPubBackData) JsonUtil.getObject4JsonString(result, SwxlPubBackData.class);
                        if (!"000000".equals(info.getRecode()) || !info.isSuccess()) {
                            msg2 += "[" + t.getLinkmanTel() + ":" + info.getMessage() + "]";
                            failure++;
                        }
                    }
                }
            } else {
                if (t.getOperator() == 3) { // 链接短信，联通专属
                    String res = swxlApi.swxlSendSMSByCRBTFail(t.getLinkmanTel());
                    if (StringUtils.isNotEmpty(res)) {
                        SwxlPubBackData info = (SwxlPubBackData) JsonUtil.getObject4JsonString(res, SwxlPubBackData.class);
                        if (!"000000".equals(info.getRecode()) || !info.isSuccess()) {
                            msg2 += "[" + t.getLinkmanTel() + ":" + info.getMessage() + "]";
                            failure++;
                        }
                    }
                }
            }
        }
        if (failure == 0) {
            return AjaxResult.success(true, msg);
        } else {
            return AjaxResult.success(false, msg2);
        }
    }

    /**
     * 刷新铃音信息
     *
     * @param threenetsRings
     * @return
     */
    public List<ThreenetsRing> getRingInfo(List<ThreenetsRing> threenetsRings) throws NoLoginException, IOException {
        for (ThreenetsRing threenetsRing : threenetsRings) {
            Boolean f = false;
            Integer operator = threenetsRing.getOperate();
            String ringName = threenetsRing.getRingName();
            ringName = StringUtils.subString(ringName, '.');
            if (operator == 1) { // 移动
                String result = miguApi.findCircleRingPageById(threenetsRing.getOperateId());
                if (StringUtils.isNotEmpty(result)) {
                    Document doc = Jsoup.parse(result);
                    Elements contents = doc.getElementsByClass("tbody_lis");
                    Elements trs = contents.get(0).getElementsByTag("tr");
                    for (int i = 0; i < trs.size(); i++) {
                        Elements tds = trs.get(i).getElementsByTag("td");
                        if (tds.get(0).text().equals(ringName)) {
                            Element el2 = tds.get(4);
                            String ringCheckmsg = el2.attr("title");
                            ringCheckmsg = ringCheckmsg.replace("激活", "下发");
                            String remark = el2.text();
                            if (StringUtils.isNotEmpty(ringCheckmsg)) {
                                threenetsRing.setRemark(ringCheckmsg); // 运营商返回的备注信息
                            }
                            // 铃音状态（1.待审核/2.激活中/3.激活成功/4.部分省份激活超时/5.部分省份激活成功/6.激活失败/7.审核不通过/8.分发失败）
                            if ("待审核".equals(remark)) {
                                threenetsRing.setRingStatus(1);
                            } else if ("激活中".equals(remark)) {
                                threenetsRing.setRingStatus(2);
                            } else if ("激活成功".equals(remark)) {
                                threenetsRing.setRingStatus(3);
                            } else if ("部分省份激活超时".equals(remark)) {
                                threenetsRing.setRingStatus(4);
                            } else if ("部分省份激活成功".equals(remark)) {
                                threenetsRing.setRingStatus(5);
                            } else {
                                threenetsRing.setRingStatus(6);
                            }
                            if (threenetsRing.getRingStatus() != 5) {
                                Element el = tds.get(8);
                                List<Node> nodes = el.childNodes();
                                if (nodes.size() > 5) {
                                    Elements temp = el.child(2).getElementsByTag("a");
                                    String t = temp.attr("onclick");
                                    int begin = t.indexOf("showConfirm('") + 13;
                                    int end = begin + 36;
                                    String t2 = t.substring(begin, end);
                                    threenetsRing.setOperateRingId(t2);
                                }
                            } else {
                                Element el = tds.get(8);
                                List<Node> nodes = el.childNodes();
                                if (nodes.size() > 5) {
                                    Elements temp = el.child(2).getElementsByTag("a");
                                    String t = temp.attr("href");
                                    int begin = t.indexOf("ringID=") + 7;
                                    int end = begin + 36;
                                    String t2 = t.substring(begin, end);
                                    threenetsRing.setOperateRingId(t2);
                                }
                            }
                            // 修改铃音信息
                            int count = SpringUtils.getBean(ThreenetsRingMapper.class).updateByPrimaryKeySelective(threenetsRing);
                            log.info("修改铃音信息结果---->" + count);
                            break;
                        }
                    }
                }
            } else if (operator == 2) { // 电信
                ThreeNetsOrderAttached attached = SpringUtils.getBean(ThreeNetsOrderAttachedMapper.class).selectByParentOrderId(threenetsRing.getOrderId());
                mcardApi.toUserList(threenetsRing.getOperateId(), attached.getMcardDistributorId());
                String result = mcardApi.getRingInfo(attached.getMcardDistributorId());
                Document doc = Jsoup.parse(result);
                Elements contents = doc.getElementsByTag("tbody");
                Elements trs = contents.get(0).getElementsByTag("tr");
                for (int i = 0; i < trs.size(); i++) {
                    Elements tds = trs.get(i).getElementsByTag("td");
                    if (tds.size() <= 0) {
                        continue;
                    }
                    if (tds.get(1).text().equals(ringName)) {
                        Element el2 = tds.get(4);
                        String remark = el2.text();
                        if (remark.equals("审核通过")) {
                            threenetsRing.setRingStatus(2);
                        } else {
                            Elements lable = tds.get(4).getElementsByTag("lable");
                            String title = lable.attr("title");
                            threenetsRing.setRingStatus(6);
                            threenetsRing.setRemark(title);
                        }
                        Element el6 = tds.get(6);
                        String value = el6.childNodes().get(1).attributes().get("data-id");
                        threenetsRing.setOperateRingId(value);
                        int count = SpringUtils.getBean(ThreenetsRingMapper.class).updateByPrimaryKeySelective(threenetsRing);
                        log.info("修改铃音信息结果---->" + count);
                    }
                }
            } else { // 联通
                String result = swxlApi.getRingInfo(threenetsRing.getOperateId());
                if (StringUtils.isNotEmpty(result)) {
                    SwxlPubBackData<SwxlQueryPubRespone<SwxlRingMsg>> backData = SpringUtils.getBean(ObjectMapper.class).readValue(result, SwxlPubBackData.class);
                    if ("000000".equals(backData.getRecode()) && backData.isSuccess()) {
                        SwxlQueryPubRespone<SwxlRingMsg> swxlQueryPubRespone = (SwxlQueryPubRespone) backData.getData();
                        if (StringUtils.isNotNull(swxlQueryPubRespone)) {
                            List ringList = swxlQueryPubRespone.getData();
                            if (StringUtils.isNotNull(ringList)) {
                                List<SwxlRingMsg> ringMsgs = new ArrayList<>();
                                for (Object obj : ringList) {
                                    JSONObject jsonObject = JSONObject.fromObject(obj); // 将数据转成json字符串
                                    SwxlRingMsg swxlRingMsg = (SwxlRingMsg) JSONObject.toBean(jsonObject, SwxlRingMsg.class); //将json转成需要的对象
                                    ringMsgs.add(swxlRingMsg);
                                }
                                if (ringMsgs.size() > 0) {
                                    for (SwxlRingMsg swxlRingMsg : ringMsgs) {
                                        if (StringUtils.isNotNull(swxlRingMsg)) {
                                            if (ringName.equals(swxlRingMsg.getRingName())) {
                                                threenetsRing.setRemark(swxlRingMsg.getRemark());
                                                threenetsRing.setOperateRingId(swxlRingMsg.getId());
                                                // 1: 审核中 2：审核通过 3：审核不通过  4：分发失败 9：激活成功
                                                // 对应状态：1.待审核/2.激活中/3.激活成功/4.部分省份激活超时/5.部分省份激活成功/6.激活失败/7.审核不通过/8.分发失败
                                                if ("1".equals(swxlRingMsg.getStatus())) {
                                                    threenetsRing.setRingStatus(2);
                                                } else if ("2".equals(swxlRingMsg.getStatus())) {
                                                    threenetsRing.setRingStatus(2);
                                                    String msg = "下发情况：";
                                                    int falgs = 0;
                                                    boolean fgla = false;
                                                    // 获取铃音分发情况
                                                    String swxlRingFenFaAreaInfo = swxlApi.getSwxlRingFenFaAreaInfo(threenetsRing.getOperateRingId());
                                                    if (StringUtils.isNotEmpty(swxlRingFenFaAreaInfo)) {
                                                        // 铃音分发实体详情转换
                                                        SwxlPubBackData<SwxlQueryPubRespone<SwxlRingAreaFenFaInfo>> info = SpringUtils.getBean(ObjectMapper.class).readValue(swxlRingFenFaAreaInfo, SwxlPubBackData.class);
                                                        if (StringUtils.isNotNull(info) && info.isSuccess()) {
                                                            SwxlQueryPubRespone<SwxlRingAreaFenFaInfo> pageData = (SwxlQueryPubRespone) info.getData();
                                                            if (StringUtils.isNotNull(pageData) && StringUtils.isNotEmpty(pageData.getData())) {
                                                                List<SwxlRingAreaFenFaInfo> swxlRingAreaFenFaInfoList = pageData.getData();
                                                                if (StringUtils.isNotNull(swxlRingAreaFenFaInfoList)) {
                                                                    List<SwxlRingAreaFenFaInfo> swxlRingAreaFenFaInfos = new ArrayList<>();
                                                                    for (Object obj : swxlRingAreaFenFaInfoList) {
                                                                        JSONObject jsonObject = JSONObject.fromObject(obj); // 将数据转成json字符串
                                                                        SwxlRingAreaFenFaInfo swxlRingAreaFenFaInfo = (SwxlRingAreaFenFaInfo) JSONObject.toBean(jsonObject, SwxlRingAreaFenFaInfo.class); //将json转成需要的对象
                                                                        swxlRingAreaFenFaInfos.add(swxlRingAreaFenFaInfo);
                                                                    }
                                                                    if (swxlRingAreaFenFaInfos.size() > 0) {
                                                                        for (SwxlRingAreaFenFaInfo swxlRingAreaFenFaInfo : swxlRingAreaFenFaInfos) {
                                                                            //判断deployStatus是否为0,0表示成功1表示激活中2表示失败
                                                                            //判断铃音是否为激活中，如果是则跳出循环，不再判断，铃音为等待审核状态
                                                                            if (swxlRingAreaFenFaInfo != null && 1 == swxlRingAreaFenFaInfo.getDeployStatus()) {//激活中
                                                                                msg += "[" + swxlRingAreaFenFaInfo.getProvinceName() + "]激活中";
                                                                            }
                                                                            if (swxlRingAreaFenFaInfo != null && 0 == swxlRingAreaFenFaInfo.getDeployStatus()) {//成功
                                                                                msg += "[" + swxlRingAreaFenFaInfo.getProvinceName() + "]成功";
                                                                                if ("集中".equals(swxlRingAreaFenFaInfo.getProvinceName())) {
                                                                                    fgla = true;
                                                                                } else {
                                                                                    falgs++;
                                                                                }
                                                                            }
                                                                            if (swxlRingAreaFenFaInfo != null && 2 == swxlRingAreaFenFaInfo.getDeployStatus()) {//激活失败
                                                                                if ("集中".equals(swxlRingAreaFenFaInfo.getProvinceName())) {
                                                                                    msg += "[" + swxlRingAreaFenFaInfo.getProvinceName() + "]激活失败,请重新上传铃音";
                                                                                } else {
                                                                                    msg += "[" + swxlRingAreaFenFaInfo.getProvinceName() + "]激活失败";
                                                                                }
                                                                            }
                                                                            if (swxlRingAreaFenFaInfo != null && 3 == swxlRingAreaFenFaInfo.getDeployStatus()) {//重新激活
                                                                                msg += "[" + swxlRingAreaFenFaInfo.getProvinceName() + "]重新激活";
                                                                            }
                                                                        }
                                                                        threenetsRing.setRemark(msg);
                                                                    } else {
                                                                        threenetsRing.setRemark(msg + "无");
                                                                    }
                                                                    if (fgla && falgs > 0) {
                                                                        threenetsRing.setRingStatus(3);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else if ("3".equals(swxlRingMsg.getStatus())) {
                                                    threenetsRing.setRingStatus(7);
                                                    threenetsRing.setRemark(swxlRingMsg.getRemark());
                                                } else if ("4".equals(swxlRingMsg.getStatus())) {
                                                    threenetsRing.setRingStatus(8);
                                                } else if ("9".equals(swxlRingMsg.getStatus())) {
                                                    threenetsRing.setRingStatus(3);
                                                }
                                                // 修改铃音信息
                                                int count = SpringUtils.getBean(ThreenetsRingMapper.class).updateByPrimaryKeySelective(threenetsRing);
                                                log.info("修改铃音信息结果---->" + count);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        log.info("联通 刷新铃音信息出错----->" + backData.getMessage());
                    }
                }
            }
        }
        return threenetsRings;
    }

    /**
     * 定时器更新三网铃音信息
     *
     * @param threenetsRings
     * @param userName
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public void getRingInfoTimeTask(List<ThreenetsRing> threenetsRings, String userName) throws NoLoginException, IOException {
        MiguApi miguApi1 = new MiguApi();
        boolean f1 = miguApi1.loginAutoParam(userName);
        for (ThreenetsRing threenetsRing : threenetsRings) {
            Boolean f = false;
            Integer operator = threenetsRing.getOperate();
            String ringName = threenetsRing.getRingName();
            ringName = StringUtils.subString(ringName, '.');
            if (operator == 1) { // 移动
                if (f1) {
                    String result = miguApi1.findCircleRingPageById(threenetsRing.getOperateId());
                    if (StringUtils.isNotEmpty(result)) {
                        Document doc = Jsoup.parse(result);
                        Elements contents = doc.getElementsByClass("tbody_lis");
                        for (int j = 0; j < contents.size(); j++) {
                            Elements trs = contents.get(j).getElementsByTag("tr");
                            for (int i = 0; i < trs.size(); i++) {
                                Elements tds = trs.get(i).getElementsByTag("td");
                                if (tds.get(0).text().equals(ringName)) {
                                    Element el2 = tds.get(4);
                                    String ringCheckmsg = el2.attr("title");
                                    ringCheckmsg = ringCheckmsg.replace("激活", "下发");
                                    String remark = el2.text();
                                    if (StringUtils.isNotEmpty(ringCheckmsg)) {
                                        threenetsRing.setRemark(ringCheckmsg); // 运营商返回的备注信息
                                    }
                                    // 铃音状态（1.待审核/2.激活中/3.激活成功/4.部分省份激活超时/5.部分省份激活成功/6.激活失败/7.审核不通过/8.分发失败）
                                    if ("待审核".equals(remark)) {
                                        threenetsRing.setRingStatus(1);
                                    } else if ("激活中".equals(remark)) {
                                        threenetsRing.setRingStatus(2);
                                    } else if ("激活成功".equals(remark)) {
                                        threenetsRing.setRingStatus(3);
                                    } else if ("部分省份激活超时".equals(remark)) {
                                        threenetsRing.setRingStatus(4);
                                    } else if ("部分省份激活成功".equals(remark)) {
                                        threenetsRing.setRingStatus(5);
                                    } else {
                                        threenetsRing.setRingStatus(6);
                                    }
                                    if (threenetsRing.getRingStatus() == 3 || threenetsRing.getRingStatus() == 4) {
                                        Element el = tds.get(8);
                                        Elements temp = el.child(2).getElementsByTag("a");
                                        String t = temp.attr("onclick");
                                        int begin = t.indexOf("showConfirm('") + 13;
                                        int end = begin + 36;
                                        String t2 = t.substring(begin, end);
                                        threenetsRing.setOperateRingId(t2);
                                    } else if (threenetsRing.getRingStatus() == 5) {
                                        Element el = tds.get(8);
                                        Elements temp = el.child(2).getElementsByTag("a");
                                        String t = temp.attr("href");
                                        int begin = t.indexOf("ringID=") + 7;
                                        int end = begin + 36;
                                        String t2 = t.substring(begin, end);
                                        threenetsRing.setOperateRingId(t2);
                                    }
                                    // 修改铃音信息
                                    int count = SpringUtils.getBean(ThreenetsRingMapper.class).updateByPrimaryKeySelective(threenetsRing);
                                    log.info("修改铃音信息结果---->" + count);
                                }
                            }
                        }
                    }
                }
            } else if (operator == 2) { // 电信
                ThreeNetsOrderAttached attached = SpringUtils.getBean(ThreeNetsOrderAttachedMapper.class).selectByParentOrderId(threenetsRing.getOrderId());
                mcardApi.toUserList(threenetsRing.getOperateId(), attached.getMcardDistributorId());
                String result = mcardApi.getRingInfo(attached.getMcardDistributorId());
                if (StringUtils.isEmpty(result)) {
                    return;
                }
                Document doc = Jsoup.parse(result);
                Elements contents = doc.getElementsByTag("tbody");
                Elements trs = contents.get(0).getElementsByTag("tr");
                for (int i = 0; i < trs.size(); i++) {
                    Elements tds = trs.get(i).getElementsByTag("td");
                    if (tds.size() <= 0) {
                        continue;
                    }
                    if (tds.get(1).text().equals(threenetsRing.getRingName())) {
                        Element el2 = tds.get(4);
                        String remark = el2.text();
                        if (remark.equals("审核通过")) {
                            threenetsRing.setRingStatus(2);
                        } else {
                            Elements lable = tds.get(4).getElementsByTag("lable");
                            String title = lable.attr("title");
                            threenetsRing.setRingStatus(6);
                            threenetsRing.setRemark(title);
                        }
                        Element el6 = tds.get(6);
                        String value = el6.childNodes().get(1).attributes().get("data-id");
                        threenetsRing.setOperateRingId(value);
                        int count = SpringUtils.getBean(ThreenetsRingMapper.class).updateByPrimaryKeySelective(threenetsRing);
                        log.info("修改铃音信息结果---->" + count);
                    }
                }
            } else { // 联通
                String result = swxlApi.getRingInfo(threenetsRing.getOperateId());
                if (StringUtils.isNotEmpty(result)) {
                    SwxlPubBackData<SwxlQueryPubRespone<SwxlRingMsg>> backData = SpringUtils.getBean(ObjectMapper.class).readValue(result, SwxlPubBackData.class);
                    if ("000000".equals(backData.getRecode()) && backData.isSuccess()) {
                        SwxlQueryPubRespone<SwxlRingMsg> swxlQueryPubRespone = (SwxlQueryPubRespone) backData.getData();
                        if (StringUtils.isNotNull(swxlQueryPubRespone)) {
                            List ringList = swxlQueryPubRespone.getData();
                            if (StringUtils.isNotNull(ringList)) {
                                List<SwxlRingMsg> ringMsgs = new ArrayList<>();
                                for (Object obj : ringList) {
                                    JSONObject jsonObject = JSONObject.fromObject(obj); // 将数据转成json字符串
                                    SwxlRingMsg swxlRingMsg = (SwxlRingMsg) JSONObject.toBean(jsonObject, SwxlRingMsg.class); //将json转成需要的对象
                                    ringMsgs.add(swxlRingMsg);
                                }
                                if (ringMsgs.size() > 0) {
                                    for (SwxlRingMsg swxlRingMsg : ringMsgs) {
                                        if (StringUtils.isNotNull(swxlRingMsg)) {
                                            if (ringName.equals(swxlRingMsg.getRingName())) {
                                                threenetsRing.setRemark(swxlRingMsg.getRemark());
                                                threenetsRing.setOperateRingId(swxlRingMsg.getId());
                                                // 1: 审核中 2：审核通过 3：审核不通过  4：分发失败 9：激活成功
                                                // 对应状态：1.待审核/2.激活中/3.激活成功/4.部分省份激活超时/5.部分省份激活成功/6.激活失败/7.审核不通过/8.分发失败
                                                if ("1".equals(swxlRingMsg.getStatus())) {
                                                    threenetsRing.setRingStatus(2);
                                                } else if ("2".equals(swxlRingMsg.getStatus())) {
                                                    threenetsRing.setRingStatus(2);
                                                    String msg = "下发情况：";
                                                    int falgs = 0;
                                                    boolean fgla = false;
                                                    // 获取铃音分发情况
                                                    String swxlRingFenFaAreaInfo = swxlApi.getSwxlRingFenFaAreaInfo(threenetsRing.getOperateRingId());
                                                    if (StringUtils.isNotEmpty(swxlRingFenFaAreaInfo)) {
                                                        // 铃音分发实体详情转换
                                                        SwxlPubBackData<SwxlQueryPubRespone<SwxlRingAreaFenFaInfo>> info = SpringUtils.getBean(ObjectMapper.class).readValue(swxlRingFenFaAreaInfo, SwxlPubBackData.class);
                                                        if (StringUtils.isNotNull(info) && info.isSuccess()) {
                                                            SwxlQueryPubRespone<SwxlRingAreaFenFaInfo> pageData = (SwxlQueryPubRespone) info.getData();
                                                            if (StringUtils.isNotNull(pageData) && StringUtils.isNotEmpty(pageData.getData())) {
                                                                List<SwxlRingAreaFenFaInfo> swxlRingAreaFenFaInfoList = pageData.getData();
                                                                if (StringUtils.isNotNull(swxlRingAreaFenFaInfoList)) {
                                                                    List<SwxlRingAreaFenFaInfo> swxlRingAreaFenFaInfos = new ArrayList<>();
                                                                    for (Object obj : swxlRingAreaFenFaInfoList) {
                                                                        JSONObject jsonObject = JSONObject.fromObject(obj); // 将数据转成json字符串
                                                                        SwxlRingAreaFenFaInfo swxlRingAreaFenFaInfo = (SwxlRingAreaFenFaInfo) JSONObject.toBean(jsonObject, SwxlRingAreaFenFaInfo.class); //将json转成需要的对象
                                                                        swxlRingAreaFenFaInfos.add(swxlRingAreaFenFaInfo);
                                                                    }
                                                                    if (swxlRingAreaFenFaInfos.size() > 0) {
                                                                        for (SwxlRingAreaFenFaInfo swxlRingAreaFenFaInfo : swxlRingAreaFenFaInfos) {
                                                                            //判断deployStatus是否为0,0表示成功1表示激活中2表示失败
                                                                            //判断铃音是否为激活中，如果是则跳出循环，不再判断，铃音为等待审核状态
                                                                            if (swxlRingAreaFenFaInfo != null && 1 == swxlRingAreaFenFaInfo.getDeployStatus()) {//激活中
                                                                                msg += "[" + swxlRingAreaFenFaInfo.getProvinceName() + "]激活中";
                                                                            }
                                                                            if (swxlRingAreaFenFaInfo != null && 0 == swxlRingAreaFenFaInfo.getDeployStatus()) {//成功
                                                                                msg += "[" + swxlRingAreaFenFaInfo.getProvinceName() + "]成功";
                                                                                if ("集中".equals(swxlRingAreaFenFaInfo.getProvinceName())) {
                                                                                    fgla = true;
                                                                                } else {
                                                                                    falgs++;
                                                                                }
                                                                            }
                                                                            if (swxlRingAreaFenFaInfo != null && 2 == swxlRingAreaFenFaInfo.getDeployStatus()) {//激活失败
                                                                                if ("集中".equals(swxlRingAreaFenFaInfo.getProvinceName())) {
                                                                                    msg += "[" + swxlRingAreaFenFaInfo.getProvinceName() + "]激活失败,请重新上传铃音";
                                                                                } else {
                                                                                    msg += "[" + swxlRingAreaFenFaInfo.getProvinceName() + "]激活失败";
                                                                                }
                                                                            }
                                                                            if (swxlRingAreaFenFaInfo != null && 3 == swxlRingAreaFenFaInfo.getDeployStatus()) {//重新激活
                                                                                msg += "[" + swxlRingAreaFenFaInfo.getProvinceName() + "]重新激活";
                                                                            }
                                                                        }
                                                                        threenetsRing.setRemark(msg);
                                                                    } else {
                                                                        threenetsRing.setRemark(msg + "无");
                                                                    }
                                                                    if (fgla && falgs > 0) {
                                                                        threenetsRing.setRingStatus(3);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else if ("3".equals(swxlRingMsg.getStatus())) {
                                                    threenetsRing.setRingStatus(7);
                                                    threenetsRing.setRemark(swxlRingMsg.getRemark());
                                                } else if ("4".equals(swxlRingMsg.getStatus())) {
                                                    threenetsRing.setRingStatus(8);
                                                } else if ("9".equals(swxlRingMsg.getStatus())) {
                                                    threenetsRing.setRingStatus(3);
                                                }
                                                // 修改铃音信息
                                                int count = SpringUtils.getBean(ThreenetsRingMapper.class).updateByPrimaryKeySelective(threenetsRing);
                                                log.info("修改铃音信息结果---->" + count);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        log.info("联通 刷新铃音信息出错----->" + backData.getMessage());
                    }
                }
            }
        }
    }

    /**
     * 设置铃音
     *
     * @param phones
     * @param threenetsRing
     * @param operate
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public AjaxResult setRing(String phones, ThreenetsRing threenetsRing, Integer operate, Integer orderId) throws IOException, NoLoginException {
        operate = 2;
        String sucMsg = "";
        String errMsg = "";
        int failure = 0;
        if (operate == 1) { // 移动
            String result = miguApi.setCircleRingById4User(phones, threenetsRing.getOperateRingId(), threenetsRing.getOperateId());
            RingSetResult rsr = (RingSetResult) JsonUtil.getObject4JsonString(result, RingSetResult.class);
            if (rsr.isSuccess()) {
                String[] phoness = phones.split(",");
                for (String phone : phoness) {
                    String ringName = StringUtils.subString(threenetsRing.getRingName(), '.');
                    // 根据父级订单ID以及电话号码查询子级订单信息
                    ThreenetsChildOrder threenetsChildOrder = SpringUtils.getBean(ThreenetsChildOrderMapper.class).findChildOrderByOrderIdAndPhone(orderId, phone);
                    if (threenetsChildOrder.getIsMonthly() == 2) {
                        threenetsChildOrder.setRingId(threenetsRing.getId());
                        threenetsChildOrder.setRingName(ringName);
                        threenetsChildOrder.setIsVideoUser(threenetsRing.getRingType().equals("视频") ? true : false);
                        threenetsChildOrder.setIsRingtoneUser(threenetsRing.getRingType().equals("视频") ? false : true);
                        threenetsChildOrder.setRemark("您的请求已受理，请稍后在【商户列表 ->号码管理】中点击刷新操作查看");
                        // 执行修改子订单操作
                        int count = SpringUtils.getBean(ThreenetsChildOrderMapper.class).updateThreeNetsChidOrder(threenetsChildOrder);
                        if (count <= 0) {
                            failure++;
                        }
                    }
                }
                if (failure == 0) {
                    sucMsg = "您的请求已受理，请稍后在【商户列表 ->号码管理】中点击刷新操作查看!";
                } else {
                    errMsg = "执行修改子订单信息出错！";
                }
            } else {
                failure++;
                errMsg = rsr.getMsg();
            }
        } else if (operate == 2) { // 电信
            String apersonnelId = "";
            String ringId = "";
            ThreeNetsOrderAttached attached = SpringUtils.getBean(ThreeNetsOrderAttachedMapper.class).selectByParentOrderId(orderId);
            //跳转到商户
            mcardApi.toUserList(attached.getMcardId(), attached.getMcardDistributorId());
            String userList = mcardApi.getUserInfo(attached.getMcardDistributorId());
            String[] phoness = phones.split(",");
            if (StringUtils.isEmpty(threenetsRing.getOperateRingId())){
                //跳转到铃音页面
                String ringList = mcardApi.toRingList(attached.getMcardDistributorId());
                //String ringList = mcardApi.toRingAlertList(apersonnelId,"61203" );
                if (StringUtils.isNotEmpty(ringList)) {
                    Document doc = Jsoup.parse(ringList);
                    Elements contents = doc.getElementsByTag("tbody");
                    Elements trs = contents.get(0).getElementsByTag("tr");
                    for (int i = 0; i < trs.size(); i++) {
                        Elements tds = trs.get(i).getElementsByTag("td");
                        if (tds.size() <= 0) {
                            continue;
                        }
                        if (tds.get(1).text().equals(threenetsRing.getRingName())) {
                            ringId = trs.get(i).attributes().get("id");
                        }
                    }
                }
            }else{
                ringId = threenetsRing.getOperateRingId();
            }
            if (StringUtils.isNotEmpty(userList)) {
                for (String phone : phoness) {
                    ThreenetsChildOrder threenetsChildOrder = SpringUtils.getBean(ThreenetsChildOrderMapper.class).findChildOrderByOrderIdAndPhone(orderId, phone);
                    Document doc = Jsoup.parse(userList);
                    Elements contents = doc.getElementsByTag("tbody");
                    Elements trs = contents.get(0).getElementsByTag("tr");
                    for (int i = 0; i < trs.size(); i++) {
                        Elements tds = trs.get(i).getElementsByTag("td");
                        if (tds.size() <= 0) {
                            continue;
                        }
                        if (tds.get(2).text().equals(phone)) {
                            Elements tag = tds.get(11).getElementsByTag("i");
                            Element element = tag.get(2);
                            apersonnelId = element.attributes().get("data-id");
                            String text = element.attributes().get("data-name");
                            String result = mcardApi.settingRing(ringId, apersonnelId, "61204");
                            if (StringUtils.isNotEmpty(result) && result.indexOf("0000") > 0) {
                                String ringName = StringUtils.subString(threenetsRing.getRingName(), '.');
                                threenetsChildOrder.setRingName(ringName);
                                threenetsChildOrder.setRemark("您的请求已受理，请稍后在【商户列表 ->号码管理】中点击刷新操作查看");
                                threenetsChildOrder.setRingId(threenetsRing.getId());
                                threenetsChildOrder.setIsVideoUser(false);
                                threenetsChildOrder.setIsRingtoneUser(true);
                                int count = SpringUtils.getBean(ThreenetsChildOrderMapper.class).updateThreeNetsChidOrder(threenetsChildOrder);
                                if (count <= 0) {
                                    failure++;
                                }
                                sucMsg = "您的请求已受理，请稍后在【商户列表 ->号码管理】中点击刷新操作查看!";
                            } else {
                                failure++;
                            }
                        }
                    }

                }
            }
        } else { // 联通
            String[] phoness = phones.split(",");
            for (String phone : phoness) {
                String result = swxlApi.setRingForPhone(phone, threenetsRing.getOperateRingId());
                if (StringUtils.isNotEmpty(result)) {
                    SwxlBaseBackMessage<SwxlAddPhoneNewResult> info = SpringUtils.getBean(ObjectMapper.class).readValue(result, SwxlBaseBackMessage.class);
                    if (StringUtils.isNotNull(info) && "000000".equals(info.getRecode()) && info.isSuccess()) {
                        String ringName = StringUtils.subString(threenetsRing.getRingName(), '.');
                        // 根据父级订单ID以及电话号码查询子级订单信息
                        ThreenetsChildOrder threenetsChildOrder = SpringUtils.getBean(ThreenetsChildOrderMapper.class).findChildOrderByOrderIdAndPhone(orderId, phone);
                        if (threenetsChildOrder.getIsMonthly() == 2) {
                            threenetsChildOrder.setRingId(threenetsRing.getId());
                            threenetsChildOrder.setRingName(ringName);
                            threenetsChildOrder.setIsVideoUser(false);
                            threenetsChildOrder.setIsRingtoneUser(true);
                            threenetsChildOrder.setRemark("您的请求已受理，请稍后在【商户列表 ->号码管理】中点击刷新操作查看");
                            // 执行修改子订单操作
                            int count = SpringUtils.getBean(ThreenetsChildOrderMapper.class).updateThreeNetsChidOrder(threenetsChildOrder);
                            if (count <= 0) {
                                failure++;
                            }
                        }
                        if (failure == 0) {
                            sucMsg = "您的请求已受理，请稍后在【商户列表 ->号码管理】中点击刷新操作查看!";
                        } else {
                            errMsg = "执行修改子订单信息出错！";
                        }
                    } else {
                        failure++;
                        errMsg += "[" + phone + "]:" + info.getMessage();
                    }
                }
            }
        }
        if (failure > 0) {
            return AjaxResult.error(errMsg);
        } else {
            return AjaxResult.success(true, sucMsg);
        }
    }


    /**
     * 保存移动订单
     *
     * @param order
     * @param attached
     * @return
     */
    public MiguAddGroupRespone addOrderByYd(ThreenetsOrder order, ThreeNetsOrderAttached attached) throws IOException, NoLoginException {
        MiguAddGroupRespone addGroupResponse = null;
        //进行同步到服务器，同步3次。
        for (int i = 0; i < 3; i++) {// 重试添加3次
            addGroupResponse = miguApi.add1(order, attached);
            if (addGroupResponse != null) {
                break;
            }
        }
        return addGroupResponse;
    }

    /**
     * 保存联通订单
     *
     * @param ringOrder
     * @param attached
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public SwxlGroupResponse addOrderByLt(ThreenetsOrder ringOrder, ThreeNetsOrderAttached attached) throws IOException, NoLoginException {
        SwxlGroupResponse swxlGroupResponse = null;
        // 添加商户,同步5次
        for (int i = 0; i < 5; i++) {
            swxlGroupResponse = swxlApi.addGroup(ringOrder, attached);
            if (swxlGroupResponse != null && 0 == swxlGroupResponse.getStatus()) {
                break;
            }
        }
        return swxlGroupResponse;
    }

    /**
     * 保存电信订单
     *
     * @param order
     * @param attached
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public McardAddGroupRespone addOrderByDx(ThreenetsOrder order, ThreeNetsOrderAttached attached) {
        McardAddGroupRespone mcardAddGroupRespone = null;
        boolean flag = ConfigUtil.getAreaArray("unable_to_open_area", order.getProvince());
        if (flag) {
            mcardAddGroupRespone = new McardAddGroupRespone();
            mcardAddGroupRespone.setCode(Const.ILLEFAL_AREA);
            mcardAddGroupRespone.setMessage("电信当前不提供" + order.getProvince() + "地区的服务");
            return mcardAddGroupRespone;
        }
        //获取商户所属地区
        if (order.getProvince().equals("河南")) {
            attached.setMcardPrice(Const.TELECOM_10_YUAN_TRAIFF);
            attached.setMcardDistributorId(Const.parent_Distributor_ID_177);
            mcardApi.toNormalUser(Const.child_Distributor_ID_177, Const.parent_Distributor_ID_177);
        } else if (configUtil.getAreaArray("ten_yuan_tariff_area", order.getProvince())) {
            attached.setMcardPrice(Const.TELECOM_10_YUAN_TRAIFF);
            attached.setMcardDistributorId(Const.parent_Distributor_ID_188);
            mcardApi.toNormalUser(Const.child_Distributor_ID_188, Const.parent_Distributor_ID_188);
        } else {
            attached.setMcardPrice(Const.TELECOM_20_YUAN_TRAIFF);
            attached.setMcardDistributorId(Const.parent_Distributor_ID_188);
            mcardApi.toNormalUser(Const.child_Distributor_ID_181, Const.parent_Distributor_ID_188);
        }
        //添加商户，同步三次
        for (int i = 0; i < 3; i++) {
            mcardAddGroupRespone = mcardApi.addGroup(order, attached);
            if (mcardAddGroupRespone != null && StringUtils.isNotEmpty(mcardAddGroupRespone.getCode())) {
                break;
            }
        }
        return mcardAddGroupRespone;
    }

    /**
     * 保存铃音
     *
     * @param ring
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public MiguAddRingRespone saveMiguRing(ThreenetsRing ring) throws IOException, NoLoginException {
        String trade = "其他普通行业";
        return miguApi.saveRing(ring, trade);
    }


    /**
     * 联通-添加铃音
     *
     * @param ring
     * @param circleID
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public boolean addRingByLt(ThreenetsRing ring, String circleID) throws IOException, NoLoginException {
        return swxlApi.addRing(ring, circleID);
    }


    /**
     * 电信-上传彩铃
     *
     * @param ring
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public boolean addRingByDx(ThreenetsRing ring, ThreeNetsOrderAttached attached) {
        mcardApi.toUserList(attached.getMcardId(), attached.getMcardDistributorId());
        return mcardApi.uploadRing(ring);
    }

    /**
     * 移动 添加成员
     *
     * @param childOrder
     * @param circleId
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public MiguAddPhoneRespone addPhoneByYd(ThreenetsChildOrder childOrder, String circleId) throws IOException, NoLoginException {
        return miguApi.addPhone(childOrder.getLinkmanTel(), circleId);
    }

    /**
     * 联通 添加成员
     *
     * @param childOrder
     * @param circleId
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public SwxlBaseBackMessage addPhoneByLt(ThreenetsChildOrder childOrder, String circleId) throws IOException, NoLoginException {
        return swxlApi.addPhone(childOrder.getLinkmanTel(), circleId);
    }

    /**
     * 电信添加成员
     *
     * @param orders
     * @param circleId
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public List<ThreenetsChildOrder> addPhoneByDx(List<ThreenetsChildOrder> orders, String circleId, String distributorId) {
        List<ThreenetsChildOrder> newList = new ArrayList<>();
        mcardApi.toUserList(circleId, distributorId);
        for (int i = 0; i < orders.size(); i++) {
            ThreenetsChildOrder childOrder = orders.get(i);
            if (ConfigUtil.getAreaArray("unable_to_open_area", childOrder.getProvince())) {
                childOrder.setStatus(Const.FAILURE_REVIEW);
                childOrder.setRemark("电信当前不提供" + childOrder.getProvince() + "地区的服务");
                newList.add(childOrder);
                continue;
            }
            McardAddPhoneRespone mcardAddPhoneRespone = mcardApi.addApersonnel(orders.get(i), distributorId);
            childOrder.setRemark(mcardAddPhoneRespone.getMessage());
            if (mcardAddPhoneRespone.getCode().equals("0000")) {
                childOrder.setStatus("审核成功");
                newList.add(childOrder);
            }
        }
        return newList;
    }

    /**
     * 添加子账号
     *
     * @param user
     * @return
     */
    public AjaxResult insertUser(User user) throws NoLoginException, IOException {
        String msg = "创建失败！";
        // 执行添加联通子渠道商
        String result = swxlApi.addChild(user);
        if (StringUtils.isNotEmpty(result)) {
            SwxlBaseBackMessage<Object> createUser = SpringUtils.getBean(ObjectMapper.class).readValue(result, SwxlBaseBackMessage.class);
            // 接口返回成功
            if (StringUtils.isNotNull(createUser) && "000000".equals(createUser.getRecode()) && createUser.isSuccess()) {
                // 执行添加到本地数据库
                int count = SpringUtils.getBean(UserMapper.class).insertUser(user);
                if (count > 0) {
                    // 修改文件状态
                    SpringUtils.getBean(FileService.class).updateStatus(user.getUserCardFan());
                    SpringUtils.getBean(FileService.class).updateStatus(user.getUserCardZhen());
                    return AjaxResult.success(true, "创建成功！");
                } else {
                    msg = "执行添加本地数据库出错！";
                }
            } else {
                msg = createUser.getMessage();
            }
        }
        return AjaxResult.error(msg);
    }

    /**
     * 移动工具箱-->用户信息
     *
     * @param ringMsisdn
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public AjaxResult getUserInfoByRingMsisdn(String ringMsisdn) throws NoLoginException, IOException {
        if (StringUtils.isNotNull(ringMsisdn)) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("userInfo", miguApi.getUserInfoByRingMsisdn(ringMsisdn));
            return AjaxResult.success(map, "查找到了");
        }
        return AjaxResult.success(false, "参数不正确");
    }

    /**
     * 联通工具箱-->用户信息-->获取沉默用户信息
     *
     * @param phoneNumber
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public String getSilentMemberByMsisdn(String phoneNumber) throws NoLoginException, IOException {
        if (StringUtils.isNotNull(phoneNumber)) {
            return swxlApi.getSilentMemberByMsisdn(phoneNumber);
        } else {
            return "未获取到用户号码";
        }
    }

    /**
     * 联通工具箱-->用户信息-->获取用户操作记录
     *
     * @param phoneNumber
     * @return
     * @throws NoLoginException
     * @throws IOException
     */
    public String getSystemLogListByMsisdn(String phoneNumber) throws NoLoginException, IOException {
        if (StringUtils.isNotNull(phoneNumber)) {
            return swxlApi.getSystemLogListByMsisdn(phoneNumber);
        } else {
            return "未获取到用户号码";
        }
    }

    //    String userInfo =null;
//		try {
//        MiguApi miguApi = null;
//        Object obj2 = session.getAttribute("miguApiLT");
//        if (obj2 instanceof MiguApiLT) {
//            miguApi = (MiguApiLT) obj2;
//        }
//        if (miguApi == null) {
//            miguApi = new MiguApiLT();
//            session.setAttribute("miguApiLT", miguApi);
//        }
//        userInfo = miguApi.getUserInfo(msisdn);
//    } catch (MiguNologinException e) {
//        System.out.println("工具箱--》用户信息出错{["+e.getMessage()+"]}");
//    }
//		return userInfo;

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
            Map<String, String> map = new HashMap<String, String>();
            map.put("ringSettingListByMsisdn", miguApi.getRingSettingListByMsisdn(msisdn));
            map.put("ringListByMsisdn", miguApi.getRingListByMsisdn(msisdn));
            return AjaxResult.success(map, "查找到了");
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
            Map<String, String> map = new HashMap<String, String>();
            String jsonStr = "[{\"toneID\":\"" + toneID + "\",\"type\":\"" + type + "\",\"settingID\":\"" + settingID + "\"}]";
            String delRingSetting = miguApi.delRingSetting(jsonStr, msisdn);
            map.put("delRingSetting", delRingSetting);
            if (delRingSetting.contains("true")) {
                return AjaxResult.success(map, "删除成功！");
            }
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
//        MiguApi miguApi = null;
//        Object obj2 = session.getAttribute("miguApiLT");
//        if (obj2 instanceof MiguApiLT) {
//            miguApi = (MiguApiLT) obj2;
//        }
//        if (miguApi == null) {
//            miguApi = new MiguApiLT();
//            session.setAttribute("miguApiLT", miguApi);
//        }
//        String delRing = miguApi.delOtherRing(toneIds+"|"+type, msisdn);
//        if (delRing.contains("true")) {
//            return true;
//        }
        if (StringUtils.isNotNull(msisdn) && StringUtils.isNotNull(toneIds) && StringUtils.isNotNull(type)) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("delRing", miguApi.delOtherRing(toneIds + "|" + type, msisdn));
            return AjaxResult.success(map, "删除成功");
        }
        return AjaxResult.success(false, "参数不正确");
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
            Map<String, String> map = new HashMap<String, String>();
            String jsonStr = "[" + vals + "]";
            map.put("delRingSetting", miguApi.delRingSetting(jsonStr, msisdn));
            return AjaxResult.success(map, "删除成功！");
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
            Map<String, String> map = new HashMap<String, String>();
            String jsonStr = "[" + vals + "]";
            map.put("delRing", miguApi.delOtherRing(jsonStr, msisdn));
            return AjaxResult.success(map, "删除成功！");
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
            Map<String, String> map = new HashMap<String, String>();
            map.put("delSilentMember", swxlApi.deleteSilentMemberByMsisdn(msisdn));
            return AjaxResult.success(map, "删除成功");
        }
        return AjaxResult.success(false, "参数不正确");
    }

    /**
     * 电信上传文件
     *
     * @param file
     * @return
     */
    public String mcardUploadFile(File file) {
        return mcardApi.uploadFile(file);
    }

    /**
     * 商户列表-->信息处理
     *
     * @param migu_id
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public AjaxResult findCricleMsgList(String migu_id) throws IOException, NoLoginException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("cricle_msg_list", miguApi.findCricleMsgList(migu_id));
        return AjaxResult.success(map, "通过migu_id找到了处理信息");
    }
}
