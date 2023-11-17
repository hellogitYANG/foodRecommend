package com.example.foodrecommend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.foodrecommend.beans.FoodSku;
import com.example.foodrecommend.beans.Merchant;
import com.example.foodrecommend.dto.FoodSkuDto;
import com.example.foodrecommend.mapper.FoodSkuMapper;
import com.example.foodrecommend.mapper.MerchantMapper;
import com.example.foodrecommend.service.FoodSkuService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author 86176
 * @description 针对表【food_sku】的数据库操作Service实现
 * @createDate 2023-11-13 00:14:57
 */
@Service
public class FoodSkuServiceImpl extends ServiceImpl<FoodSkuMapper, FoodSku> implements FoodSkuService {
    @Autowired
    private FoodSkuMapper foodSkuMapper;
    @Autowired
    private MerchantMapper merchantMapper;

    /**
     * 通过销量和评分推荐菜品
     *
     * @param n 推荐菜品的数量
     * @return 推荐的菜品列表
     * 功能描述：
     * 通过销量和评分来推荐菜品。推荐值的计算公式为：
     * 推荐值 = 菜品销量 * 商家的评分
     */
    @Override
    public List<FoodSku> recommendBySalesAndScore(Integer n) {
        // 查询数据库中的所有菜品
        List<FoodSku> foodSkus = foodSkuMapper.selectList(null);
        List<FoodSkuDto> foodSkuDtos = new ArrayList<>();
        // 遍历每一个菜品并计算它们的推荐值
        foodSkus.forEach(food -> {
            // 创建一个FoodSkuDto对象
            FoodSkuDto foodSkuDto = new FoodSkuDto();
            // 将菜品对象（FoodSku）的属性复制到FoodSkuDto对象中
            BeanUtils.copyProperties(food, foodSkuDto);
            // 获取菜品对应商家的评分
            String merchantId = food.getMerchantId();
            // 查询对应的商家信息
            Merchant merchant = merchantMapper.selectOne(new LambdaQueryWrapper<Merchant>().eq(Merchant::getId, merchantId));
            Integer star = merchant.getStar();
            // 计算推荐值，推荐值是菜品销量和商家评分的乘积
            foodSkuDto.setScoreWeightSales(food.getSalesNum() * star);
            // 将计算结果添加到foodSkuDtos列表中
            foodSkuDtos.add(foodSkuDto);
        });
        // 将foodSkuDtos列表中的元素按照推荐值的大小进行降序排序
        foodSkuDtos.sort(Comparator.comparing(FoodSkuDto::getScoreWeightSales).reversed());
        // 根据传入的数量n，获取推荐值最大的前n个菜品
        List<FoodSku> recommendedFoodSkus = new ArrayList<>();
        // 当菜品总数大于n时，返回前n个菜品；否则，返回所有菜品
        if (foodSkuDtos.size() > n) {
            recommendedFoodSkus.addAll(foodSkuDtos.subList(0, n));  // 如果数量超过n，只返回前n个
        } else {
            recommendedFoodSkus.addAll(foodSkuDtos);  // 如果没有n个菜品，那么全部返回
        }
        // 返回推荐的菜品列表
        return recommendedFoodSkus;
    }
}




