package com.example.foodrecommend.beans;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName dgtx_places
 */
@TableName(value ="dgtx_places")
@Data
public class DgtxPlaces implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @TableField(value = "parent_id")
    private Integer parentId;

    /**
     * 
     */
    @TableField(value = "cname")
    private String cname;

    /**
     * 
     */
    @TableField(value = "ctype")
    private Integer ctype;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}