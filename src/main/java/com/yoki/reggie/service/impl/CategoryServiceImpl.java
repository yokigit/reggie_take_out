package com.yoki.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yoki.reggie.common.CustomException;
import com.yoki.reggie.common.Result;
import com.yoki.reggie.mapper.CategoryMapper;
import com.yoki.reggie.pojo.Category;
import com.yoki.reggie.pojo.Dish;
import com.yoki.reggie.pojo.Setmeal;
import com.yoki.reggie.service.CategoryService;
import com.yoki.reggie.service.DishService;
import com.yoki.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-13 11:59
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public boolean remove(Long id) {
        //查询当前分类（id）关联的菜品数
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        //如果有关联的菜品，抛出一个业务异常
        if (count1 > 0) {
            throw new CustomException("当前分类关联了菜品，不能删除！");
        }

        //插叙当前分类（id）关联的套餐数
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        //如果有关联的套餐，抛出一个业务异常
        if (count2 > 0) {
            throw new CustomException("当前分类关联了套餐，不能删除");
        }

        //如果没有关联的菜品或套餐，则正常删除该分类
        return super.removeById(id);

    }


}
