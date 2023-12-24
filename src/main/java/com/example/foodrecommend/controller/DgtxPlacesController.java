package com.example.foodrecommend.controller;



import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.foodrecommend.beans.DgtxPlaces;
import com.example.foodrecommend.service.DgtxPlacesService;
import com.example.foodrecommend.utils.R;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

import static com.example.foodrecommend.utils.R.success;

/**
 * (DgtxPlaces)表控制层
 *
 * @author makejava
 * @since 2023-12-24 20:48:06
 */
@RestController
@Api(value = "地区表",tags = "地区表")
@RequestMapping("dgtxPlaces")
@CrossOrigin
public class DgtxPlacesController  {
    /**
     * 服务对象
     */
    @Resource
    private DgtxPlacesService dgtxPlacesService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param dgtxPlaces 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<DgtxPlaces> page, DgtxPlaces dgtxPlaces) {
        return success(this.dgtxPlacesService.page(page, new QueryWrapper<>(dgtxPlaces)));
    }

    //树形结构
    @GetMapping("/placesTree")
    public R getTree() {
        List<DgtxPlaces> list = this.dgtxPlacesService.list();
        //新写法
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        //转换器
        List<Tree<String>> treeNodes = TreeUtil.build(list, "0", treeNodeConfig,(treeNode, tree) -> {
            tree.setId(String.valueOf(treeNode.getId()));
            tree.setParentId(String.valueOf(treeNode.getParentId()));
            tree.setName(treeNode.getCname());
        });
        return success(treeNodes);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.dgtxPlacesService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param dgtxPlaces 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody DgtxPlaces dgtxPlaces) {
        return success(this.dgtxPlacesService.save(dgtxPlaces));
    }

    /**
     * 修改数据
     *
     * @param dgtxPlaces 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody DgtxPlaces dgtxPlaces) {
        return success(this.dgtxPlacesService.updateById(dgtxPlaces));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.dgtxPlacesService.removeByIds(idList));
    }
}

