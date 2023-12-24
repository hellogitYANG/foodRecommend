package com.example.foodrecommend.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.foodrecommend.beans.User;
import lombok.Data;

import java.util.Date;

@Data
public class FoodCommentsResponseDto {
    /**
     * 评价表ID
     */
    private String id;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 父订单id
     */
    private String orderFatherId;


    /**
     * 菜品ID
     */
    private String foodSkuId;

    /**
     * 菜品名称
     */
    private String foodName;

    /**
     * 菜品风味
     */
    private String foodTaste;

    /**
     * 商家ID
     */
    private String merchantId;

    /**
     * 商家手机号
     */
    private String merchantPhone;

    /**
     * 评论内容
     */
    private String commentContent;

    /**
     * 评论星级
     */
    private Integer commentStar;

    /**
     * 是否刷单，正常:0,疑似刷单:1
     */
    private Integer isBrush;

    /**
     * 评价时的位置，小程序获取当前位置
     */
    private String xy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    /**
     * 用户ID
     */
    private User user;

}
