package com.fh.shop.api.member.biz;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fh.shop.api.member.mapper.IMemberMapper;
import com.fh.shop.api.member.po.Member;
import com.fh.shop.api.member.vo.MemberVo;
import com.fh.shop.common.KeyUtil;
import com.fh.shop.common.ResponseEnum;
import com.fh.shop.common.SecretLogin;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.util.Md5Util;
import com.fh.shop.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service("memberService")
public class IMemberServiceImpl implements IMemberService {
    @Autowired
    private IMemberMapper memberMapper;

    //生成随机数4位数
    private static final  String codeMail = UUID.randomUUID().toString();

//    @Autowired
//    private MailUtil mailUtil;
//
//    @Autowired
//    private MailProducer mailProducer;

//    @Override
//    public ServerResponse addMem(MemberParam memberParam) {
//        String memberName = memberParam.getMemberName();
//        String nickName = memberParam.getNickName();
//        String password = memberParam.getPassword();
//        String confirmPassword = memberParam.getConfirmPassword();
//        String phone = memberParam.getPhone();
//        String email = memberParam.getEmail();
//        String code = memberParam.getCode();
//        //先判断对象有没有值
//        if(StringUtils.isEmpty(memberName) || StringUtils.isEmpty(nickName)
//                                           || StringUtils.isEmpty(confirmPassword)
//                                           || StringUtils.isEmpty(email)
//                                           ||    StringUtils.isEmpty(code)
//                                           ||    StringUtils.isEmpty(password)
//                                           ||    StringUtils.isEmpty(phone) ){
//           return ServerResponse.error(ResponseEnum.MEMBER_IS_NULL);
//        }
//        //手机号正则
//        Boolean zzphone = ZzUtil.Zzphone(phone);
//        if(zzphone==false){
//            return ServerResponse.error(ResponseEnum.PHONE_ERROR);
//        }
//        //邮箱正则
//        Boolean aBoolean = ZzUtil.ZzEmail(email);
//        if(aBoolean==false){
//            return ServerResponse.error(ResponseEnum.Email_ERROR);
//        }
//
//        //判断密码与确认密码是否一致
//        if(!password.equals(confirmPassword)){
//            return  ServerResponse.error(ResponseEnum.MEMBER_PASSWORD_ISERROR);
//        }
//
//        //判断验证码是否正确
//        String codeRedis = RedisUtil.get(phone);
//        if(StringUtils.isEmpty(codeRedis) || ! codeRedis.equals(code)){
//            return  ServerResponse.error(ResponseEnum.MEMBER_CODE_ISERROR);
//        }
//        //判断用户是否存在
//        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("memberName",memberName);
//        Member memberDB = memberMapper.selectOne(queryWrapper);
//        if(memberDB !=null){
//            return ServerResponse.error(ResponseEnum.MEMBER_IS_THIS);
//        }
//            //添加会员
//
//            Member member = new Member();
//            member.setMemberName(memberName);
//            member.setEmail(email);
//            member.setNickName(nickName);
//            member.setPassword(Md5Util.md5(password));
//            member.setPhone(phone);
//            member.setStatus(0);
//            memberMapper.insert(member);
//
//        RedisUtil.del(phone);
//        //uuid当key
//        String key = UUID.randomUUID().toString();
//        //id当value
//        Long id = member.getId();
//
//        RedisUtil.setEx(KeyUtil.ActionKey(key),id+"",5*60);
//        //同步发邮件
//        mailUtil.sendMailHtml(email,"激活账户","点击以下链接激活账户http://localhost:8084/api/member/updateStatus?id="+key);
//        //异步发邮件
//        MailMessage mailMessage = new MailMessage();
//        mailMessage.setTo(email);
//        mailMessage.setTitle("激活账户");
//        mailMessage.setContent("点击以下链接激活账户http://localhost:8084/api/member/updateStatus?id="+key);
//        mailProducer.sendMail(mailMessage);
//        return ServerResponse.success();
//    }



