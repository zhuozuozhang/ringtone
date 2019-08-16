package com.hrtxn.ringtone.project.telcertification.controller;

import com.hrtxn.ringtone.project.telcertification.domain.CertificationChildOrder;
import com.hrtxn.ringtone.project.telcertification.service.TelCertificationChildService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Author:lile
 * Date:2019-07-11 09:07
 * Description:电话认证成员控制层
 */
@Slf4j
@Controller
@RequestMapping("/telcertify")
public class TelCertificationChildController {

    @Autowired
    private TelCertificationChildService telCertificationChildService;

    /**
     * 进入成员管理页面
     * @return
     */
    @GetMapping("/toTelMembersPage")
    public String toTelMembersPage(ModelMap map){
        List<CertificationChildOrder> certificationChildOrders = telCertificationChildService.findAllChildOrder();
        map.put("telcerChildOrder",certificationChildOrders);
        return "telcertification/members";
    }
}
