package com.yoki.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yoki.reggie.pojo.User;

public interface UserService extends IService<User> {
    void sendMsg(String to, String subject, String context);
}
