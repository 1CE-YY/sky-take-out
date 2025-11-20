package com.sky.mapper;


import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入菜品口味数据
     * @param flavors 菜品口味列表
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品id删除对应的口味数据
     * @param id 菜品id
     */
    void deleteByDishId(Long id);

    /**
     * 根据菜品id查询对应的口味数据
     * @param id 菜品id
     * @return 口味列表
     */
    List<DishFlavor> getByDishId(Long id);

    /**
     * 根据菜品ids批量删除对应的口味数据
     * @param ids 菜品ids
     */
    void deleteByIds(List<Long> ids);
}
