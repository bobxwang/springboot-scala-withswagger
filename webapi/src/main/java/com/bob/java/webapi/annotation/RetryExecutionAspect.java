package com.bob.java.webapi.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Created by bob on 16/8/10.
 */
@Aspect
@Component
public class RetryExecutionAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger("RetryLogger");

    @Around("@annotation(com.bob.java.webapi.annotation.RetryExecution)")
    public Object doRetry(ProceedingJoinPoint point) throws Throwable {

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        /*判断方法所属的类是否为接口*/
        if (method.getDeclaringClass().isInterface()) {
            try {
                //获取实现方法
                method = point.getTarget().getClass()
                        .getDeclaredMethod(point.getSignature().getName(), method.getParameterTypes());
            } catch (final SecurityException exception) {
                LOGGER.error("方法重试失败, 该方法无法访问, class=" + method.getClass(), exception);
                throw exception;
            } catch (final NoSuchMethodException exception) {
                LOGGER.error("方法重试失败, 该方法不存在, class=" + method.getClass(), exception);
                throw exception;
            }
        }

        RetryExecution retryExecution = method.getAnnotation(RetryExecution.class);
        int retryAttempts = retryExecution.retryAttempts();
        long sleepInterval = retryExecution.sleepInterval();
        int allRetryAttempts = retryExecution.retryAttempts();
        while (--retryAttempts >= 0) {
            try {
                if (retryAttempts < allRetryAttempts - 1) {
                    LOGGER.info("重试Method: " + signature.toString());
                }
                return point.proceed();
            } catch (Throwable throwable) {
                LOGGER.error("重试操作出错, class=" + throwable.getClass());
                LOGGER.debug("——————————方法重试操作---------");
                LOGGER.debug("Method: " + signature.toString());
                /*重试间隔时间*/
                try {
                    Thread.sleep(sleepInterval);
                } catch (InterruptedException e) {
                }
            }
        }
        return point.proceed();
    }
}