package com.yoki.reggie.pojo;

/**
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-19 15:25
 */

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ShoppingCart {
    private Long id;
    private Long userId;
    private String name;
    private Long setmealId;
    private Long dishId;
    private String dishFlavor;
    private Integer number;
    private BigDecimal amount;
    private String image;

//    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
