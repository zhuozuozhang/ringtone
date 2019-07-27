package com.hrtxn.ringtone.project.system.user.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * Author:zcy
 * Date:2019-07-02 9:55
 * Description:<描述>
 */
@Data
public class LoginParam implements Serializable {

    private String username;
    private String password;
    private String captchaCode;
    private boolean rememberMe;
    private String CSRFToken;
}
