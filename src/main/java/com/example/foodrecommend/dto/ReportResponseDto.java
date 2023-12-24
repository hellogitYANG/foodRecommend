package com.example.foodrecommend.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.foodrecommend.beans.Merchant;
import com.example.foodrecommend.beans.User;
import lombok.Data;

import java.util.Date;

@Data
public class ReportResponseDto {
    /**
     * 举报表ID
     */
    private String id;


    /**
     * 举报文字
     */
    private String proofText;


    /**
     * 扣分
     */
    private Double deduction;

    /**
     * 证据图片链接
     */
    private String proofImgUrl;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 0：待处理，1：已处理
     */
    private Integer status;

    /**
     * 被举报商家ID
     */
    private Merchant merchantEd;

    /**
     * 举报人（用户）
     */
    private User user;

    /**
     * 举报人（商家）
     */
    private Merchant merchant;

}
