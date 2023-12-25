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

        String s="1,";
        String[] split = s.split(",");
        for (String s1 : split) {
            System.out.println(s1);
        }
    }

}
