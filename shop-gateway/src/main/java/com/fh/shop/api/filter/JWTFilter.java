package com.fh.shop.api.filter;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.vo.MemberVo;
import com.fh.shop.common.*;
import com.fh.shop.util.Md5Util;
import com.fh.shop.util.RedisUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.FaultAction;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.List;

@Component
@Slf4j
public class JWTFilter extends ZuulFilter {

    @Value("${fh.shop.checkUrls}")
    private List<String> checkUrls;

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

    @SneakyThrows
    @Override
    public Object run() throws ZuulException {
        log.info("==============================={}",checkUrls);
        //获取request
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        //获取url
        StringBuffer requestURL = request.getRequestURL();
        boolean isCheck = false;
        for(String checkUrl : checkUrls){
            //index是冲头开始获取，判断获取的长度是否大于0，如果大于零那么就说明获取到，直接跳出即可
            if(requestURL.indexOf(checkUrl) > 0){
                isCheck = true;
                break;
            }
        }
        if(!isCheck){
            //return null相当于放行
            return null;
        }
        //获取头信息
        String header = request.getHeader("x-auth");
        //判断有没有头信息
        if(header==null){
            return buildResponse(ResponseEnum.TOKEN_HEADER_IS_NULL);

        }

        //判断头信息是否规范
        String[] headerArr = header.split("\\.");
        if(headerArr.length!=2){
            return buildResponse(ResponseEnum.TOKEN_HEADER_IS_FULL);
        }
        //然后就可以进行验签
        //获取签名
        String memberVo64 = headerArr[0];
        String sout64 = headerArr[1];
        String member = new String(Base64.getDecoder().decode(memberVo64),"utf-8");
        String sout = new String(Base64.getDecoder().decode(sout64),"utf-8");
        if(!Md5Util.sout(member,SecretLogin.SECRE).equals(sout)){
            return buildResponse(ResponseEnum.TOKEN_HEADER_ERROR);
        }
        MemberVo membervo = JSON.parseObject(member, MemberVo.class);
        request.setAttribute(SecretLogin.MEMVO,membervo);

        Long id = membervo.getId();
        Boolean ex = RedisUtil.ex(KeyUtil.getksy(id));
        //判断是否过期
        if(!ex){
            return buildResponse(ResponseEnum.TOKEN_IS_NULL);
        }
        //续命
        RedisUtil.er(KeyUtil.getksy(id),SecretLogin.SECODE);
        //将member存入到request中
        //将要传给后台微服务的信息放到请求头上
        currentContext.addZuulRequestHeader(SecretLogin.MEMVO,URLEncoder.encode(member,"utf-8"));


        return null;
    }

    private Object buildResponse(ResponseEnum responseEnum) {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletResponse response = currentContext.getResponse();
        //起到拦截的作用,不会在执行转发的作用
        currentContext.setSendZuulResponse(false);
        response.setContentType("application/json;charset=utf-8");
        //提示JSON的信息
        ServerResponse error = ServerResponse.error(ResponseEnum.TOKEN_HEADER_IS_NULL);
        String res = JSON.toJSONString(error);
        currentContext.setResponseBody(res);
        return null;
    }
}
