package com.example.foodrecommend.utils;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.foodrecommend.beans.FoodStatsDictionary;
import com.example.foodrecommend.mapper.FoodStatsDictionaryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
@Component
public class CosineSimilarity {
    @Autowired
    FoodStatsDictionaryMapper foodStatsDictionaryMapper;
    private static Map<String, Double> foodStatsDictionaries = new ConcurrentHashMap<>();
    @PostConstruct
    public void init() {
        updateDictionary();
    }
    @Scheduled(fixedRate = 60000) // 定时更新，这里的fixedRate是更新的周期，单位为毫秒,1分钟更新一次
    public void updateDictionary() {
        Map<String, Double> tempMap = new ConcurrentHashMap<>();
        List<FoodStatsDictionary> foodStatsDictionaryList = foodStatsDictionaryMapper.selectList(null);
        for (FoodStatsDictionary f : foodStatsDictionaryList) {
            tempMap.put(f.getName(), f.getWeight());
        }
        foodStatsDictionaries = tempMap; // 替换旧的字典
    }
    // 计算余弦相似度
    public static double calculateCosineSimilarity(Map<String, String> vector1, Map<String, String> vector2,Map<String, Integer> tasteDictionary ) {
        // 获取两个向量的共同键
        Set<String> commonKeys = getCommonKeys(vector1, vector2);

        // 记录用户口味和当前菜品口味键（key）,值(口味的id)
        Map<String, Integer> vector1Numeric = convertToNumericVector(vector1, commonKeys,tasteDictionary);
        Map<String, Integer> vector2Numeric = convertToNumericVector(vector2, commonKeys,tasteDictionary);

        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;

        // 遍历共同键
        for (String key : commonKeys) {
            // 检查键是否存在于两个字典中
            if (vector1Numeric.get(key)==null || vector2Numeric.get(key)==null) {
                // 如果任何一个字典中不存在键，跳过当前迭代
                continue;
            }
            int value1 = vector1Numeric.get(key);
            int value2 = vector2Numeric.get(key);

            // 获取属性的权重
            double weight = foodStatsDictionaries.getOrDefault(key, 1.0); // 如果键没有对应的权重，则默认为1.0

            // 计算点积，同时考虑权重
            dotProduct += weight * value1 * value2;
            magnitude1 += weight * Math.pow(value1, 2);
            magnitude2 += weight * Math.pow(value2, 2);
        }

        // 避免除以零
        if (magnitude1 == 0.0 || magnitude2 == 0.0) {
            return 0.0;
        }

        // 计算余弦相似度
        return dotProduct / (Math.sqrt(magnitude1) * Math.sqrt(magnitude2));
    }

    // 获取两个向量的共同键
    private static Set<String> getCommonKeys(Map<String, String> vector1, Map<String, String> vector2) {
        Set<String> keys1 = vector1.keySet();
        Set<String> keys2 = vector2.keySet();

        // 获取两个向量的共同键，即取两个向量键的交集
        Set<String> commonKeys = new HashSet<>(keys1);
        commonKeys.retainAll(keys2);

        return commonKeys;
    }

    // 将口味转换为独热编码的数值向量
    private static Map<String, Integer> convertToNumericVector(Map<String, String> vector, Set<String> commonKeys,Map<String,Integer> tasteDictionary) {
        Map<String, Integer> numericVector = new HashMap<>();
        for (String key : commonKeys) {//遍历 口味、食材口味、烹饪方式
            String value = vector.get(key);//获取具体口味
            Integer integer = tasteDictionary.get(value);//获取口味独特整型
            numericVector.put(key, integer);
        }
        return numericVector;
    }
}
