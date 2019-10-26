package com.hrtxn.ringtone.project.threenets.kedas.kedasites.service;

import com.hrtxn.ringtone.common.api.KedaApi;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.constant.Constant;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.FileUtil;
import com.hrtxn.ringtone.common.utils.Mp3Util;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.common.utils.UUIDUtils;
import com.hrtxn.ringtone.freemark.config.systemConfig.RingtoneConfig;
import com.hrtxn.ringtone.project.system.File.domain.Uploadfile;
import com.hrtxn.ringtone.project.system.File.mapper.UploadfileMapper;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaChildOrder;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaOrder;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.domain.KedaRing;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.mapper.KedaChildOrderMapper;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.mapper.KedaOrderMapper;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.mapper.KedaRingMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author:zcy
 * Date:2019-08-14 17:32
 * Description：疑难杂单铃音业务处理层
 */
@Service
@Slf4j
public class KedaRingService {
    @Autowired
    private KedaRingMapper kedaRingMapper;
    @Autowired
    private UploadfileMapper uploadfileMapper;
    @Autowired
    private KedaOrderMapper kedaOrderMapper;
    @Autowired
    private KedaChildOrderMapper kedaChildOrderMapper;

    @Autowired
    private  KedaAsyncService asyncService;

    private KedaApi kedaApi = new KedaApi();

