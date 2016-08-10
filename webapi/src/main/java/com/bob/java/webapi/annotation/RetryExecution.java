package com.bob.java.webapi.annotation;

import java.lang.annotation.*;

/**
 * Created by bob on 16/8/10.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface RetryExecution {

    /**
     * 重试次数, 默认3次
     *
     * @return
     */
    int retryAttempts() default 3;

    /**
     * 重试间隔时间, 默认0秒
     *
     * @return
     */
    long sleepInterval() default 0L;
}