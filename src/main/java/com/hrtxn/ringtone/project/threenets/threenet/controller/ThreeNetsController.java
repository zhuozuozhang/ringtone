package com.hrtxn.ringtone.project.threenets.threenet.controller;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.exception.NoLoginException;
import com.hrtxn.ringtone.common.utils.MD5Utils;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.freemark.config.logConfig.Log;
import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;
import com.hrtxn.ringtone.project.system.notice.domain.Notice;
import com.hrtxn.ringtone.project.system.notice.service.NoticeService;
import com.hrtxn.ringtone.project.system.user.domain.User;
import com.hrtxn.ringtone.project.system.user.service.UserService;
import com.hrtxn.ringtone.project.threenets.threenet.domain.PlotBarPhone;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreeNetsOrderAttached;
import com.hrtxn.ringtone.project.threenets.threenet.domain.ThreenetsChildOrder;
import com.hrtxn.ringtone.project.threenets.threenet.service.ThreeNetsChildOrderService;
import com.hrtxn.ringtone.project.threenets.threenet.service.ThreeNetsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Author:lile
 * Date:2019-07-11 10:26
 * Description:三网控制器
 */
@Slf4j
@Controller
public class ThreeNetsController {

    private final String MODUL_THREENETS = "0";

    @Autowired
    private ThreeNetsService threeNetsService;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private UserService userService;
    @Autowired
    private ThreeNetsChildOrderService threeNetsChildOrderService;// 子订单

    /**
     * 跳转到代办
     *
     * @param map
     * @return
     */
    @GetMapping("/threenets/threeNetsTaskList")
    public String toThreeNetsTaskList(ModelMap map) {
        try {
            // 获取公告列表
            List<Notice> noticeList = noticeService.findNoticeListByModul(MODUL_THREENETS);
            map.put("noticeList", noticeList);
            // 获取近5日信息
            List<PlotBarPhone> plotBarPhoneList = threeNetsChildOrderService.getFiveData();
            map.put("plotBarPhoneList", plotBarPhoneList);
        } catch (Exception e) {
            log.error("获取待办任务列表,方法：toThreeNetsTaskList,错误信息", e);
        }
        return "threenets/threenet/index/index";
    }

    /**
     * 获取三网公告列表
     *
     * @return
     */
    @GetMapping("/threenets/toAnnouncementPage")
    public String toAnnouncementPage(ModelMap map) {
        try {
            List<Notice> noticeList = noticeService.findNoticeListByModul(MODUL_THREENETS);
            map.put("noticeList", noticeList);
        } catch (Exception e) {
            log.error("获取三网公告列表,方法：getThreeNetsAnnunciate,错误信息", e);
        }
        return "threenets/threenet/announcement/announcement";
    }

    /**
     * 跳转到添加商户
     *
     * @return
     */
    @GetMapping("/threenets/toAddMerchantsPage")
    public String toAddMerchantsPage() {
        return "threenets/threenet/merchants/Addmerchants";
    }

    /**
     * 添加商户号码
     *
     * @return
     */
    @GetMapping("/threenets/toAddMerchantsPhonePage/{orderId}")
    public String toAddMerchantsPhonePage(ModelMap map,@PathVariable("orderId")Integer orderId) {
        ThreeNetsOrderAttached attached = threeNetsService.getOrderAttached(orderId);
        map.put("miguId",attached.getMiguId());
        map.put("swxlId",attached.getSwxlId());
        map.put("orderId",orderId);
        return "threenets/threenet/merchants/Addnumber";
    }

    /**
     * 跳转到号码列表
     *
     * @return
     */
    @GetMapping("/threenets/toNumberListPage")
    public String toNumberListPage(ModelMap map) {
        try {
            ThreenetsChildOrder childOrder = new ThreenetsChildOrder();
            childOrder.setIsMonthly(1);
            //List<ThreenetsChildOrder> orderList = threeNetsService.getThreeNetsTaskList(childOrder,"4");
            map.put("threenets", null);
        } catch (Exception e) {
            log.error("跳转到号码列表,方法：toNumberListPage,错误信息", e);
        }
        return "threenets/threenet/number_list/number_list";
    }

