package com.example.foodrecommend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.foodrecommend.beans.*;
import com.example.foodrecommend.mapper.*;
import com.example.foodrecommend.service.OrderFatherService;
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
* @description 针对表【order_father】的数据库操作Service实现
* @createDate 2023-12-11 18:06:39
*/
@Service
public class OrderFatherServiceImpl extends ServiceImpl<OrderFatherMapper, OrderFather>
    implements OrderFatherService{
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderFatherMapper orderFatherMapper;
    @Resource
    private MerchantMapper merchantMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FoodSkuMapper foodSkuMapper;
    @Override
    public Page<Map<String, Object>> selectOrderInfoPage(Page page, String openId) {
        //结果
        List<Map<String, Object>> list=new ArrayList<>();
        //分页查出父订单
        Page<OrderFather> orderPage = orderFatherMapper.selectPage(page, new QueryWrapper<OrderFather>().eq("user_id", openId).orderByDesc("create_time"));
        List<OrderFather> records = orderPage.getRecords();
        //遍历父订单查出订单详情
        for (OrderFather record : records) {
            List<Orders> orderInfo = ordersMapper.selectList(new QueryWrapper<Orders>().eq("order_id", record.getId()));
            Map<String, Object> map = BeanUtil.beanToMap(record);
            map.put("orderInfo",orderInfo);

            list.add(map);
        }

        Page<Map<String, Object>> listPage = new Page<>();
        listPage.setTotal(orderPage.getTotal());
        listPage.setRecords(list);
        //封装到list map里返回
        return listPage;
    }

    @Override
    @Transactional
    public int addOrderFatherByIds(List<String> foodIds, User user) {
        //增加父订单
        OrderFather orderFather = new OrderFather();
        String uuid = IdUtil.fastUUID();
            orderFather.setId(uuid);
            orderFather.setUserId(user.getOpenId());
            orderFather.setUserPhone(user.getPhone());
            //根据菜品查出商家信息进行封装
            FoodSku food = foodSkuMapper.selectById(foodIds.get(0));
            Merchant merchant = merchantMapper.selectById(food.getMerchantId());
            orderFather.setMerchantId(merchant.getId());
            orderFather.setMerchantPhone(merchant.getPhone());
            orderFather.setIsBrush(0);

            orderFatherMapper.insert(orderFather);

        for (String id : foodIds) {
            //查询当前食品信息
            FoodSku foodSku = foodSkuMapper.selectById(id);

            //封装单个订单信息
            Orders orders = new Orders();

            orders.setOrderId(uuid);
            orders.setUserId(user.getOpenId());
            orders.setIsBrush(0);
            orders.setMerchantId(orderFather.getMerchantId());
            orders.setMerchantPhone(orderFather.getMerchantPhone());
            orders.setFoodName(foodSku.getName());
            orders.setFoodSkuId(foodSku.getId());
            orders.setFoodTaste(foodSku.getFoodStats());
            orders.setUserPhone(user.getPhone());

            //增加订单
            ordersMapper.insert(orders);

            //菜品增加销量
            foodSku.setSalesNum(foodSku.getSalesNum()+1);
            foodSkuMapper.updateById(foodSku);
        }

        //商家增加销量
        merchant.setSalesNum(merchant.getSalesNum()+foodIds.size());
        return merchantMapper.updateById(merchant);


    }

    @Override
    public int updateUserStatus(User user) {
        // 设置时间范围
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        LocalDateTime now = LocalDateTime.now();

        //重新计算用户口味，查出一周的订单统计//后期优化:队列
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




