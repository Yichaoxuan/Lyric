# 逝流 - 智能日记后端

基于 Spring Boot 3 + Spring AI 的智能日记服务端应用，集成阿里云通义千问大模型实现 AI 智能分析功能。

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 21 | 开发语言 |
| Spring Boot | 3.5.7 | 核心框架 |
| Spring AI | 1.1.2 | AI 大模型集成框架 |
| MyBatis | 3.0.5 | ORM 框架 |
| SQLite | 3.51 | 数据库 |
| Lombok | - | 减少样板代码 |
| Jackson | - | JSON 处理 |
| HttpClient5 | - | HTTP 客户端（调用地图 API）|
| Geohash | 1.4 | 地理哈希计算 |

## AI 模型配置

- **模型提供商**：阿里云 DashScope
- **模型名称**：qwen3.5-35b-a3b（通义千问）
- **接入方式**：OpenAI 兼容接口

## 功能模块

### 核心 API 接口

| 模块 | 路径前缀 | 功能说明 |
|------|----------|----------|
| 日记管理 | `/diary` | 日记的增删改查、AI 分析 |
| 标签管理 | `/tag` | 标签 CRUD 操作 |
| 天气服务 | `/weather` | 天气数据获取与记录 |
| 文件服务 | `/file` | 图片/文件上传下载 |
| 系统设置 | `/settings` | 用户偏好设置 |
| AI 消息 | `/messages` | AI 对话消息管理 |

### 主要功能

- 📝 **日记 CRUD**：完整的日记生命周期管理
- 🤖 **AI 智能交互**：基于通义千问模型的日记内容分析与对话
- 🗂️ **标签体系**：灵活的标签分类与管理
- 🌤️ **天气集成**：天气数据查询与关联
- 📁 **文件存储**：本地文件上传，支持缩略图自动生成
- 🗺️ **地理编码**：调用高德地图 API 进行地址解析与地理哈希计算
- 🔒 **全局异常处理**：统一的异常响应格式
- ✅ **参数校验**：请求参数验证

## 项目结构
src/main/java/com/lyric/lyric/
├── Config/
│   ├── JacksonConfig.java    # JSON 序列化配置
│   └── WebConfig.java        # Web 配置（跨域等）
├── Exception/
│   ├── BusinessException.java    # 业务异常
│   ├── GlobalExceptionHandler.java # 全局异常处理器
│   └── SystemException.java       # 系统异常
├── LyricApplication.java          # 启动类
└── ...                            # 其他业务模块

src/main/resources/
├── application.yml        # 主配置文件
├── message-config.yml     # AI 提示词配置
└── user-settings.yml      # 用户设置配置


## 数据库

- **数据库类型**：SQLite
- **数据库文件**：`data/Lyric_dev.db`
- **ORM 框架**：MyBatis
- **命名策略**：下划线转驼峰自动映射

## 开发环境要求

- JDK >= 21
- Maven 3.6+

## 安装与运行

```bash
# 使用 Maven Wrapper 构建（推荐）
./mvnw spring-boot:run

# 或使用本地 Maven
mvn spring-boot:run
```

服务启动后访问：`http://localhost:8080`

## 配置说明

### application.yml 主配置

- 应用名称：逝流
- AI 模型：通义千问 qwen3.5-35b-a3b
- 数据库：SQLite 本地文件数据库
- 文件上传限制：单文件 20GB，总请求 100GB

### 外部依赖

- **阿里云 DashScope API**：AI 模型服务
- **高德地图 API**：地理编码与定位服务
