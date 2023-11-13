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
 * @TableName merchant
 */
@TableName(value ="merchant")
@Data
public class Merchant implements Serializable {
    /**
     * 商家表ID
     */
    @TableId(value = "id")
    private String id;

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
     * 是否刷单, 正常:0,疑似刷单:1
     */
    @TableField(value = "is_brush")
    private Integer isBrush;

    /**
     * 商家评分
     */
    @TableField(value = "star")
    private Integer star;

    /**
     * 商家头像链接
     */
    @TableField(value = "merchant_img")
    private String merchantImg;

    /**
     * 店铺总销量
     */
    @TableField(value = "sales_num")
    private Integer salesNum;

    /**
     * 经纬度
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