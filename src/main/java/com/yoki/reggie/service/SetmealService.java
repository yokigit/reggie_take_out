package com.yoki.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yoki.reggie.dto.SetmealDto;
import com.yoki.reggie.pojo.Setmeal;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);

    public SetmealDto getWithDish(Long id);

    void updateWithDish(SetmealDto setmealDto);

    void deleteWithDish(Long[] ids);
}
