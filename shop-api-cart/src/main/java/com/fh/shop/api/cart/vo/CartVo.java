package com.fh.shop.api.cart.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data

public class CartVo implements Serializable {

    private List<CartSkuVo> cartVoList=new ArrayList<>();

    private  Long cartCount;

    private  String sumPrice;


}
