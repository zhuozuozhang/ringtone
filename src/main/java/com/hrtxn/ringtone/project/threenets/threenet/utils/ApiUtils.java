package com.hrtxn.ringtone.project.threenets.threenet.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrtxn.ringtone.common.api.MiguApi;
import com.hrtxn.ringtone.common.api.SwxlApi;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.exception.NoLoginException;
import com.hrtxn.ringtone.common.utils.SpringUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.common.utils.json.JsonUtil;
import com.hrtxn.ringtone.project.system.File.domain.Uploadfile;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsChildOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsRing;
import com.hrtxn.ringtone.project.threenets.threenet.json.migu.MiguAddGroupRespone;
import com.hrtxn.ringtone.project.threenets.threenet.json.migu.RefreshVbrtStatusResult;
import com.hrtxn.ringtone.project.threenets.threenet.json.swxl.*;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsChildOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsRingMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
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
     * @param flag 标识是否是下发链接短信 1、普通短信/2、链接短信
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
                    if (StringUtils.isNotEmpty(result)){
                        SwxlPubBackData info = (SwxlPubBackData) JsonUtil.getObject4JsonString(result, SwxlPubBackData.class);
                        if (!"000000".equals(info.getRecode()) || !info.isSuccess()) {
                            msg2 += "["+t.getLinkmanTel()+":"+info.getMessage()+"]";
                            failure++;
                        }
                    }
                }
            } else {
                if (t.getOperator() == 3) { // 链接短信，联通专属
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
     * 刷新铃音信息
     * @param threenetsRings
     * @return
     */
    public AjaxResult getRingInfo(List<ThreenetsRing> threenetsRings) throws NoLoginException, IOException {
        String msg = "错误消息：";
        int failure = 0;
        for (ThreenetsRing threenetsRing:threenetsRings) {
            Boolean f = false;
            Integer operator = threenetsRing.getOperate();
            if (operator ==1){ // 移动
                String result = miguApi.findCircleRingPageById(threenetsRing.getOperateId());
                if (StringUtils.isNotEmpty(result)) {
                    Document doc = Jsoup.parse(result);
                    Elements contents = doc.getElementsByClass("tbody_lis");
                    String ringName = threenetsRing.getRingName();
                    ringName = StringUtils.subString(ringName, '.');
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
                            // 铃音状态（1.待审核/2.激活中/3.激活成功/4.部分省份激活超时/5.部分省份激活成功/6.激活失败）
                            if ("待审核".equals(remark)){
                                threenetsRing.setRingStatus(1);
                            } else if ("激活中".equals(remark)){
                                threenetsRing.setRingStatus(2);
                            }else if ("激活成功".equals(remark)) {
                                threenetsRing.setRingStatus(3);
                            } else if ("部分省份激活超时".equals(remark)) {
                                threenetsRing.setRingStatus(4);
                            } else if ("部分省份激活成功".equals(remark)) {
                                threenetsRing.setRingStatus(5);
                            } else  {
                                threenetsRing.setRingStatus(6);
                            }
                            if (threenetsRing.getRingStatus() != 5){
                                Element el = tds.get(8);
                                Elements temp = el.child(2).getElementsByTag("a");
                                String t = temp.attr("onclick");
                                int begin = t.indexOf("showConfirm('") + 13;
                                int end = begin + 36;
                                String t2 = t.substring(begin, end);
                                threenetsRing.setOperateRingId(t2);
                            }else {
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

                            break;
                        }
                    }
                } else {
                    failure++;
                    msg += "["+threenetsRing.getRingName()+":更新出错！]";
                }
            } else if(operator == 2){ // 电信

            } else { // 联通

            }
            if(f){
                // 更改铃音信息

            }
        }
        if(failure > 0){
            return AjaxResult.success(false,msg);
        }else {
            return AjaxResult.success(true,"刷新成功！");
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
//                    if (order.getPaymentPrice() == 3 || order.getPaymentPrice() == 5) {
//                        order.setStatus("审核通过");
//                    } else {
//                        order.setStatus("待审核");
//                    }
                }
            return AjaxResult.success(addGroupResponse,addGroupResponse.getMsg());
        }catch (Exception e){
            log.error("对接移动 方法：saveOrderByYd  错误信息", e);
            return AjaxResult.error("保存失败");
        }
    }

    /**
     * 保存联通订单
     *
     * @param ringOrder
     * @param attachments
     * @param ring
     * @return
     * @throws IOException
     * @throws NoLoginException
     */
    public AjaxResult saveOrderByLt(ThreenetsOrder ringOrder, List<Uploadfile> attachments, ThreenetsRing ring){
        try{
            // 1.增加附件
            if (attachments != null && attachments.size() > 0) {
                for (int i = 0; i < attachments.size(); i++) {
                    Uploadfile t = attachments.get(i);
                    if (StringUtils.isEmpty(t.getXh()) || t.getId()==null) {
                        // 删除集合
                        attachments.remove(i);
                        --i;
                    }
                }
            }
            if (attachments != null && attachments.size() > 0) {
                // 附件表已经删除了
                //attachmentsRepository.addAttachments(attachments);
            }
            // 添加到音乐名片系统
            SwxlGroupResponse swxlGroupResponse = null;
            // 添加商户
            for (int i = 0; i < 5; i++) {
                swxlGroupResponse = swxlApi.addGroup(ringOrder, ring);
                if (swxlGroupResponse != null && 0 == swxlGroupResponse.getStatus()) {
                    break;
                }
            }
            // status 0正常 1异常
            if (swxlGroupResponse != null && 0 == swxlGroupResponse.getStatus()) {
                // 向商户发送开通短信,发送短信的成功或者失败,不影响后续操作
                boolean swxlSendPhoneSMS = swxlApi.SwxlSendPhoneSMS(swxlGroupResponse.getId());
                /* 添加铃音 */
                ring.setOperateId(ringOrder.getMcardId());
                SwxlRingMsg swxlRingMsg = null;
                for (int i = 0; i < 5; i++) {
                    swxlRingMsg = swxlApi.getRingInfo2(ring);
                    if (swxlRingMsg != null) {
                        break;
                    }
                }
                if (swxlRingMsg != null) {
                    ring.setRemark(swxlRingMsg.getRemark());
                    return AjaxResult.success(ring,"成功");
                    // 添加铃音到数据库
                    //ringRepository.saveRing(ring);
                } else {
                    return AjaxResult.error("获取铃音信息失败，至《铃音管理》页面刷新");
                }
            } else {
                return AjaxResult.error(swxlGroupResponse.getRemark());
            }
        }catch (Exception e){
            log.error("对接联通 方法：saveOrderByLt  错误信息", e);
            return AjaxResult.error("保存失败");
        }
    }

}
