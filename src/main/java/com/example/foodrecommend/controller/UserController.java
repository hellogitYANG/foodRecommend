package com.example.foodrecommend.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.foodrecommend.beans.FoodSku;
import com.example.foodrecommend.beans.User;
import com.example.foodrecommend.interceptor.CheckTokenInterceptor;
import com.example.foodrecommend.service.UserService;
import com.example.foodrecommend.utils.GetUserInfoByToken;
import com.example.foodrecommend.utils.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

import static com.example.foodrecommend.utils.R.success;

/**
 * (User)表控制层
 *
 * @author makejava
 * @since 2023-11-13 00:25:53
 */
@RestController
@CrossOrigin
@Api(value = "用户表",tags = "用户表")
@RequestMapping("user")
public class UserController  {
    /**
     * 服务对象
     */
    @Resource
    private UserService userService;


    @ApiOperation("登录")
    @GetMapping("/login")
    public R login(String js_code,String code) {
        try {
            return userService.login(js_code,code);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @ApiOperation("登录")
    @PostMapping("/loginByTest")
    public R loginByTest(String username,String password) {
            return userService.loginByTest(username,password);
    }

    @ApiOperation("校验token是否过期接口")
    @GetMapping("/check")
    public R userTokencheck(@RequestHeader("token") String token){
        //如果能进来成功了
        return  success(null);
    }

    @ApiOperation("通过token获取用户信息接口")
    @GetMapping("/getUserInfoByToken")
    public R getUserInfoByToken(@RequestHeader("token") String token){
        //如果能进来成功了
        return  success(userService.getUserInfoByToken(token));
    }

    @ApiOperation("用户添加修改收藏商品")
    @GetMapping("/switchCollectFoodSku")
    public R switchCollectFoodSku(String foodSkuId){
        String token = CheckTokenInterceptor.getToken();
        User user = GetUserInfoByToken.parseToken(token);
        //如果能进来成功了
        return  success(userService.addCollectFoodSku(user,foodSkuId));
    }

    @ApiOperation("获取用户收藏菜品，分页")
    @GetMapping("/getUserCollectPage")
    public R getUserCollectPage(Page<FoodSku> page){
        User user = GetUserInfoByToken.parseToken(CheckTokenInterceptor.getToken());
        //如果能进来成功了
        return  success(userService.getUserCollectPage(page,user));
    }
    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param user 查询实体
     * @return 所有数据
     */
    @ApiOperation("分页查询折扣信息")
    @GetMapping
    public R selectAll(Page<User> page, User user) {
        return success(this.userService.page(page, new QueryWrapper<>(user)));
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
        System.out.println(this.userService.getById(id));
        return success(this.userService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param user 实体对象
     * @return 新增结果
     */
    @ApiOperation("新增单条数据")
    @PostMapping
    public R insert(@RequestBody User user) {
        return success(this.userService.save(user));
    }

    /**
     * 修改数据
     *
     * @param user 实体对象
     * @return 修改结果
     */
    @ApiOperation("通过实体类主键修改单条数据")
    @PutMapping
    public R update(@RequestBody User user) {
        return success(this.userService.updateById(user));
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
        return success(this.userService.removeByIds(idList));
    }
}

