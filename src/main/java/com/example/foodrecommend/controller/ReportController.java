package com.example.foodrecommend.controller;


import com.example.foodrecommend.beans.Report;
import com.example.foodrecommend.service.ReportService;
import com.example.foodrecommend.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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


    /**
     * 新增举报记录
     *
     * @param report 实体对象
     * @return 新增结果
     */
    @ApiOperation("新增举报记录")
    @PostMapping("/insert")
    public R insert(@RequestBody Report report) {
        boolean save = this.reportService.save(report);
        return success(save);
    }

    /**
     * 查看一条举报记录的详细情况
     *
     * @param id 举报表id
     * @return 举报表对象
     */
    @ApiOperation("查看举报记录")
    @GetMapping("/check/{id}")
    public R check(@PathVariable String id) {
        // 查询举报表对象
        Report report = this.reportService.getById(id);
        return success(report);
    }

    /**
     * 管理员进行审核，提交要扣除的分数
     *
     * @param id 举报表id
     * @param star 扣除的分数
     * @return 成功/失败
     */
    @ApiOperation("管理员审核结果")
    @GetMapping("/audit/{id}/{star}")
    public R audit(@PathVariable String id, @PathVariable Integer star) {
        Boolean reviewResult = this.reportService.adminToReview(id, star);
        if (!reviewResult){
            return failure(500,"审核失败");
        }
        return success("审核成功");
    }

}

