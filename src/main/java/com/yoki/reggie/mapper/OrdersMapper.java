package com.yoki.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yoki.reggie.pojo.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
