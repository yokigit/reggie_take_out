package com.yoki.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yoki.reggie.mapper.OrderDetailMapper;
import com.yoki.reggie.pojo.OrderDetail;
import com.yoki.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-19 23:09
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
