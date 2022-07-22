package com.yoki.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yoki.reggie.common.Result;
import com.yoki.reggie.pojo.User;
import com.yoki.reggie.service.UserService;
import com.yoki.reggie.utils.EmailUtils;
import com.yoki.reggie.utils.SMSUtils;
import com.yoki.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-16 15:55
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 通过手机短信发送验证码
     *
     * @param user
     * @return
     */
//    @RequestMapping("/sendMsg")
//    public Result<String> sendPhoneMsg(@RequestBody User user, HttpSession session) {
//        //获取手机号
//        String phone = user.getPhone();
//        if (StringUtils.isNotEmpty(phone)) {
//            //生成验证码
//            String code = ValidateCodeUtils.generateValidateCode4String(6);
//
//            //通过阿里云的短信服务向指定的手机发送短信
//            String signName = "瑞吉外卖";
//            String template = "欢迎使用瑞吉餐购，登录验证码为: ${code} ,五分钟内有效，请妥善保管!";
//            SMSUtils.sendMessage(signName, template, phone, code);
//
//            //将验证码保存在session域中
//            session.setAttribute("phone", code);
//
//            return Result.success("手机短信发送成功");
//        }
//        return Result.error("手机短信发送失败");
//    }

    /**
     * 通过邮箱发送验证码
     *
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public Result<String> sendEmailMsg(@RequestBody User user, HttpSession session) {
        //获取邮箱
        String phone = user.getPhone();

        //如果邮箱不为空
        if (StringUtils.isNotEmpty(phone)) {
            log.info("登录验证码：" + phone);

            //填充邮件主题
            String subject = "【瑞吉外卖】";
            //接收方
            String to = phone;
            //生成验证码
//            String code = ValidateCodeUtils.generateValidateCode4String(6);
            String code = "aaaaaa";
            //内容
            String text = "欢迎使用瑞吉餐购，登录验证码为: " + code + " ，五分钟内有效，请妥善保管!";

            //发送
            userService.sendMsg(to, subject, text);

            //将验证码存在session域中
            session.setAttribute("phone", code);

            return Result.success("邮件发送成功");
        }
        return Result.error("邮件发送失败");
    }

    /**
     * 登录
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map<String, String> map, HttpSession session) {
        //获取验证码
        String code = map.get("code");
        //验证码是否正确
        if (StringUtils.isNotEmpty(code) && code.equals(session.getAttribute("phone"))) {
            //获取手机号
            String phone = map.get("phone");
            //根据手机号查询用户
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            //如果不存在该用户
            if (user == null) {
                //注册用户
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }

            //将用户id存在session域中
            session.setAttribute("user", user.getId());

            return Result.success(user);
        }
        return Result.error("登录失败");
    }


}
