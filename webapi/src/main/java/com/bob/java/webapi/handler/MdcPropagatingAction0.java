package com.bob.java.webapi.handler;

import org.slf4j.MDC;
import rx.functions.Action0;

import java.util.Map;

/**
 * 重写Action0,使其将当前线程的MDC值传递到下个线程中
 * <p>
 * Created by bob on 17/2/9.
 */
public class MdcPropagatingAction0 implements Action0 {

    private final Action0 action0;
    private final Map<String, String> context;

    public MdcPropagatingAction0(final Action0 action0) {
        this.action0 = action0;
        this.context = MDC.getCopyOfContextMap();
    }

    @Override
    public void call() {
        final Map<String, String> originalMdc = MDC.getCopyOfContextMap();
        if (context != null) {
            MDC.setContextMap(context);
        }
        try {
            this.action0.call();
        } finally {
            if (originalMdc != null) {
                MDC.setContextMap(originalMdc);
            }
        }
    }
}