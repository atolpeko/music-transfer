package com.mf.auth.boot.config.web;

import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Log4j2
@Component
public class InRequestLoggingInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<Long> startTimeHolder = new ThreadLocal<>();

    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler
    ) {
        startTimeHolder.set(System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler,
        ModelAndView modelAndView
    ) {
        var elapsedTime = System.currentTimeMillis() - startTimeHolder.get();
        var url = request.getRequestURL();
        log.debug("{} request for {} took {} ms", request.getMethod(),  url, elapsedTime);

        startTimeHolder.remove();
    }
}
