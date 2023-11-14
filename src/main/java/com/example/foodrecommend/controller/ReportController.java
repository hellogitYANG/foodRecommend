package com.example.foodrecommend.controller;


import com.example.foodrecommend.beans.Report;
import com.example.foodrecommend.service.ReportService;
import com.example.foodrecommend.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static com.example.foodrecommend.utils.R.success;

/**
 * (Report)表控制层
 *
 * @author makejava
 * @since 2023-11-13 00:25:53
 */
@RestController
@Api(value = "举报表",tags = "举报表")
@RequestMapping("report")
public class ReportController  {
    /**
     * 服务对象
     */
    @Resource
    private ReportService reportService;


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
     * @param id 举报单id
     * @return 举报单对象
     */
    @ApiOperation("人工审核窗口")
    @GetMapping("/auditing/{id}")
    public R auditing(@PathVariable String id) {
        Report report = this.reportService.getById(id);
        return success(report);
    }

}

