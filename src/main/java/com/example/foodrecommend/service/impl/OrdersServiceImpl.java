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
    @Autowired
    MerchantMapper merchantMapper;
    @Override
    @Transactional
    public int insertOrder(Orders orders, String token) {
        // 设置时间范围
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        LocalDateTime now = LocalDateTime.now();
        System.out.println(orders.getFoodTaste());
        //解析token
        User user = GetUserInfoByToken.parseToken(token);
        //增加订单
        int insert = ordersMapper.insert(orders);
        //菜品增加销量
        FoodSku foodSku = foodSkuMapper.selectById(orders.getFoodSkuId());
        foodSku.setSalesNum(foodSku.getSalesNum()+1);
        int insert1 = foodSkuMapper.updateById(foodSku);
        //商家增加销量
        Merchant merchant = merchantMapper.selectById(orders.getMerchantId());
        merchant.setSalesNum(merchant.getSalesNum()+1);
        int insert2 = merchantMapper.updateById(merchant);
        //重新计算用户口味，查出一周的订单统计//后期优化:队列
        if (insert>0){
            QueryWrapper<Orders> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("user_id",user.getOpenId());
            queryWrapper2.ge("create_time", oneWeekAgo).le("create_time", now);
            List<Orders> userWeekOrders = ordersMapper.selectList(queryWrapper2);

            //遍历这个用户这些订单，统计出每种口味哪样的最多。
            // 比如统计三条订单，key不变，value追加：{口味:"烧烤,巧克力，烧烤",烹饪方式:"煎,炸，煎"}
            //整理后用户口味为{口味:"烧烤",烹饪方式:"煎"}
            HashMap<String, Object> foodTasteList = new HashMap<>();

            for (Orders userOrder : userWeekOrders) {
                String foodTaste = userOrder.getFoodTaste();
                //口味json转map
                Map<String, Object> map = JSONUtil.parseObj(foodTaste);

                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if(foodTasteList.get(entry.getKey())==null){
                        //第一次
                        foodTasteList.put(entry.getKey(),entry.getValue());
                    }else {
                        //追加value
                        foodTasteList.put(entry.getKey(),foodTasteList.get(entry.getKey())+","+entry.getValue());
                    }
                }
            }
            //整理出用户口味
            HashMap<String, Object> finalFoodTaste = new HashMap<>();
            for (Map.Entry<String, Object> entry : foodTasteList.entrySet()) {
                String tasteKey = entry.getKey();
                String tasteValues = (String) entry.getValue();

                // 统计每种口味中最频繁的值
                String mostFrequentTaste = findMostFrequentValue(tasteValues);

                // 将统计结果放入 finalFoodTaste
                finalFoodTaste.put(tasteKey, mostFrequentTaste);
            }

            //转为json更新用户口味
            UpdateWrapper<User> wrapper = new UpdateWrapper<>();
            wrapper.eq("open_id",user.getOpenId());

            User newUser = new User();
            newUser.setFoodStats(JSONUtil.toJsonStr(finalFoodTaste));
            int update = userMapper.update(newUser, wrapper);
            return update;
        }
        return 0;
    }



    private static String findMostFrequentValue(String values) {
        // 将逗号分隔的值拆分成数组
        String[] valueArray = values.split(",");

        // 使用 Java 8 Stream 统计每个值的出现次数
        Map<String, Long> frequencyMap = Arrays.stream(valueArray)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // 找到出现次数最多的值
        Optional<Map.Entry<String, Long>> mostFrequentEntry = frequencyMap.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        // 返回最频繁的值，如果存在的话
        return mostFrequentEntry.map(Map.Entry::getKey).orElse(null);
    }

}




