package com.yoki.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yoki.reggie.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
