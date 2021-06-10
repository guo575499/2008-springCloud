package com.fh.shop.api;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.member.vo.MemberVo;
import com.fh.shop.common.SecretLogin;
import jdk.nashorn.internal.objects.NativeNumber;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;

public class BaseController {

    @SneakyThrows
    public MemberVo findMemberVo(HttpServletRequest request){
        String header = URLDecoder.decode(request.getHeader(SecretLogin.MEMVO),"utf-8");
        MemberVo memberVo = JSON.parseObject(header, MemberVo.class);
        return memberVo;
    }

}
