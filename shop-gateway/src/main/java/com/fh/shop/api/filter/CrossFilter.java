package com.fh.shop.api.filter;

import com.alibaba.fastjson.JSON;
import com.fh.shop.common.KeyUtil;
import com.fh.shop.common.ResponseEnum;
import com.fh.shop.common.SecretLogin;
import com.fh.shop.util.Md5Util;
import com.fh.shop.util.RedisUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Base64;

@Component
public class CrossFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletResponse response = currentContext.getResponse();
        HttpServletRequest request = currentContext.getRequest();
        //解决跨域
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");

        //解决自定义handlers
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "x-auth,content-type,fh-token");

        //处理特殊请求
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "DELETE,POST,PUT,GET");

        //解决 options 请求
        String methodHTTP = request.getMethod();
        if (methodHTTP.equalsIgnoreCase("OPTIONS")) {
            // 禁止路由 不会继续向微服务发送请求
            currentContext.setSendZuulResponse(false);
            return null;
        }

        return null;
    }
}
