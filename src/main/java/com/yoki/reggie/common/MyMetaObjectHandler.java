package com.yoki.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 公共字段自动填充
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-13 10:32
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充
     *
     * @param metaObject 封装了含有公共字段的对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("当前线程：" + Thread.currentThread().getId());

        //createTime和updateTime字段插入时自动填充
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());

        //获取当前线程的局部变量id,来对createUser和updateUser字段自动填充
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    /**
     * 更新时自动填充
     *
     * @param metaObject 封装了含有公共字段的对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("当前线程：" + Thread.currentThread().getId());

        //updateTime字段更新自动填充
        metaObject.setValue("updateTime", LocalDateTime.now());

        //获取当前线程的局部变量id
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
