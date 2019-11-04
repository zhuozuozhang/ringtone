package com.hrtxn.ringtone.project.telcertification.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.Const;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.SpringUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.freemark.config.systemConfig.RingtoneConfig;
import com.hrtxn.ringtone.project.system.File.domain.Uploadfile;
import com.hrtxn.ringtone.project.system.File.mapper.UploadfileMapper;
import com.hrtxn.ringtone.project.system.File.service.FileService;
import com.hrtxn.ringtone.project.system.user.domain.User;
import com.hrtxn.ringtone.project.system.user.mapper.UserMapper;
import com.hrtxn.ringtone.project.telcertification.domain.*;
import com.hrtxn.ringtone.project.telcertification.mapper.CertificationChildOrderMapper;
import com.hrtxn.ringtone.project.telcertification.mapper.CertificationConfigMapper;
import com.hrtxn.ringtone.project.telcertification.mapper.CertificationOrderMapper;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author:lile
 * Date:2019-07-10 16:07
 * Description:电话认证业务层
 */
@Service
public class TelCertificationService {

    @Autowired
    private CertificationOrderMapper certificationOrderMapper;
    @Autowired
    private CertificationChildOrderMapper certificationChildOrderMapper;
    @Autowired
    private CertificationConfigMapper certificationConfigMapper;

    @Autowired
    private UserMapper userMapper;


    /**
     * 获得号码认证商户订单数量
     *
     * @return
     */
    public int getCount(BaseRequest request) {
        // 是否是管理员 是则统计所有数据
        Integer userRole = ShiroUtils.getSysUser().getUserRole();
        if (userRole != 1) {
            request.setUserId(ShiroUtils.getSysUser().getId());
        }
        return certificationOrderMapper.getCount(request);
    }

    /**
     * 获得所有商户订单（可通过各种条件查找）
     *
     * @param page
     * @param request
     * @return
     */
    public AjaxResult findAllTelCertification(Page page, BaseRequest request,ModelMap map) throws ParseException {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        request.setUserId(ShiroUtils.getSysUser().getId());
        List<CertificationOrder> theTelCer = new ArrayList<CertificationOrder>();
        //通过时间段、集团名称、联系人电话、成员号码查到商户信息
        String rangeTime = request.getRangetime();
        String phoneNum = request.getPhoneNum();
        if(request.getUserId() == 16){
            request.setUserId(null);
        }
        if ((phoneNum != null && phoneNum != "") || (rangeTime != null && rangeTime != "")) {

            if (phoneNum != null && phoneNum != "") {
                //对联系人电话的处理
//                String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
//                Pattern p = Pattern.compile(regex);

                //根据成员电话号查找
                List<CertificationChildOrder> ccList = certificationChildOrderMapper.findTheChildOrder(page, request);
                Integer[] ids = new Integer[100];
                int i = 0;
                for (CertificationChildOrder cc : ccList) {
                    ids[i] = cc.getParentOrderId();
                    i++;
//                    request.setId(cc.getParentOrderId());
                }
                request.setArrayById(ids);
                System.out.println(ids);
                System.out.println(request.getArrayById());
                //成员电话号码
//                int len2 = phoneNum.length();
//                if (len2 == 11) {
//                    Matcher m = p.matcher(phoneNum);
//                    boolean isMatch = m.matches();
//                    if (!isMatch) {
//                        return AjaxResult.success(theTelCer, "手机号不正确");
//                    }
//                }
            }
            if (rangeTime != null && rangeTime != "") {
                //对时间段的处理
                String[] s = rangeTime.split("-");
                String start = s[0] + "-" + s[1] + "-" + s[2] + " 00:00:00";
                String end = s[3] + "-" + s[4] + "-" + s[5] + " 23:59:59";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date dateStart = sdf.parse(start);
                Date dateEnd = sdf.parse(end);
                request.setStart(start);
                request.setEnd(end);
            }
        }
        List<CertificationOrder> allTelCer = certificationOrderMapper.findAllTelCertification(page, request);
        int totalCount = certificationOrderMapper.getCount(request);

        for (CertificationOrder c : allTelCer) {
            formatParamFromDatabase(c);
            int member = certificationChildOrderMapper.getMemberCountByParentId(c.getId());
            c.setMemberNum(member);
            c.setStatusStr(getStatusStr(c.getTelOrderStatus()));
        }
        if (allTelCer.size() > 0 && allTelCer != null) {
            return AjaxResult.success(allTelCer, "获取到了", totalCount);
        }
        return AjaxResult.success(theTelCer, "未获取到",totalCount);
    }

