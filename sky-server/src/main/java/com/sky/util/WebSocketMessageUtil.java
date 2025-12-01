package com.sky.util;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.sky.websocket.WebSocketServer;

import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket消息推送工具类
 * 用于统一处理订单相关的WebSocket消息推送
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketMessageUtil {

    private final WebSocketServer webSocketServer;

    // 消息类型常量
    public static final int MESSAGE_TYPE_NEW_ORDER = 1;            // 新订单
    public static final int MESSAGE_TYPE_ORDER_PAID = 2;           // 订单支付
    public static final int MESSAGE_TYPE_ORDER_ACCEPTED = 3;       // 商家接单
    public static final int MESSAGE_TYPE_ORDER_DELIVERY = 4;       // 派送中
    public static final int MESSAGE_TYPE_ORDER_COMPLETED = 5;      // 订单完成
    public static final int MESSAGE_TYPE_ORDER_CANCELLED = 6;      // 订单取消

    /**
     * 发送新订单提醒
     *
     * @param orderId   订单ID
     * @param orderNumber 订单号
     */
    public void sendNewOrderMessage(Long orderId, String orderNumber) {
        sendOrderMessage(MESSAGE_TYPE_NEW_ORDER, orderId, "新订单：" + orderNumber);
    }

    /**
     * 发送订单支付成功消息
     *
     * @param orderId     订单ID
     * @param orderNumber 订单号
     */
    public void sendOrderPaidMessage(Long orderId, String orderNumber) {
        sendOrderMessage(MESSAGE_TYPE_ORDER_PAID, orderId, "订单号：" + orderNumber + " 支付成功");
    }

    /**
     * 发送商家接单消息
     *
     * @param orderId     订单ID
     * @param orderNumber 订单号
     */
    public void sendOrderAcceptedMessage(Long orderId, String orderNumber) {
        sendOrderMessage(MESSAGE_TYPE_ORDER_ACCEPTED, orderId, "订单号：" + orderNumber + " 商家已接单");
    }

    /**
     * 发送派送中消息
     *
     * @param orderId     订单ID
     * @param orderNumber 订单号
     */
    public void sendOrderDeliveryMessage(Long orderId, String orderNumber) {
        sendOrderMessage(MESSAGE_TYPE_ORDER_DELIVERY, orderId, "订单号：" + orderNumber + " 正在派送中");
    }

    /**
     * 发送订单完成消息
     *
     * @param orderId     订单ID
     * @param orderNumber 订单号
     */
    public void sendOrderCompletedMessage(Long orderId, String orderNumber) {
        sendOrderMessage(MESSAGE_TYPE_ORDER_COMPLETED, orderId, "订单号：" + orderNumber + " 已完成");
    }

    /**
     * 发送订单取消消息
     *
     * @param orderId     订单ID
     * @param orderNumber 订单号
     * @param reason      取消原因
     */
    public void sendOrderCancelledMessage(Long orderId, String orderNumber, String reason) {
        String content = "订单号：" + orderNumber + " 已取消";
        if (reason != null && !reason.trim().isEmpty()) {
            content += "，原因：" + reason;
        }
        sendOrderMessage(MESSAGE_TYPE_ORDER_CANCELLED, orderId, content);
    }

    /**
     * 发送用户催单消息
     *
     * @param orderId     订单ID
     * @param orderNumber 订单号
     */
    public void sendOrderReminderMessage(Long orderId, String orderNumber) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", 7); // 催单消息类型
        message.put("orderId", orderId);
        message.put("content", "用户催单，订单号：" + orderNumber);
        message.put("timestamp", System.currentTimeMillis());

        String jsonMessage = JSON.toJSONString(message);
        log.info("发送用户催单消息: {}", jsonMessage);
        webSocketServer.sendToAllClient(jsonMessage);
    }

    /**
     * 发送自定义订单消息
     *
     * @param type       消息类型
     * @param orderId    订单ID
     * @param content    消息内容
     */
    public void sendOrderMessage(int type, Long orderId, String content) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", type);
        message.put("orderId", orderId);
        message.put("content", content);
        message.put("timestamp", System.currentTimeMillis());

        String jsonMessage = JSON.toJSONString(message);
        log.info("发送订单消息: {}", jsonMessage);
        webSocketServer.sendToAllClient(jsonMessage);
    }

    /**
     * 发送系统消息
     *
     * @param type       消息类型
     * @param content    消息内容
     */
    public void sendSystemMessage(int type, String content) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", type);
        message.put("content", content);
        message.put("timestamp", System.currentTimeMillis());

        String jsonMessage = JSON.toJSONString(message);
        log.info("发送系统消息: {}", jsonMessage);
        webSocketServer.sendToAllClient(jsonMessage);
    }

    /**
     * 发送自定义消息
     *
     * @param messageMap 消息内容Map
     */
    public void sendCustomMessage(Map<String, Object> messageMap) {
        if (messageMap == null) {
            log.warn("消息内容不能为空");
            return;
        }

        // 添加时间戳
        messageMap.put("timestamp", System.currentTimeMillis());

        String jsonMessage = JSON.toJSONString(messageMap);
        log.info("发送自定义消息: {}", jsonMessage);
        webSocketServer.sendToAllClient(jsonMessage);
    }
}