package com.sky.mapper;


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
}
