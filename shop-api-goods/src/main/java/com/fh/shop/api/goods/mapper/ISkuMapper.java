package com.fh.shop.api.goods.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fh.shop.api.goods.vo.SkuEmailVo;
import com.fh.shop.po.goods.Sku;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface
ISkuMapper extends BaseMapper<Sku> {

    public List<Sku> findStatusList();

    List<SkuEmailVo> selectSkuStock();

//    int updateStock(CartSkuVo cartSkuVo);

    int updateSkuStock(@Param("skuId") Long skuId, @Param("count") Long count);

    void updateSales(@Param("skuCount") Long skuCount, @Param("skuId") Long skuId);
}
