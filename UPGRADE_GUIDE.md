# 依赖升级指南

本文档记录了项目依赖的升级变更以及需要的代码调整。

## 📦 依赖版本变更

### 已升级的依赖

| 依赖 | 旧版本 | 新版本 | 变更类型 |
|------|--------|--------|----------|
| MyBatis Spring Boot | 2.2.0 | 3.0.3 | 小版本升级 |
| Lombok | 1.18.20 | 1.18.30 | 补丁升级 |
| Fastjson | 1.2.76 | 1.2.83 | 安全修复 |
| Druid | 1.2.1 | 1.2.20 | 安全修复 |
| PageHelper | 1.3.0 | 1.4.7 | 小版本升级 |
| Aliyun OSS SDK | 3.10.2 | 3.17.4 | 小版本升级 |
| Knife4j | 3.0.2 | 3.0.3 | 补丁升级 |
| AspectJ | 1.9.4 | 1.9.20.1 | 小版本升级 |
| JJWT | 0.9.1 | 0.11.5 | ⚠️ 主版本升级 |
| Apache POI | 3.16 | 5.2.5 | ⚠️ 主版本升级 |

## ⚠️ 需要代码调整的升级

### 1. JJWT 0.9.1 → 0.11.5

**影响文件**: `sky-common/src/main/java/com/sky/utils/JwtUtil.java`

#### 依赖变更

JJWT 0.11.x 将单个 JAR 拆分为多个模块：

```xml
<!-- 旧版本 -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>

<!-- 新版本 -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

#### 代码调整

**旧代码** (`JwtUtil.java`):

```java
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;

public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    
    long expMillis = System.currentTimeMillis() + ttlMillis;
    Date exp = new Date(expMillis);
    
    JwtBuilder builder = Jwts.builder()
            .setClaims(claims)
            .signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8))
            .setExpiration(exp);
    
    return builder.compact();
}

public static Claims parseJWT(String secretKey, String token) {
    Claims claims = Jwts.parser()
            .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
            .parseClaimsJws(token).getBody();
    return claims;
}
```

**新代码**:

```java
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
    SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    
    long expMillis = System.currentTimeMillis() + ttlMillis;
    Date exp = new Date(expMillis);
    
    return Jwts.builder()
            .setClaims(claims)
            .signWith(key, SignatureAlgorithm.HS256)
            .setExpiration(exp)
            .compact();
}

public static Claims parseJWT(String secretKey, String token) {
    SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    
    return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
}
```

**主要变更**:
1. 使用 `Keys.hmacShaKeyFor()` 创建密钥对象
2. `signWith()` 方法参数顺序改变
3. 使用 `parserBuilder()` 替代 `parser()`
4. 需要调用 `.build()` 构建解析器

### 2. Apache POI 3.16 → 5.2.5

**影响**: 如果项目中有 Excel 导入/导出功能

#### 包名变更

POI 5.x 更改了部分包名：

```java
// 旧版本
import org.apache.poi.ss.usermodel.*;

// 新版本（大部分保持不变）
import org.apache.poi.ss.usermodel.*;

