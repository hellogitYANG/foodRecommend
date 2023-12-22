package com.example.foodrecommend.service;

import com.example.foodrecommend.beans.Report;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @author 86176
 * @description 针对表【report】的数据库操作Service
 * @createDate 2023-11-13 00:14:57
 */
public interface ReportService extends IService<Report> {

    /**
     * 处理管理员提交的审核结果，扣除商家的分数
     *
     * @param map 举报表ID 和 扣除的分数（star）
     * @return 成功/失败
     */
    Boolean handleAuditAndDeductScore(Map map);

    int updateByChuli(Report report);
}
