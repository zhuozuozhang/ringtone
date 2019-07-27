package com.hrtxn.ringtone.project.threenets.threenet.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrtxn.ringtone.common.api.McardApi;
import com.hrtxn.ringtone.common.api.MiguApi;
import com.hrtxn.ringtone.common.api.SwxlApi;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.exception.NoLoginException;
import com.hrtxn.ringtone.common.json.Attachment;
import com.hrtxn.ringtone.common.json.MiguAddGroupRespone;
import com.hrtxn.ringtone.common.utils.SpringUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.common.utils.json.JsonUtil;
import com.hrtxn.ringtone.project.system.user.domain.User;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsChildOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsOrder;
import com.hrtxn.ringtone.project.threenets.threenet.json.migu.RefreshVbrtStatusResult;
import com.hrtxn.ringtone.project.threenets.threenet.json.swxl.SwxlPhoneInfoResult;
import com.hrtxn.ringtone.project.threenets.threenet.json.swxl.SwxlPubBackData;
import com.hrtxn.ringtone.project.threenets.threenet.json.swxl.SwxlQueryPubRespone;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsChildOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.SecurityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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

    public String serviceProtocolFile; // 协议
    public String businesslicenseFile;// 营业执照

    /**
     * 获取号码信息
     *
     * @param threenetsChildOrder
     * @return
     */
    public AjaxResult getPhoneInfo(ThreenetsChildOrder threenetsChildOrder) throws Exception {
        Boolean f = false;
        String msg = "刷新失败！";
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
                }
            }
        } else if (operate == 2) {// 电信

        } else {// 联通
            String phoneInfo = swxlApi.getPhoneInfo(threenetsChildOrder.getLinkmanTel(), threenetsChildOrder.getOperateId());
            if (StringUtils.isNotEmpty(phoneInfo)) {
                // 分发实体详情转换
                ObjectMapper mapper = new ObjectMapper();
                SwxlPubBackData<SwxlQueryPubRespone<SwxlPhoneInfoResult>> info = mapper.readValue(phoneInfo, new TypeReference<SwxlPubBackData<SwxlPhoneInfoResult>>() {
                });
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
                    msg = info.getMessage();
                }
            }
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
     * 保存集团订单
     *
     * @param order
     */
    public void saveThreenetsOrder(ThreenetsOrder order){

    }

    public AjaxResult addRingWzzfOrderWeb(ThreenetsOrder ringOrder, List<Attachment> attachments, McardApi mcardApi, MiguApi miguApi, User user){
        AjaxResult result = new AjaxResult();
        int sendCount = 3;// 系统同步远程系统3次。解决网络慢的问题
        // 1.增加附件
        if (attachments != null && attachments.size() > 0) {
            for (int i = 0; i < attachments.size(); i++) {
                Attachment t = attachments.get(i);
                if (StringUtils.isEmpty(t.getXh()) || StringUtils.isEmpty(t.getAttId())) {
                    // 删除集合
                    attachments.remove(i);
                    --i;
                }
            }
        }
        if (attachments != null && attachments.size() > 0) {
            // 增加附件，应该由附件类去增加
            //attachmentsRepository.addAttachments(attachments);
        }
        // 添加到音乐名片系统
        @SuppressWarnings("unused")
        String mcardId = null;
        if (2 == ringOrder.getOperator()) {
            result = mcardApi.addRingWzzfOrder(ringOrder);
            //mcardId = mes.getMessage();
            if ((int)result.get("code")==200) {
                ringOrder.setStatus("等待审核");
                // 2.增加订单表
                //ringOrderRepository.addRingOrder(ringOrder);
            }
        }
        // 添加到咪咕平台
        if (1 == ringOrder.getOperator()) {
            // 1.先进行增加到本地数据库
            // 2.在进行同步到服务器，同步3次。
            MiguAddGroupRespone addGroupResponse = null;
            for (int i = 0; i < sendCount; i++) {// 重试添加3次
                //addGroupResponse = miguApi.add(null, ringOrder, user.getName(), user.getMobilephone(), null);
                if (addGroupResponse != null) {
                    break;
                }
            }
            if (addGroupResponse.isSuccess()) {
                // 设置集团id
                ringOrder.setOperateId(addGroupResponse.getCircleId());
                // 2.增加订单表
                ringOrder.setStatus("审核通过");
                // 获取登录用户信息
//                if (miguApi.getBS().equals(MiguApi.LT)) {
//                    ringOrder.setMiguzh(MiguApi.LT);
//                } else if (miguApi.getBS().equals(MiguApi.HT)) {
//                    ringOrder.setMiguzh(MiguApi.HT);
//                }
                //ringOrderRepository.addRingOrder(ringOrder);
                // 查询号码id
                // String phoneId=miguApi.getPhoneId(ringOrder.getLinkPhone(),
                // addGroupResponse.getCircleId());
                // 移动需要添加号码
                ThreenetsChildOrder childOrder = new ThreenetsChildOrder();
                //childOrder.setId(SecurityUtils.generateUUID());
                childOrder.setLinkman(ringOrder.getCompanyLinkman());
                childOrder.setLinkmanTel(ringOrder.getLinkmanTel());
                // apersonnel.setMcardApersonId(phoneId);
                childOrder.setOperateId(ringOrder.getMcardId());
                childOrder.setParentOrderId(ringOrder.getId());
                childOrder.setIsMonthly(1);
                childOrder.setStatus("审核通过");
                childOrder.setCreateDate(new Date());
                childOrder.setOperate(ringOrder.getOperator());
                // 添加到数据
                //apersonnelRepository.add(apersonnel);
                // 更新号码信息
                // boolean getPhoneInfoResult=false;
                // 通过异步同步号码
                //reactor.notify("event.remindOrderCrbtAndMonth", Event.wrap(new UserUpdatePhones(apersonnel, apersonnelRepository, miguApi, true)));
            }
        }
        return result;
    }

}
