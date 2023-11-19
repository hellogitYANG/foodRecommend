package com.example.foodrecommend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.foodrecommend.beans.FoodSku;

import java.util.List;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * @author 86176
 * @description 针对表【food_sku】的数据库操作Service
 * @createDate 2023-11-13 00:14:57
 */
public interface FoodSkuService extends IService<FoodSku> {
    HashMap<String, Collection<FoodSku>> getYouWantEat(String openId);

    /**
     * 通过销量和评分推荐菜品
     *
     * @param n 推荐菜品的数量
     * @return 推荐的菜品列表
     */
    List<FoodSku> recommendBySalesAndScore(Integer n);
}
