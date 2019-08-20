package com.hrtxn.ringtone.common.utils;

import com.hrtxn.ringtone.freemark.config.systemConfig.RingtoneConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

public class FileUtil {

    /**
     * 上传文件
     *
     * @param file     文件对应的byte数组流   使用file.getBytes()方法可以获取
     * @param orderId  商户ID
     * @param fileName 上传文件名
     * @throws Exception
     */
    public static String uploadFile(MultipartFile file, Integer orderId, String fileName) throws Exception {
        String extensionName = getExtensionName(file.getOriginalFilename());
        if (StringUtils.isEmpty(extensionName)){
            extensionName = getExtensionName(file.getName());
        }
        String filePath = RingtoneConfig.getProfile() + File.separator + orderId;
        String returnPath = File.separator + orderId + File.separator + fileName + "." + extensionName;
        String path = RingtoneConfig.getProfile() + File.separator + orderId + File.separator + fileName + "." + extensionName;
        byte[] bytes = file.getBytes();
        File targetFile = new File(filePath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(path);
        out.write(bytes);
        out.flush();
        out.close();
        return returnPath;
    }

    /**
     * 获取文件扩展名
     *
     * @param filename
     * @return
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * 删除文件
     *
     * @param filePath 文件
     * @return
     */
    public static boolean deleteFile(String filePath) {
        boolean flag = false;
        File file = new File(filePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 判断文件大小
     *
     * @param len
     *            文件长度
     * @param size
     *            限制大小
     * @param unit
     *            限制单位（B,K,M,G）
     * @return
     */
    public static boolean checkFileSize(Long len, int size, String unit) {
//        long len = file.length();
        double fileSize = 0;
        if ("B".equals(unit.toUpperCase())) {
            fileSize = (double) len;
        } else if ("K".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1024;
        } else if ("M".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1048576;
        } else if ("G".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1073741824;
        }
        if (fileSize > size) {
            return false;
        }
        return true;
    }
}
