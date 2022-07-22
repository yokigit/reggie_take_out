package com.yoki.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yoki.reggie.pojo.AddressBook;
import org.springframework.stereotype.Service;


public interface AddressBookService extends IService<AddressBook> {
    public void setDefaultAddressBook(AddressBook addressBook);
}
