package com.hrtxn.ringtone.project.system.user.service;

import com.hrtxn.ringtone.common.constant.AjaxResult;
import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.common.utils.MD5Utils;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.freemark.config.systemConfig.RingtoneConfig;
import com.hrtxn.ringtone.project.system.rechargelog.domain.RechargeLog;
import com.hrtxn.ringtone.project.system.rechargelog.service.RechargeLogService;
import com.hrtxn.ringtone.project.system.user.domain.Menu;
import com.hrtxn.ringtone.project.system.user.domain.RoleRelation;
import com.hrtxn.ringtone.project.system.user.domain.User;
import com.hrtxn.ringtone.project.system.user.domain.UserVo;
import com.hrtxn.ringtone.project.system.user.mapper.MenuMapper;
import com.hrtxn.ringtone.project.system.user.mapper.RoleRelationMapper;
import com.hrtxn.ringtone.project.system.user.mapper.UserMapper;
import com.hrtxn.ringtone.project.threenets.threenet.utils.ApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author:zcy
 * Date:2019-07-01 15:52
 * Description:<描述>
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RechargeLogService rechargeLogService;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private RoleRelationMapper roleRelationMapper;

    private ApiUtils apiUtils = new ApiUtils();

    /**
     * 根据用户名获取用户信息
     *
     * @param username
     * @return
     */
    public User findUserByUsername(String username) throws Exception {
        return userMapper.findUserByUserName(username);
    }

    /**
     * 修改用户信息
     *
     * @param user
     * @return
     */
    public AjaxResult updateUserById(User user) throws Exception {
        int i = userMapper.updateUserById(user);
        if (i > 0){
            ShiroUtils.getSysUser().setUserTel(user.getUserTel());
            return AjaxResult.success(true,"修改成功！");
        }
        return AjaxResult.error("修改失败！");
    }

    /**
     * 获取用户列表
     *
     * @return
     * @throws Exception
     */
    public AjaxResult getUserList(Page page, BaseRequest baseRequest) throws Exception {
        page.setPage((page.getPage() - 1) * page.getPagesize());
        if (!StringUtils.isNotNull(page)) {
            return AjaxResult.success(null, "参数格式不正确！", 0);
        }
        List<UserVo> userVoList = userMapper.getUserList(page, baseRequest);
        for (int i = 0; i < userVoList.size(); i++) {
            if (userVoList.get(i).getParentId() != 0) {
                User user = userMapper.findUserById(userVoList.get(i).getParentId());
                userVoList.get(i).setParentUserName(user.getUserName());
            }
        }
        int count = userMapper.getUserCountByCon(baseRequest);
        return AjaxResult.success(userVoList, "获取用户成功！", count);
    }

    /**
     * 修改密码
     *
     * @param id
     * @return
     * @throws Exception
     */
    public AjaxResult updateUserPassword(Integer id) {
        if (id != null && id != 0) {
            User user = new User();
            user.setId(id);
            user.setUserPassword(MD5Utils.GetMD5Code("sg123456"));
            int i = userMapper.updateUserById(user);
            if (i > 0) {
                return AjaxResult.success(true, "重置密码成功");
            } else {
                return AjaxResult.error("重置密码失败");
            }
        } else {
            return AjaxResult.error("参数格式不正确");
        }
    }

    /**
     * 根据ID 获取用户信息
     *
     * @param id
     * @return
     * @throws Exception
     */
    public User findUserByUserId(Integer id) throws Exception {
        if (id == null || id == 0) return null;
        return userMapper.findUserById(id);
    }

    /**
     * 充值 添加充值记录
     *
     * @param id
     * @param telcertification
     * @param rechargeRemark
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateTelcertificationAccount(Integer id, Float telcertification, String rechargeRemark) throws Exception {
        // 根据ID获取用户信息
        User user = userMapper.findUserById(id);
        if (user != null) {
            // 执行重置操作
            User _user = new User();
            _user.setId(id);
            _user.setTelcertificationAccount(user.getTelcertificationAccount() + telcertification);
            userMapper.updateUserById(_user);

            // 执行添加重置记录操作
            RechargeLog rechargeLog = new RechargeLog();
            rechargeLog.setRechargeOperator(ShiroUtils.getSysUser().getUserName());
            rechargeLog.setRechargePrice(telcertification);
            rechargeLog.setRechargeMoney(_user.getTelcertificationAccount());
            rechargeLog.setRechargeRemark(rechargeRemark);
            rechargeLog.setRechargeType(1);
            rechargeLog.setRechargeTime(new Date());
            rechargeLog.setUserId(id);
            rechargeLog.setUserName(user.getUserName());
            rechargeLogService.insertRechargeLog(rechargeLog);

            return AjaxResult.success(true, "充值成功");
        }
        return AjaxResult.error("执行出错");
    }

    /**
     * 根据电话号码获取用户信息
     *
     * @param phone
     * @return
     * @throws Exception
     */
    public User findUserByUserTel(String phone) throws Exception {
        return userMapper.findUserByUserTel(phone);
    }

    /**
     * 客户端 个人中心 --》修改密码
     *
     * @param userPassword
     * @param code
     * @param session
     * @return
     */
    public AjaxResult updatePassword(String userPassword, Integer code, HttpSession session) {
        if (!StringUtils.isNotEmpty(userPassword) || code == null) {
            return AjaxResult.error("参数格式不正确！");
        }
        Integer _code = (Integer) session.getAttribute("code");
        Long old = (Long) session.getAttribute("timestamp");
        if (!StringUtils.isNotNull(_code) || !StringUtils.isNotNull(old)) {
            return AjaxResult.error("验证码已失效！");
        }
        long nwe = System.currentTimeMillis();
        Long c = nwe - old;
        // 判断验证码是否失效，默认5分钟
        if (c > 300000) {
            return AjaxResult.error("验证码已失效！");
        }
        User user = new User();
        user.setId(ShiroUtils.getSysUser().getId());
        user.setUserPassword(MD5Utils.GetMD5Code(userPassword));
        int i = userMapper.updateUserById(user);
        if (i > 0) {
            // 清楚session
            session.removeAttribute("code");
            session.removeAttribute("timestamp");
            return AjaxResult.success(true, "修改密码成功！");
        } else {
            return AjaxResult.error("修改密码失败！");
        }
    }

    /**
     * 获取子账号列表
     *
     * @param page
     * @param pagesize
     * @param type
     * @return
     * @throws Exception
     */
    public AjaxResult getChildAccountList(Integer page, Integer pagesize, Integer type, String userName, String userTel) throws Exception {
        if (!StringUtils.isNotNull(page) || !StringUtils.isNotNull(pagesize) || !StringUtils.isNotNull(type)) {
            return AjaxResult.error("参数格式错误");
        }
        List<UserVo> userVoList = null;
        int totalCount = 0;
        page = (page - 1) * pagesize;
        if (type == 1) {// 获取当前登录者下级代理商
            userVoList = userMapper.findUserByparentId(page, pagesize, ShiroUtils.getSysUser().getId(),userName,userTel);
            totalCount = userMapper.getUserCount(ShiroUtils.getSysUser().getId(),userName,userTel);
        } else { // 管理员 获取所有代理商
            totalCount = userMapper.getUserCount(null,userName,userTel);
            userVoList = userMapper.findUserByparentId(page, pagesize, null,userName,userTel);
        }
        return AjaxResult.success(userVoList, "查询成功", totalCount);
    }

    /**
     * 根据父级id获取所有子级用户
     *
     * @return
     */
    public List<User> getChildUserList() throws Exception {
        Page page = new Page(0, 9999);
        Integer id = ShiroUtils.getSysUser().getId();
        List<UserVo> list = userMapper.getUserList(null, null);
        Map<Integer, List<UserVo>> map = list.stream().collect(Collectors.groupingBy(User::getParentId));
        List<User> userList = new ArrayList<>();
        User user = userMapper.findUserById(id);
        userList.add(user);
        recursion(map.get(id),map,userList);
        return userList;
    }

    //递归
    private void recursion(List<UserVo> list,Map<Integer, List<UserVo>> map,List<User> userList) {
        if (list == null || list.isEmpty()){
            return;
        }
        userList.addAll(list);
        for (int i = 0; i < list.size(); i++) {
            UserVo vo = list.get(i);
            List<UserVo> vos = map.get(vo.getId());
            recursion(vos, map,userList);
        }
    }

    /**
     * 根据用户名模糊查询
     *
     * @param name
     * @return
     * @throws Exception
     */
    public List<User> getUserByName(String name) throws Exception {
        return userMapper.findChildUser(null, name);
    }

    /**
     * 添加子账号
     *
     * @param user
     * @return
     */
    public AjaxResult insertUser(User user) throws Exception {
        if (StringUtils.isNotNull(user)
                && StringUtils.isNotEmpty(user.getUserName())
                && StringUtils.isNotEmpty(user.getUserPassword())
                && StringUtils.isNotEmpty(user.getUserTel())) {

            // 判断登录名是否重复
            User userByUserName = userMapper.findUserByUserName(user.getUserName());
            if (StringUtils.isNotNull(userByUserName)){
                return AjaxResult.error("用户名重复！");
            }
            // 判断电话号码是否重复
            User userByUserTel = userMapper.findUserByUserTel(user.getUserTel());
            if (StringUtils.isNotNull(userByUserTel)){
                return AjaxResult.error("电话号码重复!");
            }
            String passwprd = MD5Utils.GetMD5Code(user.getUserPassword());
            user.setUserPassword(passwprd);

            Integer id = ShiroUtils.getSysUser().getId();
            user.setParentId(id);
            user.setUserTime(new Date());
            if (StringUtils.isNotEmpty(user.getUserCardZhen())) {
                user.setIdGroupFrontFile(new File(RingtoneConfig.getProfile() + user.getUserCardZhen()));
            }
            if (StringUtils.isNotEmpty(user.getUserCardFan())) {
                user.setIdGroupReverseFile(new File(RingtoneConfig.getProfile() + user.getUserCardFan()));
            }
            // 执行添加子账号操作
            return apiUtils.insertUser(user);
        }
        return AjaxResult.error("参数格式不正确！");
    }

    /**
     * 获取用户总数
     *
     * @param status
     * @return
     */
    public int getCount(Integer status) {
        BaseRequest request = new BaseRequest();
        request.setUserStatus(status);
        return userMapper.getUserCountByCon(request);
    }

    /**
     * 修改用户角色
     *
     * @param id
     * @return
     * @throws Exception
     */
    public AjaxResult updateUserStatus(Integer id) throws Exception {
        if (StringUtils.isNotNull(id)) {
            // 根据ID查询用户信息
            User user = userMapper.findUserById(id);
            if (StringUtils.isNotNull(user)) {
                if (user.getUserRole() == 1) {
                    user.setUserRole(2);
                } else {
                    user.setUserRole(1);
                }
                // 修改用户角色
                int i = userMapper.updateUserById(user);
                if (i > 0) {
                    return AjaxResult.success(true, "修改成功！");
                }
                return AjaxResult.error("修改用户角色失败！");
            }
            return AjaxResult.error("无用户数据！");
        }
        return AjaxResult.error("参数格式不正确！");
    }

    /**
     * 获取菜单
     *
     * @param id
     * @return
     */
    public List<Menu> jurisdictiction(Integer id) {
        List<Menu> menuList = new ArrayList<>();
        if (StringUtils.isNotNull(id)) {
            // 获取全部的菜单
            menuList = menuMapper.findAllMenu();
            // 获取当前用户拥有的菜单
            List<RoleRelation> roleRelationList = roleRelationMapper.findRoleRelationByUserId(id);
            // 判断是否选中
            for (int i = 0; i < menuList.size(); i++) {
                for (int j = 0; j < roleRelationList.size(); j++) {
                    if (menuList.get(i).getId() == roleRelationList.get(j).getMenuId()) {
                        menuList.get(i).setCheck(true);
                    }
                }
            }
        }
        return menuList;
    }

    /**
     *  设置用户权限
     *
     * @param userId
     * @param menuArr
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateRoleRelation(Integer userId, int[] menuArr) {
        if (StringUtils.isNotNull(userId)) {
            // 删除ID对应的菜单
            roleRelationMapper.deleteRoleRelationByUserId(userId);
            // 执行批量添加菜单操作
            if (StringUtils.isNotNull(menuArr)) {
                int i = roleRelationMapper.insertRoleRelation(userId, menuArr);
                if (i > 0) {
                    return AjaxResult.success(true, "设置成功!");
                }
            } else {
                return AjaxResult.success(true, "设置成功!");
            }
        }
        return AjaxResult.error("参数格式不正确！");
    }

    /**
     * 根据电话号码获取所有下级代理商
     *
     * @param userTel
     * @return
     * @throws Exception
     */
    public AjaxResult getLowUser(String userTel) throws Exception {
        User user;
        List<UserVo> userVoList;
        // 判断号码是否为空
        if (StringUtils.isNotEmpty(userTel)){
            // 不为空则获取该电话号码下的子级代理商
            user = userMapper.findUserByUserTel(userTel);
        }else{
            // 为空则获取当前登录者下的子级代理商
            user = ShiroUtils.getSysUser();
        }
        if (StringUtils.isNotNull(user)){
            userVoList = userMapper.findUserByparentId(null, null, user.getId(),null,null);
            List<UserVo> users = getLowNode(userVoList);
            return AjaxResult.success(users,"获取数据成功！");
        }
        return AjaxResult.error("无数据！");
    }

    public AjaxResult getUpUser(String userTel) throws Exception {
        User user;
        List<UserVo> userVoList = new ArrayList<>();
        // 判断电话号码是否为空
        if (StringUtils.isNotEmpty(userTel)){
            // 不为空则获取该电话号码下的上级代理商
            user = userMapper.findUserByUserTel(userTel);
        } else {
            // 为空则获取当前登录者下的上级代理商
            user = ShiroUtils.getSysUser();
        }
        if (StringUtils.isNotNull(user)){
            userVoList = getUpNode(userVoList,user.getParentId());
            return AjaxResult.success(userVoList,"获取数据成功！");
        }
        return AjaxResult.error("无数据！");
    }

    /**
     * 递归查询当前登录者下的所有子账号，不分等级
     *
     * @param users
     * @return
     * @throws Exception
     */
    public List<UserVo> getLowNode(List<UserVo> users) throws Exception {
        if(users !=null && users.size()!=0){
            for(int i=0;i<users.size();i++){
                List<UserVo> list = userMapper.findUserByparentId(null, null,users.get(i).getId(),null,null);
                if (list != null && list.size() != 0) {
                    for (UserVo user : list) {
                        users.add(user);
                    }
                    getLowNode(list);
                }
            }
        }
        return users;
    }

    public List<UserVo> getUpNode(List<UserVo> userVoList,Integer parentId) throws Exception {
//        List<UserVo> userVoList = new ArrayList<>();
        if (parentId != 0){
            User _user = userMapper.findUserById(parentId);
            if (StringUtils.isNotNull(_user)){
                UserVo userVo = new UserVo();
                userVo.setUserTel(_user.getUserTel());
                userVo.setParentUserName("顶级");
                userVo.setUserName(_user.getUserName());
                userVo.setUserStatus(_user.getUserStatus());
                userVo.setUserTime(_user.getUserTime());
                if (_user.getParentId() != 0){
                    User user = userMapper.findUserById(_user.getParentId());
                    if (StringUtils.isNotNull(user)){
                        userVo.setParentUserName(user.getUserName());
                    }
                }
                userVoList.add(userVo);
            }
            getUpNode(userVoList,_user.getParentId());
        }
        return userVoList;
    }

}
