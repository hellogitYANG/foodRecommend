package com.example.foodrecommend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.foodrecommend.beans.Merchant;
import com.example.foodrecommend.beans.MerchantRecharge;
import com.example.foodrecommend.mapper.MerchantMapper;
import com.example.foodrecommend.mapper.MerchantRechargeMapper;
import com.example.foodrecommend.service.MerchantRechargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MerchantRechargeServiceImpl extends ServiceImpl<MerchantRechargeMapper, MerchantRecharge> implements MerchantRechargeService {

    @Autowired
    private MerchantRechargeMapper merchantRechargeMapper;
    @Autowired
    private MerchantMapper merchantMapper;

    // 增加推荐权重值
    private static final Integer INCREASE_RECOMMENDATION_WEIGHT = 1;
    // 购买广告位
    private static final Integer PURCHASE_ADVERTISING_SPACE = 2;
    // 优化搜索
    private static final Integer OPTIMIZE_SEARCH = 3;

    /**
     * 查询购买了 增加推荐权重值 的商家
     *
     * @return 商家列表
     */
    @Override
    public List<Merchant> getMerchantsWithIncreasedRecommendationWeight() {
        List<String> merchantIds = getMerchantsByRechargeFunction(INCREASE_RECOMMENDATION_WEIGHT);
        return merchantMapper.selectBatchIds(merchantIds);
    }

    /**
     * 查询购买了 广告位 的商家
     *
     * @return 商家列表
     */
    @Override
    public List<Merchant> getMerchantsWithAdSpacePurchase() {
        List<String> merchantIds = getMerchantsByRechargeFunction(PURCHASE_ADVERTISING_SPACE);
        if (merchantIds.size()>0){
            return merchantMapper.selectBatchIds(merchantIds);
        }else {
            return new ArrayList<Merchant>();
        }

    }

    /**
     * 查询购买了 优化搜索 的商家
     *
     * @return 商家列表
     */
    @Override
    public List<Merchant> getMerchantsWithSearchOptimization() {
        List<String> merchantIds = getMerchantsByRechargeFunction(OPTIMIZE_SEARCH);
        return merchantMapper.selectBatchIds(merchantIds);
    }

    private List<String> getMerchantsByRechargeFunction(Integer rechargeFunction) {
        List<MerchantRecharge> merchantRecharges = merchantRechargeMapper.selectList(new LambdaQueryWrapper<MerchantRecharge>()
                .eq(MerchantRecharge::getRechargeType, rechargeFunction)
                .le(MerchantRecharge::getStartTime, LocalDateTime.now())
                .ge(MerchantRecharge::getEndTime,LocalDateTime.now()));
        List<String> merchantIds = new ArrayList<>();
        merchantRecharges.forEach(merchantRecharge -> {
            String id = merchantRecharge.getMerchantId();
            merchantIds.add(id);
        });
        return merchantIds;
    }
}
