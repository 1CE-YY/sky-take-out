# 安全配置指南

## 🔐 JWT密钥安全

### 当前状态
- ✅ 已修复：JWT密钥现在使用环境变量配置
- ✅ 已提供默认强密钥格式
- ✅ 已创建.env.example模板

### 生产环境操作步骤

1. **生成强密钥**
```bash
# 生成管理员JWT密钥
openssl rand -base64 64

# 生成用户JWT密钥
openssl rand -base64 64
```

2. **设置环境变量**
```bash
# 创建.env文件
cp .env.example .env

# 编辑.env文件，设置实际的强密钥
# JWT_ADMIN_SECRET_KEY=your-generated-admin-secret-key
# JWT_USER_SECRET_KEY=your-generated-user-secret-key
```

3. **重启应用**
```bash
# 确保应用加载新的环境变量
./mvnw spring-boot:run
```

## 🚨 重要安全提醒

### 必须修改的配置项
- `JWT_ADMIN_SECRET_KEY`: 必须改为64位以上的Base64编码字符串
- `JWT_USER_SECRET_KEY`: 必须改为64位以上的Base64编码字符串
- 数据库密码：使用强密码
- Redis密码：使用强密码
- 支付相关密钥：使用微信支付官方提供的真实密钥

### 密钥强度要求
- 最小长度：64字符
- 包含大小写字母、数字、特殊字符
- 定期轮换（建议每3-6个月）
- 避免使用常见词汇或可预测模式

## 🔒 其他安全建议

### 1. 数据库安全
- 使用强密码策略
- 限制数据库访问IP
- 启用SQL审计日志

### 2. Redis安全
- 启用密码认证
- 绑定本地IP
- 定期备份

### 3. 应用安全
- 启用HTTPS
- 设置CORS策略
- 实施API限流
- 日志监控和报警

### 4. 支付安全
- 使用官方SDK
- 验证支付回调签名
- 定期检查支付记录

## 🛡️ 安全检查清单

- [ ] JWT密钥已改为强密钥
- [ ] 数据库密码为强密码
- [ ] Redis密码已启用
- [ ] 支付密钥为官方提供的真实密钥
- [ ] 环境变量文件(.env)已添加到.gitignore
- [ ] 生产环境启用HTTPS
- [ ] 实施日志监控
- [ ] 定期安全审计

## 📞 如有安全问题

如发现安全漏洞或有安全相关的问题，请立即联系开发团队。