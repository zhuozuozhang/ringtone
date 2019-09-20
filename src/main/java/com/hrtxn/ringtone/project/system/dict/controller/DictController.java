package com.hrtxn.ringtone.project.system.dict.controller;


import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.system.dict.domain.Dict;
import com.hrtxn.ringtone.project.system.dict.service.DictService;
import com.hrtxn.ringtone.project.system.telAscription.domain.TelAscription;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequestMapping("/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    /**
     * 获取字典列表
     * @return
     */
    @GetMapping("/toDictPage")
    public String toDictPage(ModelMap map){
        try {
        } catch (Exception e) {
            log.error("获取字典列表 方法：toDictPage 错误信息：",e);
        }
        return "admin/dict/dict_list";
    }

    /**
     * 获取字典列表
     *
     * @param page
     * @return
     */
    @ResponseBody
    @PostMapping("/queryDictList")
    public AjaxResult queryDictList(Page page, Dict dict){
        try {
            return dictService.pageDictList(page,dict);
        } catch (Exception e) {
            log.error("获取字典列列表 方法：queryDictList 错误信息：",e);
            return AjaxResult.error(e.getMessage());
        }
    }

    @RequiresRoles("admin")
    @GetMapping("/toDictAdd")
    public String toDictAddPage(){
        return "admin/dict/dict_add";
    }

    /**
     * 添加字典
     * @param dict
     * @return
     */
    @RequiresRoles("admin")
    @PostMapping("/addDict")
    @ResponseBody
    @Log(title = "添加字典",businessType = BusinessType.INSERT,operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult insertNotice(Dict dict){
        return dictService.insert(dict);
    }


    @RequiresRoles("admin")
    @GetMapping("/getDictById/{id}")
    public String getDictById(@PathVariable Integer id , ModelMap map){
        try {
            Dict dict = dictService.getDictById(id);
            map.put("dict",dict);
        } catch (Exception e) {
            log.error("跳转修改字典页面 方法：getDictById 错误信息",e);
        }
        return "admin/dict/dict_update";
    }

    /**
     * 修改字典
     * @param dict
     * @return
     */
    @RequiresRoles("admin")
    @ResponseBody
    @PutMapping("/updateDict")
    @Log(title = "修改字典",businessType = BusinessType.UPDATE,operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult updateDict(Dict dict){
        if (StringUtils.isNotNull(dict)){
            return dictService.update(dict);
        }
        return AjaxResult.error("修改失败");
    }

    /**
     * 删除公告
     * @param id
     * @return
     */
    @ResponseBody
    @DeleteMapping("/deleteDict/{id}")
    @Log(title = "删除公告",businessType = BusinessType.DELETE,operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult deleteDict(@PathVariable Integer id){
        return dictService.delete(id);
    }

}
