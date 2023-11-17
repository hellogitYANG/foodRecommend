package com.example.foodrecommend.dingshi;

import com.example.foodrecommend.beans.Merchant;
import com.example.foodrecommend.beans.User;
import com.example.foodrecommend.service.UserBansService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class taskReview {

    @Autowired
    private UserBansService userBansService;

    /**
     * 检查是否有用户针对单一商家好评频率过高并对其进行封禁
     */
    @Scheduled(cron = "0 0 0 * * ?") // 每天 0 点执行
    public void checkUserCommentFrequency() {
        List<User> suspiciousUsers = userBansService.commentFrequencyCommentBlock();
        if (suspiciousUsers != null) log.info("发现可疑用户：{}", suspiciousUsers);
    }

    /**
     * 检查是否有商家好评频率过高且用户相似度过高，提示管理员进行处理
     */
    @Scheduled(cron = "0 0 0 * * ?") // 每天 0 点执行
    public void checkMerchantCommentFrequency() {
        List<Merchant> suspiciousMerchants = userBansService.commentFrequencyAndUserSimilarityCommentBlock();
        if (suspiciousMerchants != null) log.error("发现可疑商家，请及时处理：{}", suspiciousMerchants);

    }

    /**
     * 解禁用户
     */
    @Scheduled(cron = "0 0 0 * * ?") // 每天 0 点执行
    public void checkForUnblockedUsers() {
        List<User> userList = userBansService.unlockingUsers();
        if (userList != null) log.info("解封用户列表：{}", userList);

    }

}
