package com.example.foodrecommend.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.foodrecommend.beans.UserBehavior;
import com.example.foodrecommend.service.UserBehaviorService;
import com.example.foodrecommend.utils.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

import static com.example.foodrecommend.utils.R.success;

/**
 * (UserBehavior)表控制层
 *
 * @author makejava
 * @since 2023-11-18 00:35:04
 */
@RestController
@RequestMapping("userBehavior")
public class UserBehaviorController {
    /**
     * 服务对象
     */
    @Resource
    private UserBehaviorService userBehaviorService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param userBehavior 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<UserBehavior> page, UserBehavior userBehavior) {
        return success(this.userBehaviorService.page(page, new QueryWrapper<>(userBehavior)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.userBehaviorService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param userBehavior 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody UserBehavior userBehavior) {
        return success(this.userBehaviorService.save(userBehavior));
    }

    /**
     * 修改数据
     *
     * @param userBehavior 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody UserBehavior userBehavior) {
        return success(this.userBehaviorService.updateById(userBehavior));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.userBehaviorService.removeByIds(idList));
    }
}

