package com.example.foodrecommend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.foodrecommend.beans.Merchant;
import com.example.foodrecommend.beans.Report;
import com.example.foodrecommend.mapper.ReportMapper;
import com.example.foodrecommend.service.MerchantService;
import com.example.foodrecommend.service.ReportService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author 86176
* @description 针对表【report】的数据库操作Service实现
* @createDate 2023-11-13 00:14:57
*/
@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements ReportService{

    @Resource
    private MerchantService merchantService;

    /**
     * 管理员进行审核，提交要扣除的分数
     *
     * @param id 举报表id
     * @param star 扣除的分数
     * @return 成功/失败
     */
    @Override
    public Boolean adminToReview(String id, Integer star) {
        // 查询举报表对象
        Report report = this.getById(id);
        // 对商家进行惩罚
        // 1.降分
        Merchant merchant = merchantService.getById(report.getMerchantIdEd());
        // 当前商家分数
        Integer currentMerchantStar = merchant.getStar();
        // 扣除分数
        if (currentMerchantStar < star){
            return false;
        }
        merchant.setStar(currentMerchantStar - star);
        // 更新数据库
        boolean update = merchantService.updateById(merchant);

        // TODO 2.罚款 -- 未完成

        return update;
    }

}




