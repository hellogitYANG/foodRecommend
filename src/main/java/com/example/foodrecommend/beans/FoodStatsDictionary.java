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
 * @TableName food_stats_dictionary
 */
@TableName(value ="food_stats_dictionary")
@Data
public class FoodStatsDictionary implements Serializable {
    /**
     * 口味表ID
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 口味名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 属性级别
     */
    @TableField(value = "stats_level")
    private String statsLevel;

    /**
     * 权重
     */
    @TableField(value = "weight")
    private double weight;

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