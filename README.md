# OnlySec-Labs-无相

OnlySec-Labs-无相 是一个基于Spring Boot的网络安全实训靶场，用于学习和实践Web安全相关知识。该靶场提供了多种常见Web安全漏洞的实战环境，帮助学习者深入理解各类安全漏洞的原理、利用方式和防御措施。

## 目前只实现一些基础功能,后续持续模块开发中

## web漏洞
![图片](https://github.com/user-attachments/assets/c9d461d4-af84-4cfb-9ef3-4fb4c8212b71)

## 前端加解密
![图片](https://github.com/user-attachments/assets/113ca19f-4720-4071-83a7-4ea8e70bca7d)

## docker模块

![图片](https://github.com/user-attachments/assets/139fcddc-9723-4100-b450-f543264411f7)

### 通过dcokerapi连控制docker容器,自行百度进行开启
![图片](https://github.com/user-attachments/assets/908d857a-bba6-419a-82ce-033831d8bd23)
<img width="1264" alt="图片" src="https://github.com/user-attachments/assets/c13d5c82-c7c0-45c3-8785-a8c63989b357" />


### 支持dockerfile创建容器
![图片](https://github.com/user-attachments/assets/ae295b3b-a5aa-461e-b765-4c42a1aa1de0)


## 技术栈

- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Security** - 用于安全认证和授权
- **MyBatis** - ORM框架
- **Thymeleaf** - 模板引擎
- **MySQL** - 主数据库
- **H2 Database** - 测试环境数据库
- **Log4j2** - 日志框架
- **Docker** - 容器化支持
- **Lombok** - 代码简化工具
- **BouncyCastle** - 加密库

## 项目特性

- 基于Spring Security的安全认证框架
- 完整的日志系统（使用Log4j2）
- Docker容器支持
- 数据库访问层（MyBatis）
- 前端模板引擎（Thymeleaf）
- 参数校验
- 单元测试支持

## 项目结构

```
src/main/java/com/only/www/onlyseclabs/
├── config/         # 配置类
├── controller/     # 控制器
├── entity/        # 实体类
├── mapper/        # MyBatis映射器
├── service/       # 服务层
└── utils/         # 工具类
```

## 快速开始

### 环境要求

- JDK 17 或更高版本
- Maven 3.6+
- MySQL 8.0+
- Docker（可选）

### 构建和运行

1. 克隆项目：
```bash
git clone [项目地址]
```

2. 配置数据库：
   - 在 `application.properties` 或 `application.yml` 中配置数据库连接信息

3. 构建项目：
```bash
mvn clean package
```

4. 运行应用：
```bash
java -jar target/OnlySec-Labs-0.0.1-SNAPSHOT.jar
```


## 测试

运行单元测试：
```bash
mvn test
```

## 安全特性

- 使用Spring Security进行身份认证和授权
- 集成BouncyCastle提供额外的加密算法支持
- 安全的HTTP客户端实现

## 贡献指南

欢迎提交Issue和Pull Request来帮助改进项目。

## 许可证

[待定]

## 靶场特点

### 1. 漏洞类型覆盖全面
- **认证与授权漏洞**
  - 弱密码爆破（MD5、SM4、AES等加密方式）
  - 签名验证绕过
  - 验证码绕过
  - 会话管理缺陷

- **注入类漏洞**
  - 命令注入（Command Injection）
  - SQL注入
  - XXE注入
  - 文件读取漏洞

- **文件操作漏洞**
  - 文件上传漏洞
  - 任意文件读取
  - 目录遍历

- **其他常见漏洞**
  - CSRF（跨站请求伪造）
  - SSRF（服务器端请求伪造）
  - 竞态条件（Race Condition）
  - 对象反序列化漏洞
  - 重定向漏洞

### 2. 实战环境特点
- **Docker容器化环境**
  - 支持快速部署和重置靶场环境
  - 提供容器管理接口
  - 支持实时查看容器日志
  - 支持容器状态监控

- **多场景模拟**
  - 提供多种加密方式的登录场景
  - 模拟真实业务场景
  - 支持自定义漏洞环境

- **安全防护机制**
  - 集成Spring Security框架
  - 支持多种加密算法（BouncyCastle）
  - 完善的日志记录系统

### 3. 学习价值
- 提供漏洞原理说明
- 包含漏洞利用演示
- 提供防御方案建议
- 支持漏洞复现实验
- 适合CTF训练和实战演练


