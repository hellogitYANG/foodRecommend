package com.example.foodrecommend.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.foodrecommend.beans.Merchant;
import com.example.foodrecommend.service.MerchantService;
import com.example.foodrecommend.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

import static com.example.foodrecommend.utils.R.success;

/**
 * (Merchant)表控制层
 *
 * @author makejava
 * @since 2023-11-13 00:25:53
 */
@RestController
@Api(value = "商家表",tags = "商家表")
@RequestMapping("merchant")
public class MerchantController  {
    /**
     * 服务对象
     */
    @Resource
    private MerchantService merchantService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param merchant 查询实体
     * @return 所有数据
     */
    @ApiOperation("分页查询商家信息")
    @GetMapping
    public R selectAll(Page<Merchant> page, Merchant merchant) {
        return success(this.merchantService.page(page, new QueryWrapper<>(merchant)));
    }


    @ApiOperation("获取所有商家接口")
    @GetMapping("/seletAllMerchant")
    public R selectAllText(){
        List<Merchant> list = merchantService.list();
        return success(list);
    }

    @ApiOperation("获取单个商家根据商家名字")
    @GetMapping("/selectMerchantByUsername/{username}")
    public R selectMerchantByUsername(@PathVariable String username){
        Merchant merchant = merchantService.selectMerchant(username);
        //mybaitisplus根据查询条件查询一个数据
//        QueryWrapper<Merchant> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("user_name",username);
//        Merchant one = merchantService.getOne(queryWrapper);
        return success(merchant);
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
        return success(this.merchantService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param merchant 实体对象
     * @return 新增结果
     */
    @ApiOperation("新增单条数据")
    @PostMapping
    public R insert(@RequestBody Merchant merchant) {
        return success(this.merchantService.save(merchant));
    }

    /**
     * 修改数据
     *
     * @param merchant 实体对象
     * @return 修改结果
     */
    @ApiOperation("通过实体类主键修改单条数据")
    @PutMapping
    public R update(@RequestBody Merchant merchant) {
        return success(this.merchantService.updateById(merchant));
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
        return success(this.merchantService.removeByIds(idList));
    }
}

