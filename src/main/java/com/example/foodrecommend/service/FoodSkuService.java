package com.example.foodrecommend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.foodrecommend.beans.FoodSku;
import com.example.foodrecommend.beans.User;

import java.io.Serializable;
import java.util.*;

import java.util.List;

/**
 * @author 86176
 * @description 针对表【food_sku】的数据库操作Service
 * @createDate 2023-11-13 00:14:57
 */
public interface FoodSkuService extends IService<FoodSku> {
    Page<Map<String, Collection<FoodSku>>> getYouWantEat(Page page, String openId);

    /**
     * 通过销量和评分推荐菜品
     *
     * @param n 推荐菜品的数量
     * @return 推荐的菜品列表
     */
    List<FoodSku> recommendBySalesAndScore(Integer n);

    List<FoodSku>  getLocationFood(String openId);

    Map<String,Object> getSkuInfo(User user, Serializable id);

    int saveFoodSku(FoodSku foodSku);
}
