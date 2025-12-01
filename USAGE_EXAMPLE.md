# 修复后代码使用示例

## UserContextUtil 使用示例

### 1. 基本用法

```java
// 替换原有的：
Long userId = BaseContext.getCurrentId();

// 使用新的工具类：
Long userId = UserContextUtil.getCurrentUserId();
```

### 2. 在OrderServiceImpl中的应用示例

```java
// 原有代码：
// Long userId = BaseContext.getCurrentId();

// 新代码：
// Long userId = UserContextUtil.getCurrentUserId();

// 使用函数式接口：
return UserContextUtil.executeWithUserId(userId -> {
    // 在这里执行需要用户ID的业务逻辑
    List<ShoppingCart> cartItems = shoppingCartMapper.list(
        ShoppingCart.builder().userId(userId).build()
    );
    return cartItems;
});
```

### 3. 权限验证示例

```java
// 验证用户是否有权限访问特定资源
public OrderVO getOrderDetail(Long orderId) {
    Orders order = orderMapper.getById(orderId);
    if (order == null) {
        throw new OrderBusinessException("订单不存在");
    }

    // 验证当前用户是否有权限查看该订单
    UserContextUtil.validateUserAccess(order.getUserId());

    // 继续处理订单详情...
    return orderVO;
}
```

## WebSocketMessageUtil 使用示例

### 1. 发送不同类型的订单消息

```java
// 新订单提醒
webSocketMessageUtil.sendNewOrderMessage(orderId, orderNumber);

// 订单支付成功
webSocketMessageUtil.sendOrderPaidMessage(orderId, orderNumber);

// 商家接单
webSocketMessageUtil.sendOrderAcceptedMessage(orderId, orderNumber);

// 派送中
webSocketMessageUtil.sendOrderDeliveryMessage(orderId, orderNumber);

// 订单完成
webSocketMessageUtil.sendOrderCompletedMessage(orderId, orderNumber);

// 订单取消
webSocketMessageUtil.sendOrderCancelledMessage(orderId, orderNumber, "库存不足");

// 用户催单
webSocketMessageUtil.sendOrderReminderMessage(orderId, orderNumber);
```

### 2. 自定义消息

```java
// 发送系统消息
webSocketMessageUtil.sendSystemMessage(10, "系统维护通知");

// 发送自定义消息
Map<String, Object> customMessage = new HashMap<>();
customMessage.put("type", 99);
customMessage.put("content", "自定义消息");
customMessage.put("data", additionalData);
webSocketMessageUtil.sendCustomMessage(customMessage);
```

## 安全配置使用说明

### 1. 生产环境JWT密钥设置

```bash
# 生成强密钥
openssl rand -base64 64

# 设置环境变量
export JWT_ADMIN_SECRET_KEY="your-generated-admin-secret-key"
export JWT_USER_SECRET_KEY="your-generated-user-secret-key"
```

### 2. 配置文件示例

```yaml
sky:
  jwt:
    admin-secret-key: ${JWT_ADMIN_SECRET_KEY:sky-admin-jwt-secret-key-2024-must-be-changed-in-production}
    user-secret-key: ${JWT_USER_SECRET_KEY:sky-user-jwt-secret-key-2024-must-be-changed-in-production}
```

## 单元测试运行

```bash
# 运行OrderServiceImpl测试
./mvnw test -Dtest=OrderServiceImplTest

# 运行所有测试
./mvnw test

# 生成测试报告
./mvnw surefire-report:report
```

## 日志查看

```bash
# 查看应用日志
tail -f logs/sky-take-out.log

# 查看错误日志
grep "ERROR" logs/sky-take-out.log

# 查看WebSocket消息日志
grep "WebSocket" logs/sky-take-out.log
```

## 性能监控

修复后的代码已经解决了以下性能问题：
1. ✅ N+1查询问题 - 已使用批量查询优化
2. ✅ 并发安全问题 - WebSocket使用ConcurrentHashMap
3. ✅ 异常处理改进 - 使用日志框架替代printStackTrace
4. ✅ 代码重复消除 - 使用工具类统一处理

建议监控以下指标：
- 数据库查询响应时间
- WebSocket连接数
- 内存使用情况
- 订单处理吞吐量