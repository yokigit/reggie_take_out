package com.yoki.reggie.controller;

import com.yoki.reggie.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-14 14:41
 */
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.basePath}")
    private String basePath;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file, HttpSession session) {  //参数名必须与前端的参数名保持一致，<input type="file" name="file" />
        //file是一个临时文件（c://user/yoki/AppData/local/...），本次请求结束后会销毁

        //获取原文件名
        String filename = file.getOriginalFilename();
        //获取后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
        //新文件名
        filename = UUID.randomUUID() + suffix;

        //获取服务器中 backend/images/dish 的路径
//        ServletContext context = session.getServletContext();
//        String basePath = context.getRealPath("backend/images/dish/");

        //判断当前目录是否存在
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        //将临时文件转存到指定位置
        try {
            file.transferTo(new File(basePath + filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //返回文件名，方便前端页面显示
        return Result.success(filename);
    }

    @GetMapping("/download")
    public void download(@RequestParam("name") String filename, HttpSession session, HttpServletResponse response) {

        //获取文件目录路径
//        ServletContext context = session.getServletContext();
//        String dirPath = context.getRealPath("backend/images/dish/");

        try {
            //输入流，读取文件内容
            FileInputStream inputStream = new FileInputStream(new File(basePath + filename));

            //输出流，将文件写回浏览器，以展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            byte[] bytes = new byte[1024];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
