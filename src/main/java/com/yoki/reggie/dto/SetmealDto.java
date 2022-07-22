package com.yoki.reggie.dto;


import com.yoki.reggie.pojo.Setmeal;
import com.yoki.reggie.pojo.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
