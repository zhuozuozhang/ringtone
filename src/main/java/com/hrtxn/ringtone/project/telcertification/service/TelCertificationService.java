package com.hrtxn.ringtone.project.telcertification.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationChildOrder;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationOrder;
import com.hrtxn.ringtone.project.telcertification.domain.CertificationRequest;
import com.hrtxn.ringtone.project.telcertification.mapper.CertificationChildOrderMapper;
import com.hrtxn.ringtone.project.telcertification.mapper.CertificationOrderMapper;
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
        List<CertificationOrder> theTelCer = new ArrayList<CertificationOrder>();
        //通过时间段、集团名称、联系人电话、成员号码查到商户信息
        String rangeTime = request.getRangetime();
        String phoneNum = request.getPhoneNum();

        if ((phoneNum != null && phoneNum != "") || (rangeTime != null && rangeTime != "")) {

            if (phoneNum != null && phoneNum != "") {
                //对联系人电话的处理
                String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
                Pattern p = Pattern.compile(regex);
                //成员电话号码
                int len2 = phoneNum.length();
                if (len2 == 11) {
                    Matcher m = p.matcher(phoneNum);
                    boolean isMatch = m.matches();
                    if (!isMatch) {
                        return AjaxResult.success(theTelCer, "手机号不正确");
                    }
                    //根据成员电话号查找
                    List<CertificationChildOrder> ccList = certificationChildOrderMapper.findTheChildOrder(page, request);
                    for (CertificationChildOrder cc : ccList) {
                        request.setId(cc.getParentOrderId());
                    }
                }
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
        }
        if (allTelCer.size() > 0 && allTelCer != null) {
            return AjaxResult.success(allTelCer, "获取到了", totalCount);
        }
        return AjaxResult.success(theTelCer, "未获取到");
    }

    /**
     * 通过订单id获取订单信息
     *
     * @param id
     * @return
     */
    public CertificationOrder getTelCerOrderById(Integer id, ModelMap map) {
        if (StringUtils.isNotNull(id)) {
            CertificationOrder c = certificationOrderMapper.getTelCerOrderById(id);

            formatParamFromDatabase(c);

            String json = c.getProductName();
            JSONObject js = JSON.parseObject(json);
            Map<String, Object> product = js;
            Object service = null;
            for (Map.Entry<String, Object> entry : product.entrySet()) {
                service = entry.getValue();
            }
            int num = certificationChildOrderMapper.getMemberCountByParentId(id);
            c.setMemberNum(num);
            map.put("service", service);
            map.put("telCerOrder", c);
            return c;
        }
        return null;
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
        CertificationOrder certificationOrder1 = certificationOrder;
        int i = certificationOrderMapper.updateByPrimaryKey(certificationOrder);
        if (i > 0) {
            return AjaxResult.success(i, "修改成功");
        } else {
            return AjaxResult.error("修改失败");
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
        CertificationRequest certificationProduct1 = certificationProduct;
//        int count = certificationOrderMapper.insertTelCertifyOrder(certificationOrder);
        return null;
    }
}
