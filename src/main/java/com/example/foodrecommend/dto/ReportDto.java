package com.example.foodrecommend.dto;

import com.example.foodrecommend.beans.Report;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportDto extends Report {
    /**
     * 图片路径列表
     */
    private List<String> imgs;
}
