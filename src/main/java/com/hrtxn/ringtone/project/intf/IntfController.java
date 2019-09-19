package com.hrtxn.ringtone.project.intf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrtxn.ringtone.common.utils.Const;
import com.hrtxn.ringtone.common.utils.SpringUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.intf.domain.PreementionResult;
import com.hrtxn.ringtone.project.intf.domain.TemplateResult;
import com.hrtxn.ringtone.project.numcertification.domain.NumcertificationOrder;
import com.hrtxn.ringtone.project.numcertification.service.NumCertificationService;
import com.hrtxn.ringtone.project.threenets.kedas.kedasites.json.KedaBaseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @param
 * @Author zcy
 * @Date 2019-08-29 10:01
 * @return
 */
@Controller
public class IntfController {

    @Autowired
    private NumCertificationService numCertificationService;


    @RequestMapping("/jsjt")
    public String receive(HttpServletRequest request, HttpServletResponse response,@RequestBody String body){

        String account = request.getParameter("account");
        if(Const.PREEMPTION_RESULT.equals(account)){
            //预占结果通知
            preemptionResult(request,body);
        }else if(Const.TEMPLATE_GENERATION_RESULT.equals(account)){
            //资料模板生成完成通知
            templateResult(request,body);
        }else if(Const.EXAMINE_RESULT.equals(account)){
            //资料审核通知

        }


        return "success";
    }

    /**
     * //预占结果通知
     */
    public void preemptionResult(HttpServletRequest request,String body){
        try {
            if(StringUtils.isNotEmpty(body)){
                PreementionResult preementionResult = SpringUtils.getBean(ObjectMapper.class).readValue(body, PreementionResult.class);
                System.out.print("解析成功");
                //400号码
                String applyNumber = preementionResult.getApplyNumber();
                //根据400号码查询订单
                NumcertificationOrder numcertificationOrder = numCertificationService.getOrderByphoneNum(applyNumber);
                if(numcertificationOrder != null){
                    numcertificationOrder.setAuditStatus(preementionResult.getData().getAuditStatus());
                    numcertificationOrder.setUpdateDate(preementionResult.getData().getApplyDate());
                    numcertificationOrder.setPlatform(preementionResult.getData().getPlatform());
                    numCertificationService.update(numcertificationOrder);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 资料模板生成完通知
     */
    public void templateResult(HttpServletRequest request,String body){
        try {
            if(StringUtils.isNotEmpty(body)){
                TemplateResult templateResult = SpringUtils.getBean(ObjectMapper.class).readValue(body, TemplateResult.class);
                System.out.print("解析成功");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
