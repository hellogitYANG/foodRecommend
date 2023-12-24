package com.example.foodrecommend.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.foodrecommend.beans.FoodComments;
import com.example.foodrecommend.dto.FoodCommentsResponseDto;
import com.example.foodrecommend.service.FoodCommentsService;
import com.example.foodrecommend.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static com.example.foodrecommend.utils.R.failure;
import static com.example.foodrecommend.utils.R.success;

/**
 * (FoodComments)表控制层
 *
 * @author makejava
 * @since 2023-11-13 00:25:53
 */
@RestController
@Api(value = "评价表",tags = "评价表")
@RequestMapping("foodComments")
public class FoodCommentsController  {
    /**
     * 服务对象
     */
    @Resource
    private FoodCommentsService foodCommentsService;

    /**
     * 分页查询所有数据
     *
     * @return 所有数据
     */
    @ApiOperation("分页查询评论信息")
    @GetMapping
    public R<IPage<FoodCommentsResponseDto>> selectAll(@RequestParam Map<String , Object> params) {
        IPage<FoodCommentsResponseDto> page = this.foodCommentsService.pageByParams(params);
        return success( null, page);
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
        return success(this.foodCommentsService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param foodComments 实体对象
     * @return 新增结果
     */
    @ApiOperation("通过订单ID添加单条数据,需要order_id 、comment_content、comment_star")
    @PostMapping
    public R insert(@RequestBody FoodComments foodComments,@RequestHeader String token) {
        int i = foodCommentsService.InsertByFullOrder(foodComments,token);
        if (i>0){
            return success(null);
        }
        return failure(1001,"添加失败");
    }



    /**
     * 修改数据
     *
     * @param foodComments 实体对象
     * @return 修改结果
     */
    @ApiOperation("通过实体类主键修改单条数据")
    @PutMapping
    public R update(@RequestBody FoodComments foodComments) {
        return success(this.foodCommentsService.updateById(foodComments));
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
        return success(this.foodCommentsService.removeByIds(idList));
    }
}

