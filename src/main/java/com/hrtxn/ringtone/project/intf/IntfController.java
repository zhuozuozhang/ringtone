package com.hrtxn.ringtone.project.intf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrtxn.ringtone.common.utils.Const;
import com.hrtxn.ringtone.common.utils.SpringUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.intf.domain.ExamineResult;
import com.hrtxn.ringtone.project.intf.domain.PreementionResult;
import com.hrtxn.ringtone.project.intf.domain.TemplateResult;
import com.hrtxn.ringtone.project.numcertification.domain.FourcertificationOrder;
import com.hrtxn.ringtone.project.numcertification.domain.NumcertificationOrder;
import com.hrtxn.ringtone.project.numcertification.service.FourCertificationService;
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

    @Autowired
    private FourCertificationService fourCertificationService;


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
                FourcertificationOrder fourcertificationOrder = fourCertificationService.getOrderByApplyNumber(applyNumber);

                if(fourcertificationOrder != null){
                    fourcertificationOrder.setStatus(preementionResult.getData().getAuditStatus());
                    fourcertificationOrder.setPlatform(preementionResult.getData().getPlatform());
                    fourCertificationService.update(fourcertificationOrder);
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
                String applyNumber = templateResult.getApplyNumber();
                //根据400号码查询订单
                FourcertificationOrder fourcertificationOrder = fourCertificationService.getOrderByApplyNumber(applyNumber);
                if(fourcertificationOrder != null){
                    TemplateResult.TemplateBean template = templateResult.getData();
                    String remarks = "";
                    if("true".equals(template.getCheckCompanySuccess())){
                        //模板返回数据回填订单
                        fourcertificationOrder.setCorpBusinessScope(template.getCorpBusinessScope());
                        fourcertificationOrder.setCorpCompanyDetail(template.getCorpCompanyDetail());
                        fourcertificationOrder.setCorpOfficeDetail(template.getCorpOfficeDetail());
                        fourcertificationOrder.setCorpSocietyNo(template.getCorpSocietyNo());
                        fourcertificationOrder.setLegalName(template.getLegalName());
                    }else{
                        remarks = "公司名称";
                    }
                    if("true".equals(template.getCheckOperatorCardFileSuccess())){
                        fourcertificationOrder.setLegalIdentityId(template.getLegalIdentityId());
                        fourcertificationOrder.setLegalAddress(template.getLegalAddress());
                    }else{
                        if(StringUtils.isNotEmpty(remarks)){
                            remarks = remarks + "、";
                        }
                        remarks = remarks + "法人身份证";
                    }
                    if("true".equals(template.getCheckOperatorCardFileSuccess())){
                        fourcertificationOrder.setLegalHandlerAddress(template.getLegalHandlerAddress());
                        fourcertificationOrder.setLegalHandlerIdentityId(template.getLegalHandlerIdentityId());
                    }else{
                        if(StringUtils.isNotEmpty(remarks)){
                            remarks = remarks + "、";
                        }
                        remarks = remarks + "经办人身份证";
                    }
                    if(StringUtils.isBlank(remarks)){
                        fourcertificationOrder.setStatus(Const.FOUR_ORDER_TEMPLATE_SUCCESS);
                    }else{
                        fourcertificationOrder.setStatus(Const.FOUR_ORDER_TEMPLATE_FAIL);
                        fourcertificationOrder.setRemarks(remarks + "解析失败");
                    }
                    fourcertificationOrder.setTaskId(template.getTaskId());
                    fourcertificationOrder.setTemplateUrl(template.getTemplateUrl());
                    fourCertificationService.update(fourcertificationOrder);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * //预占结果通知
     */
    public void examineResult(HttpServletRequest request,String body){
        try {
            if(StringUtils.isNotEmpty(body)){
                ExamineResult examineResult = SpringUtils.getBean(ObjectMapper.class).readValue(body, ExamineResult.class);
                System.out.print("解析成功");
                //400号码
                String applyNumber = examineResult.getApplyNumber();
                //根据400号码查询订单
                FourcertificationOrder fourcertificationOrder = fourCertificationService.getOrderByApplyNumber(applyNumber);

                if(fourcertificationOrder != null){
                    String approveStatus = examineResult.getData().getApproveStatus();
                    if("PASS".equals(approveStatus)){
                        fourcertificationOrder.setStatus(Const.FOUR_ORDER_SUBMIT_SUCCESS);
                    }else if("BACK".equals(approveStatus)){
                        fourcertificationOrder.setStatus(Const.FOUR_ORDER_SUBMIT_FAIL);
                    }
                    fourcertificationOrder.setRemarks(examineResult.getData().getApproveComment());
                }
                fourCertificationService.update(fourcertificationOrder);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
