package com.example.foodrecommend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.foodrecommend.beans.Merchant;
import com.example.foodrecommend.beans.MerchantRecharge;

import java.util.List;

public interface MerchantRechargeService extends IService<MerchantRecharge> {

    /**
     * 查询购买了 增加推荐权重值 的商家
     *
     * @return 商家列表
     */
    List<Merchant> getMerchantsWithIncreasedRecommendationWeight();

    /**
     * 查询购买了 广告位 的商家
     *
     * @return 商家列表
     */
    List<Merchant> getMerchantsWithAdSpacePurchase();

    /**
     * 查询购买了 优化搜索 的商家
     *
     * @return 商家列表
     */
    List<Merchant> getMerchantsWithSearchOptimization();
}
