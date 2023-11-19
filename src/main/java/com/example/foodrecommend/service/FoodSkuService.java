package com.example.foodrecommend.service;

import com.example.foodrecommend.beans.FoodSku;
import com.baomidou.mybatisplus.extension.service.IService;

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

}
