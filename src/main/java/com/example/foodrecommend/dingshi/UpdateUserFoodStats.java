package com.example.foodrecommend.dingshi;

import cn.hutool.db.sql.Order;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.foodrecommend.beans.Orders;
import com.example.foodrecommend.beans.User;
import com.example.foodrecommend.mapper.OrdersMapper;
import com.example.foodrecommend.mapper.UserMapper;
import com.example.foodrecommend.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UpdateUserFoodStats {

    @Autowired
    UserMapper userMapper;
    @Autowired
    OrdersMapper ordersMapper;
    //两小时一次
    @Scheduled(initialDelay=5000,fixedRate = 8000000)
    public void updateUserFoodStats(){
//        // 设置时间范围
//        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
//        LocalDateTime now = LocalDateTime.now();
//        //统计最近一周里有订单的用户
//        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
//        queryWrapper.select("DISTINCT user_id");
//        queryWrapper.ge("create_time", oneWeekAgo).le("create_time", now);
//        List<Orders> orders = ordersMapper.selectList(queryWrapper);
//
//        //遍历这些用户，查出来他们这周的订单，遍历这些订单，统计出是什么口味，更新到用户属性里
//        for (Orders order : orders) {
//            String userId = order.getUserId();
//            QueryWrapper<Orders> queryWrapper2 = new QueryWrapper<>();
//            queryWrapper2.eq("user_id",userId);
//            queryWrapper2.ge("create_time", oneWeekAgo).le("create_time", now);
//            List<Orders> userWeekOrders = ordersMapper.selectList(queryWrapper2);
//            //遍历这个用户这些订单，统计出是什么口味最多
//            HashMap<String, Integer> foodTasteList = new HashMap<>();
//            for (Orders userOrder : userWeekOrders) {
//                String foodTaste = userOrder.getFoodTaste();
//                //口味json转map
//                Map<String, Object> map = JSONUtil.parseObj(foodTaste);
//
//                for (Map.Entry<String, Object> entry : map.entrySet()) {
//                    if(foodTasteList.get(entry.getValue())==null){
//                        foodTasteList.put((String) entry.getValue(),1);
//                    }else {
//                        foodTasteList.put((String) entry.getValue(),foodTasteList.get(entry.getValue())+1);
//                    }
//                }
//            }
//            for (Map.Entry<String, Integer> entry : foodTasteList.entrySet()) {
//                System.out.println(entry.getKey() + ": " + entry.getValue());
//            }
//            System.out.println("---下一个用户---");
//        }
    }
}
