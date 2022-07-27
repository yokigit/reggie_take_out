package com.yoki.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yoki.reggie.common.Result;
import com.yoki.reggie.dto.SetmealDto;
import com.yoki.reggie.pojo.Category;
import com.yoki.reggie.pojo.Setmeal;
import com.yoki.reggie.pojo.SetmealDish;
import com.yoki.reggie.service.CategoryService;
import com.yoki.reggie.service.SetmealDishService;
import com.yoki.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-15 16:21
 */
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public Result<Page<SetmealDto>> page(@RequestParam("page") int page, @RequestParam("pageSize") int pageSize, String name) {
        //查询套餐分页
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        queryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        setmealService.page(setmealPage, queryWrapper);

        //分页构造器
        Page<SetmealDto> setmealDtoPage = new Page<>();
        //拷贝除records属性的其他属性，因为records没有categoryName，需要单独处理
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");
        List<Setmeal> setmealList = setmealPage.getRecords();
        List<SetmealDto> setmealDtoList = new ArrayList<>();
        for (Setmeal setmeal : setmealList) {
            //拷贝
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);

            //设置categoryName
            Category category = categoryService.getById(setmeal.getCategoryId());
            setmealDto.setCategoryName(category.getName());

            //加入list
            setmealDtoList.add(setmealDto);
        }
        //设置正确的records
        setmealDtoPage.setRecords(setmealDtoList);

        return Result.success(setmealDtoPage);
    }

    //优化：使用缓存注解@CacheEvict，添加套餐时若缓存中有套餐数据，则全部删除
    @CacheEvict(value = "setmealCache", allEntries = true)
    @PostMapping("")
    public Result<String> add(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return Result.success("添加套餐成功");
    }

    @GetMapping("/{id}")
    public Result<SetmealDto> get(@PathVariable("id") Long id) {
        SetmealDto setmealDto = setmealService.getWithDish(id);
        return Result.success(setmealDto);
    }

    //优化：使用缓存注解@CacheEvict，删除套餐时若缓存中有套餐数据，则全部删除
    @CacheEvict(value = "setmealCache", allEntries = true)
    @PutMapping("")
    public Result<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithDish(setmealDto);
        return Result.success("修改套餐成功");
    }

    //优化：使用缓存注解@CacheEvict，修改套餐销售状态时若缓存中有套餐数据，则全部删除
    @CacheEvict(value = "setmealCache", allEntries = true)
    @PostMapping("/status/{status}")
    public Result<String> changeStatus(@PathVariable("status") Integer status, @RequestParam("ids") Long[] ids) {
        //根据id数组查询Setmeal
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);

        //更新销售状态
        for (Setmeal setmeal : setmealList) {
            setmeal.setStatus(status);
        }

        //保存
        boolean update = setmealService.updateBatchById(setmealList);
        String str = status == 1 ? "起售" : "停售";
        if (update) {
            return Result.success(str + "成功");
        } else {
            return Result.error(str + "失败");
        }
    }

    //优化：使用缓存注解@CacheEvict，若缓存中有套餐数据，则全部删除
    @CacheEvict(value = "setmealCache", allEntries = true)
    @DeleteMapping("")
    public Result<String> delete(@RequestParam("ids") Long[] ids) {
        setmealService.deleteWithDish(ids);
        return Result.success("删除套餐成功");
    }

    //优化：使用缓存注解@Cacheable, 查询某分类套餐时若缓存中有套餐数据则直接返回，否则经过数据查询和将返回结果加入缓存中
    @Cacheable(value = "setmealCache", key = "#categoryId+'_'+#status")
    @GetMapping("/list")
    public Result<List<Setmeal>> list(Long categoryId, Integer status) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId, categoryId);
        queryWrapper.eq(Setmeal::getStatus, status);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);
        return Result.success(setmealList);
    }

    @GetMapping("/dish/{id}")
    public Result<SetmealDto> dish(@PathVariable("id") Long setmealId) {
        Setmeal setmeal = setmealService.getById(setmealId);
        if (setmeal != null) {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);

            Category category = categoryService.getById(setmeal.getCategoryId());
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }

            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
            List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);
            setmealDto.setSetmealDishes(setmealDishList);

            return Result.success(setmealDto);
        }
        return Result.error("获取套餐内容失败");
    }
}
