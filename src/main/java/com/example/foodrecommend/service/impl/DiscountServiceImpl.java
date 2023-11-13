package com.example.foodrecommend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.foodrecommend.beans.Discount;
import com.example.foodrecommend.service.DiscountService;
import com.example.foodrecommend.mapper.DiscountMapper;
import org.springframework.stereotype.Service;

/**
* @author 86176
* @description 针对表【discount】的数据库操作Service实现
* @createDate 2023-11-13 00:14:57
*/
@Service
public class DiscountServiceImpl extends ServiceImpl<DiscountMapper, Discount>
    implements DiscountService{

}




