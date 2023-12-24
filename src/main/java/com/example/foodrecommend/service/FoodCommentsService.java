package com.example.foodrecommend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.foodrecommend.beans.FoodComments;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.foodrecommend.dto.FoodCommentsResponseDto;

import java.util.Map;

/**
 * @author 86176
 * @description 针对表【food_comments】的数据库操作Service
 * @createDate 2023-11-13 00:14:57
 */
public interface FoodCommentsService extends IService<FoodComments> {
    public int InsertByFullOrder(FoodComments foodComments,String token);

    IPage<FoodCommentsResponseDto> pageByParams(Map<String, Object> params);
}
