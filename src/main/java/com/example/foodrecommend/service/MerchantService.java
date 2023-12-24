package com.example.foodrecommend.service;

import com.example.foodrecommend.beans.Merchant;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 86176
* @description 针对表【merchant】的数据库操作Service
* @createDate 2023-11-13 00:14:57
*/
public interface MerchantService extends IService<Merchant> {
    //根据商家用户名查商家信息
    public Merchant selectMerchant(String username);

    List<Merchant> getMerchantByIds(List<String> merchantIds);
}