    /**
     * 获取铃音列表
     *
     * @param page
     * @param baseRequest
     * @return
     */
    public AjaxResult getKedaRingList(Page page, BaseRequest baseRequest) throws IOException {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        // 获取铃音列表
        List<KedaRing> kedaRingList = kedaRingMapper.getKedaRingList(page, baseRequest);
        // 获取铃音数量
        int count = kedaRingMapper.getCount(baseRequest);
        KedaOrder kedaOrder = kedaOrderMapper.getKedaOrder(baseRequest.getOrderId());
        kedaRingList = kedaApi.refreshRingInfo(kedaRingList,kedaOrder.getKedaId());
        for (int i = 0; i < kedaRingList.size(); i++) {
            kedaRingMapper.updateKedaRing(kedaRingList.get(i));
        }
        log.info("获取铃音列表 ----------->");
        return AjaxResult.success(kedaRingList, "获取数据成功！", count);
    }
    /**
     * 添加鈴音
     *
     * @param kedaRing
     * @param file
     * @return
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult addKedaRing(KedaRing kedaRing, MultipartFile file) throws IOException {
        if (StringUtils.isNull(kedaRing)) return AjaxResult.error("参数格式不正确！");
        if (StringUtils.isEmpty(kedaRing.getRingName())) return AjaxResult.error("参数格式不正确！");
        if (StringUtils.isEmpty(kedaRing.getRingContent())) return AjaxResult.error("参数格式不正确！");
        if (StringUtils.isNull(file)) return AjaxResult.error("参数格式不正确！");
        KedaOrder kedaOrder = kedaOrderMapper.getKedaOrder(kedaRing.getOrderId());
        // 去除空格
        String ringName = kedaRing.getRingName().replaceAll(" ", "");
        // 去除空格和其他字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(ringName);
        ringName = m.replaceAll("").trim();
        // 根据铃音名称查询铃音信息
        BaseRequest b = new BaseRequest();
        b.setName(ringName);
        List<KedaRing> kedaRingList = kedaRingMapper.getKedaRingList(null, b);
        if (kedaRingList.size() > 0) return AjaxResult.error("铃音名称重复！");
        kedaRing.setCreateTime(new Date());
        kedaRing.setOpertateId(kedaOrder.getKedaId());
        // 文件判断
        // 判断文件类型、文件大小、文件时长
        // 文件真实名称
        String fileTrueName = file.getOriginalFilename();
        // 获取文件后缀名
        String[] fileNameS = fileTrueName.split("\\.");
        // 文件后缀名
        String fileType = '.' + fileNameS[fileNameS.length - 1];
        // 判断文件格式
        if (!fileType.equals(".mp3") && !fileType.equals(".wav")) { return AjaxResult.error("文件格式不正确！");}
        kedaRing.setRingName(ringName);
        String filePath = "/" + UUIDUtils.get8UUID();
        // 存放文件的全路径
        String path = RingtoneConfig.getProfile() + File.separator + "keda" + filePath;
        kedaRing.setRingPath(File.separator + "keda" + filePath + ringName + fileType);
        File targetFile = new File(path, ringName + fileType);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        file.transferTo(targetFile);
        // 判断文件大小
        if (targetFile.length() > 800 * 1024) { return AjaxResult.error("文件大小大于800K");}
        File source = new File(path + File.separator + ringName + fileType);
        int length = Mp3Util.getMp3TrackLength(source);
        if (length > 60) {return AjaxResult.error("文件时长大于60S");}

        // 同步添加铃音信息
        // 上传文件，得到URL
        AjaxResult ajaxResult = kedaApi.uploadRing(source,kedaRing);
        if ((int) ajaxResult.get("code") == 200) {
            String fileUrl = ajaxResult.get("msg").toString();
            if (StringUtils.isNotEmpty(fileUrl)) {kedaRing.setRingUrl(fileUrl);}
            // 执行暂存作品操作
            AjaxResult a = kedaApi.addRing(fileUrl, kedaRing);
            if ((int) a.get("code") == 200) {
                kedaRing.setRingNum(a.get("msg").toString());
            } else {
                return a;
            }
        } else {
            return ajaxResult;
        }
        // 添加本地铃音纪录  //审核中
        kedaRing.setRingStatus(1);
        int i1 = kedaRingMapper.isertKedaRingOrder(kedaRing);
        log.info("疑难杂单添加铃音信息" + i1);
        if (i1 > 0) {
            // 添加文件纪录
            Uploadfile u = new Uploadfile();
            u.setPath(File.separator + "keda" + filePath + ringName + fileType);
            u.setCreatetime(new Date());
            u.setFilename(ringName);
            u.setStatus(2);
            int i = uploadfileMapper.insertSelective(u);
            log.info("疑难杂单--添加铃音--添加文件纪录" + i);
            return AjaxResult.success(true, "添加铃音成功！");
        }
        return AjaxResult.error("添加铃音失败！");
    }
    /**
     * 删除铃音
     *
     * @param id
     * @return
     */
    public AjaxResult deleteRing(Integer id) {
        // 根据ID获取铃音信息，得到铃音本地文件路径
        BaseRequest b = new BaseRequest();
        b.setId(id);
        List<KedaRing> kedaRingList = kedaRingMapper.getKedaRingList(null, b);
        if (kedaRingList.size() > 0) {
            // 删除铃音本地文件
            FileUtil.deleteFile(RingtoneConfig.getProfile() + kedaRingList.get(0).getRingPath());
            // 删除铃音纪录
            int count = kedaRingMapper.deleteRing(id);
            if (count > 0) {return AjaxResult.success(true, "删除成功！");}
        }
        return AjaxResult.error("删除失败！");
    }
    /**
     * 疑难杂单铃音设置用户
     *
     * @param phones
     * @param orderId
     * @param id
     * @return
     * @throws IOException
     */
    public AjaxResult setRing(String phones, Integer orderId, Integer id) throws IOException {
        if (StringUtils.isEmpty(phones)) return AjaxResult.error("参数格式不正确！");
        if (StringUtils.isNull(orderId) || orderId <= 0) return AjaxResult.error("参数格式不正确！");
        if (StringUtils.isNull(id) || id <= 0) return AjaxResult.error("参数格式不正确！");

        // 根据ID获取铃音信息，进而得到铃音编号
        BaseRequest b = new BaseRequest();
        b.setId(id);
        List<KedaRing> kedaRingList = kedaRingMapper.getKedaRingList(null, b);
        if (kedaRingList.size() <= 0) return AjaxResult.error("没有获取到铃音信息！");
        // 根据子订单电话获取子订单信息
        String[] splitStr = phones.split(",");
        String[] businessEmpIdList = new String[splitStr.length];
        List<KedaChildOrder> keDaChildOrderList = new ArrayList<>();
        int i = 0;
        for (String str : splitStr) {
            b.setId(null);
            b.setTel(str);
            keDaChildOrderList = kedaChildOrderMapper.getKeDaChildOrderBacklogList(null, b);
            if (keDaChildOrderList.size() <= 0)
                return AjaxResult.error("没有获取到子订单信息！");
            if (StringUtils.isNull(keDaChildOrderList.get(0).getEmployeeId()))
                return AjaxResult.error(keDaChildOrderList.get(0).getLinkTel() + "无员工编号,请至《号码管理》刷新后重新设置！");
            businessEmpIdList[i] = keDaChildOrderList.get(0).getEmployeeId().toString();
            i++;
        }
        String businessEmpId = "";
        if (businessEmpIdList.length > 0) {
            for (int j = 0; j < businessEmpIdList.length; j++) {
                if (j == (businessEmpIdList.length - 1)) {
                    businessEmpId += businessEmpIdList[j];
                } else {
                    businessEmpId += businessEmpIdList[j] + ",";
                }
            }
        }
        KedaOrder kedaOrder = kedaOrderMapper.getKedaOrder(orderId);
        AjaxResult ajaxResult = kedaApi.setRing(kedaRingList.get(0).getRingNum(), businessEmpId, phones,kedaOrder.getKedaId());
        if ((int) ajaxResult.get("code") == 200) {
            // 执行修改子订单信息
            for (int k = 0; k < keDaChildOrderList.size(); k++) {
                keDaChildOrderList.get(k).setRingId(kedaRingList.get(0).getId());
                keDaChildOrderList.get(k).setRingName(kedaRingList.get(0).getRingName());
                int i1 = kedaChildOrderMapper.updatKedaChildOrder(keDaChildOrderList.get(k));
                log.info("疑难杂单铃音设置用户更改子订单信息：{}", i1);
            }
        }
        return ajaxResult;
    }
    /**
     * 获取铃音设置铃音列表
     *
     * @param orderId
     * @return
     */
    public AjaxResult getKedaRingSetting(Integer orderId) {
        if (StringUtils.isNull(orderId) || orderId <= 0) return AjaxResult.error("参数格式不正确！");
        // 根据条件获取铃音列表
        BaseRequest b = new BaseRequest();
        b.setOrderId(orderId);
        List<KedaRing> kedaRingList = kedaRingMapper.getKedaRingList(null, b);
        return AjaxResult.success(kedaRingList, "获取数据成功！");
    }
}
