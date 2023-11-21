package com.example.foodrecommend.beans;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName user
 */
@TableName(value ="user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    public User(String openId, String userName, String phone, String foodStats, Integer authority, Integer isBan, String collectFoodSku, String phoneLocation) {
        this.openId = openId;
        this.userName = userName;
        this.phone = phone;
        this.foodStats = foodStats;
        this.authority = authority;
        this.isBan = isBan;
        this.collectFoodSku = collectFoodSku;
        this.phoneLocation=phoneLocation;
    }

    /**
     * 用户表ID
     */
    @TableId(value = "id")
    private String id;

    /**
     * 微信用户唯一id
     */
    @TableField(value = "open_id")
    private String openId;

    /**
     * 用户名称
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 手机号码
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 喜欢的菜品属性（口味，地方），Json存储
     */
    @TableField(value = "food_stats")
    private String foodStats;

    /**
     * 用户身份，普通用户:0;商家:1;管理员:2
     */
    @TableField(value = "authority")
    private Integer authority;

    /**
     * 是否封禁, 不封禁:0,封禁:1
     */
    @TableField(value = "is_ban")
    private Integer isBan;

    /**
     * 收藏菜品Id，逗号分隔例如1,2,3
     */
    @TableField(value = "collect_food_sku")
    private String collectFoodSku;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(value = "phone_location")
    private String phoneLocation;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}