    /**
     * 跳转到业务发展数据
     *
     * @return
     */
    @GetMapping("/threenets/toDbusinessPage")
    public String toDbusinessPage(ModelMap map) {
        try {
            List<User> userList = userService.getChildUserList();
            map.put("userList", userList);
        } catch (Exception e) {
            log.error("跳转到业务发展数据 方法：toDbusinessPage 错误信息", e);
        }
        return "threenets/threenet/business/dbusiness";
    }

    /**
     * 获取月统计
     *
     * @param request
     * @return
     */
    @PostMapping("/threenets/getDbusinessDate")
    @ResponseBody
    public AjaxResult getDbusinessDate(BaseRequest request) {
        try {
            return threeNetsService.getMonthData(request);
        } catch (Exception e) {
            log.error("获取子账号列表 方法：getDbusinessDate 错误信息", e);
        }
        return null;
    }

    /**
     * 业务发展数据每月统计
     *
     * @return
     */
    @GetMapping("/threenets/toMbusinessPage")
    public String toMbusinessPage(ModelMap map) {
        try {
            List<User> userList = userService.getChildUserList();
            map.put("userList", userList);
        } catch (Exception e) {
            log.error("跳转到业务发展数据 方法：toMbusinessPage 错误信息", e);
        }
        return "threenets/threenet/business/mbusiness";
    }

    /**
     * 获取年统计
     *
     * @param request
     * @return
     */
    @PostMapping("/threenets/getMbusinessDate")
    @ResponseBody
    public AjaxResult getMbusinessDate(BaseRequest request) {
        try {
            return threeNetsService.getYearData(request);
        } catch (Exception e) {
            log.error("获取子账号列表 方法：getMbusinessDate 错误信息", e);
        }
        return null;
    }

    /**
     * 跳转到个人信息页面
     *
     * @param map
     * @return
     */
    @GetMapping("/threenets/toPersonalInformation")
    public String toPersonalInformation(ModelMap map) {
        try {
            User user = ShiroUtils.getSysUser();
            User user1 = userService.findUserByUserId(user.getId());
            if (user1 != null) {
                map.put("user", user1);
            }
        } catch (Exception e) {
            log.error("跳转到个人信息页面 方法：toPersonalInformation 错误信息", e);
        }

        return "threenets/threenet/set/set";
    }

