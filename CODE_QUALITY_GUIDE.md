# 代码质量改进指南

本文档提供代码质量改进的具体建议和示例，帮助提升项目的可维护性、可测试性和安全性。

## 📋 目录

1. [依赖注入优化](#1-依赖注入优化)
2. [异常处理改进](#2-异常处理改进)
3. [日志记录最佳实践](#3-日志记录最佳实践)
4. [代码规范](#4-代码规范)
5. [性能优化](#5-性能优化)

---

## 1. 依赖注入优化

### 问题：字段注入（不推荐）

**当前代码示例**:
```java
@RestController
@RequestMapping("/admin/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private JwtProperties jwtProperties;
}
```

### 推荐：构造器注入

**改进后的代码**:
```java
@RestController
@RequestMapping("/admin/employee")
@RequiredArgsConstructor  // Lombok 自动生成构造器
public class EmployeeController {
    private final EmployeeService employeeService;
    private final JwtProperties jwtProperties;
    
    // Lombok @RequiredArgsConstructor 会自动生成：
    // public EmployeeController(EmployeeService employeeService, JwtProperties jwtProperties) {
    //     this.employeeService = employeeService;
    //     this.jwtProperties = jwtProperties;
    // }
}
```

**优点**:
1. ✅ 依赖关系更清晰
2. ✅ 更容易进行单元测试
3. ✅ 字段可以声明为 final，保证不可变性
4. ✅ 避免循环依赖问题
5. ✅ 更符合 Spring 官方推荐

### 手动构造器注入（不使用 Lombok）

```java
@RestController
@RequestMapping("/admin/employee")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final JwtProperties jwtProperties;
    
    public EmployeeController(EmployeeService employeeService, JwtProperties jwtProperties) {
        this.employeeService = employeeService;
        this.jwtProperties = jwtProperties;
    }
}
```

---

## 2. 异常处理改进

### 2.1 全局异常处理器

创建统一的异常处理器：

```java
package com.sky.handler;

import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleBusinessException(BaseException ex) {
        log.error("业务异常：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 处理SQL异常
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleSQLException(SQLIntegrityConstraintViolationException ex) {
        log.error("SQL异常：{}", ex.getMessage());
        String message = ex.getMessage();
        if (message.contains("Duplicate entry")) {
            String[] parts = message.split(" ");
            String username = parts[2];
            return Result.error(username + " 已存在");
        }
        return Result.error("数据库操作失败");
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleValidationException(Exception ex) {
        log.error("参数校验失败：{}", ex.getMessage());
        String message = "参数校验失败";
        
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException validException = (MethodArgumentNotValidException) ex;
            if (validException.getBindingResult().hasErrors()) {
                message = validException.getBindingResult().getFieldError().getDefaultMessage();
            }
        }
        
        return Result.error(message);
    }

    /**
     * 处理未知异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleException(Exception ex) {
        log.error("系统异常：", ex);
        return Result.error("系统繁忙，请稍后再试");
    }
}
```

### 2.2 自定义业务异常

```java
package com.sky.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 用户相关
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    INVALID_PASSWORD(1003, "密码错误"),
    ACCOUNT_LOCKED(1004, "账号已被锁定"),
    
    // 订单相关
    ORDER_NOT_FOUND(2001, "订单不存在"),
    ORDER_STATUS_ERROR(2002, "订单状态错误"),
    
    // 系统相关
    SYSTEM_ERROR(9001, "系统错误"),
    UNAUTHORIZED(9002, "未授权访问");
    
    private final int code;
    private final String message;
    
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

// 使用示例
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public int getCode() {
        return errorCode.getCode();
    }
}
```

---

## 3. 日志记录最佳实践

### 3.1 不要记录敏感信息

**❌ 错误示例**:
```java
@PostMapping("/login")
public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
    log.info("员工登录：{}", employeeLoginDTO); // 可能包含密码
    // ...
}
```

**✅ 正确示例**:
```java
@PostMapping("/login")
public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
    log.info("员工登录，用户名：{}", employeeLoginDTO.getUsername());
    // ...
}
```

### 3.2 使用合适的日志级别

```java
// ERROR - 错误信息，需要立即处理
log.error("用户登录失败，用户名不存在：{}", username, exception);

// WARN - 警告信息，可能导致问题
log.warn("Redis连接失败，使用本地缓存");

// INFO - 重要的业务流程信息
log.info("订单创建成功，订单号：{}", orderId);

// DEBUG - 调试信息，详细的执行流程
log.debug("查询数据库，SQL：{}", sql);

// TRACE - 更详细的追踪信息
log.trace("方法参数：param1={}, param2={}", param1, param2);
```

### 3.3 结构化日志

```java
@Slf4j
public class OrderService {
    
    public void createOrder(Order order) {
        // 使用 MDC 添加追踪ID
        MDC.put("traceId", UUID.randomUUID().toString());
        MDC.put("userId", order.getUserId().toString());
        
        try {
            log.info("开始创建订单");
            // 业务逻辑
            log.info("订单创建成功，订单号：{}", order.getOrderNumber());
        } catch (Exception e) {
            log.error("订单创建失败", e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
}
```

### 3.4 日志配置优化

在 `application.yml` 中配置日志级别：

```yaml
logging:
  level:
    root: INFO
    com.sky: DEBUG
    com.sky.mapper: DEBUG
    # 生产环境建议设置为 INFO
    com.sky.controller: INFO
    com.sky.service: INFO
  pattern:
    console: '%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n'
    file: '%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - [%X{traceId}] %msg%n'
  file:
    name: logs/sky-take-out.log
    max-size: 10MB
    max-history: 30
```

---

## 4. 代码规范

### 4.1 常量提取

**❌ 错误示例**（魔法值）:
```java
if (employee.getStatus() == 1) {
    // ...
}

if (order.getPayStatus() == 2) {
    // ...
}
```

**✅ 正确示例**:
```java
public class StatusConstant {
    public static final Integer ENABLE = 1;
    public static final Integer DISABLE = 0;
}

public class PayStatusConstant {
    public static final Integer UNPAID = 1;
    public static final Integer PAID = 2;
    public static final Integer REFUNDED = 3;
}

// 使用
if (employee.getStatus().equals(StatusConstant.ENABLE)) {
    // ...
}
```

### 4.2 使用枚举

更好的方式是使用枚举：

```java
@Getter
@AllArgsConstructor
public enum OrderStatus {
    PENDING(1, "待支付"),
    PAID(2, "已支付"),
    CANCELLED(3, "已取消"),
    COMPLETED(4, "已完成");
    
    private final Integer code;
    private final String description;
    
    public static OrderStatus fromCode(Integer code) {
        for (OrderStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status code: " + code);
    }
}

// 使用
if (order.getStatus().equals(OrderStatus.PAID.getCode())) {
    // ...
}
```

### 4.3 参数校验

使用 JSR-303 注解进行参数校验：

```java
@Data
public class EmployeeDTO {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20之间")
    private String username;
    
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Email(message = "邮箱格式不正确")
    private String email;
}

// Controller中使用 @Valid 或 @Validated
@PostMapping
public Result save(@RequestBody @Valid EmployeeDTO employeeDTO) {
    employeeService.save(employeeDTO);
    return Result.success();
}
```

### 4.4 清理注释的代码

**❌ 错误示例**:
```java
public void save(EmployeeDTO employeeDTO) {
    Employee employee = new Employee();
    BeanUtils.copyProperties(employeeDTO, employee);
    
    //employee.setCreateTime(LocalDateTime.now());
    //employee.setUpdateTime(LocalDateTime.now());
    //employee.setCreateUser(BaseContext.getCurrentId());
    //employee.setCreateUser(BaseContext.getCurrentId());
    
    employeeMapper.insert(employee);
}
```

**✅ 正确示例**:
1. 如果代码不需要，直接删除（Git 会保留历史）
2. 如果是功能开关，使用配置或特性开关：

```java
@Value("${feature.auto-set-timestamps:true}")
private boolean autoSetTimestamps;

public void save(EmployeeDTO employeeDTO) {
    Employee employee = new Employee();
    BeanUtils.copyProperties(employeeDTO, employee);
    
    if (autoSetTimestamps) {
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        employee.setCreateUser(BaseContext.getCurrentId());
    }
    
    employeeMapper.insert(employee);
}
```

---

## 5. 性能优化

### 5.1 使用缓存

```java
@Service
@CacheConfig(cacheNames = "dishes")
public class DishServiceImpl implements DishService {
    
    @Cacheable(key = "#id")
    public Dish getById(Long id) {
        return dishMapper.getById(id);
    }
    
    @CachePut(key = "#dish.id")
    public Dish update(Dish dish) {
        dishMapper.update(dish);
        return dish;
    }
    
    @CacheEvict(key = "#id")
    public void deleteById(Long id) {
        dishMapper.deleteById(id);
    }
    
    @Caching(evict = {
        @CacheEvict(allEntries = true)
    })
    public void deleteAll() {
        dishMapper.deleteAll();
    }
}
```

### 5.2 批量操作

**❌ 低效方式**:
```java
for (Long id : ids) {
    dishMapper.deleteById(id); // N次数据库查询
}
```

**✅ 高效方式**:
```java
dishMapper.deleteByIds(ids); // 1次数据库查询

// Mapper
void deleteByIds(@Param("ids") List<Long> ids);

// XML
<delete id="deleteByIds">
    DELETE FROM dish WHERE id IN
    <foreach collection="ids" item="id" open="(" close=")" separator=",">
        #{id}
    </foreach>
</delete>
```

### 5.3 分页查询优化

```java
// 避免查询总数（如果不需要）
@GetMapping("/page")
public Result<PageResult> page(DishPageQueryDTO queryDTO) {
    // 如果前端不需要总数，可以不查询
    PageHelper.startPage(queryDTO.getPage(), queryDTO.getPageSize(), false);
    List<Dish> list = dishMapper.pageQuery(queryDTO);
    return Result.success(new PageResult(0, list));
}

// 深分页优化（使用上次查询的最后ID）
@GetMapping("/page-optimized")
public Result<List<Dish>> pageOptimized(
        @RequestParam(required = false) Long lastId,
        @RequestParam(defaultValue = "20") Integer pageSize) {
    List<Dish> list = dishMapper.pageQueryByLastId(lastId, pageSize);
    return Result.success(list);
}
```

### 5.4 N+1 查询问题

**❌ 有N+1问题**:
```java
// 查询所有订单
List<Order> orders = orderMapper.selectAll();

// 为每个订单查询详情（N次查询）
for (Order order : orders) {
    List<OrderDetail> details = orderDetailMapper.selectByOrderId(order.getId());
    order.setDetails(details);
}
```

**✅ 使用关联查询**:
```java
// 一次性查询订单和详情
List<Order> orders = orderMapper.selectAllWithDetails();

// MyBatis XML
<resultMap id="OrderWithDetails" type="Order">
    <id column="id" property="id"/>
    <!-- 其他字段 -->
    <collection property="details" ofType="OrderDetail">
        <id column="detail_id" property="id"/>
        <!-- 详情字段 -->
    </collection>
</resultMap>

<select id="selectAllWithDetails" resultMap="OrderWithDetails">
    SELECT o.*, d.id as detail_id, d.*
    FROM orders o
    LEFT JOIN order_detail d ON o.id = d.order_id
</select>
```

---

## 📝 检查清单

完成代码优化后，请检查：

### 代码结构
- [ ] 使用构造器注入代替字段注入
- [ ] 移除所有注释的代码
- [ ] 提取魔法值为常量或枚举
- [ ] 添加适当的参数校验

### 异常处理
- [ ] 实现全局异常处理器
- [ ] 使用合适的异常类型
- [ ] 记录详细的错误日志

### 日志记录
- [ ] 不记录敏感信息
- [ ] 使用合适的日志级别
- [ ] 添加必要的上下文信息
- [ ] 配置日志轮转

### 性能优化
- [ ] 为热点数据添加缓存
- [ ] 使用批量操作代替循环
- [ ] 优化数据库查询
- [ ] 避免N+1查询问题

### 代码质量
- [ ] 遵循命名规范
- [ ] 添加必要的注释（特别是复杂逻辑）
- [ ] 保持方法简短（不超过50行）
- [ ] 单一职责原则

---

**最后更新**: 2025-12-09  
**文档版本**: 1.0
