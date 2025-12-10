# 安全策略

## 🔒 报告安全漏洞

如果您发现了安全漏洞，请**不要**公开发布 Issue。请通过以下方式私密报告：

- 发送邮件至项目维护者
- 使用 GitHub 的私密漏洞报告功能

我们会尽快响应并处理您的报告。

## ⚠️ 已知安全问题

### 高优先级

1. **敏感信息泄露**
   - 配置文件中包含明文密码和密钥
   - **状态**: 🔴 需要立即修复
   - **建议**: 使用环境变量或配置中心

2. **弱密码哈希算法**
   - 当前使用 MD5 存储密码
   - **状态**: 🔴 需要立即修复
   - **建议**: 迁移到 BCrypt 或 Argon2

3. **依赖漏洞**
   - Fastjson 1.2.76 存在已知的 RCE 漏洞
   - **状态**: 🔴 需要立即修复
   - **建议**: 升级到 Fastjson2 或切换到 Jackson

### 中优先级

1. **JWT 密钥强度不足**
   - 使用简单的字符串作为密钥
   - **建议**: 使用至少 256 位的随机密钥

2. **缺少速率限制**
   - API 端点没有速率限制
   - **建议**: 添加限流保护

## 🛡️ 安全最佳实践

### 开发环境

1. **永远不要提交敏感信息**
   - 使用 `.gitignore` 排除配置文件
   - 使用模板文件代替实际配置

2. **使用强密码**
   - 数据库密码至少 16 字符
   - 包含大小写字母、数字和特殊字符

3. **定期更新依赖**
   ```bash
   mvn versions:display-dependency-updates
   ```

### 生产环境

1. **使用 HTTPS**
   - 所有生产环境必须使用 HTTPS
   - 配置 HSTS 头

2. **环境隔离**
   - 使用不同的密钥和配置
   - 限制数据库访问权限

3. **监控和日志**
   - 启用安全审计日志
   - 监控异常登录尝试
   - 不要记录敏感信息

4. **访问控制**
   - 实施最小权限原则
   - 定期审查权限配置

## 📋 安全检查清单

在部署前，请确保：

- [ ] 所有密码都使用强随机值
- [ ] JWT 密钥已更换为强随机密钥（至少 256 位）
- [ ] 配置文件不包含敏感信息
- [ ] 所有依赖都已更新到最新安全版本
- [ ] 启用了 HTTPS
- [ ] 配置了适当的 CORS 策略
- [ ] 添加了速率限制
- [ ] 启用了安全头（如 X-Content-Type-Options, X-Frame-Options 等）
- [ ] 数据库连接使用了最小权限账号
- [ ] Redis 配置了密码保护
- [ ] 日志不包含敏感信息
- [ ] 文件上传有大小和类型限制

## 🔐 推荐的安全配置

### Spring Security 配置示例

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // 根据实际情况配置
            .headers()
                .contentTypeOptions()
                .and()
                .xssProtection()
                .and()
                .frameOptions().deny()
            .and()
            .authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated();
        return http.build();
    }
}
```

### 环境变量配置示例

```bash
# .env 文件（不要提交到版本控制）
DB_PASSWORD=your_strong_password_here
REDIS_PASSWORD=your_redis_password
JWT_SECRET=your_256_bit_random_secret
ALIOSS_ACCESS_KEY=your_access_key
ALIOSS_SECRET=your_secret_key
```

## 📚 参考资源

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security 文档](https://docs.spring.io/spring-security/reference/index.html)
- [JWT 最佳实践](https://tools.ietf.org/html/rfc8725)
- [密码存储备忘录](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html)

## 📝 更新日志

| 日期 | 版本 | 变更内容 |
|------|------|----------|
| 2025-12-09 | 1.0 | 初始版本，记录已知安全问题 |

---

**最后更新**: 2025-12-09
