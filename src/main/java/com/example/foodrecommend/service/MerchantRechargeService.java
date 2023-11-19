package com.example.foodrecommend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.foodrecommend.beans.MerchantRecharge;

public interface MerchantRechargeService extends IService<MerchantRecharge> {

    /**
     * 商家进行充值
     *
     * @param merchantRecharge 商家充值的信息，应该有商家id，充值类型，充值金额，有效时间
     *                         充值类型，1：增加推荐权重，2：购买广告位，3：优化搜索
     * @return
     */
    String recharge(MerchantRecharge merchantRecharge);
}
