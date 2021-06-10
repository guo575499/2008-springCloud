package com.fh.shop.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ShopRegApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopRegApplication.class,args);
    }

}
