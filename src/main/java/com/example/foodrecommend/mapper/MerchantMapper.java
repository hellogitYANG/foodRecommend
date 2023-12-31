package com.example.foodrecommend.mapper;

import com.example.foodrecommend.beans.Merchant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

/**
* @author 86176
* @description 针对表【merchant】的数据库操作Mapper
* @createDate 2023-11-13 00:14:57
* @Entity generator.beans.Merchant
*/
@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {
    //根据商家用户名查商家信息
    Merchant selectMerchant(@Param("username") String username);
}