    public String getStatusStr(Integer status){
        if(Const.TEL_ORDER_IN_AUDIT == status){
            return "审核中";
        }else if(Const.TEL_ORDER_IN_OPENING == status){
            return "审核成功";
        }else if(Const.TEL_ORDER_FAILURE_TO_OPEN == status){
            return "审核失败";
        }
        return "";
    }


    /**
     * 通过订单id获取订单信息
     *
     * @param id
     * @return
     */
    public CertificationOrder getTelCerOrderById(Integer id, ModelMap map) {
        if (StringUtils.isNotNull(id)) {
            CertificationOrder certificationOrder = certificationOrderMapper.getTelCerOrderById(id);

            formatParamFromDatabase(certificationOrder);

            String json = certificationOrder.getProductName();
            JSONObject js = JSON.parseObject(json);
            Map<String, Object> product = js;
            Object service = null;
            for (Map.Entry<String, Object> entry : product.entrySet()) {
                service = entry.getValue();
            }
            int num = certificationChildOrderMapper.getMemberCountByParentId(id);
            certificationOrder.setMemberNum(num);
            certificationOrder.setBusinessLicense("/profile"+certificationOrder.getBusinessLicense());
            certificationOrder.setLegalPersonCardZhen("/profile"+certificationOrder.getLegalPersonCardZhen());
            certificationOrder.setLegalPersonCardFan("/profile"+certificationOrder.getLegalPersonCardFan());
            certificationOrder.setLogo("/profile"+certificationOrder.getLogo());
            certificationOrder.setAuthorization("/profile"+certificationOrder.getAuthorization());
            certificationOrder.setNumberProve("/profile"+certificationOrder.getNumberProve());
            map.put("service", service);
            map.put("telCerOrder", certificationOrder);
            return certificationOrder;
        }
        return null;
    }


