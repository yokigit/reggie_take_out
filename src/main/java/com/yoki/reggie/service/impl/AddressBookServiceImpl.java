package com.yoki.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yoki.reggie.common.BaseContext;
import com.yoki.reggie.mapper.AddressBookMapper;
import com.yoki.reggie.pojo.AddressBook;
import com.yoki.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-18 20:12
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
    @Transactional
    @Override
    public void setDefaultAddressBook(AddressBook addressBook) {
        //修改该用户原本的默认地址
        Long userId = BaseContext.getCurrentId();
        LambdaUpdateWrapper<AddressBook> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, userId);
        queryWrapper.eq(AddressBook::getIsDefault, 1);
        queryWrapper.set(AddressBook::getIsDefault, 0);
        this.update(queryWrapper);

        //更新该用户的默认地址
        addressBook.setIsDefault(1);
        this.updateById(addressBook);
    }
}
