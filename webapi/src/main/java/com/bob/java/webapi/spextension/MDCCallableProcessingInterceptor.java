package com.bob.java.webapi.spextension;

import com.bob.java.webapi.constant.MdcConstans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptorAdapter;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 直接返回callable跟webasynctask都会调用此类,在before**中将当前线程的MDC值设置到request中,这样可以在pre**中得到
 * 添加拦截器是一种方案,但也可以自定义一个SimpleAsyncTaskExecutor,在那重写execute方法将MDC值进行传递,并成为IOC的bean,参见WebConfig类
 * Created by bob on 17/2/8.
 */
public class MDCCallableProcessingInterceptor extends CallableProcessingInterceptorAdapter {

    private static final Logger log = LoggerFactory.getLogger(MDCCallableProcessingInterceptor.class);

    @Override
    public <T> void beforeConcurrentHandling(NativeWebRequest request, Callable<T> task) throws Exception {
        // 经测试,是Tomcat的请求线程
        log.info("client id -> " + MDC.get(MdcConstans.MDC_ClientRequest_ID));
        log.info("remote ip -> " + MDC.get(MdcConstans.MDC_REMOTE_IP));
        request.setAttribute("mdcmap", MDC.getCopyOfContextMap(), -1);
        super.beforeConcurrentHandling(request, task);
    }

    @Override
    public <T> void preProcess(NativeWebRequest request, Callable<T> task) throws Exception {
        // 经测试,在这已经是工作线程,Tomcat的请求线程已经释放,在这直接拿MDC值的话为空
        Map map = (Map<String,String>) request.getAttribute("mdcmap", -1);
        log.info("preProcess client id -> " + map.get(MdcConstans.MDC_ClientRequest_ID));
        log.info("preProcess remote ip -> " + map.get(MdcConstans.MDC_REMOTE_IP));
        MDC.setContextMap(map);
        super.preProcess(request, task);
    }
}