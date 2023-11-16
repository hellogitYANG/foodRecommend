package com.example.foodrecommend.service;

import com.example.foodrecommend.beans.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.foodrecommend.utils.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.RequestHeader;

/**
* @author 86176
* @description 针对表【user】的数据库操作Service
* @createDate 2023-11-13 00:14:57
*/
public interface UserService extends IService<User> {
    public R login(String jscode, String code) throws JsonProcessingException;
    //通过token获取用户信息
    public User getUserInfoByToken(String token);
}
