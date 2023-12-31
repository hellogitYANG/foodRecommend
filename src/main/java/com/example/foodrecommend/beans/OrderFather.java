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
 * @TableName order_father
 */
@TableName(value ="order_father")
@Data
public class OrderFather implements Serializable {
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
     * 是否已评论
     */
    @TableField(value = "is_comment")
    private Integer isComment;
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