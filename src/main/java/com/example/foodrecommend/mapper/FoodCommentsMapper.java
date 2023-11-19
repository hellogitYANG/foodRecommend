package com.example.foodrecommend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.foodrecommend.beans.FoodComments;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 86176
* @description 针对表【food_comments】的数据库操作Mapper
* @createDate 2023-11-13 00:14:57
* @Entity generator.beans.FoodComments
*/
@Mapper
public interface FoodCommentsMapper extends BaseMapper<FoodComments> {

    /**
     * 查询同一用户给同一评星的订单数大于n的商家
     *
     * @return 订单信息
     */
    @Select("SELECT merchant_id, COUNT(DISTINCT id) AS food_comments_count " +
            "FROM food_comments " +
            "WHERE comment_star IS NOT NULL " +
            "GROUP BY merchant_id, user_id, comment_star " +
            "HAVING COUNT(DISTINCT id) > #{n}")
    List<FoodComments> findMerchantsWithMoreThan10Orders(int n);
}




