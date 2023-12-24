package com.example.foodrecommend.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.foodrecommend.beans.Report;
import com.example.foodrecommend.beans.User;
import com.example.foodrecommend.dto.ReportDto;
import com.example.foodrecommend.dto.ReportResponseDto;
import com.example.foodrecommend.interceptor.CheckTokenInterceptor;
import com.example.foodrecommend.service.ReportService;
import com.example.foodrecommend.utils.GetUserInfoByToken;
import com.example.foodrecommend.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

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
@CrossOrigin
public class ReportController {
    /**
     * 服务对象
     */
    @Resource
    private ReportService reportService;

    /**
     * 处理管理员提交的审核结果，扣除商家的分数
     *
     * @param map 举报表ID 和 扣除的分数（star）
     * @return 提交结果
     */
    @ApiOperation("处理管理员提交的审核结果")
    @PostMapping("/submitAuditResult")
    public R submitAuditResult(@RequestBody Map map) {
        return success(this.reportService.handleAuditAndDeductScore(map));
    }

    /**
     * 分页查询所有数据
     *
     * @param page   分页对象
     * @param report 查询实体
     * @return 所有数据
     */
    @ApiOperation("管理端分页查询举报信息")
    @GetMapping("/page")
    public R selectAll(Page<Report> page, Report report) {
//        // 获取Token
//        String token = CheckTokenInterceptor.getToken();
//        User user = GetUserInfoByToken.parseToken(token);
//        // 设置用户ID
//        report.setUserId(user.getOpenId());
        return success(this.reportService.page(page, new QueryWrapper<>(report)));
    }
    @ApiOperation("分页查询举报信息")
    @GetMapping
    public R<IPage<ReportResponseDto>> selectAll(@RequestParam Map<String, Object> params) {
        // 设置用户I
        IPage<ReportResponseDto> page = this.reportService.pageByParams(params);
        return success(null , page);
    }
    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @ApiOperation("通过主键查询单条数据")
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.reportService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param reportDto 实体对象
     * @return 新增结果
     */
    @ApiOperation("新增单条数据")
    @PostMapping
    public R insert(@RequestBody ReportDto reportDto) {
        this.reportService.report(reportDto);

        return success("投诉成功" , null);
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

    @ApiOperation("管理端通过实体类主键修改单条数据")
    @PutMapping("/chuli")
    public R updateByChuli(@RequestBody Report report) {
        return success(this.reportService.updateByChuli(report));
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
}

