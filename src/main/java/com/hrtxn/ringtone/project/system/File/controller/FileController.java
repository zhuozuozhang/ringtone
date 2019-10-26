package com.hrtxn.ringtone.project.system.File.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.utils.DateUtils;
import com.hrtxn.ringtone.common.utils.FileUtil;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.system.File.service.FileService;
import com.hrtxn.ringtone.project.telcertification.service.TelCertificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    @Autowired
    private TelCertificationService telCertificationService;

    /**
     * 铃音
     *
     * @param ringFile
     * @param ringName
     * @param folderName
     * @return
     */
    @PostMapping("/system/upload/ringFile")
    @ResponseBody
    @Log(title = "上传文件", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult uploadRingFile(@RequestParam("ringFile") MultipartFile ringFile, String ringName, String folderName) {
        ringName = ringName + DateUtils.getTimeRadom();
        String extensionName = FileUtil.getExtensionName(ringFile.getOriginalFilename());
        if (extensionName.equals("mp3") || extensionName.equals("wav")) {
            boolean m = FileUtil.checkFileSize(ringFile.getSize(), 1, "M");
            if (!m) {
                return AjaxResult.error("文件大小超限");
            }
        }
        if (extensionName.equals("mp4") || extensionName.equals("mov")) {
            boolean m = FileUtil.checkFileSize(ringFile.getSize(), 48, "M");
            if (!m) {
                return AjaxResult.error("文件大小超限");
            }
        }
        return fileService.upload(ringFile, ringName, folderName);
    }

    //@PathVariable String apersonId

    /**
     * 添加企业资质
     *
     * @param companyFile
     * @param folderName
     * @return
     */
    @PostMapping("/system/upload/companyFile")
    @ResponseBody
    @Log(title = "上传文件", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult uploadCompanyFile(@RequestParam("companyFile") MultipartFile companyFile, String folderName) {
        return fileService.upload(companyFile, "企业资质", folderName);
    }


    /**
     * 客户确认涵
     *
     * @param clientFile
     * @param folderName
     * @return
     */
    @PostMapping("/system/upload/clientFile")
    @ResponseBody
    @Log(title = "上传文件", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult uploadClientFile(@RequestParam("clientFile") MultipartFile clientFile, String folderName) {
        return fileService.upload(clientFile, "客户确认函", folderName);
    }

    /**
     * 免短协议
     *
     * @param protocolFiles
     * @param folderName
     * @return
     */
    @PostMapping("/system/upload/protocolFile")
    @ResponseBody
    @Log(title = "上传文件", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult uploadProtocolFile(@RequestParam("protocolFile") List<MultipartFile> protocolFiles, String folderName) {
        return fileService.uploads(protocolFiles, "免短协议", folderName);
        // return fileService.upload(protocolFile, "免短协议", folderName);
    }

    /**
     * 主题证明
     *
     * @param mainFile
     * @param folderName
     * @return
     */
    @PostMapping("/system/upload/mainFile")
    @ResponseBody
    @Log(title = "上传文件", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult uploadMainFile(@RequestParam("mainFile") MultipartFile mainFile, String folderName) {
        return fileService.upload(mainFile, "主体证明", folderName);
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
                return fileService.upload(protocolFile, "正面照", radom.toString());
            } else { // 身份证反面照
                return fileService.upload(protocolFile, "反面照", radom.toString());
            }
        }
        return AjaxResult.error("参数格式不正确！");
    }

    /**
     * 上传营业执照
     *
     * @param businessLicense
     * @param folderName
     * @return
     */
    @PostMapping("/system/upload/businessLicense")
    @ResponseBody
    @Log(title = "上传营业执照", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.TELCERTIFICATION)
    public AjaxResult businessLicense(@RequestParam("businessLicense") MultipartFile businessLicense, String folderName) {
        return fileService.upload(businessLicense, "营业执照", folderName);
    }

    /**
     * 上传法人身份证正面
     *
     * @param legalPersonCardZhen
     * @param folderName
     * @return
     */
    @PostMapping("/system/upload/legalPersonCardZhen")
    @ResponseBody
    @Log(title = "上传法人身份证正面", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.TELCERTIFICATION)
    public AjaxResult legalPersonCardZhen(@RequestParam("legalPersonCardZhen") MultipartFile legalPersonCardZhen, String folderName) {
        return fileService.upload(legalPersonCardZhen, "法人身份证正面", folderName);
    }

    /**
     * 上传法人身份证反面
     *
     * @param legalPersonCardFan
     * @param folderName
     * @return
     */
    @PostMapping("/system/upload/legalPersonCardFan")
    @ResponseBody
    @Log(title = "上传法人身份证反面", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.TELCERTIFICATION)
    public AjaxResult legalPersonCardFan(@RequestParam("legalPersonCardFan") MultipartFile legalPersonCardFan, String folderName) {
        return fileService.upload(legalPersonCardFan, "法人身份证反面", folderName);
    }

    /**
     * 上传LOGO
     *
     * @param logo
     * @param folderName
     * @return
     */
    @PostMapping("/system/upload/logo")
    @ResponseBody
    @Log(title = "上传LOGO", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.TELCERTIFICATION)
    public AjaxResult logo(@RequestParam("logo") MultipartFile logo, String folderName) {
        return fileService.upload(logo, "logo", folderName);
    }

    /**
     * 上传授权书
     *
     * @param authorization
     * @param
     * @return
     */
    @PostMapping("/system/upload/authorization")
    @ResponseBody
    @Log(title = "上传授权书", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.TELCERTIFICATION)
    public AjaxResult authorization(@RequestParam("authorization") MultipartFile authorization, String folderName) {
        return fileService.upload(authorization, "授权书", folderName);
    }

    /**
     * 上传号码证明
     *
     * @param numberProve
     * @param folderName
     * @return
     */
    @PostMapping("/system/upload/numberProve")
    @ResponseBody
    @Log(title = "上传号码证明", businessType = BusinessType.INSERT, operatorLogType = OperatorLogType.TELCERTIFICATION)
    public AjaxResult numberProve(@RequestParam("numberProve") MultipartFile numberProve, String folderName) {
        return fileService.upload(numberProve, "号码证明", folderName);
    }


    /**
     * 400文件
     * @param legalCardUrl
     * @param folderName
     * @return
     */
    @PostMapping("/system/upload/legalCardUrl")
    @ResponseBody
    public AjaxResult legalCardUrl(@RequestParam("legalCardUrl") MultipartFile legalCardUrl, String folderName) {
        return fileService.upload(legalCardUrl, legalCardUrl.getOriginalFilename(), folderName);
    }

    /**
     * 400文件
     * @param operatorCardUrl
     * @param folderName
     * @return
     */
    @PostMapping("/system/upload/operatorCardUrl")
    @ResponseBody
    public AjaxResult operatorCardUrl(@RequestParam("operatorCardUrl") MultipartFile operatorCardUrl, String folderName) {
        operatorCardUrl.getName();
        return fileService.upload(operatorCardUrl, operatorCardUrl.getOriginalFilename(), folderName);
    }


    /**
     * 400文件
     * @param fourFileUpload
     * @param folderName
     * @return
     */
    @PostMapping("/system/upload/fourFileUpload")
    @ResponseBody
    public AjaxResult fourFileUpload(@RequestParam("fourFileUpload") MultipartFile fourFileUpload, String folderName) {
        return fileService.upload(fourFileUpload, fourFileUpload.getOriginalFilename(), folderName);
    }


    /**
     * 科大10元
     * @param protocolTelecom10File
     * @param folderName
     * @return
     */
    @PostMapping("/system/upload/protocolTelecom10")
    @ResponseBody
    public AjaxResult protocolTelecom10(@RequestParam("protocolTelecom10File") MultipartFile protocolTelecom10File, String folderName) {
        return fileService.upload(protocolTelecom10File,"电信10元资质文件", folderName);
    }

    /**
     * 科大20元
     * @param protocolTelecom20File
     * @param folderName
     * @return
     */
    @PostMapping("/system/upload/protocolTelecom20")
    @ResponseBody
    public AjaxResult protocolTelecom20(@RequestParam("protocolTelecom20File") MultipartFile protocolTelecom20File, String folderName) {
        return fileService.upload(protocolTelecom20File, "电信20元资质文件", folderName);
    }
}
