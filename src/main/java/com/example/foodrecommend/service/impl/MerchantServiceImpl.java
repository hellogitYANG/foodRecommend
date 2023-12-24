package com.example.foodrecommend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.foodrecommend.beans.Merchant;
import com.example.foodrecommend.service.MerchantService;
import com.example.foodrecommend.mapper.MerchantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author 86176
* @description 针对表【merchant】的数据库操作Service实现
* @createDate 2023-11-13 00:14:57
*/
@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant>
    implements MerchantService{

    @Resource
    MerchantMapper merchantMapper;

    @Override
    public Merchant selectMerchant(String username) {
        Merchant merchant = merchantMapper.selectMerchant(username);
        return merchant;
    }

    @Override
    public List<Merchant> getMerchantByIds(List<String> merchantIds) {
        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Merchant::getId , merchantIds);
        return this.list(queryWrapper);
    }
}




