package com.hrtxn.ringtone.project.threenets.threenet.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.exception.NoLoginException;
import com.hrtxn.ringtone.project.system.File.service.FileService;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsRing;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsRingMapper;
import com.hrtxn.ringtone.project.threenets.threenet.utils.ApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Author:lile
 * Date:2019/7/11 15:45
 * Description:铃音业务层
 */
@Service
@Slf4j
public class ThreeNetsRingService {

    private final String[] VIDEO = {"mp4", "mov"};
    private final String[] AUDIO = {"wav", "mp3"};
    @Autowired
    private ThreenetsRingMapper threenetsRingMapper;
    @Autowired
    private FileService fileService;
    private ApiUtils apiUtils = new ApiUtils();

    /**
     * 获取铃音总数
     *
     * @param request
     * @return
     */
    public int getCount(BaseRequest request) {
        return threenetsRingMapper.getCount(request);
    }

    /**
     * 获取铃音列表
     *
     * @param page
     * @param request
     * @return
     */
    public List<ThreenetsRing> getChildOrderList(Page page, BaseRequest request) throws NoLoginException, IOException {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<ThreenetsRing> ringList = threenetsRingMapper.getRingList(page, request);
        // 刷新当前铃音列表
        ringList = apiUtils.getRingInfo(ringList);
        return ringList;
    }

    /**
     * 根据父级订单获取铃音
     *
     * @param id
     * @return
     * @throws Exception
     */
    public List<ThreenetsRing> listByOrderId(Integer id) throws Exception {
        return threenetsRingMapper.selectByOrderId(id);
    }

    /**
     * 根据id获取铃音
     *
     * @param id
     * @return
     * @throws Exception
     */
    public ThreenetsRing getRing(Integer id) throws Exception {
        return threenetsRingMapper.selectByPrimaryKey(id);
    }

    /**
     * 保存铃音
     *
     * @param ring
     * @return
     */
    @Transactional
    public ThreenetsRing save(ThreenetsRing ring) {
        String extensionsName = ring.getRingWay().substring(ring.getRingWay().indexOf("."));
        boolean isVideo = Arrays.asList(VIDEO).contains(extensionsName);
        ring.setRingType(isVideo ? "视频" : "音频");
        ring.setRingStatus(1);
        ring.setCreateTime(new Date());
        //保存铃音
        threenetsRingMapper.insertThreeNetsRing(ring);
        //修改文件状态
        fileService.updateStatus(ring.getRingWay());
        return ring;
    }

    /**
     * 删除铃音
     *
     * @param id
     * @return
     */
    @Transactional
    public AjaxResult delete(Integer id) {
        int sum = threenetsRingMapper.deleteByPrimaryKey(id);
        if (sum > 0) {
            return AjaxResult.success(sum, "删除成功");
        } else {
            return AjaxResult.error("删除失败");
        }
    }

    /**
     * 克隆铃音
     *
     * @param id
     * @return
     */
    public void cloneRing(Integer id) throws Exception {
        ThreenetsRing ring = threenetsRingMapper.selectByPrimaryKey(id);
        ring.setRingStatus(1);
        threenetsRingMapper.insertThreeNetsRing(ring);
    }

    /**
     * 获取铃音所属运营商以及刷新铃音信息
     *
     * @param orderId
     * @return
     * @throws Exception
     */
    public String getRingOperate(Integer orderId) throws Exception {
        List<ThreenetsRing> threenetsRings = threenetsRingMapper.selectByOrderId(orderId);
        String operate = "";
        if (threenetsRings.size() > 0) {
            // 获取铃音运营商
            for (ThreenetsRing threenetsRing : threenetsRings) {
                if (threenetsRing.getOperate() == 1) {
                    operate += threenetsRing.getOperate() + ",";
                } else if (threenetsRing.getOperate() == 2) {
                    operate += threenetsRing.getOperate() + ",";
                } else {
                    operate += threenetsRing.getOperate() + ",";
                }
            }
        }
            // 刷新铃音信息
//            AjaxResult ajaxResult = apiUtils.getRingInfo(threenetsRings);
//            log.info("刷新铃音信息结果"+ajaxResult.toString());
////            if ((Boolean)ajaxResult.get("data")){
//                return AjaxResult.success(operate,"刷新成功!");
////            }else{
////                return AjaxResult.success(null,ajaxResult.get("msg").toString());
////            }
//        }else{
//            return AjaxResult.success(null,"无数据!");
//        }
        return operate;
    }
}
