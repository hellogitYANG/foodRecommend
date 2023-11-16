package com.example.foodrecommend.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.foodrecommend.beans.Discount;
import com.example.foodrecommend.service.DiscountService;
import com.example.foodrecommend.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

import static com.example.foodrecommend.utils.R.success;

/**
 * (Discount)表控制层
 *
 * @author makejava
 * @since 2023-11-13 00:25:52
 */
@RestController
@Api(value = "折扣表",tags = "折扣表")
@RequestMapping("discount")
public class DiscountController  {
    /**
     * 服务对象
     */
    @Resource
    private DiscountService discountService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param discount 查询实体
     * @return 所有数据
     */
    @GetMapping
    @ApiOperation("分页查询折扣信息")
    public R selectAll(Page<Discount> page, Discount discount) {
        return success(this.discountService.page(page, new QueryWrapper<>(discount)));
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
        return success(this.discountService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param discount 实体对象
     * @return 新增结果
     */
    @ApiOperation("新增单条数据")
    @PostMapping
    public R insert(@RequestBody Discount discount) {
        return success(this.discountService.save(discount));
    }

    /**
     * 修改数据
     *
     * @param discount 实体对象
     * @return 修改结果
     */
    @ApiOperation("通过实体类主键修改单条数据")
    @PutMapping
    public R update(@RequestBody Discount discount) {
        return success(this.discountService.updateById(discount));
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
        return success(this.discountService.removeByIds(idList));
    }
}

