package com.example.foodrecommend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.foodrecommend.beans.FoodComments;
import com.example.foodrecommend.beans.Merchant;
import com.example.foodrecommend.beans.User;
import com.example.foodrecommend.beans.UserBans;
import com.example.foodrecommend.mapper.FoodCommentsMapper;
import com.example.foodrecommend.mapper.MerchantMapper;
import com.example.foodrecommend.mapper.UserBansMapper;
import com.example.foodrecommend.mapper.UserMapper;
import com.example.foodrecommend.service.UserBansService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @description 针对表【user_bans】的数据库操作Service实现
 */
@Service
@Slf4j
public class UserBansServiceImpl extends ServiceImpl<UserBansMapper, UserBans> implements UserBansService {
    @Autowired
    private FoodCommentsMapper foodCommentsMapper;
    @Autowired
    private UserBansMapper userBansMapper;
    @Autowired
    private UserMapper userMapper;
    @Resource
    private MerchantMapper merchantMapper;

    // 好评次数阈值，超过这个值就会被BAN
    private static final int GOOD_COMMENT_THRESHOLD = 5;
    // 同一评星数
    private static final int NUMBER_OF_SAME_RATING_STARS = 5;
    // 认为是好评的值
    private static final int GOOD_COMMENT_VALUE = 7;
    // 刷单禁言的天数
    private static final int NUMBER_OF_DAYS_OF_BAN_ON_BRUSHING_ORDERS = 30;
    // 长期封禁天数
    private static final int LONG_TERM_BAN_DAYS = 365 * 5;
    // 封禁状态
    private static final int BAN_STAtUS = 1;
    // 未封禁状态
    private static final int NULL_BAN_STATUS = 0;
    // 定义好评率过高的阈值
    private static final double MAX_POSITIVE_REVIEW_RATE = 0.98;
    // 初始封禁次数
    private static final int NUMBER_OF_INITIAL_LOCKDOWNS = 1;
    // 商家评论数，用来判断是否超过这个次数，如果没超过就不用计算了
    private static final int CURRENT_NUMBER_OF_MERCHANT_COMMENTS = 50;
    // private static final int CURRENT_NUMBER_OF_MERCHANT_COMMENTS = 5; // 测试

    /**
     * 检查是否有用户针对单一商家好评频率过高并对其进行封禁
     * 惩罚条件：1小时内 单一用户对单一商家的好评数超过 5 次
     *
     * @return 可疑用户列表
     */
    public List<User> commentFrequencyCommentBlock() {
        // 从数据库获取评价数据（1小时内）
        // 1.计算日期值
        LocalDateTime startOfDay = LocalDateTime.now().minusHours(1);
        LocalDateTime endOfDay = LocalDateTime.now();
        // 2.查询当日的评价数据
        List<FoodComments> foodComments = foodCommentsMapper.selectList(new LambdaQueryWrapper<FoodComments>()
                .between(FoodComments::getCreateTime, startOfDay, endOfDay));
        // 防重复列表：存储已计算的用户和商家的组合（形式为"userId_merchantId"），避免对同一个用户向同一个商家的重复评价进行多次计算
        List<String> preventDuplication = new ArrayList<>();
        // 封装异常用户
        List<User> suspiciousUsers = new ArrayList<>();
        // 将评价数据分析并审核
        for (FoodComments comment : foodComments) {
            // 生成唯一标识字符串，由“用户ID_商家ID”构成，用于在防重复列表中检查是否已经对该用户和商家组合进行过计算
            String userId = comment.getUserId();
            String merchantId = comment.getMerchantId();
            String str = new StringJoiner("_").add(userId).add(merchantId).toString();
            // 判断是否已经审核
            if (preventDuplication.contains(str)) {
                continue;
            }
            // 开始审核，并将字符串添加进防重复列表中
            preventDuplication.add(str);
            // 获取用户对此商家的好评次数
            Long goodCommentCount = foodCommentsMapper.selectCount(new LambdaQueryWrapper<FoodComments>()
                    .eq(FoodComments::getMerchantId, merchantId)
                    .eq(FoodComments::getUserId, userId)
                    .gt(FoodComments::getCommentStar, GOOD_COMMENT_VALUE));
            // 获取用户对象
            User user = userMapper.selectById(userId);
            // 判断好评次数是否大于阈值
            if (goodCommentCount > GOOD_COMMENT_THRESHOLD) { // 大于
                // 判断是否存在user_bans表中
                UserBans userBans = userBansMapper.selectOne(new LambdaQueryWrapper<UserBans>().eq(UserBans::getUserId, userId));
                // 获取当前时间及封禁结束时间
                LocalDateTime banStartTime = LocalDateTime.now();
                LocalDateTime banEndTime = banStartTime.plusDays(NUMBER_OF_DAYS_OF_BAN_ON_BRUSHING_ORDERS);
                // 如果用户已在user_bans表中，那么更新其封禁信息
                if (userBans != null) {
                    // 判断用户是否已经被封禁了，如果已经被封禁则不进行操作，防止重复封禁
                    if (user.getIsBan() == BAN_STAtUS) {
                        continue;
                    }
                    // 计算封禁次数
                    int timesBanned = userBans.getTimesBanned() + 1;
                    // 如果该用户的封禁次数已经大于3，将封禁5年
                    if (timesBanned > 3) {
                        LocalDateTime fiveYearsLater = banStartTime.plusDays(LONG_TERM_BAN_DAYS);
                        userBans.setBanDays(LONG_TERM_BAN_DAYS);
                        userBans.setBanEndTime(fiveYearsLater);
                    } else {
                        // 否则正常更新信息
                        userBans.setBanDays(NUMBER_OF_DAYS_OF_BAN_ON_BRUSHING_ORDERS);
                        userBans.setBanEndTime(banEndTime);
                    }
                    userBans.setTimesBanned(timesBanned); // 更新它在user_bans表的封禁次数
                    userBans.setBanStartTime(banStartTime);
                    userBansMapper.updateById(userBans);
                } else { // 如果用户不在user_bans表中，那么首次添加该用户的封禁信息
                    userBans = new UserBans();
                    userBans.setUserId(comment.getUserId());
                    userBans.setTimesBanned(NUMBER_OF_INITIAL_LOCKDOWNS); // 初始设置封禁次数为1
                    userBans.setBanDays(NUMBER_OF_DAYS_OF_BAN_ON_BRUSHING_ORDERS);
                    userBans.setBanStartTime(banStartTime);
                    userBans.setBanEndTime(banEndTime);
                    userBansMapper.insert(userBans); // 将数据添加到user_bans表中
                }
                // 更新用户表的ban属性为 1（封禁）
                user.setIsBan(BAN_STAtUS);
                userMapper.updateById(user);

                suspiciousUsers.add(user);
            }
        }
        // 打印处理结果
        if (!suspiciousUsers.isEmpty()) log.info("发现可疑用户：{}", suspiciousUsers);
        return suspiciousUsers;
    }

