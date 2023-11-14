package com.example.foodrecommend.controller;


import com.example.foodrecommend.beans.Merchant;
import com.example.foodrecommend.beans.Report;
import com.example.foodrecommend.service.MerchantService;
import com.example.foodrecommend.service.ReportService;
import com.example.foodrecommend.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static com.example.foodrecommend.utils.R.failure;
import static com.example.foodrecommend.utils.R.success;

/**
 * (Report)表控制层
 *
 * @author makejava
 * @since 2023-11-13 00:25:53
 */
@RestController
@Api(value = "举报表", tags = "举报表")
@RequestMapping("report")
public class ReportController {
    /**
     * 服务对象
     */
    @Resource
    private ReportService reportService;
    @Resource
    private MerchantService merchantService;


    /**
     * 新增数据
     *
     * @param report 实体对象
     * @return 新增结果
     */
    @ApiOperation("添加举报记录")
    @PostMapping
    public R insert(@RequestBody Report report) {
        boolean save = this.reportService.save(report);
        return success(save);
    }

    /**
     * 人工审核
     *
     * @param id 举报表id
     * @return 举报表对象
     */
    @ApiOperation("管理员审核窗口")
    @GetMapping("/audit/{id}")
    public R auditWindow(@ApiParam(value = "举报表 ID", required = true) @PathVariable String id) {
        // 查询举报表对象
        Report report = this.reportService.getById(id);
        return success(report);
    }

    @ApiOperation("管理员审核结果")
    @PutMapping("/punishment")
    public R auditResults(
            @ApiParam(value = "举报表 ID", required = true) @RequestParam String id,
            @ApiParam(value = "扣除的分数", required = true) @RequestParam Integer star) {
        // 查询举报表对象
        Report report = this.reportService.getById(id);
        // 对商家进行惩罚
        // 1.降分
        Merchant merchant = merchantService.getById(report.getMerchantIdEd());
        // 当前商家分数
        Integer currentMerchantStar = merchant.getStar();
        // 扣除分数
        if (currentMerchantStar < star){
            return failure(400, "扣除分数比商家当前分数高");
        }
        merchant.setStar(currentMerchantStar - star);
        // 更新数据库
        merchantService.updateById(merchant);
        // TODO 2.罚款
        return success(merchant);
    }

}

