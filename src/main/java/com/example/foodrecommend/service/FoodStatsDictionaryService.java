package com.example.foodrecommend.service;

import com.example.foodrecommend.beans.FoodStatsDictionary;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.foodrecommend.utils.TreeNode;

import java.util.ArrayList;

/**
* @author 86176
* @description 针对表【food_stats_dictionary】的数据库操作Service
* @createDate 2023-11-13 00:14:57
*/
public interface FoodStatsDictionaryService extends IService<FoodStatsDictionary> {

    ArrayList<TreeNode> getDictionaryTree();
}
