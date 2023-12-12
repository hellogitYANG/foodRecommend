package com.example.foodrecommend.service.impl;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.foodrecommend.beans.FoodSku;
import com.example.foodrecommend.beans.Merchant;
import com.example.foodrecommend.beans.Orders;
import com.example.foodrecommend.beans.User;
import com.example.foodrecommend.mapper.FoodSkuMapper;
import com.example.foodrecommend.mapper.MerchantMapper;
import com.example.foodrecommend.mapper.UserMapper;
import com.example.foodrecommend.service.OrdersService;
import com.example.foodrecommend.mapper.OrdersMapper;
import com.example.foodrecommend.utils.GetUserInfoByToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* @author 86176
* @description 针对表【orders】的数据库操作Service实现
* @createDate 2023-11-13 00:14:57
*/
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
    implements OrdersService{
    @Autowired
    OrdersMapper ordersMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    FoodSkuMapper foodSkuMapper;
    @Resource
    MerchantMapper merchantMapper;





}




