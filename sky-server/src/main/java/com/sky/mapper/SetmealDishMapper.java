package com.sky.mapper;


import com.sky.entity.SetmealDish;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品ids查询对应的套餐ids
     * @param ids 菜品ids
     * @return 套餐ids
     */
    List<Long> getSetmealIdsByDishIds(List<Long> ids);

    /**
     * 批量插入套餐和菜品的关联数据
     * @param setmealDishes 套餐和菜品关联数据
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id删除对应的套餐和菜品关联数据
     * @param id 套餐id
     */
    void deleteBySetmealId(Long id);

    /**
     * 根据套餐id查询对应的套餐和菜品关联数据
     * @param id 套餐id
     * @return 套餐和菜品关联数据
     */
    List<SetmealDish> getBySetmealId(Long id);
}
