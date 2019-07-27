package com.hrtxn.ringtone.freemark.config.logConfig;

import com.hrtxn.ringtone.common.utils.ServletUtils;
import com.hrtxn.ringtone.common.utils.ShiroUtils;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.freemark.config.AsyncConfig.AsyncConfig;
import com.hrtxn.ringtone.freemark.enums.BusinessStatus;
import com.hrtxn.ringtone.project.system.log.domain.OperateLog;
import com.hrtxn.ringtone.project.system.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;

/**
 * Author:zcy
 * Date:2019-06-24 11:54
 * Description:日志配置切面
 */
@Slf4j
@Aspect
@Component
public class MyLogAop {

    @Pointcut("@annotation(com.hrtxn.ringtone.freemark.config.logConfig.Log)")
    public void myLogPointCut() {
    }

    /**
     * 前置通知 用于拦截操作
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "myLogPointCut()")
    public void doBefore(JoinPoint joinPoint) {
        handleLog(joinPoint, null);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(value = "myLogPointCut()", throwing = "e")
    public void doAfter(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, e);
    }

    protected void handleLog(final JoinPoint joinPoint, final Exception e) {
        log.info("添加操作日志开始====================");
        try {
            // 获得注解
            Log logAnno = getAnnotationLog(joinPoint);
            if (logAnno == null) {
                return;
            }
            OperateLog operateLog = new OperateLog();
            User currentUser = ShiroUtils.getSysUser();
            if(currentUser != null){
                // 设置操作者
                operateLog.setOperateLogUser(currentUser.getUserName());
            }
            // 设置IP
            String ip = ShiroUtils.getIp();
            if ("0:0:0:0:0:0:0:1".equals(ip)){
                ip = "127.0.0.1";
            }
            operateLog.setIpAddress(ip);
            // 判断是否出现异常
            if (e != null) {
                log.error(e.getMessage());
                // 程序执行中出现异常执行
                operateLog.setOperateLogStatus(BusinessStatus.FAIL.ordinal());
                // 保存异常信息
                operateLog.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
            } else {
                // 程序执行成功时执行
                operateLog.setOperateLogStatus(BusinessStatus.SUCCESS.ordinal());
            }
            String className = joinPoint.getTarget().getClass().getName();// 获取执行方法所在类名
            String methodName = joinPoint.getSignature().getName();// 获取执行方法名
            // 设置方法名称
            operateLog.setOperateLogMethod(className + "." + methodName + "()");
            // 设置操作时间
            operateLog.setOperateLogTime(new Date());
            // 设置操作URL
            operateLog.setOperateLogUrl(ServletUtils.getRequest().getRequestURI());
            // 设置操作日志业务类型
            operateLog.setOperateLogType(logAnno.businessType().ordinal());
            // 设置操作日志操作类型
            operateLog.setOperateLogClassify(logAnno.operatorLogType().ordinal());
            // 设置标题
            operateLog.setOperateLogTitle(logAnno.title());
            // 判断是否需要保存参数和值
            if (logAnno.isSaveRequestData()) {
                // 获取参数的信息，传入到数据库中。
                setRequestValue(joinPoint, operateLog);
            }
            // 保存数据库
            AsyncConfig.ac().operateLogTask(operateLog);
        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("==前置通知异常==");
            log.error("异常信息:{}", exp.getMessage());
            exp.printStackTrace();
        }
        log.info("添加操作日志结束====================");
    }

    /**
     * 获取请求的参数，放到log中
     * @param joinPoint
     * @param operateLog
     */
    private void setRequestValue(JoinPoint joinPoint, OperateLog operateLog) {
        HashMap m = new HashMap();
        // 获取所有的参数名
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        String[] parameterNames = methodSignature.getParameterNames();// 存放所有参数名

        // 获取所有参数对用的数据
        Object[] args = joinPoint.getArgs();
        if (ArrayUtils.isNotEmpty(args)) {
            for (int i = 0; i < args.length; i++) {
                m.put(parameterNames[i], args[i]);
            }
        }
        operateLog.setOperateLogParam(m.toString());
    }

    /**
     * 是否存在注解，如果存在就获取
     */
    private Log getAnnotationLog(JoinPoint joinPoint) throws Exception {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if (method != null) {
            return method.getAnnotation(Log.class);
        }
        return null;
    }
}