    /**
     * 三网客户端个人设置修改
     *
     * @param user
     * @return
     */
    @PutMapping("/threenets/personalInformation")
    @ResponseBody
    @Log(title = "个人信息-->个人设置", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult personalInformation(User user) {
        user.setId(ShiroUtils.getSysUser().getId());
        boolean b = userService.updateUserById(user);
        if (b) {
            return AjaxResult.success(true, "修改成功");
        }
        return null;
    }

    /**
     * 跳转到修改密码
     *
     * @return
     */
    @GetMapping("/threenets/toUpdatePasswor")
    public String toUpdatePasswor() {
        return "threenets/threenet/set/change_pass";
    }

    /**
     * 个人信息-->修改密码
     *
     * @return
     */
    @ResponseBody
    @PutMapping("/threenets/updatePasswor")
    @Log(title = "个人信息-->修改密码", businessType = BusinessType.UPDATE, operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult updatePasswor(String oldPassword, String newPassword) {
        try {
            User _user = userService.findUserByUserId(ShiroUtils.getSysUser().getId());
            if (_user != null) {
                if (!StringUtils.equals(MD5Utils.GetMD5Code(oldPassword), _user.getUserPassword())) {
                    return AjaxResult.error(1001, "原密码不正确");
                } else {
                    _user.setUserPassword(MD5Utils.GetMD5Code(newPassword));
                }
            }
            boolean b = userService.updateUserById(_user);
            if (b) {
                return AjaxResult.success(true, "修改密码成功");
            }
        } catch (Exception e) {
            log.error("个人信息-->修改密码 方法：updatePasswor 错误信息", e);
        }
        return null;
    }

    /**
     * 获取三网公告列表
     *
     * @param map
     * @return
     */
    @GetMapping("/threenets/threeNetsAnnunciate")
    public String getThreeNetsAnnunciate(ModelMap map) {
        try {
            List<Notice> noticeList = noticeService.findNoticeListByModul(MODUL_THREENETS);
            Notice notice = noticeList.size() > 0 ? noticeList.get(0) : new Notice();
            map.put("noticeList", noticeList);
            map.put("notice", notice);
        } catch (Exception e) {
            log.error("获取三网公告列表,方法：getThreeNetsAnnunciate,错误信息", e);
        }
        return "threenets/threenet/index/announcement";
    }

    /**
     * 移动工具箱-删除铃音
     *
     * @return
     */
    @GetMapping("/threenets/toYdDeleteRingPage")
    public String toYdDeleteRingPage() {
        return "threenets/threenet/public/delringtone";
    }

    /**
     * 跳转到子账号列表
     *
     * @param msg
     * @param map
     * @return
     */
    @GetMapping("/threenets/toChildAccountPage")
    public String toChildAccountPage(String msg, ModelMap map) {
        log.info("msg------------------>" + msg);
        map.put("msg", msg);
        return "threenets/threenet/childaccount/childaccount";
    }

    /**
     * 获取子账号列表
     *
     * @return
     */
    @PostMapping("/threenets/getChildAccountList")
    @ResponseBody
    public AjaxResult getChildAccountList(Integer page, Integer pagesize, Integer type) {
        try {
            return userService.getChildAccountList(page, pagesize, type);
        } catch (Exception e) {
            log.error("获取子账号列表 方法：getChildAccountList 错误信息", e);
        }
        return null;
    }

    /**
     * 跳转到子账号添加页面
     *
     * @return
     */
    @GetMapping("/threenets/toChildAccountAddPage")
    public String toChildAccountAddPage() {
        return "threenets/threenet/childaccount/add";
    }

    /**
     * 进入子账号
     *
     * @param id
     * @param session
     * @return
     */
    @GetMapping("/threenets/enterSubUser/{id}")
    public String enterSubUser(@PathVariable Integer id, HttpSession session, Model mdoel) {
        try {
            AjaxResult result = threeNetsService.enterSubUser(id, session);
            Boolean b = (Boolean) result.get("data");
            if (b) {
                return "redirect:/threenets/threeNetsTaskList";
            } else {
                mdoel.addAttribute("msg", result.get("msg"));
            }
        } catch (Exception e) {
            log.error("进入子账号 方法：enterSubUser 错误信息", e);
            mdoel.addAttribute("msg", "执行出错");
        }
        return "redirect:/threenets/toChildAccountPage";
    }

    /**
     * 添加子账号
     *
     * @param user
     * @return
     */
    @PostMapping("/threenets/insertUser")
    @ResponseBody
    @Log(title = "添加子账号",businessType = BusinessType.INSERT,operatorLogType = OperatorLogType.THREENETS)
    public AjaxResult insertUser(User user) throws NoLoginException, IOException {
        return userService.insertUser(user);
    }

    /**
     * 验证手机运营商
     *
     * @param phone
     * @return
     */
    @PostMapping("/threenets/getOperate")
    @ResponseBody
    public AjaxResult matchingOperate(String phone) {
        try {
            return threeNetsService.matchingOperate(phone);
        } catch (Exception e) {
            return AjaxResult.error("验证手机运营商失败！");
        }
    }

    /**
     * 移动工具箱-用户信息
     *
     * @return
     */
    @GetMapping("/threenets/toYdUserInfoPage")
    public String toYdUserInfoPage() {
        return "threenets/threenet/public/moblieuser";
    }

    /**
     * 联通工具箱-用户信息
     *
     * @return
     */
    @GetMapping("/threenets/toLtUserInfoPage")
    public String toLtUserInfoPage() {
        return "threenets/threenet/public/unicomuser";
    }
}
