package com.fh.shop.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.fh.shop.api.cate.mapper")
@ComponentScan(basePackages = {"com.fh.shop.api.cate.controller"})
@ComponentScan(basePackages = {"com.fh.shop.api.config.SwaggerConfig"})
public class ShopCateApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopCateApplication.class,args);
    }

}
