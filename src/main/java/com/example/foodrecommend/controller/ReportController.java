package com.example.foodrecommend.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.foodrecommend.beans.Report;
import com.example.foodrecommend.service.ReportService;
import com.example.foodrecommend.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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
     * 分页查询所有数据
     *
     * @param page   分页对象
     * @param report 查询实体
     * @return 所有数据
     */
    @ApiOperation("分页查询举报信息")
    @GetMapping
    public R selectAll(Page<Report> page, Report report) {
        return success(this.reportService.page(page, new QueryWrapper<>(report)));
    }

    /**
     * 新增数据
     *
     * @param report 实体对象
     * @return 新增结果
     */
    @ApiOperation("新增单条数据")
    @PostMapping
    public R insert(@RequestBody Report report) {
        return success(this.reportService.save(report));
    }

    /**
     * 修改数据
     *
     * @param report 实体对象
     * @return 修改结果
     */
    @ApiOperation("通过实体类主键修改单条数据")
    @PutMapping
    public R update(@RequestBody Report report) {
        return success(this.reportService.updateById(report));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @ApiOperation("根据主键集合删除数据")
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.reportService.removeByIds(idList));
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
     * @param id   举报表id
     * @param star 扣除的分数
     * @return 成功/失败
     */
    @ApiOperation("管理员审核结果")
    @GetMapping("/audit/{id}/{star}")
    public R audit(@PathVariable String id, @PathVariable Integer star) {
        Boolean reviewResult = this.reportService.adminToReview(id, star);
        if (!reviewResult) {
            return failure(500, "审核失败");
        }
        return success("审核成功");
    }

}

