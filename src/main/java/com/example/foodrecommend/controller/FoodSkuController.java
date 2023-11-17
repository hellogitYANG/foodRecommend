package com.example.foodrecommend.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.foodrecommend.beans.FoodSku;
import com.example.foodrecommend.service.FoodSkuService;
import com.example.foodrecommend.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

import static com.example.foodrecommend.utils.R.failure;
import static com.example.foodrecommend.utils.R.success;

/**
 * (FoodSku)表控制层
 *
 * @author makejava
 * @since 2023-11-13 00:25:53
 */
@RestController
@Api(value = "菜品表", tags = "菜品表")
@RequestMapping("foodSku")
public class FoodSkuController {
    /**
     * 服务对象
     */
    @Resource
    private FoodSkuService foodSkuService;

    /**
     * 分页查询所有数据
     *
     * @param page    分页对象
     * @param foodSku 查询实体
     * @return 所有数据
     */
    @ApiOperation("分页查询折扣信息")
    @GetMapping
    public R selectAll(Page<FoodSku> page, FoodSku foodSku) {
        return success(this.foodSkuService.page(page, new QueryWrapper<>(foodSku)));
    }

    @ApiOperation("根据商家ID分页查询菜品信息")
    @GetMapping("/selectFoodByMerchantID/{merchantId}")
    public R selectFoodByMerchantID(Page<FoodSku> page, @PathVariable String merchantId) {
        return success(this.foodSkuService.page(page, new QueryWrapper<FoodSku>().eq("merchant_id", merchantId)));
    }

    /**
     * 通过销量和评分推荐菜品
     *
     * @param n 推荐菜品的数量（非必须的，默认值为10）
     * @return 推荐的菜品列表
     */
    @ApiOperation("通过销量和评分推荐菜品")
    @ApiImplicitParam(name = "n", value = "推荐菜品的数量", required = false, defaultValue = "10")
    @GetMapping("/recommend/{n}")
    public R recommend(@PathVariable Integer n) {
        List<FoodSku> foodSkus = foodSkuService.recommendBySalesAndScore(n);
        if (foodSkus == null){
            return failure(500, "推荐失败");
        }
        return success(foodSkus);
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
        return success(this.foodSkuService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param foodSku 实体对象
     * @return 新增结果
     */
    @ApiOperation("新增单条数据")
    @PostMapping
    public R insert(@RequestBody FoodSku foodSku) {
        return success(this.foodSkuService.save(foodSku));
    }

    /**
     * 修改数据
     *
     * @param foodSku 实体对象
     * @return 修改结果
     */
    @ApiOperation("通过实体类主键修改单条数据")
    @PutMapping
    public R update(@RequestBody FoodSku foodSku) {
        return success(this.foodSkuService.updateById(foodSku));
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
        return success(this.foodSkuService.removeByIds(idList));
    }
}

