package com.yoki.reggie.pojo;

/**
 * 用户
 *
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-16 13:38
 */

import lombok.Data;

import java.io.Serializable;
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;


    //姓名
    private String name;


    //手机号（邮箱）
    private String phone;


    //性别 0 女 1 男
    private String sex;


    //身份证号
    private String idNumber;


    //头像
    private String avatar;


    //状态 0:禁用，1:正常
    private Integer status;
}
