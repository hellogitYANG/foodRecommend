package com.example.foodrecommend;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.foodrecommend.beans.FoodSku;
import com.example.foodrecommend.beans.FoodStatsDictionary;
import com.example.foodrecommend.beans.Merchant;
import com.example.foodrecommend.mapper.FoodSkuMapper;
import com.example.foodrecommend.mapper.FoodStatsDictionaryMapper;
import com.example.foodrecommend.mapper.MerchantMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qiniu.util.Json;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
class FoodRecommendApplicationTests {
    @Autowired
    FoodSkuMapper foodSkuMapper;

    @Autowired
    MerchantMapper merchantMapper;
    @Autowired
    FoodStatsDictionaryMapper foodStatsDictionaryMapper;
    @Test
    void contextLoads() throws Exception {
        //搜索所有菜品
        List<FoodSku> foodSkus = foodSkuMapper.selectList(null);
        //获取所有类型,如果不存在此类型就设置为此类型的其他
        List<Map<String, Object>> results = foodStatsDictionaryMapper.selectMaps(new QueryWrapper<FoodStatsDictionary>().select("stats_level").groupBy("stats_level"));
        //遍历菜品，如果菜品有类型不包含，添加此类型其他
        for (FoodSku skus : foodSkus) {
            Map<String, Object> map = JSONUtil.toBean(skus.getFoodStats(), Map.class);

            for (Map<String, Object> result : results) {
                String statsLevel = (String)result.get("stats_level");
                //如果不包含此类型，设置为此类型的其他
                if(!map.containsKey(statsLevel)){
                    map.put(statsLevel,"其他"+"("+statsLevel+")");
                }
            }
            String parseStats = JSONUtil.parse(map).toString();
            skus.setFoodStats(parseStats);
            foodSkuMapper.updateById(skus);
        }
    }

    @Test
    void contextLoads2()  {
        //搜索所有菜品
        List<Merchant> merchants = merchantMapper.selectList(null);

        for(Merchant m : merchants) {
            String foodImg = m.getMerchantImg();

            String updatedUrl = foodImg.replace("s4xxacvii.hn-bkt.clouddn.com", "s6mrs5dkj.sabkt.gdipper.com");

            m.setMerchantImg(updatedUrl);
            merchantMapper.updateById(m);
        }

    }

}