// 但某些类的包路径可能改变，需要根据实际使用情况调整
```

#### API 变更

一些 API 方法签名发生了变化，具体需要根据编译错误进行调整。

常见变更：
1. 某些 deprecated 方法被移除
2. 日期处理方式改进
3. 字体和样式处理优化

**建议**: 运行编译查看具体错误，然后根据错误信息调整代码。

### 3. MyBatis Spring Boot 2.2.0 → 3.0.3

**影响**: 通常无需代码调整，但建议测试所有数据库操作

#### 可能的变更

1. 配置属性名称可能略有变化
2. 某些 deprecated 配置被移除
3. 性能优化和 bug 修复

**建议**: 
- 检查 `application.yml` 中的 MyBatis 配置
- 运行所有数据库相关的测试

## 🔧 升级步骤

### 步骤 1: 备份

```bash
git checkout -b upgrade-dependencies
git commit -am "Backup before dependency upgrade"
```

### 步骤 2: 更新 POM 文件

POM 文件已经更新，包含所有新版本。

### 步骤 3: 清理和重新构建

```bash
mvn clean
rm -rf ~/.m2/repository/io/jsonwebtoken
rm -rf ~/.m2/repository/org/apache/poi
mvn install
```

### 步骤 4: 更新 JwtUtil 代码

按照上面的示例更新 `JwtUtil.java` 文件。

### 步骤 5: 编译测试

```bash
mvn clean compile
```

检查是否有编译错误，特别关注：
- JWT 相关的代码
- POI 相关的 Excel 处理代码

### 步骤 6: 运行测试

```bash
mvn test
```

### 步骤 7: 手动测试

启动应用并测试关键功能：
1. 登录功能（测试 JWT）
2. Excel 导入/导出（如果有）
3. 数据库查询和更新
4. 文件上传功能

## 📝 注意事项

### JWT 密钥长度要求

JJWT 0.11.x 对 HS256 算法要求密钥至少 256 位（32 字节）。

当前配置的密钥过短：
- `admin-secret-key: itcast` (6 字节)
- `user-secret-key: itheima` (7 字节)

**必须更新为至少 32 字节的密钥**，例如：

```yaml
sky:
  jwt:
    admin-secret-key: "your-256-bit-secret-key-here-at-least-32-bytes"
    user-secret-key: "another-256-bit-secret-key-here-at-least-32"
```

生成安全密钥的方法：

```bash
# 使用 OpenSSL 生成随机密钥
openssl rand -base64 32

# 或使用 Java
java -cp commons-codec-1.15.jar org.apache.commons.codec.cli.Digest -a SHA-256 -r $(openssl rand -base64 32)
```

### Fastjson 升级说明

虽然升级到了 1.2.83，但建议长期迁移到：
1. **Fastjson2** (阿里巴巴新版本，更安全)
2. **Jackson** (Spring Boot 默认，更稳定)

### 兼容性测试

建议在以下环境进行完整测试：
- [ ] 开发环境
- [ ] 测试环境
- [ ] 预生产环境
- [ ] 生产环境

## 🐛 常见问题

### Q1: JWT 创建失败，提示密钥太短

**错误信息**: 
```
The signing key's size is 48 bits which is not secure enough for the HS256 algorithm.
```

**解决方案**: 将 JWT 密钥更新为至少 256 位（32 字节）。

### Q2: POI 编译错误

**错误信息**: 
```
cannot find symbol: class XXX
```

**解决方案**: 
1. 检查导入的包名是否正确
2. 查看 POI 5.x 的迁移文档
3. 更新相关的 API 调用

### Q3: MyBatis 配置不生效

**解决方案**: 检查 `application.yml` 中的配置属性名称是否需要更新。

## 📚 参考文档

- [JJWT 0.11 迁移指南](https://github.com/jwtk/jjwt#install-jdk-maven)
- [Apache POI 变更日志](https://poi.apache.org/changes.html)
- [MyBatis Spring Boot 文档](https://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/)
- [Fastjson 安全公告](https://github.com/alibaba/fastjson/wiki/security_update_20170315)

## ✅ 升级检查清单

完成升级后，请确认：

- [ ] 所有依赖版本已更新
- [ ] JwtUtil 代码已更新
- [ ] JWT 密钥已更换为安全的 256 位密钥
- [ ] 项目可以成功编译
- [ ] 所有测试通过
- [ ] 登录功能正常
- [ ] Excel 功能正常（如果有）
- [ ] 数据库操作正常
- [ ] 文件上传功能正常
- [ ] 在测试环境验证通过
- [ ] 性能没有明显下降
- [ ] 日志没有异常错误

---

**最后更新**: 2025-12-09  
**文档版本**: 1.0
