package com.example.foodrecommend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.foodrecommend.beans.FoodStatsDictionary;
import com.example.foodrecommend.service.FoodStatsDictionaryService;
import com.example.foodrecommend.mapper.FoodStatsDictionaryMapper;
import com.example.foodrecommend.utils.TreeConverter;
import com.example.foodrecommend.utils.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
* @author 86176
* @description 针对表【food_stats_dictionary】的数据库操作Service实现
* @createDate 2023-11-13 00:14:57
*/
@Service
public class FoodStatsDictionaryServiceImpl extends ServiceImpl<FoodStatsDictionaryMapper, FoodStatsDictionary>
    implements FoodStatsDictionaryService{
    @Autowired
    FoodStatsDictionaryMapper foodStatsDictionaryMapper;
    @Override
    public ArrayList<TreeNode> getDictionaryTree() {
        List<FoodStatsDictionary> foodStatsDictionaries = foodStatsDictionaryMapper.selectList(null);
        Map<String, Object> data = new HashMap<>();
        for (FoodStatsDictionary foodStats : foodStatsDictionaries) {
            //如果已经有这个键，则追加值
            if (data.containsKey(foodStats.getStatsLevel())){
                List<String> newName = (List<String>) data.get(foodStats.getStatsLevel());
                newName.add(foodStats.getName());
                data.put(foodStats.getStatsLevel(),newName);
            }else {
                data.put(foodStats.getStatsLevel(),new ArrayList<>(Arrays.asList(foodStats.getName())));
            }
        }
        ArrayList<TreeNode> treeNodes = TreeConverter.mapToTree(data);

        return treeNodes;
    }
}




