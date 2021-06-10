package com.fh.shop.api.cate.biz;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.cate.mapper.ICateMapper;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.po.cate.Cate;
import com.fh.shop.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("cateService")
public class ICateServiceImpl implements ICateService {

    @Autowired
    private ICateMapper cateMapper;



    @Override
    public ServerResponse findCate() {
        //首先需要去redis缓存查询数据
        String cateLis = RedisUtil.get("cateList");
        //如果有数据则直接返回list(将json格式的string转换为list)
        if(StringUtils.isEmpty(cateLis)){
            List<Cate> cateList = JSON.parseArray(cateLis, Cate.class);
            return ServerResponse.success(cateList);
        }
        List<Cate> cateList = cateMapper.selectList(null);
        //通过 falstJson 将java对象转换为json格式的字符穿
        String cate = JSON.toJSONString(cateList);
        RedisUtil.set("cateList",cate);
        return ServerResponse.success(cateList);
    }




}
