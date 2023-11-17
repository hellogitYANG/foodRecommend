package com.example.foodrecommend.service;

import com.example.foodrecommend.beans.Report;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 86176
* @description 针对表【report】的数据库操作Service
* @createDate 2023-11-13 00:14:57
*/
public interface ReportService extends IService<Report> {

    /**
     * 管理员进行审核，提交要扣除的分数
     *
     * @param id 举报表id
     * @param star 扣除的分数
     * @return 成功/失败
     */
    Boolean adminToReview(String id, Integer star);

}
