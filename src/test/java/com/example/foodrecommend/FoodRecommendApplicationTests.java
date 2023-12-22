package com.example.foodrecommend;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.example.foodrecommend.beans.FoodSku;
import com.example.foodrecommend.mapper.FoodSkuMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qiniu.util.Json;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Iterator;
import java.util.List;

@SpringBootTest
class FoodRecommendApplicationTests {
    @Autowired
    FoodSkuMapper foodSkuMapper;
    @Test
    void contextLoads() throws Exception {
        List<FoodSku> foodSkus = foodSkuMapper.selectList(null);
        ObjectMapper mapper = new ObjectMapper();

        for (FoodSku sku : foodSkus) {
            JsonNode root = mapper.readTree(sku.getFoodStats());
            ObjectNode newNode = mapper.createObjectNode();

            Iterator<String> fieldNames = root.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode fieldValue = root.get(fieldName);

                // 如果字段值是数组，只取第一个元素；如果不是，直接使用该值
                if (fieldValue.isArray() && fieldValue.size() > 0) {
                    newNode.put(fieldName, fieldValue.get(0).asText());
                } else {
                    newNode.put(fieldName, fieldValue.asText());
                }
            }

            sku.setFoodStats(newNode.toString());
            foodSkuMapper.updateById(sku);
        }
    }

}
