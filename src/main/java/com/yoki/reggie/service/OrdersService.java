package com.yoki.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yoki.reggie.pojo.Orders;

public interface OrdersService extends IService<Orders> {
    void submitOrders(Orders orders);
}
