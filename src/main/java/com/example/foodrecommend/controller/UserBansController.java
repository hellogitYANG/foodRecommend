package com.example.foodrecommend.controller;

import com.example.foodrecommend.service.UserBansService;
import com.example.foodrecommend.utils.R;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "用户封禁表", tags = "用户封禁表")
@RequestMapping("userBans")
public class UserBansController {

    @Autowired
    private UserBansService userBansService;

    /**
     * 检查是否有用户针对单一商家好评频率过高并对其进行封禁
     *
     * @return 可疑用户列表
     */
    @GetMapping("/commentFrequencyCommentBlock")
    public R checkCommentFrequencyAndBlock() {
        return R.success(userBansService.commentFrequencyCommentBlock());
    }

    /**
     * 查询好评频率过高且用户相似度过高的商家
     *
     * @return 可疑商家列表
     */
    @GetMapping("/commentFrequencyAndUserSimilarityCommentBlock")
    public R queryCommentFrequencyAndUserSimilarity() {
        return R.success(userBansService.commentFrequencyAndUserSimilarityCommentBlock());
    }

    /**
     * 解禁用户
     *
     * @return 解禁的用户对象列表
     */
    @GetMapping("/unlockingUsers")
    public R unlockUsers() {
        return R.success(userBansService.unlockingUsers());
    }

}
