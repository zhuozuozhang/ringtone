package com.hrtxn.ringtone.project.telcertification.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.Const;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationConfig;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationOrder;
import com.hrtxn.ringtone.project.telcertification.domain.PlotBarData;
import com.hrtxn.ringtone.project.telcertification.domain.TelCerDistributor;
import com.hrtxn.ringtone.project.telcertification.mapper.CertificationConfigMapper;
import com.hrtxn.ringtone.project.telcertification.mapper.CertificationOrderMapper;
import com.hrtxn.ringtone.project.telcertification.mapper.TelCerDistributorMapper;
import net.sf.json.JSONString;
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
    private CertificationOrderMapper certificationOrderMapper;

    /**
     * 获得渠道商信息
     * @param page
     * @param request
     * @return
     */
    public AjaxResult getTelCerDistributor(Page page,BaseRequest request) {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        List<TelCerDistributor> allDisList = telCerDistributorMapper.getAllTelCerDistributorInfo(page,request);
        List<TelCerDistributor> theDisList = new ArrayList<TelCerDistributor>();
        Integer userId = ShiroUtils.getSysUser().getId();

        TelCerDistributor telCerDistributor = new TelCerDistributor();
        telCerDistributor.setDistributorId(ShiroUtils.getSysUser().getId());
        telCerDistributor.setDistributorName(ShiroUtils.getSysUser().getUserName());
        if (ShiroUtils.getSysUser().getParentId() != 0 || ShiroUtils.getSysUser().getParentId() != 2){
            telCerDistributor.setStage("非顶级");
        }
        telCerDistributor.setStage("顶级");
        TelCerDistributor allService = telCerDistributorMapper.getServiceNum(userId);
        TelCerDistributor lastMonth = telCerDistributorMapper.getLastMonth(userId);
        TelCerDistributor theMonthService = telCerDistributorMapper.getTheMonthService(userId);
        TelCerDistributor todayService = telCerDistributorMapper.getTodayService(userId);
        telCerDistributor.setTotal(allService.getTeddyNum() + allService.getTelBondNum() + allService.getColorPrintNum() + allService.getHangupMessageNum());
        telCerDistributor.setLastMonthTotal(lastMonth.getTeddyNum() + lastMonth.getTelBondNum() + lastMonth.getColorPrintNum() + lastMonth.getHangupMessageNum());
        telCerDistributor.setTheMonthTotal(theMonthService.getTeddyNum() + theMonthService.getTelBondNum() + theMonthService.getColorPrintNum() + theMonthService.getHangupMessageNum());
        telCerDistributor.setTodayTotal(todayService.getTeddyNum() + todayService.getTelBondNum() + todayService.getColorPrintNum() + todayService.getHangupMessageNum());

        if(allDisList.size() <= 0){
            int insert = telCerDistributorMapper.insert(telCerDistributor);
            if(insert > 0){
                request.setDistributorId(userId);
                theDisList = telCerDistributorMapper.getAllTelCerDistributorInfo(page,request);
                return AjaxResult.success(theDisList,"新建成功",insert);
            }
        }else{
            for (TelCerDistributor dis : allDisList) {
                if(userId.equals(dis.getDistributorId())){
                    int update = telCerDistributorMapper.updateByPrimaryKey(telCerDistributor);
                    if(update > 0){
                        request.setDistributorId(userId);
                        theDisList = telCerDistributorMapper.getAllTelCerDistributorInfo(page,request);
                        return AjaxResult.success(theDisList,"更新成功",update);
                    }
                    return AjaxResult.success(telCerDistributor,"更新失败",update);
                }
            }
        }
        return AjaxResult.success(500,theDisList,"查询失败",0);
    }

    /**
     * 进入订购统计页面
     * @param map
     * @return
     */
    public TelCerDistributor getServiceNum(ModelMap map) {
        Integer userId = ShiroUtils.getSysUser().getId();
        TelCerDistributor allService = telCerDistributorMapper.getServiceNum(userId);
        TelCerDistributor theMonthService = telCerDistributorMapper.getTheMonthService(userId);
        TelCerDistributor todayService = telCerDistributorMapper.getTodayService(userId);

        List<CertificationOrder> hangUpListsAll = certificationOrderMapper.gethangUpListsAll(userId);
        List<CertificationOrder> hangUpListsTheMonth = certificationOrderMapper.getHangUpListsTheMonth(userId);
        List<CertificationOrder> hangUpListsToday = certificationOrderMapper.getHangUpListsToday(userId);

        Float hangUpMessageAll = dealWithHangUpLists(hangUpListsAll);
        Float hangUpMessageTheMonth = dealWithHangUpLists(hangUpListsTheMonth);
        Float hangUpMessageToday = dealWithHangUpLists(hangUpListsToday);

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

        Float teddyAndTelBondAndHangUp1 = (allService.getTeddyNum() + allService.getTelBondNum())*teddyPrice + hangUpMessageAll;
        Float teddyAndTelBondAndHangUp2 = (theMonthService.getTeddyNum() + theMonthService.getTelBondNum())*teddyPrice + hangUpMessageTheMonth;
        Float teddyAndTelBondAndHangUp3 = (todayService.getTeddyNum() + todayService.getTelBondNum())*teddyPrice + hangUpMessageToday;

        allService.setHangUpMessage(hangUpMessageAll);
        theMonthService.setHangUpMessage(hangUpMessageTheMonth);
        todayService.setHangUpMessage(hangUpMessageToday);

        allService.setTeddyAndTelBondAndHangUp(teddyAndTelBondAndHangUp1);
        theMonthService.setTeddyAndTelBondAndHangUp(teddyAndTelBondAndHangUp2);
        todayService.setTeddyAndTelBondAndHangUp(teddyAndTelBondAndHangUp3);

        map.put("allService",allService);
        map.put("theMonthService",theMonthService);
        map.put("todayService",todayService);
        map.put("configList",certificationConfigMapper.getAllConfig(page));
        return allService;
    }

    /**
     * 计算总挂机短信金额
     * @param hangUpListsAll
     * @return
     */
    private Float dealWithHangUpLists(List<CertificationOrder> hangUpListsAll) {
        Float hangUpMessageAll = Float.valueOf(0);
        for (CertificationOrder telcerOrder : hangUpListsAll) {
            String product = telcerOrder.getProductName();
            JSONObject jsonObject = JSON.parseObject(product);
//            JSONObject service = jsonObject.getJSONObject("service");
            JSONArray jsonService = jsonObject.getJSONArray("service");
            for (int i = 0; i < jsonService.size(); i++) {
                JSONObject partService = jsonService.getJSONObject(i);
                if(Const.HANGUPMESSAGE.equals(partService.getString("name"))){
                    String cost = partService.getString("cost");
                    Float costFloat = Float.valueOf(cost);
                    hangUpMessageAll += costFloat;
                }
            }
        }
        return hangUpMessageAll;
    }

