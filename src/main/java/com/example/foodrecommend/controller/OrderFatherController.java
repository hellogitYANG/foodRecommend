package com.example.foodrecommend.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.foodrecommend.beans.OrderFather;
import com.example.foodrecommend.beans.User;
import com.example.foodrecommend.interceptor.CheckTokenInterceptor;
import com.example.foodrecommend.service.OrderFatherService;
import com.example.foodrecommend.utils.GetUserInfoByToken;
import com.example.foodrecommend.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

import static com.example.foodrecommend.utils.R.success;

/**
 * (OrderFather)表控制层
 *
 * @author makejava
 * @since 2023-12-11 18:06:55
 */
@RestController
@Api(value = "订单父表",tags = "订单父表")
@RequestMapping("orderFather")
public class OrderFatherController  {
    /**
     * 服务对象
     */
    @Resource
    private OrderFatherService orderFatherService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param orderFather 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<OrderFather> page, OrderFather orderFather) {
        return success(this.orderFatherService.page(page, new QueryWrapper<>(orderFather)));
    }


    @ApiOperation("分页订单信息")
    @PostMapping("/OrderInfo/Page")
    public R selectOrderInfoPage(@RequestBody Page page) {
        String token = CheckTokenInterceptor.getToken();
        User user = GetUserInfoByToken.parseToken(token);
        return success(this.orderFatherService.selectOrderInfoPage(page,user.getOpenId()));
    }
    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.orderFatherService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param orderFather 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody OrderFather orderFather) {
        return success(this.orderFatherService.save(orderFather));
    }

    /**
     * 修改数据
     *
     * @param orderFather 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody OrderFather orderFather) {
        return success(this.orderFatherService.updateById(orderFather));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.orderFatherService.removeByIds(idList));
    }
}

