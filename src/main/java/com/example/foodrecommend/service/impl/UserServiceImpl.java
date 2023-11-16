package com.example.foodrecommend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.foodrecommend.beans.User;
import com.example.foodrecommend.dingshi.SendMessage;
import com.example.foodrecommend.mapper.ReportMapper;
import com.example.foodrecommend.service.UserService;
import com.example.foodrecommend.mapper.UserMapper;
import com.example.foodrecommend.utils.GetUserInfoByToken;
import com.example.foodrecommend.utils.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.foodrecommend.utils.R.failure;
import static com.example.foodrecommend.utils.R.success;

/**
* @author 86176
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-11-13 00:14:57
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ObjectMapper objectMapper;
    @Resource
    UserMapper userMapper;
    @Autowired
    ReportMapper reportMapper;

    @Override
    public R login(String jscode, String code) throws JsonProcessingException {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=wx417de6da67c6718e&secret=913c2e22bd7247bacba325cc4681ad9f&grant_type=authorization_code&js_code=" + jscode;
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
        Map<String, Object> resultMap = objectMapper.readValue(forEntity.getBody(), Map.class);

        String openid = (String) resultMap.get("openid");
        System.out.println(openid);
        System.out.println(code);

        User user = userMapper.selectOne(new QueryWrapper<User>().eq("open_id",openid));


        if (user != null) {
            String token = getToken(user);

            return success(token,user);
        } else {
            //如果没有就查出手机号并创建用户
            //查手机号
            String token = SendMessage.token;
            String phoneurl = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=" + token;
            //请求头
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("Content-Type", "application/json; charset=UTF-8");
            //请求参数
            Map map = new HashMap<String, String>();
            map.put("code", code);
            HttpEntity httpEntity = new HttpEntity<>(map, requestHeaders);
            //获取手机信息
            ResponseEntity<Map> response2 = restTemplate.postForEntity(phoneurl, httpEntity, Map.class);
            Map<String, Object> phoneInfo = (Map<String, Object>) response2.getBody().get("phone_info");
            //新增信息
            User newuser = new User(openid, "傻逼", "123456", "特殊口味", 0, 0, String.valueOf(phoneInfo.get("phoneNumber")));
            int insert = userMapper.insert(newuser);
            if (insert > 0) {
                token = getToken(newuser);
                return success(token,newuser);
            }
        }
        return failure(1001, "登录错误");
    }

    @Override
    public User getUserInfoByToken(String token) {
        return GetUserInfoByToken.parseToken(token);
    }

    public static String getToken(User user) {
        //使用jwt规则生成token字符串
        JwtBuilder builder = Jwts.builder();

        HashMap<String, Object> map = new HashMap<>();
        map.put("openId", user.getOpenId());
        map.put("authority", user.getAuthority());
        map.put("isBan", user.getIsBan());
        map.put("phone", user.getPhone());
        map.put("foodStats", user.getFoodStats());
        map.put("collectFoodSku", user.getCollectFoodSku());

        String token = builder.setSubject("token")                     //主题，就是token中携带的数据
                .setIssuedAt(new Date())                            //设置token的生成时间
                .setId(user.getOpenId())                                //设置用户id为token  id
                .setClaims(map)                                     //map中可以存放用户的角色权限信息
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) //设置token过期时间,一天
                .signWith(SignatureAlgorithm.HS256, "tuijian666")     //设置加密方式和加密密码
                .compact();
        return token;
    }
}