//    public AjaxResult getMonthData(BaseRequest request) throws Exception {
//        if (request.getUserId() == null) {
//            request.setUserId(ShiroUtils.getSysUser().getId());
//        }
//        if (request.getUserId().equals(0)) {
//            request.setUserId(ShiroUtils.getSysUser().getUserRole().equals(1) ? null : ShiroUtils.getSysUser().getId());
//            request.setArrayById(getChildUserList());
//        }
//        List<PlotBarData> list = telCerDistributorMapper.getMonthData(request);
//        list = formatterDate(list, request);
//        return AjaxResult.success(list, "获取成功");
//    }
//
//    public Integer[] getChildUserList() throws Exception {
//        Page page = new Page(0, 9999);
//        Integer id = ShiroUtils.getSysUser().getId();
//        List<UserVo> list = userMapper.getUserList(null, null);
//        Map<Integer, List<UserVo>> map = list.stream().collect(Collectors.groupingBy(User::getParentId));
//        List<User> userList = new ArrayList<>();
//        recursion(map.get(id), map, userList);
//        Integer[] ids = new Integer[userList.size() + 1];
//        for (int i = 0; i < userList.size(); i++) {
//            User user = userList.get(i);
//            ids[i] = user.getId();
//        }
//        ids[userList.size()] = id;
//        return ids;
//    }
//
//    //递归
//    private void recursion(List<UserVo> list, Map<Integer, List<UserVo>> map, List<User> userList) {
//        if (list == null || list.isEmpty()) {
//            return;
//        }
//        userList.addAll(list);
//        for (int i = 0; i < list.size(); i++) {
//            UserVo vo = list.get(i);
//            List<UserVo> vos = map.get(vo.getId());
//            recursion(vos, map, userList);
//        }
//    }
//
//    /**
//     * 格式化数据
//     *
//     * @param list
//     * @param request
//     * @return
//     */
//    private List<PlotBarPhone> formatterDate(List<PlotBarPhone> list, BaseRequest request) {
//        List<PlotBarPhone> newList = new ArrayList<>();
//        Map<String, List<PlotBarPhone>> collect = list.stream().collect(Collectors.groupingBy(PlotBarPhone::getDateTimes));
//        for (String time : collect.keySet()) {
//            PlotBarPhone plotBarPhone = new PlotBarPhone();
//            List<PlotBarPhone> plotBarPhones = collect.get(time);
//            List<PlotBarPhone> monthly = plotBarPhones.stream().filter(plot -> plot.getIsMonthly().equals(2)).collect(Collectors.toList());
//            List<PlotBarPhone> unsubscribe = plotBarPhones.stream().filter(plot -> plot.getIsMonthly().equals(3)).collect(Collectors.toList());
//            plotBarPhone.setDateTimes(time);
//            plotBarPhone.setAddUser(monthly.size() > 0 ? monthly.get(0).getAllNumber() : 0);
//            plotBarPhone.setUnsubscribeUser(unsubscribe.size() > 0 ? unsubscribe.get(0).getAllNumber() : 0);
//            if (plotBarPhone.getAddUser() == 0 && plotBarPhone.getUnsubscribeUser() == 0) {
//                continue;
//            }
//            newList.add(plotBarPhone);
//        }
//        newList.sort((o1, o2) -> o2.getDateTimes().compareTo(o1.getDateTimes()));
//        ThreenetsChildOrder threenetsChildOrder = new ThreenetsChildOrder();
//        threenetsChildOrder.setUserId(request.getUserId());
//        threenetsChildOrder.setOperator(request.getOperator());
//        threenetsChildOrder.setIsMonthly(2);
//        threenetsChildOrder.setMonth(request.getMonth());
//        threenetsChildOrder.setYear(request.getYear());
//        Integer count = threenetsChildOrderMapper.getCount(threenetsChildOrder);
//        for (PlotBarPhone plotBarPhone : newList) {
//            plotBarPhone.setCumulativeUser(count);
//            count = count - plotBarPhone.getAddUser();
//            count = count + plotBarPhone.getUnsubscribeUser();
//        }
//        if (newList.isEmpty()) {
//            String month = DateUtils.getMonth() < 10 ? "0" + DateUtils.getMonth() : DateUtils.getMonth() + "";
//            String day = DateUtils.getDay() < 10 ? "0" + DateUtils.getDay() : DateUtils.getDay() + "";
//            String time = DateUtils.getYear() + "-" + month + (StringUtils.isEmpty(request.getYear()) ? "-" + day : "");
//            PlotBarPhone plotBarPhone = new PlotBarPhone();
//            plotBarPhone.setDateTimes(time);
//            plotBarPhone.setAddUser(0);
//            plotBarPhone.setUnsubscribeUser(0);
//            plotBarPhone.setCumulativeUser(count);
//            newList.add(plotBarPhone);
//        }
//        return newList;
//    }
}
