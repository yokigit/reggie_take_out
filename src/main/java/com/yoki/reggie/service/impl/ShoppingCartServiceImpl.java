package com.yoki.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yoki.reggie.mapper.ShoppingCartMapper;
import com.yoki.reggie.pojo.ShoppingCart;
import com.yoki.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-19 15:32
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
