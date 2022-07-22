package com.yoki.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yoki.reggie.common.Result;
import com.yoki.reggie.dto.DishDto;
import com.yoki.reggie.pojo.Dish;

public interface DishService extends IService<Dish> {
    public void saveWithFlavor(DishDto dishDto);

    public void removeWithFlavor(Long[] ids);

    public DishDto getWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);
}
