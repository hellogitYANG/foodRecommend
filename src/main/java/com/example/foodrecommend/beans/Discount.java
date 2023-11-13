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
 * @TableName discount
 */
@TableName(value ="discount")
@Data
public class Discount implements Serializable {
    /**
     * 折扣表ID
     */
    @TableId(value = "id")
    private String id;

    /**
     * 菜品ID
     */
    @TableField(value = "food_id")
    private String foodId;

    /**
     * 打折（0.6）
     */
    @TableField(value = "discount_num")
    private Double discountNum;

    /**
     * 开始时间
     */
    @TableField(value = "start_time")
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    private Date endTime;

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