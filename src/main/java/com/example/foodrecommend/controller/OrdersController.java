package com.example.foodrecommend.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.foodrecommend.beans.FoodSku;
import com.example.foodrecommend.beans.Orders;
import com.example.foodrecommend.service.OrdersService;
import com.example.foodrecommend.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

import static com.example.foodrecommend.utils.R.success;

/**
 * (Orders)表控制层
 *
 * @author makejava
 * @since 2023-11-13 00:25:53
 */
@RestController
@Api(value = "订单表",tags = "订单表")
@RequestMapping("orders")
public class OrdersController  {
    /**
     * 服务对象
     */
    @Resource
    private OrdersService ordersService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param orders 查询实体
     * @return 所有数据
     */
    @ApiOperation("分页查询折扣信息")
    @GetMapping
    public R selectAll(Page<Orders> page, Orders orders) {
        return success(this.ordersService.page(page, new QueryWrapper<>(orders)));
    }

    @ApiOperation("根据用户openid获取用户订单信息接口")
    @GetMapping("/selectOrderByUserOpenId/{openId}")
    public R selectOrderByUserOpenId(Page<Orders> page, @PathVariable String openId) {
        return success(this.ordersService.page(page,new QueryWrapper<Orders>().eq("user_id",openId)));
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
        return success(this.ordersService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param orders 实体对象
     * @return 新增结果
     */
    @ApiOperation("新增单条数据")
    @PostMapping
    public R insert(@RequestBody Orders orders, @RequestHeader("token") String token) {
        int i = this.ordersService.insertOrder(orders, token);
        return success(null);
    }

    /**
     * 修改数据
     *
     * @param orders 实体对象
     * @return 修改结果
     */
    @ApiOperation("通过实体类主键修改单条数据")
    @PutMapping
    public R update(@RequestBody Orders orders) {
        return success(this.ordersService.updateById(orders));
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
        return success(this.ordersService.removeByIds(idList));
    }
}

