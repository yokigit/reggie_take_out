package com.yoki.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yoki.reggie.common.Result;
import com.yoki.reggie.pojo.Category;
import com.yoki.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.yoki.reggie.common.Result.success;

/**
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-13 21:43
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result<String> add(@RequestBody Category category) {
        boolean save = categoryService.save(category);
        String type = category.getType() == 1 ? "菜品分类" : "套餐分类";
        if (save) {
            return success("新增" + type + "成功");
        } else {
            return Result.error("新增" + type + "失败");
        }
    }

    @GetMapping("/page")
    public Result<Page<Category>> page(int page, int pageSize) {
        //分页构造器
        Page<Category> categoryPage = new Page<>(page, pageSize);
        //条件构造器
        //按sort降序升序排列
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);

        //查询
        categoryService.page(categoryPage, queryWrapper);
        return success(categoryPage);

    }

    @DeleteMapping("")
    public Result<String> delete(@RequestParam("ids") Long id) {
        boolean remove = categoryService.remove(id);
        if (remove) {
            return Result.success("删除分类成功");
        } else {
            return Result.success("删除分类失败");
        }
    }

    @PutMapping
    public Result<String> update(@RequestBody Category category) {
        boolean update = categoryService.updateById(category);
        if (update) {
            return Result.success("修改分类成功");
        } else {
            return Result.error("修改分类失败");
        }
    }

    @GetMapping("/list")
    public Result<List<Category>> list(Integer type) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(!(type == null), Category::getType, type);
        List<Category> categoryList = categoryService.list(queryWrapper);
        return Result.success(categoryList);
    }

}
