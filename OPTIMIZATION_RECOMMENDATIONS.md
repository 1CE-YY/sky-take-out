# Sky Take Out 项目优化建议

## 📋 目录
1. [安全性问题](#1-安全性问题-高优先级)
2. [依赖版本升级](#2-依赖版本升级)
3. [配置管理优化](#3-配置管理优化)
4. [代码质量改进](#4-代码质量改进)
5. [架构和性能优化](#5-架构和性能优化)
6. [文档和测试](#6-文档和测试)

---

## 1. 安全性问题 (⚠️ 高优先级)

### 1.1 敏感信息泄露
**问题**: `application-dev.yml` 文件包含明文的敏感信息：
- 数据库密码: `DemonJoker0rHB98`
- Redis 密码: `123456`
- 阿里云 OSS 凭证: `access-key-id` 和 `access-key-secret`
- 微信支付配置信息

**风险等级**: 🔴 严重

**解决方案**:
1. 将敏感配置移到环境变量或独立的配置文件
2. 使用 Spring Cloud Config 或 Vault 管理敏感信息
3. 在 `.gitignore` 中添加 `application-dev.yml`
4. 立即更换所有已泄露的密钥和密码

**示例代码**:
```yaml
# application.yml
spring:
  datasource:
    password: ${DB_PASSWORD:}
  redis:
    password: ${REDIS_PASSWORD:}
```

### 1.2 弱密码算法
**问题**: 使用 MD5 哈希存储密码（见 `EmployeeServiceImpl.java` 第54行）

**风险等级**: 🔴 严重

**解决方案**:
使用 BCrypt 或 Argon2 等现代密码哈希算法：
```java
// 推荐使用 Spring Security 的 BCryptPasswordEncoder
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

### 1.3 JWT 安全问题
**问题**: 
- JWT 密钥过于简单 (`itcast`, `itheima`)
- 使用已废弃的 JJWT 0.9.1 版本

**风险等级**: 🟠 高

**解决方案**:
1. 使用强随机密钥（至少 256 位）
2. 升级 JJWT 到 0.11.x 或更高版本
3. 考虑使用 RS256 (非对称加密) 替代 HS256

---

## 2. 依赖版本升级

### 2.1 Fastjson 严重漏洞
**当前版本**: 1.2.76  
**风险**: 已知多个 RCE (远程代码执行) 漏洞

**建议**:
- 升级到 Fastjson 2.x (推荐 2.0.43+)
- 或迁移到更安全的 Jackson (Spring Boot 默认)

```xml
<dependency>
    <groupId>com.alibaba.fastjson2</groupId>
    <artifactId>fastjson2</artifactId>
    <version>2.0.43</version>
</dependency>
```

### 2.2 Apache POI 版本过旧
**当前版本**: 3.16  
**最新版本**: 5.2.x

**建议**: 升级到 5.2.5 或更高版本，修复多个安全漏洞

```xml
<poi>5.2.5</poi>
```

### 2.3 Spring Boot 版本升级
**当前版本**: 2.7.3  
**建议**: 升级到 2.7.x 最新版本或考虑升级到 3.x

### 2.4 其他依赖更新建议

| 依赖 | 当前版本 | 建议版本 | 原因 |
|------|----------|----------|------|
| Lombok | 1.18.20 | 1.18.30+ | Bug 修复和功能改进 |
| MyBatis | 2.2.0 | 3.0.3 | 性能和安全改进 |
| Druid | 1.2.1 | 1.2.20+ | 安全漏洞修复 |

---

## 3. 配置管理优化

### 3.1 环境配置隔离
**问题**: 敏感信息直接写在配置文件中

**建议**:
1. 创建 `application-dev.yml.template` 模板文件
2. 本地开发使用环境变量
3. 生产环境使用配置中心

### 3.2 循环依赖配置
**问题**: `allow-circular-references: true` 掩盖了设计问题

**建议**: 重构代码消除循环依赖，而不是启用此配置

### 3.3 硬编码配置
**问题**: 
- 店铺地址硬编码: `北京市海淀区土地十街10号`
- 百度地图 AK 直接暴露

**建议**: 移至数据库或配置中心

---

## 4. 代码质量改进

### 4.1 使用字段注入
**问题**: 大量使用 `@Autowired` 字段注入

**当前代码**:
```java
@Autowired
private EmployeeService employeeService;
```

**推荐改为构造器注入**:
```java
private final EmployeeService employeeService;

public EmployeeController(EmployeeService employeeService) {
    this.employeeService = employeeService;
}
```

**优点**: 更好的可测试性、不可变性、明确的依赖关系

### 4.2 异常处理
**建议**: 
- 添加全局异常处理器
- 统一错误响应格式
- 记录详细的错误日志

### 4.3 日志管理
**建议**:
1. 不要记录敏感信息（密码、令牌等）
2. 使用结构化日志
3. 添加请求追踪 ID

### 4.4 注释的代码
**问题**: `EmployeeServiceImpl.java` 中有大量注释掉的代码

**建议**: 
- 移除注释的代码（版本控制系统会保留历史）
- 或使用条件编译/功能开关

### 4.5 魔法值
**建议**: 将硬编码的字符串和数字提取为常量

---

## 5. 架构和性能优化

### 5.1 缓存策略
**建议**:
1. 为常用数据添加 Redis 缓存（如菜品、套餐信息）
2. 实现缓存预热和更新策略
3. 使用 Spring Cache 抽象

**示例**:
```java
@Cacheable(value = "dishes", key = "#id")
public Dish getDishById(Long id) {
    return dishMapper.getById(id);
}
```

### 5.2 数据库优化
**建议**:
1. 为频繁查询的字段添加索引
2. 优化 N+1 查询问题
3. 使用数据库连接池监控

### 5.3 API 设计
**建议**:
1. 遵循 RESTful 设计原则
2. 添加 API 版本控制
3. 实现统一的分页和排序

### 5.4 异步处理
**建议**: 对于订单通知、报表生成等耗时操作，使用异步处理或消息队列

### 5.5 限流和熔断
**建议**: 
1. 添加接口限流（如 Resilience4j 或 Sentinel）
2. 实现服务熔断机制
3. 添加降级策略

---

## 6. 文档和测试

### 6.1 缺少文档
**问题**: 项目没有 README.md

**建议添加**:
- 项目介绍和功能说明
- 环境要求和依赖
- 快速开始指南
- API 文档链接
- 部署说明
- 贡献指南

### 6.2 缺少测试
**问题**: `.gitignore` 中排除了所有测试文件

**建议**:
1. 添加单元测试（Service 层）
2. 添加集成测试（Controller 层）
3. 使用 Testcontainers 进行数据库测试
4. 设置 CI/CD 流程

**目标覆盖率**: 
- 核心业务逻辑: 80%+
- 整体代码: 60%+

### 6.3 API 文档
**现状**: 使用 Knife4j (Swagger)

**建议**: 
1. 完善 API 注释
2. 添加请求/响应示例
3. 考虑使用 SpringDoc (OpenAPI 3.0)

---

## 7. DevOps 和部署

### 7.1 容器化
**建议**: 添加 Dockerfile 和 docker-compose.yml

**示例 Dockerfile**:
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 7.2 健康检查
**建议**: 添加 Spring Boot Actuator 健康检查端点

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### 7.3 监控和日志
**建议**:
1. 集成 Prometheus + Grafana 监控
2. 使用 ELK/EFK 栈收集日志
3. 添加分布式追踪 (如 Zipkin)

---

## 8. 其他建议

### 8.1 Git 管理
**建议**:
1. 添加 `.editorconfig` 统一代码风格
2. 使用 Git hooks 进行代码检查
3. 完善 `.gitignore` 文件

### 8.2 代码规范
**建议**:
1. 使用 Checkstyle 或 SpotBugs
2. 配置 IDE 代码格式化规则
3. 建立代码审查流程

### 8.3 国际化
**建议**: 如果需要支持多语言，使用 Spring i18n

---

## 📊 优先级总结

### 🔴 立即处理（安全问题）
1. ✅ 移除配置文件中的敏感信息
2. ✅ 更换所有已泄露的密钥
3. ✅ 升级 Fastjson 到安全版本
4. ✅ 替换 MD5 密码哈希为 BCrypt

### 🟠 高优先级（1-2周内）
1. 升级其他有漏洞的依赖
2. 实现环境变量配置
3. 添加基本的单元测试
4. 完善错误处理和日志

### 🟡 中优先级（1个月内）
1. 重构消除循环依赖
2. 添加缓存策略
3. 优化数据库查询
4. 完善 API 文档

### 🟢 低优先级（持续改进）
1. 代码风格统一
2. 添加监控和追踪
3. 容器化部署
4. 性能调优

---

## 📝 实施建议

1. **分阶段实施**: 不要一次性修改所有内容，按优先级逐步改进
2. **向后兼容**: 升级依赖时注意 API 变化
3. **充分测试**: 每次修改后进行完整的回归测试
4. **文档先行**: 重大变更前先更新文档
5. **团队协作**: 确保团队成员了解所有变更

---

## 🔗 参考资源

- [Spring Boot 安全最佳实践](https://spring.io/guides/topicals/spring-security-architecture/)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [阿里巴巴 Java 开发手册](https://github.com/alibaba/p3c)
- [Spring Boot Actuator 指南](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

---

**最后更新**: 2025-12-09  
**文档版本**: 1.0
