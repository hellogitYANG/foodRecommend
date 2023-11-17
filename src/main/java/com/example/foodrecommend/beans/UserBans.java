package com.example.foodrecommend.beans;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户封禁表
 *
 * @TableName user_bans
 */
@TableName(value ="user_bans")
@Data
public class UserBans implements Serializable {

    /**
     * 封禁表ID
     */
    @TableId(value = "id")
    private String id;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 封禁天数
     */
    @TableField(value = "ban_days")
    private Integer banDays;

    /**
     * 封禁开始时间
     */
    @TableField(value = "ban_start_time")
    private LocalDateTime banStartTime;

    /**
     * 封禁结束时间
     */
    @TableField(value = "ban_end_time")
    private LocalDateTime banEndTime;

    /**
     * 被封禁的次数
     */
    @TableField(value = "times_banned")
    private Integer timesBanned;

}
