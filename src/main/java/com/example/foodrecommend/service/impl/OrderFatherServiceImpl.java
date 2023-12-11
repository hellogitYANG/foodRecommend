package com.example.foodrecommend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.foodrecommend.beans.OrderFather;
import com.example.foodrecommend.beans.Orders;
import com.example.foodrecommend.mapper.OrdersMapper;
import com.example.foodrecommend.service.OrderFatherService;
import com.example.foodrecommend.mapper.OrderFatherMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
}




