package com.example.foodrecommend.dto;

import com.example.foodrecommend.beans.FoodSku;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodSkuDto extends FoodSku {
    // 推荐值 = 菜品销量 * 商家的评分
    private Integer ScoreWeightSales;
}
