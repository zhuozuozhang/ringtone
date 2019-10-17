package com.hrtxn.ringtone.project.threenets.threenet.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.exception.NoLoginException;
import com.hrtxn.ringtone.common.utils.DateUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.system.File.service.FileService;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreeNetsOrderAttached;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsChildOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsOrder;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsRing;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreeNetsOrderAttachedMapper;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsChildOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsRingMapper;
import com.hrtxn.ringtone.project.threenets.threenet.utils.ApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author:lile
 * Date:2019/7/11 15:45
 * Description:铃音业务层
 */
@Service
@Slf4j
public class ThreeNetsRingService {

    private final String[] VIDEO = {".mp4", ".mov"};

    @Autowired
    private ThreenetsOrderMapper threenetsOrderMapper;

    @Autowired
    private ThreenetsRingMapper threenetsRingMapper;

    @Autowired
    private FileService fileService;
    @Autowired
    private ThreenetsChildOrderMapper threenetsChildOrderMapper;

    @Autowired
    private ThreeNetsAsyncService threeNetsAsyncService;

    @Autowired
    private ThreeNetsOrderAttachedMapper attachedMapper;

    private ApiUtils apiUtils = new ApiUtils();
    @Value("${ringtone.profile-url}")
    private String url;

    /**
     * 增
     *
     * @param ring
     * @return
     */
    public Integer insert(ThreenetsRing ring) {
        return threenetsRingMapper.insertThreeNetsRing(ring);
    }


    /**
     * 根据订单id获取父级订单
     *
     * @param id
     * @return
     */
    public ThreenetsOrder getOrderById(Integer id) throws Exception {
        return threenetsOrderMapper.selectByPrimaryKey(id);
    }

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
    public AjaxResult getChildOrderList(Page page, BaseRequest request) throws NoLoginException, IOException {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<ThreenetsRing> ringList = threenetsRingMapper.getRingList(page, request);
        for(ThreenetsRing ts : ringList){
            ts.setFileUrl(url+"profile/"+ts.getRingWay().replace("\\","/"));
        }
        // 刷新当前铃音列表
        ThreeNetsOrderAttached attached = attachedMapper.selectByParentOrderId(request.getId());
        return apiUtils.refreshRingInfo(ringList, attached);
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

    @Transactional
    public AjaxResult saveRing(ThreenetsRing ring) throws Exception {
        ThreenetsOrder order = threenetsOrderMapper.selectByPrimaryKey(ring.getOrderId());
        ring.setCompanyName(order.getCompanyName());

        ThreenetsChildOrder childOrder = new ThreenetsChildOrder();
        childOrder.setParentOrderId(ring.getOrderId());
        List<ThreenetsChildOrder> childOrders = threenetsChildOrderMapper.listByParamNoPage(childOrder);

        Map<Integer, List<ThreenetsChildOrder>> collect = childOrders.stream().collect(Collectors.groupingBy(ThreenetsChildOrder::getOperator));
        int num = 1;
        for (Integer operator : collect.keySet()) {
            String extensionsName = ring.getRingWay().substring(ring.getRingWay().indexOf("."));
            boolean isVideo = Arrays.asList(VIDEO).contains(extensionsName);
            ring.setRingType(isVideo ? "视频" : "音频");
            ring.setRingStatus(2);
            ring.setCreateTime(new Date());
            ring.setOperate(operator);
            if (ring.getRingType().equals("视频") && operator != 1) {
                continue;
            }
            if (num > 1) {
                String path = fileService.cloneFile(ring);
                ring.setRingWay(path);
                ring.setRingName(path.substring(path.lastIndexOf("\\") + 1));
            }else{
                ring.setRingName(ring.getRingName() + DateUtils.getTimeRadom() + extensionsName);
            }
            if (operator == 1) {
                threenetsRingMapper.insertThreeNetsRing(ring);
                threeNetsAsyncService.ringToneUploadByMobile(ring);
            }
            if (operator == 3) {
                threenetsRingMapper.insertThreeNetsRing(ring);
                threeNetsAsyncService.ringToneUploadByUnicom(ring);
            }
            if (operator == 2) {
                threenetsRingMapper.insertThreeNetsRing(ring);
                threeNetsAsyncService.ringToneUploadByTelecom(ring);
            }
            num++;
        }
        return AjaxResult.success(ring, "保存成功");
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
        ring.setRingStatus(2);
        ring.setCreateTime(new Date());
        ring.setRingName(ring.getRingName() + extensionsName);
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
        String path = fileService.cloneFile(ring);
        ring.setRingName(path.substring(path.lastIndexOf("\\") + 1));
        ring.setRingWay(path);
        ring.setRingStatus(2);
        ring.setRemark("");
        threenetsRingMapper.insertThreeNetsRing(ring);
        if (ring.getOperate() == 3) {
            threeNetsAsyncService.ringToneUploadByUnicom(ring);
        }
        if (ring.getOperate() == 1) {
            threeNetsAsyncService.ringToneUploadByMobile(ring);
        }
        if (ring.getOperate() == 2) {
            threeNetsAsyncService.ringToneUploadByTelecom(ring);
        }
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
        return operate;
    }

    /**
     * 设置铃音
     *
     * @param phones
     * @param orderId
     * @param operate
     * @param id
     * @return
     * @throws Exception
     */
    public AjaxResult setRing(String phones, Integer orderId, Integer operate, Integer id) throws Exception {
        if (StringUtils.isNotEmpty(phones) && StringUtils.isNotNull(orderId)
                && StringUtils.isNotNull(operate) && StringUtils.isNotNull(id)) {
            // 根据ID获取铃音信息
            ThreenetsRing threenetsRing = threenetsRingMapper.selectByPrimaryKey(id);
            if (StringUtils.isNotNull(threenetsRing)) {
                return apiUtils.setRing(phones, threenetsRing, operate, orderId);
            } else {
                return AjaxResult.error("无铃音数据！");
            }
        }
        return AjaxResult.error("参数格式不正确！");
    }

    /**
     * 获取设置铃音激活成功铃音数据数据
     *
     * @param page
     * @param orderId
     * @param operate
     * @return
     */
    public AjaxResult getThreeNetsRingSetingList(Page page, Integer orderId, Integer operate) throws Exception {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<ThreenetsRing> ringList = threenetsRingMapper.getSetRingList(page, orderId, operate);
        // 获取设置铃音激活成功铃音总数
        int count = threenetsRingMapper.getSetRingCount(orderId, operate);
        if (ringList.size() > 0) {
            return AjaxResult.success(ringList, "获取数据成功！", count);
        }
        return AjaxResult.error("无数据！");
    }

    /**
     * 修改
     *
     * @param ring
     */
    public void update(ThreenetsRing ring) {
        threenetsRingMapper.updateByPrimaryKeySelective(ring);
    }

    /**
     * 移动铃音重新激活
     *
     * @param id
     */
    public void reactivateRing(Integer id) {
        try{
            ThreenetsRing threenetsRings = threenetsRingMapper.selectByPrimaryKey(id);
            ThreenetsOrder order = threenetsOrderMapper.selectByPrimaryKey(threenetsRings.getOrderId());
            apiUtils.reactivateRing(threenetsRings,order.getCompanyName());
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
