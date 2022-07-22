package com.yoki.reggie.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

//@JsonFormat(shape = JsonFormat.Shape.STRING)  将java对象转换为json，解决雪花算法生成的id为18位，而前端最大支持16位数，发送数据到前端时id请求丢失
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;  //需要进行md5加密

    private String phone;

    private String sex;

    private String idNumber;  //身份证号码

    private Integer status;   //是否禁用

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;   //公共字段，插入时自动填充，mybatis plus执行insert语句时起着作用

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;   //公共字段，插入和更新时自动填充，mybatis plus执行insert或者update语句时起着作用

    @TableField(fill = FieldFill.INSERT)    //公共字段，插入时自动填充
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)   //公共字段，插入和更新时自动填充
    private Long updateUser;

}
