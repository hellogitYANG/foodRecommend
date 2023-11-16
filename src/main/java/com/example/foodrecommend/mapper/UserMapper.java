package com.example.foodrecommend.mapper;

import com.example.foodrecommend.beans.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 86176
* @description 针对表【user】的数据库操作Mapper
* @createDate 2023-11-13 00:14:57
* @Entity generator.beans.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




