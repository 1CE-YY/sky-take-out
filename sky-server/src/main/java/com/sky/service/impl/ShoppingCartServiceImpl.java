package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartMapper shoppingCartMapper;

    private final DishMapper dishMapper;

    private final SetmealMapper setmealMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        // 查询当前菜品或套餐是否在购物车中
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);

        if (shoppingCarts != null && !shoppingCarts.isEmpty()) {
            // 已存在，更新数量
            ShoppingCart existingCart = shoppingCarts.get(0);
            existingCart.setNumber(existingCart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(existingCart);
        } else {
            // 不存在，新增记录
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            if (shoppingCart.getDishId() != null) {
                // 菜品
                Dish dish = dishMapper.getById(shoppingCart.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            } else if (shoppingCart.getSetmealId() != null) {
                // 套餐
                Setmeal setmeal = setmealMapper.getById(shoppingCart.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCartMapper.insert(shoppingCart);
        }

    }
}
