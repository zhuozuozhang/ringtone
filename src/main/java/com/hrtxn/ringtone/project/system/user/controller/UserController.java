package com.hrtxn.ringtone.project.system.user.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.system.user.domain.Menu;
import com.hrtxn.ringtone.project.system.user.domain.User;
import com.hrtxn.ringtone.project.system.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-01 15:51
 * Description:用户操作
 */
@Slf4j
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 根据用户名获取用户信息
     *
     * @param name
     * @return
     */
    @GetMapping("/findUserByUsername/{name}")
    @ResponseBody
    public User findUserByUsername(@PathVariable("name") String name) {
        User user = null;
        try {
            user = userService.findUserByUsername(name);
        } catch (Exception e) {
            log.error("根据用户名获取用户信息,方法：【{}】,错误信息：【{}】", "findUserByUsername", e.getMessage());
        }
        return user;
    }

    /**
     * 跳转代理商列表
     *
     * @param map
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/admin/userList")
    public String getUserList(ModelMap map) {
        return "admin/user/user_list";
    }

    /**
     * 获取代理商列表
     *
     * @param page
     * @return
     */
    @ResponseBody
    @RequiresRoles("admin")
    @PostMapping("/admin/getUserList")
    public AjaxResult getUserList(Page page, BaseRequest baseRequest) {
        try {
            return userService.getUserList(page, baseRequest);
        } catch (Exception e) {
            log.error("获取代理商列表 方法[{getUserList}] 错误信息", e);
            return AjaxResult.success(null, e.getMessage(), 0);
        }
    }

    /**
     * 跳转到充值页面
     *
     * @param id
     * @param map
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/admin/toChargePage/{id}")
    public String toChargePage(@PathVariable Integer id, ModelMap map) {
        map.put("id", id);
        return "admin/user/user_recharge";
    }

    /**
     * @param id
     * @param map
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/admin/toUserDetailPage/{id}")
    public String toUserDetail(@PathVariable Integer id, ModelMap map) {
        try {
            User user = userService.findUserByUserId(id);
            System.out.println(user.toString());
            if (user != null) map.put("user", user);
            return "admin/user/user_detail";
        } catch (Exception e) {
            log.error("跳转用户详情页 方法：toUserDetail，错误信息", e);
        }
        return null;
    }

    /**
     * 修改用户状态
     *
     * @param id
     * @param userStatus
     * @return
     */
    @RequiresRoles("admin")
    @PutMapping("/admin/updateUserStatus")
    @ResponseBody
    @Log(title = "修改用户状态", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult updateUserStatus(Integer id, Boolean userStatus) {
        User user = new User();
        user.setId(id);
        user.setUserStatus(userStatus);
        boolean b = userService.updateUserById(user);
        return AjaxResult.success(b, "更新成功");
    }

    /**
     * 重置密码
     *
     * @param id
     * @return
     */
    @PutMapping("/admin/updateUserPassword/{id}")
    @ResponseBody
    @Log(title = "重置密码", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult updateUserPassword(@PathVariable Integer id) {
        return userService.updateUserPassword(id);
    }

    /**
     * 号码认证充值
     *
     * @param id
     * @param telcertification
     * @param rechargeRemark
     * @return
     */
    @RequiresRoles("admin")
    @PutMapping("/admin/updateTelcertificationAccount/{id}")
    @ResponseBody
    @Log(title = "充值", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult updateTelcertificationAccount(
            @PathVariable Integer id,
            Float telcertification,
            String rechargeRemark) {
        try {
            return userService.updateTelcertificationAccount(id, telcertification, rechargeRemark);
        } catch (Exception e) {
            log.error("充值，方法：updateTelcertificationAccount，错误信息", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 跳转到用户搜索页面
     *
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/admin/toUserPartnerListPage")
    public String toUserPartnerListPage() {
        return "admin/user/user_partner_list";
    }

    /**
     * 修改用户角色
     *
     * @param id
     * @return
     */
    @PutMapping("/admin/updateUserStatus/{id}")
    @ResponseBody
    @RequiresRoles("admin")
    @Log(title = "修改用户角色", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult updateUserStatus(@PathVariable Integer id) throws Exception {
        return userService.updateUserStatus(id);
    }

    /**
     * 跳转到设置权限页面
     *
     * @param id
     * @param map
     * @return
     */
    @GetMapping("/admin/toSetJurisdictionPage/{id}")
    public String toSetJurisdictionPage(@PathVariable Integer id, ModelMap map) {
        List<Menu> menuList = userService.jurisdictiction(id);
        map.put("menuList", menuList);
        map.put("id", id);
        return "admin/system/jurisdiction/jurisdiction_edit";
    }

    /**
     * 设置用户权限
     *
     * @param userId
     * @param menuArr
     * @return
     */
    @PutMapping("/admin/updateRoleRelation")
    @RequiresRoles("admin")
    @ResponseBody
    @Log(title = "修改用户权限", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.ADMIN)
    public AjaxResult updateRoleRelation(Integer userId, int[] menuArr) {
        return userService.updateRoleRelation(userId, menuArr);
    }


    /**
     * 获取各级代理用户
     *
     * @param
     * @return
     */
    @PostMapping("/admin/findUserPartnerList")
    @RequiresRoles("admin")
    @ResponseBody
    public AjaxResult findUserPartnerList(Page page, String phone) {
        return userService.findUserPartnerList(page, phone);
    }

    /****************************************************** 大大的分界线 *************************************************/
    /**
     * 根据用户名模糊查询
     *
     * @param name
     * @return
     */
    @PostMapping("/admin/findUserLikeName")
    @ResponseBody
    public AjaxResult findUserLikeName(String name) {
        try {
            List<User> list = userService.getUserByName(name);
            return AjaxResult.success(list, "获取成功");
        } catch (Exception e) {
            log.error("根据用户名模糊查询,findUserLikeName", e.getMessage());
        }
        return null;
    }
}
