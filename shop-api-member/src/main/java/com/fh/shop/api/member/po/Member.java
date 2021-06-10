package com.fh.shop.api.member.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class Member implements Serializable {

        private Long id;

        private String memberName;

        private String password;

        private String nickName;

        private String email;

        private String phone;

        private int status;

        private Long score;



}
