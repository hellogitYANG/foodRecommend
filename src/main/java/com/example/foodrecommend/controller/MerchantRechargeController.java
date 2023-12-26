package com.example.foodrecommend.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.foodrecommend.beans.FoodSku;
import com.example.foodrecommend.beans.MerchantRecharge;
import com.example.foodrecommend.mapper.MerchantMapper;
import com.example.foodrecommend.service.MerchantRechargeService;
import com.example.foodrecommend.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.foodrecommend.utils.R.success;

@RestController
@Api(value = "商家充值表",tags = "商家充值表")
@RequestMapping("merchantRecharge")
@CrossOrigin
public class MerchantRechargeController {

    @Autowired
    private MerchantRechargeService merchantRechargeService;
    @Autowired
    private MerchantMapper merchantMapper;

    /**
     * 查询购买了 增加推荐权重值 的商家
     *
     * @return 商家列表
     */
    @GetMapping("/increased-weight")
    @ApiOperation("查询购买了增加推荐权重值的商家")
    public R listMerchantsWithIncreasedWeight() {
        return success(this.merchantRechargeService.getMerchantsWithIncreasedRecommendationWeight());
    }

    /**
     * 查询购买了 广告位 的商家
     *
     * @return 商家列表
     */
    @GetMapping("/ad-space")
    @ApiOperation("查询购买了广告位的商家")
    public R listMerchantsWithAdSpacePurchase() {
        return success(this.merchantRechargeService.getMerchantsWithAdSpacePurchase());
    }

    /**
     * 查询购买了 优化搜索 的商家
     *
     * @return 商家列表
     */
    @GetMapping("/search-optimization")
    @ApiOperation("查询购买了优化搜索的商家")
    public R listMerchantsWithSearchOptimization() {
        return success(this.merchantRechargeService.getMerchantsWithSearchOptimization());
    }

    /**
     * 分页查询所有数据
     *
     * @param page   分页对象
     * @param merchantRecharge 查询实体
     * @return 所有数据
     */
    @ApiOperation("分页查询折扣信息")
    @GetMapping
    public R selectAll(Page<MerchantRecharge> page, MerchantRecharge merchantRecharge) {
        Page<MerchantRecharge> page1 = this.merchantRechargeService.page(page, new QueryWrapper<>(merchantRecharge).orderByDesc("create_time"));
        List<Map<String, Object>> hashMaps = new ArrayList<>();
        for (MerchantRecharge f : page1.getRecords()) {
            //这个东西把foodskuID转化成Long,本应该是String的，实体类间数据不严谨。到前端精度丢失
            Map<String, Object> map = BeanUtil.beanToMap(f);
            if(!f.getMerchantId().isEmpty()){
                String merchantName = merchantMapper.selectById(f.getMerchantId()).getUserName();
                map.put("merchantName",merchantName);
            }
            hashMaps.add(map);
        }
        Page page2 = new Page();
        page2.setRecords(hashMaps);
        page2.setTotal(page1.getTotal());
        page2.setCurrent(page1.getCurrent());
        return success(page2);
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
        return success(this.merchantRechargeService.getById(id));
    }


    @ApiOperation("查询广告位")
    @GetMapping("/getLunBo")
    public R selectLunBo() {
        return success(this.merchantRechargeService.list(new QueryWrapper<MerchantRecharge>().eq("recharge_type",2)));
    }
    /**
     * 新增数据
     *
     * @param merchantRecharge 实体对象，充值类型，1：增加推荐权重，2：购买广告位，3：优化搜索
     * @return 新增结果
     */
    @ApiOperation("新增单条数据")
    @PostMapping
    public R insert(@RequestBody MerchantRecharge merchantRecharge) {
        return success(this.merchantRechargeService.save(merchantRecharge));
    }

    /**
     * 修改数据
     *
     * @param merchantRecharge 实体对象
     * @return 修改结果
     */
    @ApiOperation("通过实体类主键修改单条数据")
    @PutMapping
    public R update(@RequestBody MerchantRecharge merchantRecharge) {
        return success(this.merchantRechargeService.updateById(merchantRecharge));
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
        return success(this.merchantRechargeService.removeByIds(idList));
    }

    /**
     * 删除数据

     */
    @ApiOperation("根据主键id删除数据")
    @DeleteMapping("{id}")
    public R deleteById(@PathVariable Serializable id) {
        return success(this.merchantRechargeService.removeById(id));
    }

}
