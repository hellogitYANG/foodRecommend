package com.example.foodrecommend.utils;

import cn.hutool.json.JSONUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.foodrecommend.beans.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.Map;

import static javax.crypto.Cipher.SECRET_KEY;

public class GetUserInfoByToken {
    static String mima="tuijian666";
    public static User parseToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(mima).parseClaimsJws(token).getBody();

        User user = new User();
        user.setOpenId((String)claims.get("openId"));
        user.setAuthority((Integer) claims.get("authority"));
        user.setIsBan((Integer) claims.get("isBan"));
        user.setPhone((String) claims.get("phone"));
        user.setUserName((String) claims.get("userName"));
        user.setCollectFoodSku((String) claims.get("collect_food_sku"));
        //foodStats是map json字符串
        String foodStatsJson =(String) claims.get("foodStats");

        if (foodStatsJson==null){
            user.setFoodStats(null);
        }else {
            Map<String, Object> foodStatsMap = JSONUtil.parseObj(foodStatsJson);
            user.setFoodStats(foodStatsJson);
        }

        user.setCollectFoodSku((String) claims.get("collectFoodSku"));

        return user;
    }
}
