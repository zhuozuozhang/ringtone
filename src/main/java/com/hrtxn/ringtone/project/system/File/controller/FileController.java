package com.hrtxn.ringtone.project.system.File.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.utils.DateUtils;
import com.hrtxn.ringtone.common.utils.FileUtil;
import com.hrtxn.ringtone.common.utils.StringUtils;
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
     * @param parentOrderId
     * @return
     */
    @PostMapping("/system/upload/ringFile")
    @ResponseBody
    @Log(title = "上传文件", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult uploadRingFile(@RequestParam("ringFile") MultipartFile ringFile, String ringName, Integer parentOrderId) {
        ringName = ringName + DateUtils.getTimeRadom();
        String extensionName = FileUtil.getExtensionName(ringFile.getOriginalFilename());
        if (extensionName.equals("mp3") || extensionName.equals("wav")) {
            boolean m = FileUtil.checkFileSize(ringFile.getSize(), 1, "M");
            if (!m){
                return AjaxResult.error("文件大小超限");
            }
        }
        if (extensionName.equals("mp4") || extensionName.equals("mov")) {
            boolean m = FileUtil.checkFileSize(ringFile.getSize(), 48, "M");
            if (!m){
                return AjaxResult.error("文件大小超限");
            }
        }
        return fileService.upload(ringFile, ringName, parentOrderId);
    }

    //@PathVariable String apersonId

    /**
     * 添加企业资质
     *
     * @param companyFile
     * @param parentOrderId
     * @return
     */
    @PostMapping("/system/upload/companyFile")
    @ResponseBody
    @Log(title = "上传文件", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult uploadCompanyFile(@RequestParam("companyFile") MultipartFile companyFile, Integer parentOrderId) {
        return fileService.upload(companyFile, "企业资质", parentOrderId);
    }


    /**
     * 客户确认涵
     *
     * @param clientFile
     * @param parentOrderId
     * @return
     */
    @PostMapping("/system/upload/clientFile")
    @ResponseBody
    @Log(title = "上传文件", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult uploadClientFile(@RequestParam("clientFile") MultipartFile clientFile, Integer parentOrderId) {
        return fileService.upload(clientFile, "客户确认函", parentOrderId);
    }

    /**
     * 免短协议
     *
     * @param protocolFile
     * @param parentOrderId
     * @return
     */
    @PostMapping("/system/upload/protocolFile")
    @ResponseBody
    @Log(title = "上传文件", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult uploadProtocolFile(@RequestParam("protocolFile") MultipartFile protocolFile, Integer parentOrderId) {
        return fileService.upload(protocolFile, "免短协议", parentOrderId);
    }

    /**
     * 主题证明
     *
     * @param protocolFile
     * @param parentOrderId
     * @return
     */
    @PostMapping("/system/upload/mainFile")
    @ResponseBody
    @Log(title = "上传文件", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult uploadMainFile(@RequestParam("protocolFile") MultipartFile protocolFile, Integer parentOrderId) {
        return fileService.upload(protocolFile, "主体证明", parentOrderId);
    }

    /**
     * 添加子账号 -- 身份证上传
     *
     * @param protocolFile
     * @param flag
     * @param radom
     * @return
     */
    @PostMapping("/system/upload/cardImage")
    @ResponseBody
    @Log(title = "身份证上传", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult cardImage(@RequestParam("file") MultipartFile protocolFile, Integer flag, Integer radom) {
        if (StringUtils.isNotNull(protocolFile) && StringUtils.isNotNull(flag) && StringUtils.isNotNull(radom)) {
            if (flag == 1) { // 身份证正面照
                return fileService.upload(protocolFile, "正面照", radom);
            } else { // 身份证反面照
                return fileService.upload(protocolFile, "反面照", radom);
            }
        }
        return AjaxResult.error("参数格式不正确！");
    }
}
