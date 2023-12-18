package com.example.foodrecommend.beans;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName food_comments
 */
@TableName(value ="food_comments")
@Data
public class FoodComments implements Serializable {
    /**
     * 评价表ID
     */
    @TableId(value = "id")
    private String id;

    /**
     * 订单ID
     */
    @TableField(value = "order_id")
    private String orderId;

    /**
     * 父订单id
     */
    @TableField(value = "order_father_id")
    private String orderFatherId;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private String userId;

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
     * 评论内容
     */
    @TableField(value = "comment_content")
    private String commentContent;

    /**
     * 评论星级
     */
    @TableField(value = "comment_star")
    private Integer commentStar;

    /**
     * 是否刷单，正常:0,疑似刷单:1
     */
    @TableField(value = "is_brush")
    private Integer isBrush;

    /**
     * 评价时的位置，小程序获取当前位置
     */
    @TableField(value = "xy")
    private String xy;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}