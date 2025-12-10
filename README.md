# 🍜 苍穹外卖 (Sky Take Out)

## 📖 项目介绍

苍穹外卖是一个基于 Spring Boot 的外卖管理系统，提供完整的后台管理功能，包括员工管理、菜品管理、订单处理、数据统计等功能。

### 主要功能

- 👥 **员工管理**: 员工账号管理、权限控制
- 🍱 **菜品管理**: 菜品分类、菜品信息、套餐管理
- 🛒 **订单管理**: 订单查询、订单处理、订单统计
- 📊 **数据统计**: 营业数据、销售统计、图表展示
- 💰 **支付功能**: 微信支付集成
- 📦 **文件上传**: 阿里云 OSS 集成

## 🏗️ 技术栈

- **后端框架**: Spring Boot 2.7.3
- **数据库**: MySQL 8.x
- **ORM 框架**: MyBatis
- **缓存**: Redis
- **API 文档**: Knife4j (Swagger)
- **构建工具**: Maven
- **其他**: Lombok, JWT, FastJson, Druid

## 📋 项目结构

```
sky-take-out/
├── sky-common/          # 公共模块（工具类、常量、异常等）
├── sky-pojo/            # 实体类模块
├── sky-server/          # 服务模块（主应用）
│   ├── controller/      # 控制器层
│   ├── service/         # 业务逻辑层
│   ├── mapper/          # 数据访问层
│   └── resources/       # 配置文件和资源
├── pom.xml             # 父 POM 文件
└── README.md           # 项目说明
```

## 🚀 快速开始

### 环境要求

- JDK 8 或以上
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+

### 安装步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/1CE-YY/sky-take-out.git
   cd sky-take-out
   ```

2. **创建数据库**
   ```sql
   CREATE DATABASE sky_take_out DEFAULT CHARACTER SET utf8mb4;
   ```

3. **配置应用**
   
   复制配置模板文件：
   ```bash
   cd sky-server/src/main/resources
   cp application-dev.yml.template application-dev.yml
   ```
   
   编辑 `application-dev.yml`，填入实际的配置信息：
   - 数据库连接信息
   - Redis 连接信息
   - 阿里云 OSS 配置（如需要文件上传功能）
   - 微信支付配置（如需要支付功能）

4. **构建项目**
   ```bash
   mvn clean install
   ```

5. **运行应用**
   ```bash
   cd sky-server
   mvn spring-boot:run
   ```

6. **访问应用**
   - 应用地址: http://localhost:8080
   - API 文档: http://localhost:8080/doc.html

## ⚙️ 配置说明

### 数据库配置

```yaml
sky:
  datasource:
    host: localhost
    port: 3306
    database: sky_take_out
    username: root
    password: your_password
```

### Redis 配置

```yaml
sky:
  redis:
    host: localhost
    port: 6379
    password: your_password
    database: 10
```

### JWT 配置

⚠️ **安全提示**: 生产环境请务必修改默认的 JWT 密钥

```yaml
sky:
  jwt:
    admin-secret-key: 建议使用强随机密钥
    admin-ttl: 7200000  # 2小时
```

## 🔒 安全建议

⚠️ **重要**: 请查看 [OPTIMIZATION_RECOMMENDATIONS.md](OPTIMIZATION_RECOMMENDATIONS.md) 了解详细的安全优化建议

1. **配置文件安全**
   - 不要将 `application-dev.yml` 提交到版本控制
   - 使用环境变量或配置中心管理敏感信息
   - 定期更换密钥和密码

2. **密码安全**
   - 建议升级密码哈希算法（从 MD5 到 BCrypt）
   - 使用强密码策略

3. **依赖安全**
   - 定期更新依赖版本
   - 特别注意 Fastjson 等有已知漏洞的依赖

## 📚 API 文档

项目使用 Knife4j 提供 API 文档，启动应用后访问：

```
http://localhost:8080/doc.html
```

## 🧪 测试

```bash
# 运行测试
mvn test

# 生成测试覆盖率报告
mvn clean test jacoco:report
```

## 📦 部署

### 打包应用

```bash
mvn clean package -DskipTests
```

生成的 JAR 文件位于 `sky-server/target/` 目录

### 运行 JAR

```bash
java -jar sky-server/target/sky-take-out-1.0-SNAPSHOT.jar
```

### Docker 部署（推荐）

详见 [部署文档](docs/DEPLOYMENT.md)（待添加）

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

[根据实际情况添加许可证信息]

## 📞 联系方式

- 项目地址: https://github.com/1CE-YY/sky-take-out
- 问题反馈: [Issues](https://github.com/1CE-YY/sky-take-out/issues)

## 🙏 致谢

感谢所有为本项目做出贡献的开发者！

---

**注意**: 本项目仅供学习交流使用
