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
import org.springframework.data.redis.core.RedisTemplate;
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

    @Autowired
    RedisTemplate redisTemplate;

    @PostMapping("")
    public Result<String> add(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        //优化：如果增加菜品，且redis缓存中含有该菜品对应的分类的菜品数据，则清空该数据
        Long categoryId = dishDto.getCategoryId();
        if (redisTemplate.opsForHash().get("dishDtoList", String.valueOf(categoryId)) != null) {
            redisTemplate.opsForHash().delete("dishDtoList", String.valueOf(categoryId));
        }
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

        //优化：如果某类菜品更新了，且redis缓存中含有该类的菜品数据，则清空该数据
        Long categoryId = dishDto.getCategoryId();
        if (redisTemplate.opsForHash().get("dishDtoList", String.valueOf(categoryId)) != null) {
            redisTemplate.opsForHash().delete("dishDtoList", String.valueOf(categoryId));
        }
        return Result.success("修改菜品成功");
    }

    @DeleteMapping("")
    public Result<String> delete(@RequestParam("ids") Long[] ids) {
        //优化：如果删除的菜品对应的分类在redis缓存中有该分类下的菜品数据，则清空该数据
        for (Long dishId : ids) {
            Long categoryId = dishService.getById(dishId).getCategoryId();
            if (redisTemplate.opsForHash().get("dishDtoList", String.valueOf(categoryId)) != null) {
                redisTemplate.opsForHash().delete("dishDtoList", String.valueOf(categoryId));
            }
        }
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
        //优化：如果改变销售状态的菜品对应的分类在redis缓存中有该分类下的菜品数据，则清空该数据
        for (Long dishId : ids) {
            Long categoryId = dishService.getById(dishId).getCategoryId();
            if (redisTemplate.opsForHash().get("dishDtoList", String.valueOf(categoryId)) != null) {
                redisTemplate.opsForHash().delete("dishDtoList", String.valueOf(categoryId));
            }
        }

        String str = status == 1 ? "起售" : "停售";
        if (update) {
            return Result.success(str + "成功");
        } else {
            return Result.success(str + "失败");
        }
    }

    @GetMapping("/list")
    public Result<List<DishDto>> list(@RequestParam("categoryId") Long categoryId) {
        List<DishDto> dishDtoList = null;
        //优化：从redis中获取菜品数据 ( 数据模型：dishDtoList:(categoryId, 数据) )
        dishDtoList = (List<DishDto>) redisTemplate.opsForHash().get("dishDtoList", String.valueOf(categoryId));
        //如果存在该菜品数据
        if (dishDtoList != null) {
            //直接返回，不查询数据库
            return Result.success(dishDtoList);
        }

        //如果不存在，查询数据库
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId, categoryId);
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        List<Dish> dishList = dishService.list(queryWrapper);
        dishDtoList = new ArrayList<>();
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
        //将查询到的数据存在redis中，数据模型hash
        redisTemplate.opsForHash().put("dishDtoList", String.valueOf(categoryId), dishDtoList);

        //再返回
        return Result.success(dishDtoList);
    }
}
