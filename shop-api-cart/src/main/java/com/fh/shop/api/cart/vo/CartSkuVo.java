package com.fh.shop.api.cart.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CartSkuVo implements Serializable {

    private Long skuId;

    private String skuName;

    private String price;

    private  Long count;

    private String subPrice;

    private String image;

}
