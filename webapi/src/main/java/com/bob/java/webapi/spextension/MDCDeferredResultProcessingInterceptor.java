package com.bob.java.webapi.spextension;

import com.bob.java.webapi.constant.MdcConstans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptorAdapter;

import java.util.Map;

/**
 * 想将当前线程MDC值进递,在DeferredResutl中不能像Callable一样,添加拦截器几乎不起作用,几乎可以认定此类是个无用类
 * 如果想传递可以参照CallableController类那样将callable进行转换,或者利用SimpleAsyncTaskExecutor的execute方案来做
 * <p>
 * Created by bob on 17/2/9.
 */
public class MDCDeferredResultProcessingInterceptor extends DeferredResultProcessingInterceptorAdapter {

    private static final Logger log = LoggerFactory.getLogger(MDCDeferredResultProcessingInterceptor.class);

    @Override
    public <T> void beforeConcurrentHandling(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
        log.info("beforeConcurrentHandling client id -> " + MDC.get(MdcConstans.MDC_ClientRequest_ID));
        log.info("beforeConcurrentHandling remote ip -> " + MDC.get(MdcConstans.MDC_REMOTE_IP));
        request.setAttribute("deferredresultmdcmap", MDC.getCopyOfContextMap(), -1);
        super.beforeConcurrentHandling(request, deferredResult);
    }

    @Override
    public <T> void preProcess(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
        // 经测试此线程跟beforeConcurrentHandling是同一个
        log.info("preProcess client id -> " + MDC.get(MdcConstans.MDC_ClientRequest_ID));
        log.info("preProcess remote ip -> " + MDC.get(MdcConstans.MDC_REMOTE_IP));
        super.preProcess(request, deferredResult);
    }

    @Override
    public <T> void postProcess(NativeWebRequest request, DeferredResult<T> deferredResult, Object concurrentResult) throws Exception {
        // 这是跟beforeConcurrentHandling不同的线程,并且已经可以知道deferredResult的result即入参值concurrentResult
        Map map = (Map) request.getAttribute("deferredresultmdcmap", -1);
        log.info("postProcess client id -> " + map.get(MdcConstans.MDC_ClientRequest_ID));
        log.info("postProcess remote ip -> " + map.get(MdcConstans.MDC_REMOTE_IP));
        MDC.setContextMap(map);
        super.postProcess(request, deferredResult, concurrentResult);
    }

    @Override
    public <T> void afterCompletion(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
        // 这又是另一个线程,所以这里相当于有三个线程
        log.info("afterCompletion client id -> " + MDC.get(MdcConstans.MDC_ClientRequest_ID));
        log.info("afterCompletion remote ip -> " + MDC.get(MdcConstans.MDC_REMOTE_IP));
        super.afterCompletion(request, deferredResult);
    }
}