    /**
     * 检查是否有商家好评频率过高且用户相似度过高，提示管理员进行处理
     * 惩罚条件：1小时内 评论数超过50次、好评率超过98% 并且 同一用户给同一评星的订单数大于5的商家
     *
     * @return 可疑商家列表
     */
    public List<Merchant> commentFrequencyAndUserSimilarityCommentBlock() {
        // 查询全部商家
        List<Merchant> merchants = merchantMapper.selectList(null);
        // 计算日期值
        LocalDateTime startOfDay = LocalDateTime.now().minusHours(1);
        LocalDateTime endOfDay = LocalDateTime.now();
        // 1.得到好评率高的商家
        List<Merchant> highGoodRateMerchants = new ArrayList<>();
        for (Merchant merchant : merchants) {
            // 获取商家的评价数
            Long commentsCount = foodCommentsMapper.selectCount(new LambdaQueryWrapper<FoodComments>()
                    .eq(FoodComments::getMerchantId, merchant.getId())
                    .between(FoodComments::getCreateTime, startOfDay, endOfDay));
            // 判断商家评价数是否超过50次，如果没超过就不计算了
            if (commentsCount < CURRENT_NUMBER_OF_MERCHANT_COMMENTS) {
                continue;
            }
            // 获取商家的好评数（前一天到当前时间的）
            Long goodCommentCount = foodCommentsMapper.selectCount(new LambdaQueryWrapper<FoodComments>()
                    .eq(FoodComments::getMerchantId, merchant.getId())
                    .gt(FoodComments::getCommentStar, GOOD_COMMENT_VALUE)
                    .between(FoodComments::getCreateTime, startOfDay, endOfDay));
            // 计算好评率
            double goodCommentRate = (goodCommentCount * 1.0) / commentsCount;
            // 判断是否大于阈值
            if (goodCommentRate >= MAX_POSITIVE_REVIEW_RATE) {
                // 计入列表中
                highGoodRateMerchants.add(merchant);
            }
        }

        // 2. 查询用户相似度高的商家
        List<FoodComments> merchantsWithProblematicUserComments = foodCommentsMapper.findMerchantsWithMoreThan10Orders(NUMBER_OF_SAME_RATING_STARS);
        List<Merchant> highSimilarityMerchants = new ArrayList<>();
        merchantsWithProblematicUserComments.forEach(merchant -> {
            highSimilarityMerchants.add(merchantMapper.selectById(merchant.getMerchantId()));
        });
        // 3. 取交集,获取同时满足两条条件的商家
        List<Merchant> suspiciousMerchants = highGoodRateMerchants.stream()
                .filter(highSimilarityMerchants::contains)
                .collect(Collectors.toList());
        // 打印处理结果
        if (!suspiciousMerchants.isEmpty()) log.error("发现可疑商家，请及时处理：{}", suspiciousMerchants);
        return suspiciousMerchants;
    }

    /**
     * 解禁用户
     * 解禁条件：当前时间 大于 解禁时间
     *
     * @return 解禁的用户对象
     */
    public List<User> unlockingUsers() {
        // 查询封禁表中已过期的记录
        // 获取当前时间
        LocalDateTime currentTime = LocalDateTime.now();
        List<UserBans> userBansList = userBansMapper.selectList(new LambdaQueryWrapper<UserBans>().lt(UserBans::getBanEndTime, currentTime));
        List<User> userList = new ArrayList<>();
        for (UserBans userBans : userBansList) {
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getId, userBans.getUserId())
                    .eq(User::getIsBan, BAN_STAtUS));
            if (user != null) {
                // 根据用户id更新用户表把封禁状态改为未封禁
                user.setIsBan(NULL_BAN_STATUS);
                userMapper.updateById(user);
                userList.add(user);
            }
        }
        // 打印处理结果
        if (!userList.isEmpty()) log.info("解封用户列表：{}", userList);
        return userList;
    }
}



