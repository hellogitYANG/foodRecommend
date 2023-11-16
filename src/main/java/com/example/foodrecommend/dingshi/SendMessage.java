package com.example.foodrecommend.dingshi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SendMessage {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    RestTemplate restTemplate;
    public static String token="nullToken";
    @Scheduled(initialDelay=5000,fixedRate = 8000000)
    public void gettoken() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl("https://api.weixin.qq.com").path("/cgi-bin/token")
                .queryParam("grant_type","client_credential").queryParam("appid","wx417de6da67c6718e")
                .queryParam("secret","913c2e22bd7247bacba325cc4681ad9f") .build(true);
        URI rootUrl = uriComponents.toUri();
        HttpHeaders headers = new HttpHeaders();

        ResponseEntity<String> response = restTemplate.exchange(rootUrl, HttpMethod.GET, new HttpEntity<String>(headers), String.class);
        Map userMap = objectMapper.readValue(response.getBody(), Map.class);
        token= (String) userMap.get("access_token");
    }


    }


