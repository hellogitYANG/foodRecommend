package com.example.foodrecommend.dto;

import com.example.foodrecommend.beans.Report;
import lombok.*;

import java.util.List;

@Data
public class ReportDto {
    /**
     * 图片路径列表
     */
    private List<String> images;

    private String desc;

    private String merchantId;

    private String merchantIdEd;
}
