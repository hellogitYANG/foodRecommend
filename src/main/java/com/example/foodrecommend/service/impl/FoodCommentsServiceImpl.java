package com.example.foodrecommend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.foodrecommend.beans.FoodComments;
import com.example.foodrecommend.beans.Orders;
import com.example.foodrecommend.beans.User;
import com.example.foodrecommend.mapper.OrdersMapper;
import com.example.foodrecommend.service.FoodCommentsService;
import com.example.foodrecommend.mapper.FoodCommentsMapper;
import com.example.foodrecommend.utils.GetUserInfoByToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    FoodCommentsMapper foodCommentsMapper;

    @Override
    public int InsertByFullOrder(FoodComments foodComments,String token) {
        User user = GetUserInfoByToken.parseToken(token);

        String orderId = foodComments.getOrderId();
        Orders orders = ordersMapper.selectById(orderId);
        if (!user.getOpenId().equals(orders.getUserId())){
            return 0;
        }
        BeanUtil.copyProperties(orders,foodComments,"id");


//        foodComments.setFoodName(orders.getFoodName());
//        foodComments.setFoodSkuId(orders.getFoodSkuId());
//        foodComments.setUserId(orders.getUserId());
//        foodComments.setFoodTaste(orders.getFoodTaste());
//        foodComments.setIsBrush(orders.getIsBrush());
//        foodComments.setMerchantId(orders.getMerchantId());
//        foodComments.setMerchantPhone(orders.getMerchantPhone());

        int insert = foodCommentsMapper.insert(foodComments);
        return insert;
    }
}




