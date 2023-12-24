package com.example.foodrecommend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.foodrecommend.beans.FoodComments;
import com.example.foodrecommend.beans.OrderFather;
import com.example.foodrecommend.beans.Orders;
import com.example.foodrecommend.beans.User;
import com.example.foodrecommend.dto.FoodCommentsResponseDto;
import com.example.foodrecommend.mapper.FoodCommentsMapper;
import com.example.foodrecommend.mapper.OrderFatherMapper;
import com.example.foodrecommend.mapper.OrdersMapper;
import com.example.foodrecommend.service.FoodCommentsService;
import com.example.foodrecommend.service.UserService;
import com.example.foodrecommend.utils.GetUserInfoByToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Autowired
    private UserService userService;

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

    @Override
    public IPage<FoodCommentsResponseDto> pageByParams(Map<String, Object> params) {
        LambdaQueryWrapper<FoodComments> queryWrapper = new LambdaQueryWrapper<>();

        //分页参数
        long current = 1;
        long size = 10;
        if(params.get("current") != null){
            current = Long.parseLong((String)params.get("current"));
        }
        if(params.get("size") != null){
            size = Long.parseLong((String)params.get("size"));
        }
        if (params.get("merchant_id") != null && !"0".equalsIgnoreCase(params.get("merchant_id").toString())) {
            queryWrapper.eq(FoodComments::getMerchantId , params.get("merchant_id").toString());
        }
        if (params.get("keyword") != null) {
            queryWrapper.and(w -> {
                w.eq(FoodComments::getId , params.get("keyword")).or().like(FoodComments::getCommentStar , params.get("keyword"));
            });
        }
        Page<FoodComments> page = this.page(new Page<FoodComments>(current, size), queryWrapper);

        IPage<FoodCommentsResponseDto> iPage = new Page<>();
        iPage.setPages(page.getPages());
        iPage.setTotal(page.getTotal());
        iPage.setSize(page.getSize());
        iPage.setCurrent(page.getCurrent());

        if (page.getTotal() <= 0) {
            return iPage;
        }

        List<FoodComments> comments = page.getRecords();
        List<String> list = comments.stream().map(FoodComments::getUserId).collect(Collectors.toList());
        List<User> users = userService.getUsersByInOpenId(list);
        Map<String, User> userMap = users.stream().collect(Collectors.toMap(User::getOpenId, Function.identity(), (k1, k2) -> k2));

        List<FoodCommentsResponseDto> responseDtos = comments.stream().map(c -> {
            FoodCommentsResponseDto responseDto = new FoodCommentsResponseDto();
            BeanUtils.copyProperties(c, responseDto);
            if (userMap.containsKey(c.getUserId())) {
                responseDto.setUser(userMap.get(c.getUserId()));
            }
            return responseDto;
        }).collect(Collectors.toList());

        iPage.setRecords(responseDtos);
        return iPage;
    }
}




