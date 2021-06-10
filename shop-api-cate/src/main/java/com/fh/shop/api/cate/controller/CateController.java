package com.fh.shop.api.cate.controller;

import com.fh.shop.api.cate.biz.ICateService;
import com.fh.shop.common.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api")
@Api(tags = "类型")
@Slf4j
public class CateController {

    @Resource(name = "cateService")
    private ICateService cateService;

    @Value("${server.port}")
    private String port;


    @RequestMapping(value = "/cates",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询分类")
    public ServerResponse findCate(){
        System.out.println("Git测试");
        log.info("端口号:{}",port);
        return  cateService.findCate();
    }


}
