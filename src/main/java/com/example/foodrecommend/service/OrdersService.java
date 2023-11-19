package com.example.foodrecommend.service;

import com.example.foodrecommend.beans.FoodSku;
import com.example.foodrecommend.beans.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 86176
* @description 针对表【orders】的数据库操作Service
* @createDate 2023-11-13 00:14:57
*/
public interface OrdersService extends IService<Orders> {

    public int insertOrder(Orders orders,String token);

}
