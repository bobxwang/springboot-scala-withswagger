package com.bob.java.webapi.filter;

import com.bob.java.webapi.constant.AppConstants;
import com.bob.java.webapi.constant.MdcConstans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by bob on 17/2/6.
 */
public class MDCFilter extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(MDCFilter.class);

    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        handlerRemoteIp(request);
        handlerClientRequestId(request);

        try {
            log.info(urlPathHelper.getPathWithinApplication(request) + " -> 开始客户端请求ip -> " + MDC.get(MdcConstans.MDC_REMOTE_IP) + " 标识符是 -> " + MDC.get(MdcConstans.MDC_ClientRequest_ID));
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
            log.info(urlPathHelper.getPathWithinApplication(request) + " -> 结束客户端请求ip -> " + MDC.get(MdcConstans.MDC_REMOTE_IP) + " 标识符是 -> " + MDC.get(MdcConstans.MDC_ClientRequest_ID));
        }
    }

    private void handlerClientRequestId(HttpServletRequest request) {
        String clientRequestId = request.getHeader(AppConstants.X_Client_Request_Id);
        if (StringUtils.isEmpty(clientRequestId)) {
            clientRequestId = UUID.randomUUID().toString();
            log.debug("无法从http头获得请求标识, 自己生成一个: " + clientRequestId);
        }
        MDC.put(MdcConstans.MDC_ClientRequest_ID, clientRequestId);
    }

    private void handlerRemoteIp(HttpServletRequest request) {
        String remoteIp = request.getHeader(AppConstants.X_FORWARDED_FOR_HEADER);
        if (StringUtils.isEmpty(remoteIp)) {
            remoteIp = request.getHeader(AppConstants.X_REMOTE_IP_HEADER);
            if (StringUtils.isEmpty(remoteIp)) {
                remoteIp = request.getRemoteAddr();
                if (StringUtils.isEmpty(remoteIp)) {
                    log.debug("无法从http头获得remote_ip, 使用默认值127.0.0.1.");
                    MDC.put(MdcConstans.MDC_REMOTE_IP, "127.0.0.1");
                    return;
                }
            }
        }

        if (remoteIp.contains(",")) {
            remoteIp = StringUtils.split(remoteIp, ",")[0].trim();
        }
        MDC.put(MdcConstans.MDC_REMOTE_IP, remoteIp);
    }
}