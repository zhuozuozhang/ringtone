package com.hrtxn.ringtone.freemark.config.logConfig;

import com.hrtxn.ringtone.freemark.enums.BusinessType;
import com.hrtxn.ringtone.freemark.enums.OperatorLogType;

import java.lang.annotation.*;

/**
 * Author:zcy
 * Date:2019-06-24 11:49
 * Description:操作记录
 */
@Target({ElementType.PARAMETER,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 操作日志标题
     * @return
     */
    public String title() default "";

    /**
     * 业务类型
     * @return
     */
    public BusinessType businessType() default BusinessType.OTHER;

    /**
     * 操作类型
     * @return
     */
    public OperatorLogType operatorLogType() default OperatorLogType.OTHER;

    /**
     * 是否保存请求的参数
     */
    public boolean isSaveRequestData() default true;

}
