package com.yoki.reggie.common;

import com.baomidou.mybatisplus.extension.api.R;
import com.yoki.reggie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 对于使用RestController、Controller的类抛出的异常，都有该异常处理器来处理
 *
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-12 14:59
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody     //或者不额外使用@ResponseBody，@ControllerAdvice替换成 @RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)  //该方法对该异常进行处理
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException exception) {
        log.info("异常信息：" + exception.getMessage());
        if (exception.getMessage().contains("Duplicate entry")) {
            String[] split = exception.getMessage().split(" ");
            return Result.error(split[2] + "已存在");
        }
        return Result.error("未知的错误");
    }

    @ExceptionHandler(CustomException.class)
    public Result<String> exceptionHandler(CustomException ex) {
        String err = ex.getMessage();
        log.info(ex.getMessage());
        return Result.error(ex.getMessage());
    }


}
