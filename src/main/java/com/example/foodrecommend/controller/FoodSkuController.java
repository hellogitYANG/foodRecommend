package com.example.foodrecommend.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.foodrecommend.beans.FoodSku;
import com.example.foodrecommend.beans.FoodStatsDictionary;
import com.example.foodrecommend.beans.Merchant;
import com.example.foodrecommend.beans.User;
import com.example.foodrecommend.interceptor.CheckTokenInterceptor;
import com.example.foodrecommend.mapper.*;
import com.example.foodrecommend.service.FoodSkuService;
import com.example.foodrecommend.utils.GetUserInfoByToken;
import com.example.foodrecommend.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@CrossOrigin
public class FoodSkuController {
    @Autowired
    FoodSkuMapper foodSkuMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    FoodCommentsMapper foodCommentsMapper;
    @Autowired
    FoodStatsDictionaryMapper foodStatsDictionaryMapper;
    @Autowired
    MerchantMapper merchantMapper;
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
    @ApiOperation("分页菜品信息")
    @GetMapping
    public R selectAll(Page<FoodSku> page, FoodSku foodSku) {
        String foodName = foodSku.getName();
        foodSku.setName(null);
        QueryWrapper<FoodSku> foodSkuQueryWrapper = new QueryWrapper<>(foodSku);
        foodSkuQueryWrapper.orderByDesc("create_time");
        if(foodName!=null){
            foodSkuQueryWrapper.like("name",foodName);
        }
        Page<FoodSku> page1 = this.foodSkuService.page(page, foodSkuQueryWrapper);
        List<Map<String, Object>> hashMaps = new ArrayList<>();
        for (FoodSku f : page1.getRecords()) {
            Map<String, Object> map = BeanUtil.beanToMap(f);
            String merchantName = merchantMapper.selectById(f.getMerchantId()).getUserName();
            map.put("merchantName",merchantName);
            hashMaps.add(map);
        }
        Page page2 = new Page();
        BeanUtil.copyProperties(page1,page2);
        page2.setRecords(hashMaps);
        return success(page2);
    }

    @ApiOperation("根据商家ID分页查询菜品信息")
    @GetMapping("/selectFoodByMerchantIDPage/{merchantId}")
    public R selectFoodByMerchantIDPage(Page<FoodSku> page, @PathVariable String merchantId) {
        return success(this.foodSkuService.page(page, new QueryWrapper<FoodSku>().eq("merchant_id", merchantId)));
    }

    @ApiOperation("根据商家ID查询菜品信息")
    @GetMapping("/selectFoodByMerchantID/{merchantId}")
    public R selectFoodByMerchantID(@PathVariable String merchantId) {
        return success(this.foodSkuService.list(new QueryWrapper<FoodSku>().eq("merchant_id", merchantId)));
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

    @ApiOperation("获取猜你想吃")
    @PostMapping("/getYouWantEat")
    public R selectFoodByMerchantID(@RequestBody Page page) {
        String token = CheckTokenInterceptor.getToken();
        User user = GetUserInfoByToken.parseToken(token);
        return success(this.foodSkuService.getYouWantEat(page,user.getOpenId()));
    }

    @ApiOperation("获取家乡美食")
    @GetMapping("/getLocationFood")
    public R getLocationFood(Page<FoodSku> page) {
        String token = CheckTokenInterceptor.getToken();
        User user = GetUserInfoByToken.parseToken(token);
        return success(this.foodSkuService.getLocationFood(page,user.getOpenId()));
    }
    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @ApiOperation("通过主键查询菜品单条数据，包含当前用户有无收藏,顺便添加浏览记录")
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        String openId = GetUserInfoByToken.parseToken(CheckTokenInterceptor.getToken()).getOpenId();
        return success(this.foodSkuService.getSkuInfo(openId,id));
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
        return success(this.foodSkuService.saveFoodSku(foodSku));
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
        String foodStats = foodSku.getFoodStats();
        String[] split = foodStats.split(",");
        HashMap<String, String> mapStats = new HashMap<>();
        for (String s : split) {
            FoodStatsDictionary dictionary = foodStatsDictionaryMapper.selectList(new QueryWrapper<FoodStatsDictionary>().eq("name", s)).get(0);
            mapStats.put(dictionary.getStatsLevel(),dictionary.getName());
        }
        String parseStats = JSONUtil.parse(mapStats).toString();
        foodSku.setFoodStats(parseStats);
        return success(foodSkuMapper.updateById(foodSku));
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

    /**
     * 删除数据

     */
    @ApiOperation("根据主键id删除数据")
    @DeleteMapping("{id}")
    public R deleteById(@PathVariable Serializable id) {
        return success(this.foodSkuService.removeById(id));
    }
}

