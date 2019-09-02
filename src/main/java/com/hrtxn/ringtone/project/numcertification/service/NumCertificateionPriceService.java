package com.hrtxn.ringtone.project.numcertification.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.numcertification.domain.NumcertificationPrice;
import com.hrtxn.ringtone.project.numcertification.mapper.NumCertificateionPriceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author zcy
 * @Date 2019-08-31 17:23
 */
@Service
public class NumCertificateionPriceService {

    @Autowired
    private NumCertificateionPriceMapper numCertificateionPriceMapper;

    /**
     * 获取价格配置列表
     *
     * @author zcy
     * @date 2019-9-2 9:53
     */
    public AjaxResult getNumPriceList(Page page) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        // 执行获取价格配置列表
        List<NumcertificationPrice> numcertificationPriceList = numCertificateionPriceMapper.getNumPriceList(page, null);
        // 获取总数
        int count = numCertificateionPriceMapper.getCount();
        return AjaxResult.success(numcertificationPriceList, "获取数据成功！", count);
    }

    /**
     * 根据ID获取价格配置信息
     *
     * @author zcy
     * @date 2019-9-2 13:29
     */
    public NumcertificationPrice selectById(Integer id) {
        if (StringUtils.isNull(id)) {
            return null;
        }
        // 执行获取价格配置信息
        BaseRequest b = new BaseRequest();
        b.setId(id);
        List<NumcertificationPrice> numPriceList = numCertificateionPriceMapper.getNumPriceList(null, b);
        if (StringUtils.isNotNull(numPriceList) && numPriceList.size() > 0) {
            return numPriceList.get(0);
        }
        return null;
    }

    /**
     * 修改价格配置信息
     *
     * @author zcy
     * @date 2019-9-2 13:29
     */
    public AjaxResult updateNumcertificationPrice(NumcertificationPrice numcertificationPrice) {
        if (StringUtils.isNull(numcertificationPrice)) {
            return AjaxResult.error();
        }
        // 执行修改操作
        int count = numCertificateionPriceMapper.updateNumcertificationPrice(numcertificationPrice);
        if (count > 0) {
            return AjaxResult.success(true, "修改成功！");
        }
        return AjaxResult.error("修改失败！");
    }
}
