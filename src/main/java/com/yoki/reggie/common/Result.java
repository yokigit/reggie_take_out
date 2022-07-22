package com.yoki.reggie.common;

import com.baomidou.mybatisplus.extension.api.R;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-11 14:58
 * 通用返回结果，服务端响应的数据最终都会封装成该类型的对象
 */
@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;
    private Map<String, Object> map = new HashMap<>();

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<>();
        result.data = object;
        result.code = 1;
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.msg = msg;
        result.code = 0;
        return result;
    }

    public Result<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}
