package com.yoki.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yoki.reggie.common.BaseContext;
import com.yoki.reggie.common.CustomException;
import com.yoki.reggie.mapper.OrdersMapper;
import com.yoki.reggie.pojo.*;
import com.yoki.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-19 23:07
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Transactional
    @Override
    public void submitOrders(Orders orders) {
        //获取用户id
        Long userId = BaseContext.getCurrentId();
        //获取username
        User user = userService.getById(userId);
        String username = user.getName();

        //生成id,number
        long orderId = IdWorker.getId();

        //获取addressBookId
        Long addressBookId = orders.getAddressBookId();
        //获取phone,consignee,address
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null) {
            throw new CustomException("您的地址信息有误，暂不能下单!");
        }
        String phone = addressBook.getPhone();
        String consignee = addressBook.getConsignee();
        String address = (addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail());

        //根据userId查询用户的购物车信息
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new CustomException("购物车为空，不能下单！");
        }

        //要保存的订单详情集合
        List<OrderDetail> orderDetailList = new ArrayList<>();

        // 购物车中 商品 的总金额 需要保证在多线程的情况下 也是能计算正确的，故需要使用原子类
        AtomicInteger amount = new AtomicInteger(0);

        for (ShoppingCart shoppingCart : shoppingCartList) {
            //某个订单详情
            OrderDetail orderDetail = new OrderDetail();

            orderDetail.setName(shoppingCart.getName());
            orderDetail.setOrderId(orderId);
            orderDetail.setDishId(shoppingCart.getDishId());
            orderDetail.setSetmealId(shoppingCart.getSetmealId());
            orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
            orderDetail.setNumber(shoppingCart.getNumber());
            orderDetail.setAmount(shoppingCart.getAmount());
            orderDetail.setImage(shoppingCart.getImage());

            //计算总价
            amount.addAndGet(shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber())).intValue());

            //加入订单详情集合
            orderDetailList.add(orderDetail);
        }

        //设置订单
        orders.setId(orderId);
        orders.setNumber(String.valueOf(orderId));
        orders.setStatus(2);
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserName(username);
        orders.setPhone(phone);
        orders.setAddress(address);
        orders.setConsignee(consignee);

        //保存订单
        this.save(orders);

        //保存订单详情
        orderDetailService.saveBatch(orderDetailList);

        //清空购物车
        shoppingCartService.remove(queryWrapper);
    }
}
