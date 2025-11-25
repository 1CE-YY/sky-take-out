package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单数据
     *
     * @param order
     */
    void insert(Orders order);

    /**
     * 根据订单号和用户id查询订单
     *
     * @param outTradeNo
     * @param userId
     * @return
     */
    Orders getByNumberAndUserId(String outTradeNo, Long userId);

    /**
     * 更新订单数据
     *
     * @param order
     */
    void update(Orders order);

    /**
     * 更新订单状态
     *
     * @param orderStatus
     * @param orderPayStatus
     * @param checkoutTime
     * @param orderNumber
     */
    void updateStatus(Integer orderStatus, Integer orderPayStatus, LocalDateTime checkoutTime, String orderNumber);

    /**
     * 订单分页查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单
     *
     * @param id
     * @return
     */
    Orders getById(Long id);

    /**
     * 统计各个状态的订单数量
     *
     * @param toBeConfirmed
     * @return
     */
    Integer countStatus(Integer toBeConfirmed);
}
