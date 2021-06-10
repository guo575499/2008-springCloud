package com.fh.shop.api.member.param;

import com.fh.shop.api.member.po.Member;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel
public class MemberParam extends Member implements Serializable {
    @ApiModelProperty(value = "确认密码",required = true)
    private String confirmPassword;
    @ApiModelProperty(value = "验证码",required = true)
    private String code;

}
