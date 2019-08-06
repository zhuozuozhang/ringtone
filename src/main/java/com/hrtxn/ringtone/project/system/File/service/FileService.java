package com.hrtxn.ringtone.project.system.File.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.utils.FileUtil;
import com.hrtxn.ringtone.project.system.File.domain.Uploadfile;
import com.hrtxn.ringtone.project.system.File.mapper.UploadfileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
     * @param orderId
     * @return
     */
    public AjaxResult upload(MultipartFile file, String fileName, Integer orderId) {
        try {
            String path = FileUtil.uploadFile(file, orderId, fileName);
            //保存到数据库
            Uploadfile uploadfile = new Uploadfile();
            uploadfile.setPath(path);
            uploadfile.setFilename(fileName);
            uploadfile.setStatus(1);
            uploadfile.setCreatetime(new Date());
            uploadfileMapper.insertSelective(uploadfile);
            return AjaxResult.success(path, "文件上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("文件上传失败");
        }
    }

    /**
     * 修改文件使用状态
     * @param path
     */
    public void updateStatus (String path){
        Uploadfile uploadfile = new Uploadfile();
        uploadfile.setPath(path);
        uploadfile.setStatus(2);
        uploadfileMapper.updateByPath(uploadfile);
    }

    /**
     * 修改文件使用状态
     * @param path
     */
    public void deleteFile (String path){
        Uploadfile uploadfile = new Uploadfile();
        uploadfile.setPath(path);
        uploadfile.setStatus(1);
        uploadfileMapper.updateByPath(uploadfile);
    }

    /**
     * 获取附件
     *
     * @return
     */
    public List<Uploadfile> listUploadfile(Integer orderId){
        String path = "\\"+orderId;
        return uploadfileMapper.selectByPath(path);
    }

}
