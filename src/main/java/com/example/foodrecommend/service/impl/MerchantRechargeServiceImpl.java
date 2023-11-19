package com.example.foodrecommend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.foodrecommend.beans.MerchantRecharge;
import com.example.foodrecommend.mapper.MerchantRechargeMapper;
import com.example.foodrecommend.service.MerchantRechargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MerchantRechargeServiceImpl extends ServiceImpl<MerchantRechargeMapper, MerchantRecharge> implements MerchantRechargeService {

    @Autowired
    private MerchantRechargeMapper merchantRechargeMapper;

    // 增加权重值
    private static final Integer INCREASE_RECOMMENDATION_WEIGHT = 1;
    // 购买广告位
    private static final Integer PURCHASE_ADVERTISING_SPACE = 2;
    // 优化搜索
    private static final Integer OPTIMIZE_SEARCH = 3;

    /**
     * 商家进行充值
     *
     * @param merchantRecharge 商家充值的信息，应该有商家id，充值类型，充值金额，有效时间
     *                         充值类型，1：增加推荐权重，2：购买广告位，3：优化搜索
     * @return
     */
    @Override
    public String recharge(MerchantRecharge merchantRecharge) {
        // 判断充值类型，进行对应的业务操作
        Integer rechargeType = merchantRecharge.getRechargeType();

        return null;
    }
}
