package com.hrtxn.ringtone.project.threenets.threenet.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.FileUtil;
import com.hrtxn.ringtone.project.system.File.mapper.UploadfileMapper;
import com.hrtxn.ringtone.project.system.File.service.FileService;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsRing;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsOrderMapper;
import com.hrtxn.ringtone.project.threenets.threenet.mapper.ThreenetsRingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Author:lile
 * Date:2019/7/11 15:45
 * Description:铃音业务层
 */
@Service
public class ThreeNetsRingService {

    private final String[] VIDEO = {"mp4","mov"};

    private final String[] AUDIO = {"wav","mp3"};

    @Autowired
    private ThreenetsRingMapper threenetsRingMapper;

    @Autowired
    private FileService fileService;

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
    public List<ThreenetsRing> getChildOrderList(Page page, BaseRequest request) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        return threenetsRingMapper.getRingList(page, request);
    }

    /**
     * 根据父级订单获取铃音
     *
     * @param id
     * @return
     * @throws Exception
     */
    public List<ThreenetsRing> listByOrderId(Integer id) throws Exception{
        return threenetsRingMapper.selectByOrderId(id);
    }

    /**
     * 根据id获取铃音
     *
     * @param id
     * @return
     * @throws Exception
     */
    public ThreenetsRing getRing(Integer id)throws Exception{
        return threenetsRingMapper.selectByPrimaryKey(id);
    }

    /**
     * 保存铃音
     * @param ring
     * @return
     */
    @Transactional
    public ThreenetsRing save(ThreenetsRing ring) {
        String extensionsName = ring.getRingWay().substring(ring.getRingWay().indexOf("."));
        boolean isVideo = Arrays.asList(VIDEO).contains(extensionsName);
        ring.setRingType(isVideo?"视频":"音频");
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
     * @param id
     * @return
     */
    public void cloneRing(Integer id) throws Exception{
        ThreenetsRing ring = threenetsRingMapper.selectByPrimaryKey(id);
        ring.setRingStatus(1);
        threenetsRingMapper.insertThreeNetsRing(ring);
    }
}
