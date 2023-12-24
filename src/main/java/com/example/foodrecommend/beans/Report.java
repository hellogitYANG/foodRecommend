package com.example.foodrecommend.beans;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName report
 */
@TableName(value ="report")
@Data
public class Report implements Serializable {
    /**
     * 举报表ID
     */
    @TableId(value = "id")
    private String id;

    /**
     * 被举报商家ID
     */
    @TableField(value = "merchant_id_ed")
    private String merchantIdEd;

    /**
     * 举报人（用户）
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 举报人（商家）
     */
    @TableField(value = "merchant_id")
    private String merchantId;

    /**
     * 举报文字
     */
    @TableField(value = "proof_text")
    private String proofText;

    /**
     * 0：待处理，1：已处理
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 扣分
     */
    @TableField(value = "deduction")
    private Double deduction;

    /**
     * 证据图片链接
     */
    @TableField(value = "proof_img_url")
    private String proofImgUrl;

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