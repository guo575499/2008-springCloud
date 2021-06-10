package com.fh.shop.api.member.controller;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.BaseController;
import com.fh.shop.api.member.biz.IMemberService;
import com.fh.shop.api.member.vo.MemberVo;
import com.fh.shop.common.KeyUtil;
import com.fh.shop.common.SecretLogin;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.common.SystemConstant;
import com.fh.shop.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Api(tags= "会员信息")
@Transactional(rollbackFor = Exception.class)
public class MemberController extends BaseController {


    @Resource(name = "memberService")
    private IMemberService service;

    @Autowired
    private HttpServletRequest request;
//    @Autowired
//    private HttpServletResponse response;
//    @Value("${error}")
//    private String error;
//    @Value("${success}")
//    private  String success;

//    @ApiOperation("会员注册")
//    @PostMapping("/member/addMem")
//    public ServerResponse addMem(MemberParam memberParam){
//        return service.addMem(memberParam);
//    }
//
//    @ApiOperation("修改状态")
//    @GetMapping("/member/updateStatus")
//    @ApiImplicitParam(name = "id",value = "uuid",required = true)
//    public void updateStatus(String id) throws IOException {
//        int code=service.updateStatus(id);
//        if(code==SystemConstant.ACTION){
//            //证明不成功
//            response.sendRedirect(error);
//        }else if(code==SystemConstant.InACTION){
//            response.sendRedirect(success);
//        }
//    }
//
//    @PostMapping("/member/sendMail")
//    public ServerResponse sendMail(@RequestBody Map<String,String> map){
//        String id = map.get("id");
//        String email = map.get("email");
//        return service.sendMail(id,email);
//    }


    @ApiOperation("会员登录")
    @PostMapping("/member/login")
    @ApiImplicitParams({
            @ApiImplicitParam(value ="会员名",name = "memberName",dataType = "java.lang.String",required = true),
            @ApiImplicitParam(value ="密码",name = "password",dataType = "java.lang.String",required = true)
    })
    public ServerResponse login(String memberName,String password){
        return service.login(memberName,password);
    }


//    @ApiOperation("判断会员名")
//    @ApiImplicitParam(name = "memberName",value = "会员名",dataType = "java.lang.String",required = true)
//    @PostMapping("/member/ckeckMemberName")
//    public ServerResponse ckeckMemberName(String memberName){
//        return service.ckeckMemberName(memberName);
//    }
//
//
//    @ApiOperation("判断会员手机号")
//    @PostMapping("/member/checkPhone")
//    @ApiImplicitParam(name = "phone",value = "手机号",dataType = "java.lang.String",required = true)
//    public ServerResponse checkPhone(String phone){
//        return service.checkPhone(phone);
//    }
//
//
//
//    @PostMapping("/member/checkEmail")
//    @ApiImplicitParam(name = "email",value = "邮箱",dataType = "java.lang.String",required = true)
//    public ServerResponse checkEmail(String email){
//        return service.checkEmail(email);

//    }



    @GetMapping("/member/getMemVo")
    @SneakyThrows
    @ApiOperation("查询用户信息")
    @ApiImplicitParam(name = "x-auth",value = "请求头",paramType = "header",required = true,dataType = "java.lang.String")
    public ServerResponse getMemVo(){
//        MemberVo memberVo = (MemberVo) request.getAttribute(SecretLogin.MEMVO);
        MemberVo memberVo = findMemberVo(request);
        Long id = memberVo.getId();
        String count = RedisUtil.hget(KeyUtil.buildCartKey(id), SystemConstant.COUNT);
          Map map=  new HashMap();
          map.put("memberVo",memberVo);
          map.put("count",count);
        return ServerResponse.success(map);
    }


    @ApiOperation("注销")
    @GetMapping("/member/loginOut")
    @SneakyThrows
    public ServerResponse loginOut(){
        MemberVo memberVo = findMemberVo(request);
        Long id = memberVo.getId();
        RedisUtil.del(KeyUtil.getksy(id));
        return ServerResponse.success();
    }
//
//    @PostMapping("/member/postCode")
//    public ServerResponse postCode(String memberName,String email){
//        return service.postCode(memberName,email);
//    }
//
//
//    @PostMapping("/member/checkCode")
//    @ApiImplicitParams({
//            @ApiImplicitParam(value ="验证码",name = "code",dataType = "java.lang.String",required = true),
//            @ApiImplicitParam(value ="邮箱",name = "email",dataType = "java.lang.String",required = true),
//            @ApiImplicitParam(value ="key",name = "id",dataType = "java.lang.String",required = true)
//    })
//    public ServerResponse checkCode(String code,String email,String id){
//        return service.checkCode(code,email,id);
//    }
//
//    @GetMapping("/member/checkPassword")
//    @ApiImplicitParam(value ="旧密码",name = "oldpassword",dataType = "java.lang.String",required = true)
//    @Check
//    public ServerResponse checkPassword(String oldpassword){
//        MemberVo memberVo = (MemberVo) request.getAttribute(SecretLogin.MEMVO);
//        String memberName = memberVo.getMemberName();
//        return service.checkPassword(memberName,oldpassword);
//    }
//
//
//    @GetMapping("/member/confrim")
//    @ApiImplicitParams({
//            @ApiImplicitParam(value ="密码",name = "password",dataType = "java.lang.String",required = true),
//            @ApiImplicitParam(value ="确认密码",name = "confrimpassword",dataType = "java.lang.String",required = true),
//    })
//    @Check
//    public ServerResponse confrim(String password,String confrimpassword){
//        MemberVo memberVo = (MemberVo) request.getAttribute(SecretLogin.MEMVO);
//        return service.confrimpassWord(memberVo.getMemberName(),password,confrimpassword);
//    }
//
////    @ApiOperation(value="2", notes="修改密码")
//    @PostMapping("/member/updatePassword")
//    public ServerResponse updatePassword(MemberParam memberParam){
//        return service.updatePassword(memberParam);
//    }


}
