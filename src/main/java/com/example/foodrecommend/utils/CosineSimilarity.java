package com.example.foodrecommend.utils;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.foodrecommend.beans.FoodStatsDictionary;
import com.example.foodrecommend.mapper.FoodStatsDictionaryMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class CosineSimilarity {

    // 计算余弦相似度
    public static double calculateCosineSimilarity(Map<String, String> vector1, Map<String, String> vector2,Map<String, Integer> tasteDictionary ) {
        // 获取两个向量的共同键
        Set<String> commonKeys = getCommonKeys(vector1, vector2);

        // 将口味转换为向量表示
        Map<String, Integer> vector1Numeric = convertToNumericVector(vector1, commonKeys,tasteDictionary);
        Map<String, Integer> vector2Numeric = convertToNumericVector(vector2, commonKeys,tasteDictionary);

        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;

        // 遍历共同键
        for (String key : commonKeys) {
            int value1 = vector1Numeric.get(key);
            int value2 = vector2Numeric.get(key);

            // 计算点积
            dotProduct += value1 * value2;
            magnitude1 += Math.pow(value1, 2);
            magnitude2 += Math.pow(value2, 2);
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
