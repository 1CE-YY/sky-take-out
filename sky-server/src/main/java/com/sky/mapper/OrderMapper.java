package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    /**
     * 根据状态和下单时间小于指定时间查询订单
     *
     * @param pendingPayment
     * @param time
     * @return
     */
    List<Orders> getByStatusAndOrdertimeLT(Integer pendingPayment, LocalDateTime time);

    /**
     * 统计订单总销售额
     * @param map
     * @return
     */
    Double sumByMap(Map map);

    /**
     * 根据参数统计订单数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);

    /**
     * 获取销售排行榜前十名
     * @param beginTime
     * @param endTime
     * @return
     */
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime beginTime, LocalDateTime endTime);
}
