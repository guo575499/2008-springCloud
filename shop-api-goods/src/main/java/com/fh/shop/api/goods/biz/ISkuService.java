package com.fh.shop.api.goods.biz;

import com.fh.shop.common.ServerResponse;
import com.fh.shop.po.goods.Sku;

public interface ISkuService {


    ServerResponse findStatusList();

    ServerResponse<Sku> findSku(Long id);


//    void emailSku();

}
