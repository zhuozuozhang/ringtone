package com.hrtxn.ringtone.project.threenets.threenet.utils;

import com.hrtxn.ringtone.common.exception.NoLoginException;
import com.hrtxn.ringtone.common.utils.FileUtil;
import com.hrtxn.ringtone.common.utils.SpringUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.freemark.config.systemConfig.RingtoneConfig;
import com.hrtxn.ringtone.project.system.File.domain.Uploadfile;
import com.hrtxn.ringtone.project.system.File.mapper.UploadfileMapper;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsChildOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsRing;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsChildOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsRingMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Author:zcy
 * Date:2019-08-05 13:17
 * Description:定时任务
 */
@Component
@Slf4j
public class TimeTask {

    /**
     * 删除无效文件
     * 每天00:05执行
     * 删除status为1的数据
     */
    @Scheduled(cron = "0 30 02 ? * *")
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

}
