package com.hrtxn.ringtone.project.telcertification.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.Const;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationConfig;
import com.hrtxn.ringtone.project.telcertification.mapper.CertificationConfigMapper;
import org.aspectj.weaver.loadtime.Aj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: yuanye
 * Date:2019/8/24 13:32
 * Description: 号码认证配置业务层
 */
@Service
public class TelCertificationConfigService {
    @Autowired
    private CertificationConfigMapper certificationConfigMapper;

    /**
     * 获得全部配置信息
     * @param page
     * @param map
     * @return
     */
    public AjaxResult getAllConfig(Page page, ModelMap map) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        int count = certificationConfigMapper.getCount();
        List<CertificationConfig> list = certificationConfigMapper.getAllConfig(page);
        for (CertificationConfig cc: list) {
            //号码认证业务类型（1.泰迪熊/2.电话邦/3.彩印/4.挂机短信）
            if (cc.getType() == 1) {
                cc.setName("泰迪熊");
            } else if (cc.getType() == 2) {
                cc.setName("电话邦");
            } else if (cc.getType() == 3) {
                cc.setName("彩印");
            } else if (cc.getType() == 4) {
                cc.setName("挂机短信");
            }
        }
        map.put("configList",list);
        return AjaxResult.success(list,"获取到全部配置信息",count);
    }

    /**
     * 修改号码认证业务费用配置
     * @param certificationConfig
     * @return
     */
    public AjaxResult editTelCerConfig(CertificationConfig certificationConfig) {
        if(StringUtils.isNotNull(certificationConfig) && StringUtils.isNotNull(certificationConfig.getId()) && certificationConfig.getId() != 0){
            int count = certificationConfigMapper.updateTelCerConfig(certificationConfig);
            if(count > 0){
                return AjaxResult.success(true, "修改成功！");
            }
            return AjaxResult.error("修改失败！");
        }
        return AjaxResult.error("参数格式错误！");
    }

    public CertificationConfig getTelCerConfigById(Integer id) {
        CertificationConfig certificationConfig = certificationConfigMapper.selectByPrimaryKey(id);
        if(Const.TEL_CER_CONFIG_TYPE_TEDDY.equals(certificationConfig.getType())){
            certificationConfig.setName(Const.TEDDY);
        }
        if(Const.TEL_CER_CONFIG_TYPE_TELBOND.equals(certificationConfig.getType())){
            certificationConfig.setName(Const.TELBOND);
        }
        if(Const.TEL_CER_CONFIG_TYPE_COLORPRINT.equals(certificationConfig.getType())){
            certificationConfig.setName(Const.COLORPRINT);
        }
        if(Const.TEL_CER_CONFIG_TYPE_HANGUPMESSAGE.equals(certificationConfig.getType())){
            certificationConfig.setName(Const.HANGUPMESSAGE);
        }
        return certificationConfig;
    }
}
