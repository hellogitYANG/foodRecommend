package com.example.foodrecommend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.foodrecommend.beans.Merchant;
import com.example.foodrecommend.beans.User;
import com.example.foodrecommend.beans.UserBans;
import com.example.foodrecommend.dto.MerchantBansDto;

import java.util.List;

/**
* @description 针对表【user_bans】的数据库操作Service
*/
public interface UserBansService extends IService<UserBans> {
    /**
     * 检查是否有用户针对单一商家好评频率过高并对其进行封禁
     *
     * @return 可疑用户列表
     */
    List<User> commentFrequencyCommentBlock();

    /**
     * 检查是否有商家好评频率过高且用户相似度过高，提示管理员进行处理
     *
     * @return 可疑商家列表
     */
    List<MerchantBansDto> commentFrequencyAndUserSimilarityCommentBlock();

    /**
     * 解禁用户
     *
     * @return  解禁的用户对象
     */
    List<User> unlockingUsers();
}
