package com.yoki.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yoki.reggie.common.Result;
import com.yoki.reggie.pojo.Employee;
import com.yoki.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-11 14:54
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public Result<Employee> login(HttpSession session, @RequestBody Employee employee) {
        //1、将password进行MD5加密
        String password = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());
        //2、根据username查询用户
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee employee1 = employeeService.getOne(queryWrapper);
        //2.1 检查用户是否存在
        if (employee1 == null) {
            return Result.error("登录失败");
        }
        //3、检查密码是否正确
        if (!employee1.getPassword().equals(password)) {
            return Result.error("登录失败");
        }
        //4、检查用户是否禁用
        if (employee1.getStatus() == 0) {
            return Result.error("账号已禁用");
        }

        //5、将用户id放在session域中
        session.setAttribute("employee", employee1.getId());
        return Result.success(employee1);
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpSession session) {
        session.removeAttribute("employee");
        return Result.success("退出成功");
    }

    @GetMapping("/page")
    public Result<Page<Employee>> page(int page, int pageSize, String name) {
        //分页构造器
        Page<Employee> employeePage = new Page<>(page, pageSize);

        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件(如果name不为空，才进行查询)
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(employeePage, queryWrapper);

        return Result.success(employeePage);
    }

    @PostMapping("")
    public Result<String> save(@RequestBody Employee employee, HttpSession session) {
        log.info("当前线程：" + Thread.currentThread().getId());

        ServletContext servletContext = session.getServletContext();
        String realPath = servletContext.getRealPath("/backend/images");

        //根据username查询用户
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee employee1 = employeeService.getOne(queryWrapper);

        //给每个用户设置初始密码，并经过MD5加密
        String initialPassword = "123456";
        employee.setPassword(DigestUtils.md5DigestAsHex(initialPassword.getBytes()));

        //设置创建时间
//            employee.setCreateTime(LocalDateTime.now());

        //设置更新时间
//            employee.setUpdateTime(LocalDateTime.now());

        //设置创建人
//            employee.setCreateUser((Long) session.getAttribute("employee"));

        //设置更新人
//            employee.setUpdateUser((Long) session.getAttribute("employee"));

        //保存
        employeeService.save(employee);
        return Result.success("添加员工成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody Employee employee, HttpSession session) {
        log.info("当前线程：" + Thread.currentThread().getId());

        //设置更新时间和更新人
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser((Long) session.getAttribute("employee"));

        //保存
        boolean update = employeeService.updateById(employee);
        if (update) {
            return Result.success("账号状态修改成功");
        } else {
            return Result.error("账号状态修改失败");
        }
    }

    @GetMapping("/{id}")
    public Result<Employee> queryEmployeeById(@PathVariable("id") Long id) {
        //根据id查询用户
//        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Employee::getId, id);
//        Employee employee = employeeService.getOne(queryWrapper);
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return Result.success(employee);
        } else {
            return Result.error("回显数据失败");
        }
    }
}