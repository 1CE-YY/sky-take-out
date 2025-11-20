package com.sky.mapper;

import com.sky.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    Integer countByCategoryId(Long categoryId);

    /**
     * 新增菜品
     * @param dish
     */
    void insert(Dish dish);
}
