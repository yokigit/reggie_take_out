package com.yoki.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yoki.reggie.common.Result;
import com.yoki.reggie.pojo.Category;

public interface CategoryService extends IService<Category> {
    public boolean remove(Long id);
}
