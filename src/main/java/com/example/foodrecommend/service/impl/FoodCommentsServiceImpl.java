package com.example.foodrecommend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.foodrecommend.beans.FoodComments;
import com.example.foodrecommend.beans.OrderFather;
import com.example.foodrecommend.beans.Orders;
import com.example.foodrecommend.beans.User;
import com.example.foodrecommend.mapper.FoodCommentsMapper;
import com.example.foodrecommend.mapper.OrderFatherMapper;
import com.example.foodrecommend.mapper.OrdersMapper;
import com.example.foodrecommend.service.FoodCommentsService;
import com.example.foodrecommend.utils.GetUserInfoByToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author 86176
* @description 针对表【food_comments】的数据库操作Service实现
* @createDate 2023-11-13 00:14:57
*/
@Service
public class FoodCommentsServiceImpl extends ServiceImpl<FoodCommentsMapper, FoodComments>
    implements FoodCommentsService{

    @Autowired
    OrdersMapper ordersMapper;

    @Autowired
    OrderFatherMapper orderFatherMapper;
    @Autowired
    FoodCommentsMapper foodCommentsMapper;

    @Override
    @Transactional
    public int InsertByFullOrder(FoodComments foodComments,String token) {
        User user = GetUserInfoByToken.parseToken(token);

        String orderFatherId = foodComments.getOrderFatherId();
        List<Orders> ordersList = ordersMapper.selectList(new QueryWrapper<Orders>().eq("order_father_id", orderFatherId).eq("user_id",user.getOpenId()));
        for (Orders orders : ordersList) {
            BeanUtil.copyProperties(orders,foodComments,"id");
            foodComments.setId(null);
            foodComments.setOrderId(orders.getId());
            foodCommentsMapper.insert(foodComments);
        }
        OrderFather orderFather = orderFatherMapper.selectById(orderFatherId);
        orderFather.setIsComment(1);
        orderFatherMapper.updateById(orderFather);
        return 1;
    }
}




