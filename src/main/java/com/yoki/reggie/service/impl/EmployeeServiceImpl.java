package com.yoki.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yoki.reggie.mapper.EmployeeMapper;
import com.yoki.reggie.pojo.Employee;
import com.yoki.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-11 14:51
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
