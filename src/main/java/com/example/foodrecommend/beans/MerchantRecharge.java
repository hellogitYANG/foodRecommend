package com.example.foodrecommend.beans;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 
 * @TableName merchant_recharge
 */
@TableName(value ="merchant_recharge")
@Data
public class MerchantRecharge implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 充值表ID
     */
    @TableId(value = "id")
    private String id;

    /**
     * 商家ID
     */
    @TableField(value = "merchant_id")
    private String merchantId;

    /**
     * 充值类型，1：增加推荐权重，2：购买广告位，3：优化搜索
     */
    @TableField(value = "recharge_type")
    private Integer rechargeType;

    /**
     * 充值金额
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 有效时间（以天为单位）
     */
    @TableField(value = "validity")
    private Integer validity;

    /**
     * 2广告页面图片
     */
    @TableField(value = "img_url")
    private String imgUrl;

    /**
     * 1菜品ID
     */
    @TableField(value = "food_sku_id")
    private String foodSkuId;

    /**
     * 开始时间
     */
    @TableField(value = "start_time")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    private LocalDateTime endTime;

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
}
