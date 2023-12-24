package com.example.foodrecommend.dto;

import com.example.foodrecommend.beans.FoodSku;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodSkuRecommend extends FoodSku {
    /**
     * 推荐类型
     */
    private String status;

}
