package com.bob.java.webapi.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by bob on 17/1/4.
 */
public class ObservableReturnValueHandler implements AsyncHandlerMethodReturnValueHandler {

    private static final Logger log = LoggerFactory.getLogger(ObservableReturnValueHandler.class);

    @Override
    public boolean isAsyncReturnValue(Object returnValue, MethodParameter returnType) {
        return returnValue != null && supportsReturnType(returnType);
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return Observable.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }

        final Observable<?> observable = Observable.class.cast(returnValue);
        log.debug("handleReturnValue begin to process");
        WebAsyncUtils.getAsyncManager(webRequest)
                .startDeferredResultProcessing(new ObservableAdapter<>(observable), mavContainer);
        log.debug("handleReturnValue stop to process");
    }

    public class ObservableAdapter<T> extends DeferredResult<T> {
        public ObservableAdapter(Observable<T> observable) {
            log.debug("observableAdapter begin to process");
            observable.subscribeOn(Schedulers.newThread())
                    .subscribe(
                            t -> {
                                setResult(t);
                                log.info("observableAdapter set the result value to DeferredResult");
                            },
                            throwable -> setErrorResult(throwable)
                    );
            log.debug("observableAdapter stop to process");
        }
    }
}