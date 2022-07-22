package com.yoki.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yoki.reggie.common.Result;
import com.yoki.reggie.dto.DishDto;
import com.yoki.reggie.mapper.DishMapper;
import com.yoki.reggie.pojo.Dish;
import com.yoki.reggie.pojo.DishFlavor;
import com.yoki.reggie.service.DishFlavorService;
import com.yoki.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-14 00:43
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品信息
        this.save(dishDto);

        //菜品id
        Long dishId = dishDto.getId();

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        //设置每个口味对应的菜品
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }

        //保存菜品口味
        dishFlavorService.saveBatch(flavors);
    }

    public DishDto getWithFlavor(Long id) {
        DishDto dishDto = new DishDto();
        Dish dish = this.getById(id);
        BeanUtils.copyProperties(dish, dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品
        this.updateById(dishDto);

        //设置每个flavor对应的dish
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishDto.getId());
        }

        //删除原来的口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //更新口味
        dishFlavorService.saveBatch(flavors);
    }

    @Transactional
    @Override
    public void removeWithFlavor(Long[] ids) {
        //删除菜品
        this.removeByIds(Arrays.asList(ids));

        //删除各个菜品对应的口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId, ids);
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        List<Long> flavorIds = new ArrayList<>();
        for (DishFlavor flavor : flavors) {
            flavorIds.add(flavor.getId());
        }
        dishFlavorService.removeByIds(flavorIds);
    }

}
