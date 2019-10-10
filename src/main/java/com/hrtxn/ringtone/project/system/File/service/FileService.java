package com.hrtxn.ringtone.project.system.File.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.utils.DateUtils;
import com.hrtxn.ringtone.common.utils.FileUtil;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.freemark.config.systemConfig.RingtoneConfig;
import com.hrtxn.ringtone.project.system.File.domain.Uploadfile;
import com.hrtxn.ringtone.project.system.File.mapper.UploadfileMapper;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsRing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.List;

/**
 * Author:lile
 * Date:2019/7/20 9:16
 * Description:
 */
@Service
public class FileService {

    private final String TYPE_RING = "ring";

    @Autowired
    private UploadfileMapper uploadfileMapper;

    /**
     * 保存文件
     *
     * @param file
     * @param fileName
     * @param folderName
     * @return
     */
    public AjaxResult upload(MultipartFile file, String fileName, String folderName) {
        try {
            String path = FileUtil.uploadFile(file, folderName, fileName);
            //保存到数据库
            Uploadfile uploadfile = new Uploadfile();
            uploadfile.setPath(path);
            uploadfile.setFilename(fileName);
            uploadfile.setStatus(1);
            uploadfile.setCreatetime(new Date());
            uploadfileMapper.insertSelective(uploadfile);
            if(StringUtils.isNotBlank(path)){
                path = path.replace("\\","/");
            }
            return AjaxResult.success(path, "文件上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("文件上传失败");
        }
    }

    /**
     * 保存文件
     *
     * @param files
     * @param fileName
     * @param folderName
     * @return
     */
    public AjaxResult uploads(List<MultipartFile> files, String fileName, String folderName) {
        try {
            if (files.isEmpty()) {
                return AjaxResult.error("文件上传失败");
            }
            String paths = "";
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                String newFileName = fileName + (i + 1);
                String path = FileUtil.uploadFile(file, folderName, newFileName);
                //保存到数据库
                Uploadfile uploadfile = new Uploadfile();
                uploadfile.setPath(path);
                uploadfile.setFilename(newFileName);
                uploadfile.setStatus(1);
                uploadfile.setCreatetime(new Date());
                uploadfileMapper.insertSelective(uploadfile);
                paths = paths + path + ";";
            }
            return AjaxResult.success(paths, "文件上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("文件上传失败");
        }
    }

    /**
     * 修改文件使用状态
     *
     * @param path
     */
    public void updateStatus(String path) {
        Uploadfile uploadfile = new Uploadfile();
        uploadfile.setPath(path);
        uploadfile.setStatus(2);
        uploadfileMapper.updateByPath(uploadfile);
    }

    /**
     * 修改文件使用状态
     *
     * @param path
     */
    public void deleteFile(String path) {
        Uploadfile uploadfile = new Uploadfile();
        uploadfile.setPath(path);
        uploadfile.setStatus(1);
        uploadfileMapper.updateByPath(uploadfile);
    }

    /**
     * 铃音文件克隆
     *
     * @param ring
     * @return
     */
    public String cloneFile(ThreenetsRing ring) {
        try {
            File file = new File(RingtoneConfig.getProfile() + ring.getRingWay());
            FileInputStream inputStream = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile(file.getName(), inputStream);
            String fileName = "";
            if (ring.getRingName().indexOf(".") > 0) {
                boolean result = ring.getRingName().substring(ring.getRingName().length() - 10, ring.getRingName().lastIndexOf(".")).matches("[0-9]+");
                if (result) {
                    fileName = ring.getRingName().substring(0, ring.getRingName().length() - 10) + DateUtils.getTimeRadom();
                } else {
                    fileName = ring.getRingName().substring(0, ring.getRingName().length() - 4) + DateUtils.getTimeRadom();
                }
            } else {
                fileName = ring.getRingName();
            }
            String folderName = ring.getRingWay().substring(ring.getRingWay().indexOf("\\") + 1, ring.getRingWay().lastIndexOf("\\"));
            return FileUtil.uploadFile(multipartFile, folderName, fileName);
        } catch (Exception e) {
            return null;
        }
    }
}