    public AjaxResult examine(CertificationOrder telcerOrder){
        try {
            CertificationOrder certificationOrder = certificationOrderMapper.getTelCerOrderById(telcerOrder.getId());
            if(Const.TEL_ORDER_IN_OPENING == telcerOrder.getTelOrderStatus()){
                Float price = certificationChildOrderMapper.queryPriceByPid(telcerOrder.getId());
                User user = userMapper.findUserById(certificationOrder.getUserId());
                if(user.getTelcertificationAccount() < price){
                    telcerOrder.setTelOrderStatus(Const.TEL_ORDER_FAILURE_TO_OPEN);
                    telcerOrder.setRemark("余额不足，请联系管理员充值！");
                    certificationOrderMapper.examine(telcerOrder);
                    return AjaxResult.error("审核失败，余额不足！");
                }
            }

            certificationOrderMapper.examine(telcerOrder);

            CertificationChildOrder certificationChildOrder = new CertificationChildOrder();
            if(Const.TEL_ORDER_IN_OPENING == telcerOrder.getTelOrderStatus()){
                certificationChildOrder.setTelChildOrderStatus(telcerOrder.getTelOrderStatus());
                certificationChildOrder.setParentOrderId(telcerOrder.getId());
                certificationChildOrderMapper.updateExamine(certificationChildOrder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AjaxResult.success("审核成功！");
    }

    /**
     * 格式化`tb_telcertification_order`数据库中的数据
     *
     * @param c
     */
    private void formatParamFromDatabase(CertificationOrder c) {

        if (StringUtils.isNotNull(c) && c != null) {
            //号码认证订单状态（1.开通中/2.开通成功/3.开通失败/4.续费中/5.续费成功/6.续费失败）
            if (c.getTelOrderStatus() == 1) {
                c.setStatusName("开通中");
            } else if (c.getTelOrderStatus() == 2) {
                c.setStatusName("开通成功");
            } else if (c.getTelOrderStatus() == 3) {
                c.setStatusName("开通失败");
            } else if (c.getTelOrderStatus() == 4) {
                c.setStatusName("续费中");
            } else if (c.getTelOrderStatus() == 5) {
                c.setStatusName("续费成功");
            } else if (c.getTelOrderStatus() == 6) {
                c.setStatusName("续费失败");
            } else {
                c.setStatusName("未知");
            }

            //如果备注中的内容为空，则改变值为无
            if (c.getRemark() == "" || c.getRemark() == null) {
                c.setRemark("无");
            }
        } else {
            return;
        }
    }

    /**
     * 删除商户信息
     * @param id
     * @return
     */
    public AjaxResult deleteTelCer(Integer id) {
        if (StringUtils.isNotNull(id) && id != 0) {
            int count = certificationOrderMapper.deleteByPrimaryKey(id);
            if (count > 0) {
                return AjaxResult.success(true, "删除成功！");
            }
            return AjaxResult.error("删除失败！");
        }
        return AjaxResult.error("参数格式错误！");
    }

    /**
     * 修改商户信息
     * @param certificationOrder
     * @return
     */
    public AjaxResult update(CertificationOrder certificationOrder) {
        certificationOrder.setTelOrderTime(new Date());
        int i = certificationOrderMapper.updateByPrimaryKey(certificationOrder);
        if (i > 0) {
            SpringUtils.getBean(FileService.class).updateStatus(certificationOrder.getBusinessLicense());
            SpringUtils.getBean(FileService.class).updateStatus(certificationOrder.getLegalPersonCardZhen());
            SpringUtils.getBean(FileService.class).updateStatus(certificationOrder.getLegalPersonCardFan());
            SpringUtils.getBean(FileService.class).updateStatus(certificationOrder.getLogo());
            SpringUtils.getBean(FileService.class).updateStatus(certificationOrder.getAuthorization());
            SpringUtils.getBean(FileService.class).updateStatus(certificationOrder.getNumberProve());
            return AjaxResult.success(i, "修改商户信息成功");
        } else {
            return AjaxResult.error("修改商户信息失败");
        }
    }

    /**
     * 添加号码认证商户订单
     *
     * @param certificationProduct
     * @return
     */
    public AjaxResult addTelCertifyOrder(CertificationRequest certificationProduct) {
        if (!StringUtils.isNotNull(certificationProduct)) {
            return AjaxResult.error("参数不正确！");
        }
        Page page = new Page();
        page.setPage(0);
        page.setPagesize(5);
        List<CertificationConfig> allConfig = certificationConfigMapper.getAllConfig(page);
        List<CertificationConfig> newConfig = new ArrayList<CertificationConfig>();
        if(certificationProduct.getTeddy() != null && certificationProduct.getTeddy() != ""){
            CertificationConfig certificationConfig = new CertificationConfig();
            certificationConfig.setName(certificationProduct.getTeddy());
            String year = String.valueOf(certificationProduct.getYear());
            certificationConfig.setPeriodOfValidity(year + "年");
            for (CertificationConfig config : allConfig) {
                if(config.getType().equals(Const.TEL_CER_CONFIG_TYPE_TEDDY)){
                    Float years = Float.parseFloat(year);
                    if(certificationProduct.getUnitPrice() == 0){
                        certificationProduct.setUnitPrice(config.getPrice());
                    }
                    certificationConfig.setCost(config.getPrice()*years);
                }
            }
            newConfig.add(certificationConfig);
        }
        if(certificationProduct.getTelBond() != null && certificationProduct.getTelBond() != ""){
            CertificationConfig certificationConfig = new CertificationConfig();
            certificationConfig.setName(certificationProduct.getTelBond());
            String year = String.valueOf(certificationProduct.getYear());
            certificationConfig.setPeriodOfValidity(year + "年");
            for (CertificationConfig config : allConfig) {
                if(config.getType().equals(Const.TEL_CER_CONFIG_TYPE_TELBOND)){
                    Float years = Float.parseFloat(year);
                    certificationConfig.setCost(config.getPrice()*years);
                }
            }
            newConfig.add(certificationConfig);
        }
        if(certificationProduct.getColorPrint() != null && certificationProduct.getColorPrint() != ""){
            CertificationConfig certificationConfig = new CertificationConfig();
            certificationConfig.setName(certificationProduct.getColorPrint());
            certificationConfig.setPeriodOfValidity(Const.TEL_CER_COLORPRINT_ETERNAL);
            certificationConfig.setCost(Const.TEL_CER_COLORPRINT_COST);
            newConfig.add(certificationConfig);
        }
        if(certificationProduct.getHangUpMessage() != null && certificationProduct.getHangUpMessage() != ""){
            CertificationConfig certificationConfig = new CertificationConfig();
            certificationConfig.setName(certificationProduct.getHangUpMessage());
            certificationConfig.setCost(certificationProduct.getHangUpMessagePrice());
            certificationConfig.setItemPerMonth(certificationProduct.getItemPerMonth());
            newConfig.add(certificationConfig);
        }

        JSONArray jsonArray = JSONArray.fromObject(newConfig);
        String service = "{\"service\":"+jsonArray.toString()+"}";
        certificationProduct.setUserId(ShiroUtils.getSysUser().getId());
        certificationProduct.setTelOrderStatus(Const.TEL_CER_STATUS_OPENING);
        certificationProduct.setTelOrderTime(new Date());
        certificationProduct.setProductName(service);

        int count = certificationOrderMapper.insertTelCertifyOrder(certificationProduct);
        int lastInsertId = certificationOrderMapper.getLastInsertId();

        String[] phoneNumberArray = new String[0];
        int childCount = 0;
        if(certificationProduct.getPhoneNumberArray()[0] != "") {
            phoneNumberArray = certificationProduct.getPhoneNumberArray();
            List<CertificationChildOrder> list = new ArrayList<CertificationChildOrder>();
            for (int i = 0; i < phoneNumberArray.length; i++) {
                String[] phoneNum = phoneNumberArray[i].split("\"");
                CertificationChildOrder childOrder = new CertificationChildOrder();
                if(phoneNum.length > 1){
                    for (int j = 0; j < 1; j++) {
                        childOrder.setTelChildOrderNum(phoneNum[1]);
                        childOrder.setTelChildOrderPhone(phoneNum[1]);
                        childOrder.setYears(certificationProduct.getYear());
                        childOrder.setPrice(certificationProduct.getUnitPrice());
                        childOrder.setTelChildOrderStatus(Const.TEL_CER_STATUS_OPENING);
                        childOrder.setBusinessFeedback("暂无");
                        childOrder.setTelChildOrderCtime(new Date());
                        childOrder.setTelChildOrderOpenTime(null);
                        childOrder.setTelChildOrderExpireTime(null);
                        childOrder.setParentOrderId(lastInsertId);
                        childOrder.setConsumeLogId(1);
                    }
                    list.add(childOrder);
                }
            }
            if(list.size() > 0){
                childCount = certificationChildOrderMapper.batchInsertChildOrder(list);
            }
        }

        if(count > 0){
            if(childCount > 0){
                updateFileStatus(certificationProduct);
                return AjaxResult.success(true,"添加商户信息和成员号码成功！");
            }
            updateFileStatus(certificationProduct);
            return AjaxResult.success(true,"添加商户成功！");
        }
        return AjaxResult.error("添加失败");
    }

    /**
     * 修改上传文件的状态
     * @param certificationProduct
     */
    private void updateFileStatus(CertificationRequest certificationProduct) {
        SpringUtils.getBean(FileService.class).updateStatus(certificationProduct.getBusinessLicense());
        SpringUtils.getBean(FileService.class).updateStatus(certificationProduct.getLegalPersonCardZhen());
        SpringUtils.getBean(FileService.class).updateStatus(certificationProduct.getLegalPersonCardFan());
        SpringUtils.getBean(FileService.class).updateStatus(certificationProduct.getLogo());
        SpringUtils.getBean(FileService.class).updateStatus(certificationProduct.getAuthorization());
        SpringUtils.getBean(FileService.class).updateStatus(certificationProduct.getNumberProve());
    }

    /**
     * 验证商户名称是否重复
     * @param telCompanyName
     * @return
     */
    public AjaxResult isRepetitionByName(String telCompanyName) {
        boolean isItRedundant = false;
        List<CertificationOrder> list = certificationOrderMapper.isRepetitionByName(telCompanyName);
        if (list != null && list.size() >= 1) {
            for (int i = 0; i < list.size(); i++) {
                String cName = list.get(i).getTelCompanyName();
                if (cName.length() <= 6) {
                    if (cName.equals(telCompanyName)){
                        return AjaxResult.error("商户名称不允许重复！");
                    }else{
                        continue;
                    }
                }
                boolean result = cName.substring(cName.length() - 6).matches("[0-9]+");
                if (result) {
                    isItRedundant = cName.substring(0, cName.length() - 6).equals(telCompanyName);
                } else {
                    isItRedundant = cName.equals(telCompanyName);
                }
            }
        }
        if (isItRedundant) {
            return AjaxResult.error("商户名称不允许重复！");
        } else {
            return AjaxResult.success("商户名称可用！");
        }
    }

    /**
     * 验证联系人电话是否重复
     * @param telLinkPhone
     * @return
     */
    public AjaxResult isRepetitionByTelLinkPhone(String telLinkPhone) {
        List<CertificationOrder> list = certificationOrderMapper.isRepetitionByTelLinkPhone(telLinkPhone);
        if(list != null && list.size() >= 1){
            for (int i = 0; i < list.size(); i++) {
                String cTelPhone = list.get(i).getTelLinkPhone();
                if(cTelPhone.length() <= 12 && cTelPhone.length() >10){
                    if(cTelPhone.equals(telLinkPhone)){
                        return AjaxResult.error(500,"联系人电话不允许重复！");
                    }else{
                        continue;
                    }
                }
            }
        }
        return AjaxResult.success("联系人电话未重复！");
    }
}
