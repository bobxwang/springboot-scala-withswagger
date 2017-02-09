package com.bob.java.webapi.spextension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import static com.bob.java.webapi.spextension.MDCAwareCallable.wrap;

/**
 * Created by bob on 17/2/8.
 */
public class MDCSimpleAsyncTaskExecutor extends SimpleAsyncTaskExecutor {

    private Logger log = LoggerFactory.getLogger(MDCSimpleAsyncTaskExecutor.class);

    @Override
    public void execute(Runnable task, long startTimeout) {

        log.info("MDCSimpleAsyncTaskExecutor begin to execute");
        super.execute(wrap(task, MDC.getCopyOfContextMap()), startTimeout);
        log.info("MDCSimpleAsyncTaskExecutor stop to execute");
    }
}