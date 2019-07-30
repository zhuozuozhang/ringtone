package com.hrtxn.ringtone.project.system.File.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.utils.FileUtil;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.system.File.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * Author:lile
 * Date:2019/7/20 9:16
 * Description: 文件上传
 */
@Slf4j
@Controller
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 铃音
     *
     * @param ringFile
     * @param ringName
     * @param orderId
     * @return
     */
    @PostMapping("/system/upload/ringFile")
    @ResponseBody
    @Log(title = "上传文件", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult uploadRingFile(@RequestParam("ringFile") MultipartFile ringFile, String ringName, Integer orderId) {
        String type = "ring";
        return fileService.upload(ringFile, ringName, type, orderId);
    }

    //@PathVariable String apersonId

    /**
     * 添加企业资质
     *
     * @param companyFile
     * @param orderId
     * @return
     */
    @PostMapping("/system/upload/companyFile")
    @ResponseBody
    @Log(title = "上传文件", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult uploadCompanyFile(@RequestParam("companyFile") MultipartFile companyFile, Integer orderId) {
        String type = "company";
        return fileService.upload(companyFile, "企业资质", type, orderId);
    }


    /**
     * 客户确认涵
     *
     * @param clientFile
     * @param orderId
     * @return
     */
    @PostMapping("/system/upload/clientFile")
    @ResponseBody
    @Log(title = "上传文件", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult uploadClientFile(@RequestParam("clientFile") MultipartFile clientFile, Integer orderId) {
        String type = "client";
        return fileService.upload(clientFile, "客户确认函", type, orderId);
    }

    /**
     * 免短协议
     *
     * @param protocolFile
     * @param orderId
     * @return
     */
    @PostMapping("/system/upload/protocolFile")
    @ResponseBody
    @Log(title = "上传文件", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult uploadProtocolFile(@RequestParam("protocolFile") MultipartFile protocolFile, Integer orderId) {
        String type = "protocol";
        return fileService.upload(protocolFile, "免短协议", type, orderId);
    }

    /**
     * 主题证明
     *
     * @param protocolFile
     * @param orderId
     * @return
     */
    @PostMapping("/system/upload/mainFile")
    @ResponseBody
    @Log(title = "上传文件", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult uploadMainFile(@RequestParam("protocolFile") MultipartFile protocolFile, Integer orderId) {
        String type = "main";
        return fileService.upload(protocolFile, "主体证明", type, orderId);
    }


}
