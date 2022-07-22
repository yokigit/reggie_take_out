package com.yoki.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.yoki.reggie.common.BaseContext;
import com.yoki.reggie.common.Result;
import com.yoki.reggie.pojo.AddressBook;
import com.yoki.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

/**
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-18 20:19
 */
@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @GetMapping("/list")
    public Result<List<AddressBook>> List() {
        List<AddressBook> addressBookList = addressBookService.list();
        return Result.success(addressBookList);
    }

    @PostMapping("")
    public Result<String> add(@RequestBody AddressBook addressBook, HttpSession session) {
        addressBook.setUserId((Long) session.getAttribute("user"));
        boolean save = addressBookService.save(addressBook);
        if (save) {
            return Result.success("新增地址成功");
        }
        return Result.error("新增地址失败");
    }

    @GetMapping("/{id}")
    public Result<AddressBook> get(@PathVariable("id") Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return Result.success(addressBook);
        }
        return Result.error("回显数据失败");
    }

    @PutMapping("")
    public Result<String> update(@RequestBody AddressBook addressBook) {
        boolean update = addressBookService.updateById(addressBook);
        if (update) {
            return Result.success("修改地址成功");
        }
        return Result.error("修改地址失败");
    }

    @PutMapping("/default")
    public Result<String> setDefault(@RequestBody AddressBook addressBook, HttpSession session) {
        addressBookService.setDefaultAddressBook(addressBook);
        return Result.success("修改默认地址成功");
    }

    @GetMapping("/default")
    public Result<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (addressBook != null) {
            return Result.success(addressBook);
        }
        return Result.error("没有找到默认地址");
    }

    @DeleteMapping("")
    public Result<String> delete(@RequestParam("ids") Long[] ids) {
        boolean remove = addressBookService.removeByIds(Arrays.asList(ids));
        if (remove) {
            return Result.success("删除地址成功");
        }
        return Result.error("删除地址失败");
    }
}
