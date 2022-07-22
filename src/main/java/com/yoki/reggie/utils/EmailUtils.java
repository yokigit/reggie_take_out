package com.yoki.reggie.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-16 19:08
 */
@Component
public class EmailUtils {
    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private JavaMailSender javaMailSender;

    public  void send(String to, String subject, String text) {
        //设置邮件信息
        SimpleMailMessage mailMessage=new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);

        //发送邮件
        javaMailSender.send(mailMessage);
    }
}
