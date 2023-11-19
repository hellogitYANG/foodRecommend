package generator.beans;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName user_behavior
 */
@TableName(value ="user_behavior")
@Data
public class UserBehavior implements Serializable {
    /**
     * 
     */
    @TableId(value = "id")
    private Integer id;

    /**
     * 用户openId
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 行为：浏览:0
     */
    @TableField(value = "behavior")
    private Integer behavior;

    /**
     * 
     */
    @TableField(value = "food_sku_id")
    private String foodSkuId;

    /**
     * 
     */
    @TableField(value = "merchant_id")
    private String merchantId;

    /**
     * 
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 
     */
    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}