package com.yoki.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yoki.reggie.common.BaseContext;
import com.yoki.reggie.common.Result;
import com.yoki.reggie.pojo.ShoppingCart;
import com.yoki.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-19 15:33
 */
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        //获取userId
        Long userId = BaseContext.getCurrentId();

        //查询当前菜品或套餐是否在购物车中
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        queryWrapper.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId());
        queryWrapper.eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);
        //如果在购物车中，则更新number
        if (shoppingCart1 != null) {
            shoppingCart1.setNumber(shoppingCart1.getNumber() + 1);
            shoppingCartService.updateById(shoppingCart1);
            return Result.success(shoppingCart1);
        }

        //如果不在，将number设置为1
        shoppingCart.setNumber(1);
        //设置userId
        shoppingCart.setUserId(userId);
        //设置创建时间
        shoppingCart.setCreateTime(LocalDateTime.now());
        //保存
        boolean save = shoppingCartService.save(shoppingCart);
        if (save) {
            return Result.success(shoppingCart);
        }
        return Result.error("添加购物车失败");
    }

    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        return Result.success(shoppingCartList);
    }

    @DeleteMapping("clean")
    public Result<String> clear() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        boolean remove = shoppingCartService.remove(queryWrapper);
        if (remove) {
            return Result.success("清空购物车成功");
        }
        return Result.error("清空购物车失败");
    }

    @PostMapping("/sub")
    public Result<String> sub(@RequestBody ShoppingCart shoppingCart) {
        Long userId = BaseContext.getCurrentId();
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        queryWrapper.eq(dishId != null, ShoppingCart::getDishId, dishId);
        queryWrapper.eq(setmealId != null, ShoppingCart::getSetmealId, setmealId);
        ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);
        if (shoppingCart1 != null) {
            if (shoppingCart1.getNumber() > 1) {
                shoppingCart1.setNumber(shoppingCart1.getNumber() - 1);
            } else {
                shoppingCartService.remove(queryWrapper);
            }
            shoppingCartService.updateById(shoppingCart1);
            return Result.success("删减当前商品在购物车中的数量成功");
        }
        return Result.error("删减当前商品在购物车中的数量失败");
    }


}
