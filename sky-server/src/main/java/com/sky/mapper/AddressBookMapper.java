package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    /**
     * 条件查询地址簿信息
     *
     * @param addressBook
     * @return
     */
    List<AddressBook> list(AddressBook addressBook);


    /**
     * 新增地址
     *
     * @param addressBook
     * @return
     */
    void insert(AddressBook addressBook);

    /**
     * 根据id查询地址
     *
     * @param id
     * @return
     */
    AddressBook getById(Long id);

    /**
     * 更新地址
     *
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 更新默认地址
     *
     * @param addressBook
     */
    void updateIsDefaultByUserId(AddressBook addressBook);

    /**
     * 根据id删除地址
     *
     * @param id
     */
    void deleteById(Long id);
}
