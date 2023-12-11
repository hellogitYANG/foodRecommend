package com.example.foodrecommend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.foodrecommend.beans.OrderFather;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
* @author 86176
* @description 针对表【order_father】的数据库操作Service
* @createDate 2023-12-11 18:06:39
*/
public interface OrderFatherService extends IService<OrderFather> {
    Page<Map<String, Object>> selectOrderInfoPage(Page page, String openId);
}
