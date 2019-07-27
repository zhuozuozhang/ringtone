package com.hrtxn.ringtone.freemark.config.shiroConfig;

import com.hrtxn.ringtone.project.system.user.domain.User;
import com.hrtxn.ringtone.project.system.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Author:zcy
 * Date:2019-06-26 17:52
 * Description:shiro实现认证、授权处理
 */
@Slf4j
public class MyShiroRealm extends AuthorizingRealm {

    private UserMapper userMapper;

    @Autowired
    private void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        log.info("shiro 开始授权--时间："+new Date());
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        User user = (User)principalCollection.getPrimaryPrincipal();
        if (user != null){
            if (user.getUserRole() == 1){
                info.addRole("admin");
                info.addRole("threenets");
            }else{
                info.addRole("agent");
            }
        }
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        log.info("shiro 登录认证--时间："+new Date());
        UsernamePasswordToken token = (UsernamePasswordToken)authenticationToken;
        User user = null;
        try {
            user = userMapper.findUserByUserName(token.getUsername());
            // 状态
            if (!user.getUserStatus()){
                return null;
            }
        } catch (Exception e) {
            log.error("shiro 登录认证,方法：【{}】,错误信息：【{}】","doGetAuthenticationInfo",e);
        }
        if (user == null){
            // 账号不正确
            return null;
        }
        return new SimpleAuthenticationInfo(user,user.getUserPassword(),getName());// 验证密码
    }
}
