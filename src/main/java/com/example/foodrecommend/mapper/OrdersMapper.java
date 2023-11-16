package com.example.foodrecommend.mapper;

import com.example.foodrecommend.beans.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 86176
* @description 针对表【orders】的数据库操作Mapper
* @createDate 2023-11-13 00:14:57
* @Entity generator.beans.Orders
*/
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

}




