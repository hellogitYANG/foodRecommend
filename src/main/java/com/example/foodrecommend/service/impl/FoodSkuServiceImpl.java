package com.example.foodrecommend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.foodrecommend.beans.FoodSku;
import com.example.foodrecommend.service.FoodSkuService;
import com.example.foodrecommend.mapper.FoodSkuMapper;
import org.springframework.stereotype.Service;

/**
* @author 86176
* @description 针对表【food_sku】的数据库操作Service实现
* @createDate 2023-11-13 00:14:57
*/
@Service
public class FoodSkuServiceImpl extends ServiceImpl<FoodSkuMapper, FoodSku>
    implements FoodSkuService{

}




