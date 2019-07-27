package com.hrtxn.ringtone.project.telcertification.controller;

import com.hrtxn.ringtone.project.telcertification.service.TelCertificationChildService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Author:lile
 * Date:2019-07-11 09:07
 * Description:电话认证成员控制层
 */
@Slf4j
@Controller
public class TelCertificationChildController {

    @Autowired
    private TelCertificationChildService telCertificationChildService;
}
