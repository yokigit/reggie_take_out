package com.yoki.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yoki.reggie.common.Result;
import com.yoki.reggie.dto.DishDto;
import com.yoki.reggie.pojo.Category;
import com.yoki.reggie.pojo.Dish;
import com.yoki.reggie.pojo.DishFlavor;
import com.yoki.reggie.service.CategoryService;
import com.yoki.reggie.service.DishFlavorService;
import com.yoki.reggie.service.DishService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-14 19:18
 */
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping("")
    public Result<String> add(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return Result.success("新增菜品成功");
    }

    @GetMapping("/page")
    public Result<Page<DishDto>> page(int page, int pageSize, String name) {
        //分页构造器
        Page<Dish> dishPage = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //根据名称查询
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        //根据sort降序查询
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行查询
        dishService.page(dishPage, queryWrapper);

        //分页构造器
        Page<DishDto> dishDtoPage = new Page<>();
        //拷贝,records没有categoryName字段，需要单独处理
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");
        //处理records
        List<DishDto> dishDtoList = new ArrayList<>();
        List<Dish> records = dishPage.getRecords();
        for (Dish dish : records) {
            DishDto dishDto = new DishDto();
            //拷贝
            BeanUtils.copyProperties(dish, dishDto);

            //设置categoryName
            Long categoryId = dish.getCategoryId();
            Category category = categoryService.getById(categoryId);
            dishDto.setCategoryName(category.getName());

            //加入list
            dishDtoList.add(dishDto);
        }
        //设置正确的records
        dishDtoPage.setRecords(dishDtoList);

        //返回分页结果
        return Result.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public Result<DishDto> get(@PathVariable("id") Long id) {
        DishDto dishDto = dishService.getWithFlavor(id);
        return Result.success(dishDto);
    }

    @PutMapping("")
    public Result<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return Result.success("修改菜品成功");
    }

    @DeleteMapping("")
    public Result<String> delete(@RequestParam("ids") Long[] ids) {
        dishService.removeWithFlavor(ids);
        return Result.success("删除菜品成功");
    }

    @PostMapping("/status/{status}")
    public Result<String> changeStatus(@RequestParam("ids") Long[] ids, @PathVariable("status") Integer status) {

        //根据id数组获取菜品
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids);
        List<Dish> dishList = dishService.list(queryWrapper);

        //更新status
        for (Dish dish : dishList) {
            dish.setStatus(status);
        }

        //更新菜品
        boolean update = dishService.updateBatchById(dishList);
        String str = status == 1 ? "起售" : "停售";
        if (update) {
            return Result.success(str + "成功");
        } else {
            return Result.success(str + "失败");
        }
    }

    @GetMapping("/list")
    public Result<List<DishDto>> list(@RequestParam("categoryId") Long categoryId) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId, categoryId);
        List<Dish> dishList = dishService.list(queryWrapper);
        List<DishDto> dishDtoList = new ArrayList<>();
        for (Dish dish : dishList) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);

            Category category = categoryService.getById(categoryId);
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }

            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId, dish.getId());
            List<DishFlavor> flavorList = dishFlavorService.list(queryWrapper1);
            dishDto.setFlavors(flavorList);

            dishDtoList.add(dishDto);
        }
        return Result.success(dishDtoList);
    }
}
