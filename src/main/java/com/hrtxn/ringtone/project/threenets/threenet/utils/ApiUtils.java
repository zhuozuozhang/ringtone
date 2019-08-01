package com.hrtxn.ringtone.project.threenets.threenet.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrtxn.ringtone.common.api.MiguApi;
import com.hrtxn.ringtone.common.api.SwxlApi;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.exception.NoLoginException;
import com.hrtxn.ringtone.common.utils.SpringUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.common.utils.json.JsonUtil;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreeNetsOrderAttached;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsChildOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsRing;
import com.hrtxn.ringtone.project.threenets.threenet.json.migu.MiguAddGroupRespone;
import com.hrtxn.ringtone.project.threenets.threenet.json.migu.RefreshVbrtStatusResult;
import com.hrtxn.ringtone.project.threenets.threenet.json.migu.RingSetResult;
import com.hrtxn.ringtone.project.threenets.threenet.json.swxl.*;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsChildOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsRingMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
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
                            JSONObject jsonObject=JSONObject.fromObject(obj); // 将数据转成json字符串
                            SwxlPhoneInfoResult swxlPhoneInfoResult = (SwxlPhoneInfoResult)JSONObject.toBean(jsonObject, SwxlPhoneInfoResult.class); //将json转成需要的对象
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
                                Elements temp = el.child(2).getElementsByTag("a");
                                String t = temp.attr("onclick");
                                int begin = t.indexOf("showConfirm('") + 13;
                                int end = begin + 36;
                                String t2 = t.substring(begin, end);
                                threenetsRing.setOperateRingId(t2);
                            } else {
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
                            break;
                        }
                    }
                }
            } else if (operator == 2) { // 电信

            } else { // 联通
                String result = swxlApi.getRingInfo(threenetsRing.getOperateId());
                if (StringUtils.isNotEmpty(result)) {
                    SwxlPubBackData<SwxlQueryPubRespone<SwxlRingMsg>> backData = SpringUtils.getBean(ObjectMapper.class).readValue(result,SwxlPubBackData.class);
                    if ("000000".equals(backData.getRecode()) && backData.isSuccess()){
                        SwxlQueryPubRespone<SwxlRingMsg> swxlQueryPubRespone = (SwxlQueryPubRespone) backData.getData();
                        if (StringUtils.isNotNull(swxlQueryPubRespone)) {
                            List ringList = swxlQueryPubRespone.getData();
                            if (StringUtils.isNotNull(ringList)){
                                List<SwxlRingMsg> ringMsgs = new ArrayList<>();
                                for (Object obj : ringList) {
                                    JSONObject jsonObject=JSONObject.fromObject(obj); // 将数据转成json字符串
                                    SwxlRingMsg swxlRingMsg = (SwxlRingMsg)JSONObject.toBean(jsonObject, SwxlRingMsg.class); //将json转成需要的对象
                                    ringMsgs.add(swxlRingMsg);
                                }
                                if (ringMsgs.size() > 0){
                                    for (SwxlRingMsg swxlRingMsg:ringMsgs){
                                        if (StringUtils.isNotNull(swxlRingMsg)){
                                            if (ringName.equals(swxlRingMsg.getRingName())){
                                                threenetsRing.setRemark(swxlRingMsg.getRemark());
                                                threenetsRing.setOperateRingId(swxlRingMsg.getId());
                                                // 1: 审核中 2：审核通过 3：审核不通过  4：分发失败 9：激活成功
                                                // 对应状态：1.待审核/2.激活中/3.激活成功/4.部分省份激活超时/5.部分省份激活成功/6.激活失败/7.审核不通过/8.分发失败
                                                if ("1".equals(swxlRingMsg.getStatus())){
                                                    threenetsRing.setRingStatus(2);
                                                } else if ("2".equals(swxlRingMsg.getStatus())){
                                                    threenetsRing.setRingStatus(2);
                                                    String msg = "下发情况：";
                                                    int falgs = 0;
                                                    boolean fgla = false;
                                                    // 获取铃音分发情况
                                                    String swxlRingFenFaAreaInfo = swxlApi.getSwxlRingFenFaAreaInfo(threenetsRing.getOperateRingId());
                                                    if (StringUtils.isNotEmpty(swxlRingFenFaAreaInfo)) {
                                                        // 铃音分发实体详情转换
                                                        SwxlPubBackData<SwxlQueryPubRespone<SwxlRingAreaFenFaInfo>> info = SpringUtils.getBean(ObjectMapper.class).readValue(swxlRingFenFaAreaInfo,SwxlPubBackData.class);
                                                        if (StringUtils.isNotNull(info) && info.isSuccess()){
                                                            SwxlQueryPubRespone<SwxlRingAreaFenFaInfo> pageData = (SwxlQueryPubRespone) info.getData();
                                                            if (StringUtils.isNotNull(pageData) && StringUtils.isNotEmpty(pageData.getData())) {
                                                                List<SwxlRingAreaFenFaInfo> swxlRingAreaFenFaInfoList = pageData.getData();
                                                                if (StringUtils.isNotNull(swxlRingAreaFenFaInfoList)){
                                                                    List<SwxlRingAreaFenFaInfo> swxlRingAreaFenFaInfos = new ArrayList<>();
                                                                    for (Object obj : swxlRingAreaFenFaInfoList) {
                                                                        JSONObject jsonObject=JSONObject.fromObject(obj); // 将数据转成json字符串
                                                                        SwxlRingAreaFenFaInfo swxlRingAreaFenFaInfo = (SwxlRingAreaFenFaInfo)JSONObject.toBean(jsonObject, SwxlRingAreaFenFaInfo.class); //将json转成需要的对象
                                                                        swxlRingAreaFenFaInfos.add(swxlRingAreaFenFaInfo);
                                                                    }
                                                                    if (swxlRingAreaFenFaInfos.size() > 0){
                                                                        for (SwxlRingAreaFenFaInfo swxlRingAreaFenFaInfo : swxlRingAreaFenFaInfos) {
                                                                            //判断deployStatus是否为0,0表示成功1表示激活中2表示失败
                                                                            //判断铃音是否为激活中，如果是则跳出循环，不再判断，铃音为等待审核状态
                                                                            if(swxlRingAreaFenFaInfo != null && 1 ==swxlRingAreaFenFaInfo.getDeployStatus()){//激活中
                                                                                msg += "["+swxlRingAreaFenFaInfo.getProvinceName()+"]激活中";
                                                                            }
                                                                            if(swxlRingAreaFenFaInfo != null && 0 ==swxlRingAreaFenFaInfo.getDeployStatus()){//成功
                                                                                msg += "["+swxlRingAreaFenFaInfo.getProvinceName()+"]成功";
                                                                                if("集中".equals(swxlRingAreaFenFaInfo.getProvinceName())){
                                                                                    fgla = true;
                                                                                }else{
                                                                                    falgs++;
                                                                                }
                                                                            }
                                                                            if(swxlRingAreaFenFaInfo != null && 2 ==swxlRingAreaFenFaInfo.getDeployStatus()){//激活失败
                                                                                if("集中".equals(swxlRingAreaFenFaInfo.getProvinceName())){
                                                                                    msg += "["+swxlRingAreaFenFaInfo.getProvinceName()+"]激活失败,请重新上传铃音";
                                                                                }else{
                                                                                    msg += "["+swxlRingAreaFenFaInfo.getProvinceName()+"]激活失败";
                                                                                }
                                                                            }
                                                                            if(swxlRingAreaFenFaInfo != null && 3 ==swxlRingAreaFenFaInfo.getDeployStatus()){//重新激活
                                                                                msg += "["+swxlRingAreaFenFaInfo.getProvinceName()+"]重新激活";
                                                                            }
                                                                        }
                                                                        threenetsRing.setRemark(msg);
                                                                    }else{
                                                                        threenetsRing.setRemark(msg+"无");
                                                                    }
                                                                    if(fgla && falgs > 0){
                                                                        threenetsRing.setRingStatus(3);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else if ("3".equals(swxlRingMsg.getStatus())){
                                                    threenetsRing.setRingStatus(7);
                                                    threenetsRing.setRemark(swxlRingMsg.getRemark());
                                                } else if ("4".equals(swxlRingMsg.getStatus())){
                                                    threenetsRing.setRingStatus(8);
                                                } else if ("9".equals(swxlRingMsg.getStatus())){
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
                    }else{
                        log.info("联通 刷新铃音信息出错----->"+backData.getMessage());
                    }
                }
            }
        }
        return threenetsRings;
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
                        threenetsChildOrder.setRingName(ringName);
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

        } else { // 联通
            String[] phoness = phones.split(",");
            for (String phone : phoness) {
                String result = swxlApi.setRingForPhone(phone, threenetsRing.getOperateRingId());
                if (StringUtils.isNotEmpty(result)){
                    SwxlBaseBackMessage<SwxlAddPhoneNewResult> info = SpringUtils.getBean(ObjectMapper.class).readValue(result,SwxlBaseBackMessage.class);
                    if (StringUtils.isNotNull(info) && "000000".equals(info.getRecode()) && info.isSuccess()){
                        String ringName = StringUtils.subString(threenetsRing.getRingName(), '.');
                        // 根据父级订单ID以及电话号码查询子级订单信息
                        ThreenetsChildOrder threenetsChildOrder = SpringUtils.getBean(ThreenetsChildOrderMapper.class).findChildOrderByOrderIdAndPhone(orderId, phone);
                        if (threenetsChildOrder.getIsMonthly() == 2) {
                            threenetsChildOrder.setRingName(ringName);
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
                    }else{
                        failure++;
                        errMsg += "["+phone+"]:"+info.getMessage();
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
            addGroupResponse = miguApi.add(order, attached);
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
     * 保存铃音
     *
     * @param ring
     * @param circleID
     * @param groupName
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public AjaxResult saveMiguRing(ThreenetsRing ring, String circleID, String groupName) throws IOException, NoLoginException {
        return miguApi.saveRing(ring, circleID, groupName);
    }

    /**
     * 联通保存铃音
     *
     * @param ring
     * @param circleID
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public String saveSwxlRing(ThreenetsRing ring, String circleID) throws IOException, NoLoginException {
        return swxlApi.addRing(ring, circleID);
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
    public String addRingByLt(ThreenetsRing ring, String circleID) throws IOException, NoLoginException {
        return swxlApi.addRing(ring, circleID);
    }


    /**
     * 移动 添加成员
     *
     * @param orders
     * @param circleId
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public String addPhoneByYd(List<ThreenetsChildOrder> orders, String circleId) throws IOException, NoLoginException {
        if (circleId == null) {
            return "集团ID错误！";
        }
        String data = "";
        for (int i = 0; i < orders.size(); i++) {
            data = data + orders.get(i).getLinkmanTel() + (i == orders.size() - 1 ? "" : ",");
        }
        return miguApi.addPhone(data, circleId);
    }

    /**
     * 联通 添加成员
     *
     * @param orders
     * @param circleId
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public String addPhoneByLt(List<ThreenetsChildOrder> orders, String circleId) throws IOException, NoLoginException {
        if (circleId == null) {
            return "集团ID错误";
        }
        String data = "";
        for (int i = 0; i < orders.size(); i++) {
            data = data + orders.get(i).getLinkmanTel() + (i == orders.size() - 1 ? "" : ",");
        }
        return swxlApi.addPhone(data, circleId);
    }

}
