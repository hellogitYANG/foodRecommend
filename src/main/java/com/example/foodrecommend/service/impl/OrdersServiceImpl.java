package com.example.foodrecommend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.foodrecommend.beans.Orders;
import com.example.foodrecommend.service.OrdersService;
import com.example.foodrecommend.mapper.OrdersMapper;
import org.springframework.stereotype.Service;

/**
* @author 86176
* @description 针对表【orders】的数据库操作Service实现
* @createDate 2023-11-13 00:14:57
*/
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
    implements OrdersService{

}




