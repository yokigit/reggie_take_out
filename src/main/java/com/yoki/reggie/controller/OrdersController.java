package com.yoki.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yoki.reggie.common.Result;
import com.yoki.reggie.dto.OrdersDto;
import com.yoki.reggie.pojo.OrderDetail;
import com.yoki.reggie.pojo.Orders;
import com.yoki.reggie.service.OrderDetailService;
import com.yoki.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-19 23:11
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders) {
        ordersService.submitOrders(orders);
        return Result.success("下订单成功");
    }

    @GetMapping("/userPage")
    public Result<Page<OrdersDto>> page(@RequestParam("page") int page, @RequestParam("pageSize") int pageSize) {
        //分页构造器
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //按付款时间降序排序
        queryWrapper.orderByDesc(Orders::getCheckoutTime);
        //执行
        ordersService.page(ordersPage, queryWrapper);

        //拷贝除records外的其他属性
        BeanUtils.copyProperties(ordersPage, ordersDtoPage, "records");

        //为每个orderDto设置orderDetail
        List<Orders> ordersList = ordersPage.getRecords();
        List<OrdersDto> ordersDtoList = new ArrayList<>();
        for (Orders orders : ordersList) {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(orders, ordersDto);

            Long ordersId = orders.getId();

            if (ordersId != null) {
                LambdaQueryWrapper<OrderDetail> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(OrderDetail::getOrderId, ordersId);
                List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper1);
                ordersDto.setOrderDetails(orderDetailList);
            }
            ordersDtoList.add(ordersDto);
        }
        ordersDtoPage.setRecords(ordersDtoList);

        return Result.success(ordersDtoPage);
    }
}
