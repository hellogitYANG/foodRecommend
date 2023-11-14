package com.example.foodrecommend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.foodrecommend.beans.User;
import com.example.foodrecommend.service.UserService;
import com.example.foodrecommend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
* @author 86176
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-11-13 00:14:57
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    @Autowired
    RestTemplate restTemplate;

}




