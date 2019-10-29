package com.hrtxn.ringtone.project.threenets.threenet.utils;

import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.exception.NoLoginException;
import com.hrtxn.ringtone.common.utils.*;
import com.hrtxn.ringtone.freemark.config.systemConfig.RingtoneConfig;
import com.hrtxn.ringtone.project.system.File.domain.Uploadfile;
import com.hrtxn.ringtone.project.system.File.mapper.UploadfileMapper;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsChildOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsRing;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsChildOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsRingMapper;
import com.hrtxn.ringtone.project.threenets.threenet.service.ThreeNetsAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author:zcy
 * Date:2019-08-05 13:17
 * Description:定时任务
 */
@Component
@Slf4j
public class TimeTask {

    /**
     * 每周执行一次
     * 删除无效文件
     * 每天00:05执行
     * 删除status为1的数据
     */
    @Scheduled(cron = "0 30 02 ? * 1")
    public void deleteFile() {
        // 获取所有无效文件
        List<Uploadfile> uploadfileList = SpringUtils.getBean(UploadfileMapper.class).selectAllFile();
        for (Uploadfile uploadfile : uploadfileList) {
            if (StringUtils.isNotNull(uploadfile) && StringUtils.isNotEmpty(uploadfile.getPath())) {
                // 获取文件路径，删除磁盘文件
                boolean b = FileUtil.deleteFile(RingtoneConfig.getProfile() + uploadfile.getPath());
                if (b) {
                    // 删除文件记录
                    int i = SpringUtils.getBean(UploadfileMapper.class).deleteByPrimaryKey(uploadfile.getId());
                    log.info("定时器 删除无效文件 路径：[{}]删除结果：[{}]", uploadfile.getPath(), i);

                }
            }
        }
    }

    /**
     * 刷新铃音
     * 每天12:30执行
     * 获取待审核、激活中铃音
     */
    @Scheduled(cron = "0 30 12 ? * *")
    public void refreshRing() throws NoLoginException, IOException {
        ApiUtils apiUtils = new ApiUtils();
        log.info("开始更新铃音......");
        // 获取待审核、激活中铃音
        List<ThreenetsRing> threenetsRingList24 = SpringUtils.getBean(ThreenetsRingMapper.class).findRingIsNotSuccess(24);
        if (StringUtils.isNotNull(threenetsRingList24) && threenetsRingList24.size() > 0) {
            apiUtils.getRingInfoTimeTask(threenetsRingList24, "睿智广告001");
        }
        List<ThreenetsRing> threenetsRingList = SpringUtils.getBean(ThreenetsRingMapper.class).findRingIsNotSuccess(0);
        if (StringUtils.isNotNull(threenetsRingList) && threenetsRingList.size() > 0) {
            apiUtils.getRingInfoTimeTask(threenetsRingList, "中高俊聪");
        }
        log.info("更新铃音结束......");
    }

    /**
     * 刷新号码信息
     * 每天12:30执行
     * 获取未包月子账号更新信息
     */
    @Scheduled(cron = "0 30 12 ? * *")
    public void refreshIsNotMonthlyPhoneInfo() throws Exception {
        List<ThreenetsChildOrder> threenetsChildOrderList24 = SpringUtils.getBean(ThreenetsChildOrderMapper.class).findThreenetChildTimeTask(1, 24);
        if (StringUtils.isNotNull(threenetsChildOrderList24) && threenetsChildOrderList24.size() > 0) {
            ApiUtils apiUtils = new ApiUtils();
            apiUtils.getPhoneInfoTimeTask(threenetsChildOrderList24, "睿智广告001");
        }

        List<ThreenetsChildOrder> threenetsChildOrderList = SpringUtils.getBean(ThreenetsChildOrderMapper.class).findThreenetChildTimeTask(1, 0);
        if (StringUtils.isNotNull(threenetsChildOrderList) && threenetsChildOrderList.size() > 0) {
            ApiUtils apiUtils = new ApiUtils();
            apiUtils.getPhoneInfoTimeTask(threenetsChildOrderList, "中高俊聪");
        }
    }

    /**
     * 刷新号码信息
     * 每月15号00:00执行
     * 获取已包月子账号信息
     */
    @Scheduled(cron = "0 00 00 15 * ?")
    public void refreshIsMonthlyPhoneInfo() throws Exception {
        List<ThreenetsChildOrder> threenetsChildOrderList24 = SpringUtils.getBean(ThreenetsChildOrderMapper.class).findThreenetChildTimeTask(2, 24);
        if (StringUtils.isNotNull(threenetsChildOrderList24) && threenetsChildOrderList24.size() > 0) {
            ApiUtils apiUtils = new ApiUtils();
            apiUtils.getPhoneInfoTimeTask(threenetsChildOrderList24, "睿智广告001");
        }

        List<ThreenetsChildOrder> threenetsChildOrderList = SpringUtils.getBean(ThreenetsChildOrderMapper.class).findThreenetChildTimeTask(2, 0);
        if (StringUtils.isNotNull(threenetsChildOrderList) && threenetsChildOrderList.size() > 0) {
            ApiUtils apiUtils = new ApiUtils();
            apiUtils.getPhoneInfoTimeTask(threenetsChildOrderList, "中高俊聪");
        }
    }

    /**
     * 刷新电信商户信息,同步人员信息和铃音信息
     * 每天3:30执行
     * 获取未包月子账号更新信息
     */
    @Scheduled(cron = "0 30 3 ? * *")
    public void refreshMcardMerchants() {
        Page page = new Page();
        BaseRequest request = new BaseRequest();
        request.setOperator(2);
        request.setStart(DateUtils.getPastDate(7));
        request.setEnd(DateUtils.getFetureDate(1));
        List<ThreenetsOrder> list = SpringUtils.getBean(ThreenetsOrderMapper.class).getAllorderList(page, request);
        for (int i = 0; i < list.size(); i++) {
            SpringUtils.getBean(ThreeNetsAsyncService.class).refreshTelecomMerchantInfo(list.get(i));
        }
    }

    //同步钱两个小时创建电信商户，同步没有添加成功的商户到对应运营商
    @Scheduled(cron = "0 40 10 ? * *")
    public void checkMerchantInfo() {
        //获取两个小时之前的商户信息，现在是14点，则获取10点到12点的数据
        ThreenetsOrder order = new ThreenetsOrder();
        //获取4个小时前的时间
        order.setStartTime(DateUtils.getPastTime(4));
        //获取两个小时前的时间
        order.setEndTime(DateUtils.getPastTime(2));
        List<ThreenetsOrder> list = SpringUtils.getBean(ThreenetsOrderMapper.class).listByParamNoPage(order);
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                ThreenetsOrder o = list.get(i);
                ThreenetsChildOrder childOrder = new ThreenetsChildOrder();
                childOrder.setParentOrderId(o.getId());
                List<ThreenetsChildOrder> clist = SpringUtils.getBean(ThreenetsChildOrderMapper.class).listByParamNoPage(childOrder);
                Map<Integer, List<ThreenetsChildOrder>> map = clist.stream().collect(Collectors.groupingBy(ThreenetsChildOrder::getOperator));
                if (map.get(Const.OPERATORS_TELECOM)!= null){
                    log.info("同步两个小时之前的电信数据----->");
                    //SpringUtils.getBean(ThreeNetsAsyncService.class).refreshTelecomMerchantInfo(order);
                }
            }
        }
    }
}