    @Override
    public ServerResponse login(String memberName, String password) {
//        在登陆的时候首先要判断用户，密码不为空
            if(StringUtils.isEmpty(memberName) || StringUtils.isEmpty(password)){
                return ServerResponse.error(ResponseEnum.MEMBER_LOJIN_IS_NOLL);
            }

//         判断用户是否正确
        QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<>();
        memberQueryWrapper.eq("memberName",memberName);
        Member member = memberMapper.selectOne(memberQueryWrapper);
        //判断账号是否激活

        if(member==null){
            return ServerResponse.error(ResponseEnum.MEMBER_LOJIN_IS_EX);
        }
//        判断密码是否正确
        if(!Md5Util.md5(password).equals(member.getPassword())){
            return ServerResponse.error(ResponseEnum.MEMBER_LOJIN_PASSWORD_ERROR);
        }
        //判断用户状态是否进行激活
        int status = member.getStatus();
        if(status==0){
            String email = member.getEmail();
            Long id = member.getId();
            Map map=new HashMap();
            map.put("email",email);
            map.put("id",id);
            return ServerResponse.error(ResponseEnum.MEMBER_LOJIN_STATUS_ERROR,map);
        }

//   ----------------------------------------获取签名
        //        签名=MD5(用户信息+secret密钥）
        MemberVo memberVo = new MemberVo();
        memberVo.setId(member.getId());
        memberVo.setMemberName(member.getMemberName());

        memberVo.setNickName(member.getNickName());

        String memberVoJsonStr = JSON.toJSONString(memberVo);
        String secre = SecretLogin.SECRE;
        String sign = Md5Util.md5(memberVoJsonStr + secre);
//   ----------------------------------------获取签名

//               返回   用户信息+签名通过basc64(encode是加 decode是解)
        String memberInfo = Base64.getEncoder().encodeToString(memberVoJsonStr.getBytes());
        String signBase64 = Base64.getEncoder().encodeToString(sign.getBytes());

        RedisUtil.setEx(KeyUtil.getksy(memberVo.getId()),"",SecretLogin.SECODE);

        return ServerResponse.success(memberInfo+"."+signBase64);
    }

//    @Override
//    public ServerResponse ckeckMemberName(String memberName) {
//        if(StringUtils.isEmpty(memberName)){
//            return ServerResponse.error(ResponseEnum.MEMBER_REG_MEMBERNAME_IS_BULL);
//        }
//        QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<>();
//        memberQueryWrapper.eq("memberName",memberName);
//        Member member = memberMapper.selectOne(memberQueryWrapper);
//        if(member!=null){
//            return ServerResponse.error(ResponseEnum.MEMBER_REG_MEMBERNAME_IS_E);
//        }
//        return ServerResponse.success();
//    }
//
//    @Override
//    public ServerResponse checkPhone(String phone) {
//        if(StringUtils.isEmpty(phone)){
//            return ServerResponse.error(ResponseEnum.MEMBER_REG_PHONE_IS_BULL);
//        }
//        QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<>();
//        memberQueryWrapper.eq("phone",phone);
//        Member member = memberMapper.selectOne(memberQueryWrapper);
//        if(member!=null){
//            return ServerResponse.error(ResponseEnum.MEMBER_REG_PHONE_IS_EX
//            );
//        }
//        return ServerResponse.success();
//    }
//
//    @Override
//    public ServerResponse checkEmail(String email) {
//        if(StringUtils.isEmpty(email)){
//            return ServerResponse.error(ResponseEnum.MEMBER_REG_EMAIL_IS_NULL);
//        }
//        QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<>();
//        memberQueryWrapper.eq("email",email);
//        Member member = memberMapper.selectOne(memberQueryWrapper);
//        if(member!=null){
//            return ServerResponse.error(ResponseEnum.MEMBER_REG_EMAIL_IS_EX
//            );
//        }
//        return ServerResponse.success();
//    }
//
//    @Override
//    public ServerResponse postCode(String memberName, String email) {
//        if(StringUtils.isEmpty(memberName) || StringUtils.isEmpty(email)){
//            return  ServerResponse.error(ResponseEnum.MEMBER_IS_NULL);
//        }
//        //首先我们要判断用户名是否和邮箱保持一致
//        QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<>();
//        memberQueryWrapper.eq("memberName",memberName);
//        Member member = memberMapper.selectOne(memberQueryWrapper);
//        String email1 = member.getEmail();
//        if(email1.equals(email)){
//            return  ServerResponse.error(ResponseEnum.MEMBER_REG_EMAIL_MEMBERNAME_IS_YES);
//        }
//        //发送短信并且生成验证码给好友的邮箱 将验证码存入redis
//        String code=codeMail;
//        String subJect="帮我找回密码验证码是："+codeMail;
//        mailUtil.sendMailHtml("1124992392@qq.com","找回密码",subJect);
//        RedisUtil.setEx(SecretLogin.CODEPASSWORD,code,2*60);
//        return ServerResponse.success();
//    }
//
//    @Override
//    public ServerResponse checkCode(String code, String email, String id) {
//        //非空
//        if(StringUtils.isEmpty(email) || StringUtils.isEmpty(code) || StringUtils.isEmpty(id)){
//            return  ServerResponse.error(ResponseEnum.MEMBER_CODE_IS_NULL);
//        }
//        //验证验证码
//        String rediscode = RedisUtil.get(KeyUtil.buildImageCodeKey(id));
//        if(!rediscode.equals(code)){
//            return ServerResponse.error(ResponseEnum.MEMBER_CODE_IS_NO_YES);
//        }
//        //验证邮箱
//        QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<>();
//        memberQueryWrapper.eq("email",email);
//        Member member = memberMapper.selectOne(memberQueryWrapper);
//        if(member==null){
//            return ServerResponse.error(ResponseEnum.MEMBER_EMAIL_IS_NOT);
//        }
//        //发送邮箱
//
//        String newPassword = RandomStringUtils.randomAlphanumeric(6);
//        member.setPassword(Md5Util.md5(newPassword));
//        memberMapper.updateById(member);
//        //同步发邮件
//        mailUtil.sendMailHtml(email,"找回密码","您的密码更改为"+newPassword+"请及时更改");
//        //异步发邮件
//        MailMessage mailMessage = new MailMessage();
//        mailMessage.setTo(email);
//        mailMessage.setTitle("找回密码");
//        mailMessage.setContent("您的密码更改为"+newPassword+"请及时更改");
//        mailProducer.sendMail(mailMessage);
//        RedisUtil.del(KeyUtil.buildImageCodeKey(id));
//        return ServerResponse.success();
//    }
//
//
//
//    @Override
//    public ServerResponse updatePassword(MemberParam memberParam) {
//        String password = memberParam.getPassword();
//        String confirmPassword = memberParam.getConfirmPassword();
//        String memberName = memberParam.getMemberName();
//        if(!password.equals(confirmPassword)){
//            return  ServerResponse.error(ResponseEnum.MEMBER_PASSWORD_ISERROR);
//        }
//        if(StringUtils.isEmpty(password)){
//            return  ServerResponse.error(ResponseEnum.UPDATE_PASSWORD_IS_NULL);
//        }
//        if(StringUtils.isEmpty(confirmPassword)){
//            return  ServerResponse.error(ResponseEnum.UPDATE_CONFRIM_PASSWORD_IS_NULL);
//        }
//        QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<>();
//        memberQueryWrapper.eq("memberName",memberName);
//        Member memberDB = memberMapper.selectOne(memberQueryWrapper);
//        memberDB.setPassword(Md5Util.md5(password));
//        memberMapper.updateById(memberDB);
//        return ServerResponse.success();
//    }
//
//    @Override
//    public ServerResponse checkPassword(String memberName,String oldpassword) {
//        if(StringUtils.isEmpty(oldpassword)){
//            return  ServerResponse.error(ResponseEnum.MEMBER_OLD_PASSWORD_NULL);
//        }
//        QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<>();
//        memberQueryWrapper.eq("memberName",memberName);
//        Member memberDB = memberMapper.selectOne(memberQueryWrapper);
//        //判断旧密码是否正确
//        if(!memberDB.getPassword().equals(Md5Util.md5(oldpassword))){
//            return  ServerResponse.error(ResponseEnum.MEMBER_OLD_PASSWORD_ERROR);
//        }
//        return ServerResponse.success(ResponseEnum.MEMBER_OLD_PASSWORD_YES);
//    }
//
//    @Override
//    public ServerResponse confrimpassWord(String memberName, String password, String confrimpassword) {
//        //非空验证
//        if(StringUtils.isEmpty(password)){
//            return  ServerResponse.error(ResponseEnum.UPDATE_PASSWORD_IS_NULL);
//        }
//        if(StringUtils.isEmpty(confrimpassword)){
//            return  ServerResponse.error(ResponseEnum.UPDATE_CONFRIM_PASSWORD_IS_NULL);
//        }
//        //是否一致
//        if(!password.equals(confrimpassword)){
//            return  ServerResponse.error(ResponseEnum.MEMBER_PASSWORD_ISERROR);
//        }
//        //修改密码
//        QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<>();
//        memberQueryWrapper.eq("memberName",memberName);
//        Member memberDB = memberMapper.selectOne(memberQueryWrapper);
//        memberDB.setPassword(Md5Util.md5(password));
//        memberMapper.updateById(memberDB);
//        RedisUtil.del(KeyUtil.getksy(memberDB.getId()));
//        return ServerResponse.success();
//    }
//
//    @Override
//    public int updateStatus(String id) {
//        //判断是否攻击者
//        String res = RedisUtil.get(KeyUtil.ActionKey(id));
//        if(StringUtils.isEmpty(res)){
//            return SystemConstant.ACTION;
//        }else {
//            Member member = new Member();
//            member.setId(Long.parseLong(res));
//            member.setStatus(SystemConstant.STATUS);
//            memberMapper.updateById(member);
//        }
//        RedisUtil.del(KeyUtil.ActionKey(id));
//        return SystemConstant.InACTION;
//    }
//
//    @Override
//    public ServerResponse sendMail(String id, String email) {
//        //非空验证
//        if(StringUtils.isEmpty(id) || StringUtils.isEmpty(email)){
//            return ServerResponse.error();
//        }
//        //存入redis
//        String uuid = UUID.randomUUID().toString();
//        RedisUtil.setEx(KeyUtil.ActionKey(uuid),id,5*60);
//        //发送邮件
//        mailUtil.sendMailHtml(email,"激活账户","点击以下链接激活账户http://localhost:8084/api/member/updateStatus?id="+uuid);
//        return ServerResponse.success();
//    }


}
