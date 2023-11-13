package com.example.foodrecommend.beans;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName food_sku
 */
@TableName(value ="food_sku")
@Data
public class FoodSku implements Serializable {
    /**
     * 菜品表ID
     */
    @TableId(value = "id")
    private String id;

    /**
     * 菜品名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 属性，Json存储，例如{"主要口味":"辣味"，"食材口味":"面食"，"地区":"广东"}
     */
    @TableField(value = "food_stats")
    private String foodStats;

    /**
     * 菜品图片
     */
    @TableField(value = "food_img")
    private String foodImg;

    /**
     * 菜品销量
     */
    @TableField(value = "sales_num")
    private Integer salesNum;

    /**
     * 菜品价格
     */
    @TableField(value = "money")
    private BigDecimal money;

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