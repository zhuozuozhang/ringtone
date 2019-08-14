package com.hrtxn.ringtone.project.threenets.kedas.kedasites.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Author:zcy
 * Date:2019-08-14 17:32
 * Description:疑难杂单铃音
 */
@Controller
@RequestMapping("/threenets/clcy/")
public class KedaRingController {

    @GetMapping("toRingList/{id}/{name}")
    private String toRingList(@PathVariable Integer id, @PathVariable String name, ModelMap map){
        map.put("id",id);
        map.put("name",name);
        return "threenets/kedas/kedasites/merchants/ring_list";
    }

}
