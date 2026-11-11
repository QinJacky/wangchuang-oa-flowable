# 忘川 OA 工作流系统（wangchuan-oa-flowable）

轻量级 OA 工作流系统，基于 **RuoYi-Vue** 基座整合 **Flowable** 工作流引擎。

---

## 一、技术栈

| 分类 | 技术 | 版本 |
| --- | --- | --- |
| 核心框架 | Spring Boot | 2.5.15 |
| 安全框架 | Spring Security | 5.7.12 |
| 持久层 | MyBatis / MyBatis-Plus | mybatis-spring-boot 2.2.2 |
| 工作流 | Flowable | 随工程依赖引入（首次启动自动建 `act_*` 表） |
| 数据库连接池 | Druid | 1.2.23 |
| 数据库驱动 | MySQL Connector/J | 8.0.33 |
| 缓存 | Redis（Lettuce）+ Redisson | 3.16.2 |
| JSON | fastjson2 | 2.0.57 |
| 鉴权 | JWT | 0.9.1 |
| 分页 | PageHelper | 1.4.7 |
| 接口文档 | Swagger | 3.0.0 |
| 其他 | MapStruct、POI、Kaptcha、yauaa 等 | — |
| JDK | Java 8（1.8） | — |

---

## 二、环境要求

- **JDK**：1.8 及以上
- **Maven**：3.6 及以上
- **MySQL**：5.7 / 8.0
- **Redis**：2.8 及以上（默认 `127.0.0.1:6379`，库 0，密码 `cw123456`）

> 注：Redis 与 MySQL 需提前启动并可达，否则应用启动失败。

---

## 三、模块说明

```
wangchuan-oa-flowable
├── pom.xml                  # 父工程（groupId=com.wangchuan，artifactId=wangchuan，version=1.0.0）
├── wangchuan-admin/         # 后台管理 / Web 启动模块（含启动类、Controller、Service、工作流业务）
├── wangchuan-common/        # 公共模块（工具类、核心 domain、Redis 封装、安全、常量等）
├── sql/                     # 数据库脚本
│   ├── table.sql           # 表结构（需先执行）
├── doc/                     # 其他文档
└── LICENSE
```

---

## 四、本地部署步骤

### 1. 初始化数据库

```bash
# 登录 MySQL 后执行（顺序不能反）
mysql> source /path/to/sql/table.sql
```

- `table.sql`：建表（含业务表与字典、用户等基础表）。

### 2. 调整基础配置

主要配置文件在 `wangchuan-admin/src/main/resources/`：

| 文件 | 说明 |
| --- | --- |
| `application.yml` | 公共配置（端口、Redis、MySQL 地址、token、MyBatis、XSS 等） |
| `application-dev.yml` | 开发环境（默认激活） |
| `application-test.yml` | 测试环境 |
| `application-prod.yml` | 生产环境 |

需要修改的常见项：

- `spring.datasource`：MySQL 连接地址 / 账号 / 密码
- `spring.redis`：Redis 地址 / 密码（默认 `127.0.0.1:6379`，密码 `cw123456`）
- `token.secret`：令牌密钥（生产环境请替换为强随机值）

### 3. 编译打包

在工程根目录执行：

```bash
mvn clean install -DskipTests
```

打包完成后，启动模块为 `wangchuan-admin`：

```bash
java -jar wangchuan-admin/target/wangchuan-admin-1.0.0.jar
```

开发阶段也可直接在 IDE 中运行 `com.wangchuan.wangchuanApplication` 启动类（已开启 devtools 热部署）。

---

## 五、多环境切换

环境通过 Spring Profile 控制，**不是 Maven Profile**：

- 默认激活环境：`dev`（`application.yml` 中 `spring.profiles.active: dev`）。
- 切换环境使用启动参数：

```bash
java -jar wangchuan-admin-1.0.0.jar --spring.profiles.active=prod
```

---

## 六、访问信息

| 项目 | 值 |
| --- | --- |
| 服务端口 | `8080` |
| 上下文路径 | `/` |
| 后端接口前缀 | `/` （按模块划分，如 `/system`、`/workflow`） |
| Swagger 文档 | `http://localhost:8080/swagger-ui/index.html` |
| 默认管理员 | 见 `sql/data.sql` 中初始账号（如 `admin`） |

---

## 七、关键配置速查（`application.yml`）

```yaml
server:
  port: 8080
  servlet:
    context-path: /

spring:
  profiles:
    active: dev          # 默认 dev
  redis:
    host: 127.0.0.1
    port: 6379
    password: cw123456
    database: 0

token:
  header: Authorization
  secret: abcdefghijklmnopqrstuvwxyz

mybatis:
  typeAliasesPackage: com.wangchuan.**.domain
  mapperLocations: classpath*:mapper/**/*Mapper.xml

flowable:
  database-schema-update: true   # 首次启动自动建流程表
  async-executor-activate: false
```

> 注意：本项目根包为 `com.wangchuan`，MyBatis 别名扫描与 Mapper 扫描均基于该根包（`com.wangchuan.**.mapper`）。

---
