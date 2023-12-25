package com.example.foodrecommend.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
public class GetPhoneInfo {
    private static final String HOST = "https://ali-mobile.showapi.com";
    private static final String PATH = "/6-1";

    // 你的AppCode
    private static final String APP_CODE = "3381d747b0a54996bb7727dc62eeb0de";

    public static String getMobileLocation(String phoneNumber) {
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "APPCODE " + APP_CODE);

        // 构建URI
        URI uri = URI.create(HOST + PATH + "?num=" + phoneNumber);

        // 构建请求实体
        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, uri);

        // 创建RestTemplate实例
        RestTemplate restTemplate = new RestTemplate();

        try {
            // 发送请求
            ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

            // 解析响应
            String responseBody = responseEntity.getBody();
            // 这里你需要根据实际情况解析JSON字符串，以下是一个简单的假设
            Map<String,Object> map= JSONUtil.parseObj(responseBody);
            Object showapiResBody = map.get("showapi_res_body");
            Map<String, Object> phonemap = BeanUtil.beanToMap(showapiResBody);
            String prov = (String) phonemap.get("prov");
            String city = (String) phonemap.get("city");
            //如果后面包含市，就去掉市
            if (city != null && city.endsWith("市")) {
                city = city.substring(0, city.length() - 1);
            }
            return prov+","+city;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
