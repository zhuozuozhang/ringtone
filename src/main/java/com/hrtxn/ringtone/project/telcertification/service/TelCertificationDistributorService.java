package com.hrtxn.ringtone.project.telcertification.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.Const;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationConfig;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationOrder;
import com.hrtxn.ringtone.project.telcertification.domain.TelCerDistributor;
import com.hrtxn.ringtone.project.telcertification.mapper.CertificationChildOrderMapper;
import com.hrtxn.ringtone.project.telcertification.mapper.CertificationConfigMapper;
import com.hrtxn.ringtone.project.telcertification.mapper.TelCerDistributorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: yuanye
 * @Date: Created in 14:58 2019/9/19
 * @Description:
 * @Modified By:
 */
@Service
public class TelCertificationDistributorService {
    @Autowired
    private TelCerDistributorMapper telCerDistributorMapper;
    @Autowired
    private CertificationConfigMapper certificationConfigMapper;
    @Autowired
    private CertificationChildOrderMapper certificationChildOrderMapper;

    public AjaxResult getTelCerDistributor(Page page,BaseRequest request) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<TelCerDistributor> allDisList = telCerDistributorMapper.getAllTelCerDistributorInfo(page,request);
        List<TelCerDistributor> theDisList = new ArrayList<TelCerDistributor>();
        Integer userId = ShiroUtils.getSysUser().getId();
        for (TelCerDistributor dis : allDisList) {
            TelCerDistributor telCerDistributor = new TelCerDistributor();
            telCerDistributor.setDistributorId(ShiroUtils.getSysUser().getId());
            telCerDistributor.setDistributorName(ShiroUtils.getSysUser().getUserName());
            if (ShiroUtils.getSysUser().getParentId() != 0 || ShiroUtils.getSysUser().getParentId() != 2){
                telCerDistributor.setStage("非顶级");
            }
            telCerDistributor.setStage("顶级");
            TelCerDistributor allService = telCerDistributorMapper.getServiceNum(userId);
            telCerDistributor.setTotal(allService.getTotal());
            telCerDistributor.setLastMonthTotal(2);
            telCerDistributor.setTheMonthTotal(2);
            telCerDistributor.setTodayTotal(2);
            if(userId.equals(dis.getDistributorId())){
                int update = telCerDistributorMapper.updateByPrimaryKey(telCerDistributor);
                if(update > 0){
                    request.setDistributorId(userId);
                    theDisList = telCerDistributorMapper.getAllTelCerDistributorInfo(page,request);
                    return AjaxResult.success(theDisList,"更新成功",update);
                }
                return AjaxResult.success(telCerDistributor,"更新失败",update);
            }
            int insert = telCerDistributorMapper.insert(telCerDistributor);
            if(insert > 0){
                request.setDistributorId(userId);
                theDisList = telCerDistributorMapper.getAllTelCerDistributorInfo(page,request);
                return AjaxResult.success(theDisList,"新建成功",insert);
            }
        }
        return AjaxResult.error("查询失败");

//        List<CertificationConfig> all = certificationConfigMapper.getAllConfig(page);
//        int count = certificationConfigMapper.getCount();
//
//        List<TelCerDistributor> telCerDistributorList = telCerDistributorMapper.getAllTelCerDistributorInfo(page);
//        for (TelCerDistributor telCerDistributor : telCerDistributorList) {
//            telCerDistributor.setDistributorName(ShiroUtils.getSysUser().getUserName());
//
//            telCerDistributor.setTotal(1);
//            telCerDistributor.setLastMonthTotal(1);
//            telCerDistributor.setTheMonthTotal(1);
//            telCerDistributor.setYesterdayTotal(1);
//        }
//        return AjaxResult.success(telCerDistributorList,"获取成功",telCerDistributorList.size());
    }

    public TelCerDistributor getServiceNum(ModelMap map) {
        Integer userId = ShiroUtils.getSysUser().getId();
        TelCerDistributor allService = telCerDistributorMapper.getServiceNum(userId);
        TelCerDistributor theMonthService = telCerDistributorMapper.getTheMonthService(userId);
        TelCerDistributor todayService = telCerDistributorMapper.getTodayService(userId);
        Page page = new Page();
        page.setPage(0);
        page.setPagesize(10);
        List<CertificationConfig> configs = certificationConfigMapper.getAllConfig(page);
        Float teddyPrice = null;
        for (CertificationConfig c : configs) {
            if(Const.TEL_CER_CONFIG_TYPE_TEDDY.equals(c.getType())){
                teddyPrice = c.getPrice();
            }
        }
        Float teddyAndTelBond1 = (allService.getTeddyNum() + allService.getTelBondNum())*teddyPrice;
        Float teddyAndTelBond2 = (theMonthService.getTeddyNum() + theMonthService.getTelBondNum())*teddyPrice;
        Float teddyAndTelBond3 = (todayService.getTeddyNum() + todayService.getTelBondNum())*teddyPrice;
//        Float teddyAndTelBondAndHangUp1 = teddyAndTelBond1 + allService.getHangUpMessage();
//        Float teddyAndTelBondAndHangUp2 = teddyAndTelBond2 + allService.getHangUpMessage();
//        Float teddyAndTelBondAndHangUp3 = teddyAndTelBond3 + allService.getHangUpMessage();
        allService.setTeddyAndTelBond(teddyAndTelBond1);
        theMonthService.setTeddyAndTelBond(teddyAndTelBond2);
        todayService.setTeddyAndTelBond(teddyAndTelBond3);
        map.put("allService",allService);
        map.put("theMonthService",theMonthService);
        map.put("todayService",todayService);
        map.put("configList",certificationConfigMapper.getAllConfig(page));
        return allService;
    }
}
