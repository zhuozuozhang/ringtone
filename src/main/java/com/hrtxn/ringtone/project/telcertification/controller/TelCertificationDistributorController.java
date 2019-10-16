package com.hrtxn.ringtone.project.telcertification.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationConfig;
import com.hrtxn.ringtone.project.telcertification.domain.TelCerDistributor;
import com.hrtxn.ringtone.project.telcertification.service.TelCertificationConfigService;
import com.hrtxn.ringtone.project.telcertification.service.TelCertificationDistributorService;
import com.hrtxn.ringtone.project.telcertification.service.TelCertificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author: yuanye
 * @Date: Created in 14:57 2019/9/19
 * @Description:
 * @Modified By:
 */
@Slf4j
@Controller
@RequestMapping("/telcertify")
public class TelCertificationDistributorController {
    @Autowired
    private TelCertificationDistributorService telCertificationDistributorService;

    /**
     * 进入订购统计页面
     * @return
     */
    @GetMapping("/toServiceStatisticsPage")
    public String toServiceStatisticsPage(TelCerDistributor telCerDistributor, ModelMap map){
        telCertificationDistributorService.getServiceNum(map);
        return "telcertification/statistics";
    }

    /**
     * 获取订购统计渠道商信息
     * @return
     */
    @PostMapping("/getTelCerDistributor")
    @ResponseBody
    @Log(title = "获取业务开通数量",operatorLogType = OperatorLogType.TELCERTIFICATION,businessType = BusinessType.INSERT)
    public AjaxResult getTelCerDistributor(Page page, BaseRequest request){
        return telCertificationDistributorService.getTelCerDistributor(page, request);
    }

}
