package com.example.foodrecommend.controller;

import com.example.foodrecommend.beans.MerchantRecharge;
import com.example.foodrecommend.service.MerchantRechargeService;
import com.example.foodrecommend.utils.R;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "商家充值表",tags = "商家充值表")
@RequestMapping("merchantRecharge")
public class MerchantRechargeController {

    @Autowired
    private MerchantRechargeService merchantRechargeService;

    /**
     * 商家进行充值
     *
     * @param merchantRecharge 商家充值的信息，应该有商家id，充值类型，充值金额，有效时间
     *                         充值类型，1：增加推荐权重，2：购买广告位，3：优化搜索
     * @return
     */
    public R recharge(@RequestBody MerchantRecharge merchantRecharge){
        return R.success(merchantRechargeService.recharge(merchantRecharge));
    }

    /**
     * 查询全部广告投放的商家
     * @return
     */
    public R queryMerchantsForAdvertisingPlacement(){
        return null;
    }



}
