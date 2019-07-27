package com.hrtxn.ringtone.common.exception;

/**
 * Author:zcy
 * Date:2019-07-22 15:47
 * Description:自定义三网未登录异常类
 */
public class NoLoginException extends Exception {
    public NoLoginException(String msg) {
        super(msg);
    }
}
