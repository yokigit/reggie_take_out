package com.yoki.reggie.common;

/**
 * 封装ThreadLocal来操作当前线程的局部变量id
 *
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-13 10:43
 */
public class BaseContext {
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
