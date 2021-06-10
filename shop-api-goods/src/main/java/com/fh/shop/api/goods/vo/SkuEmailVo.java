package com.fh.shop.api.goods.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data

public class SkuEmailVo implements Serializable {

    private String  skuName;

    private BigDecimal price;

    private int stock;

    private String brandName;

    private String cateName;

}
