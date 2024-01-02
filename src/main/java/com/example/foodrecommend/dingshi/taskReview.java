package com.example.foodrecommend.dingshi;


import com.example.foodrecommend.dto.MerchantBansDto;
import com.example.foodrecommend.service.UserBansService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 定时任务审查：
 * - 1.用户针对单一商家好评频率过高，封禁用户3天，超过3次封禁5年
 * - 2.商家好评频率过高且用户相似度过高，提示管理员进行处理
 * - 3.解禁用户
 */
@Component
@Slf4j
public class TaskReview {

    @Autowired
    private UserBansService userBansService;

    /**
     * 检查是否有用户针对单一商家好评频率过高并对其进行封禁
     */
    @Scheduled(cron = "0 0 * * * ?") // 每一小时执行一次
    public void checkUserCommentFrequency() {
        userBansService.commentFrequencyCommentBlock();
    }

    /**
     * 检查是否有商家好评频率过高且用户相似度过高，提示管理员进行处理
     */
    @Scheduled(cron = "0 * * * * ?") // 每分钟执行一次
    public void checkMerchantCommentFrequency() {
        List<MerchantBansDto> suspiciousMerchants = userBansService.commentFrequencyAndUserSimilarityCommentBlock();
    }

    /**
     * 解禁用户
     */
    @Scheduled(cron = "0 0 * * * ?") // 每一小时执行一次
    public void checkForUnblockedUsers() {
        userBansService.unlockingUsers();
    }

}
