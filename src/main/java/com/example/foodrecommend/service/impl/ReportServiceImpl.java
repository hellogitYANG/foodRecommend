package com.example.foodrecommend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.foodrecommend.beans.Merchant;
import com.example.foodrecommend.beans.Report;
import com.example.foodrecommend.mapper.ReportMapper;
import com.example.foodrecommend.service.MerchantService;
import com.example.foodrecommend.service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author 86176
 * @description 针对表【report】的数据库操作Service实现
 * @createDate 2023-11-13 00:14:57
 */
@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements ReportService {

    @Resource
    private MerchantService merchantService;
    @Resource
    private ReportMapper reportMapper;

    /**
     * 管理员进行审核，提交要扣除的分数
     *
     * @param map 举报表ID 和 扣除的分数（star）
     * @return 成功/失败
     */
    @Override
    public Boolean handleAuditAndDeductScore(Map map) {
        // 举报表id
        String id = (String) map.get("id");
        // 扣除的分数
        double star = (double) map.get("star");
        // 查询举报表对象
        Report report = this.getById(id);
        // 对商家进行惩罚
        // 1.降分
        Merchant merchant = merchantService.getById(report.getMerchantIdEd());
        // 当前商家分数
        double currentMerchantStar = merchant.getStar();
        // 扣除分数
        if (currentMerchantStar < star) {
            return false;
        }
        merchant.setStar(currentMerchantStar - star);
        // 更新数据库
        boolean update = merchantService.updateById(merchant);

        // TODO 2.罚款 -- 未完成

        return update;
    }

    @Override
    @Transactional
    public int updateByChuli(Report report) {
        //设置已处理
        report.setStatus(1);
        //对应商家进行扣分
        Merchant merchant = merchantService.getById(report.getMerchantIdEd());
        if(merchant.getStar()<report.getDeduction()){
            merchant.setStar(0.0);
        }else {
            merchant.setStar(merchant.getStar()-report.getDeduction());
        }
        merchantService.updateById(merchant);
        return reportMapper.updateById(report);
    }

}




