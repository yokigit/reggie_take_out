package com.yoki.reggie.common;

/**
 * 自定义业务异常类
 *
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-14 10:28
 */
public class CustomException extends RuntimeException {
    public CustomException(String message){
        super(message);
    }
}
