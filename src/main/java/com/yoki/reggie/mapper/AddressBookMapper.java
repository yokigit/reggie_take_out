package com.yoki.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yoki.reggie.pojo.AddressBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
