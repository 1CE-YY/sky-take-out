package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

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
}
