package com.example.foodrecommend.beans;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.models.auth.In;
import lombok.Data;

/**
 * 
 * @TableName orders
 */
@TableName(value ="orders")
@Data
public class Orders implements Serializable {
    /**
     * 订单表ID
     */
    @TableId(value = "id")
    private String id;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 用户手机号
     */
    @TableField(value = "user_phone")
    private String userPhone;

    /**
     * 菜品ID
     */
    @TableField(value = "food_sku_id")
    private String foodSkuId;

    /**
     * 菜品名称
     */
    @TableField(value = "food_name")
    private String foodName;

    /**
     * 菜品风味
     */
    @TableField(value = "food_taste")
    private String foodTaste;

    /**
     * 商家ID
     */
    @TableField(value = "merchant_id")
    private String merchantId;

    /**
     * 商家手机号
     */
    @TableField(value = "merchant_phone")
    private String merchantPhone;

    /**
     * 是否刷单，正常:0,疑似刷单:1
     */
    @TableField(value = "is_brush")
    private Integer isBrush;

    /**
     * 下单地址
     */
    @TableField(value = "address")
    private String address;

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

    @TableField(value = "mark")
    private Integer mark;
    /**
     * 父订单id
     */
    @TableField(value = "order_father_id")
    private String orderFatherId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}