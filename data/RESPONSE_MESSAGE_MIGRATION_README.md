# 响应消息数据库重构指南

## 概述
响应消息配置已从 YAML 配置文件迁移到 SQLite 数据库存储。

## 主要变更

### 1. 新增文件
- `ResponseMessagePojo.java` - 响应消息实体类
- `ResponseMessageMapper.java` - 数据访问层接口
- `ResponseMessageService.java` - 业务服务层
- `MsgConfig.java` (已修改) - 配置类，现在从数据库加载
- `migration_response_message.sql` - 数据迁移脚本

### 2. 修改文件
- `application.yml` - 移除了 `message-config.yml` 的引用
- `MessageService.java` - 更新配置保存方法调用
- `MsgConfig.java` - 改为从数据库加载配置

## 使用步骤

### 第一步：执行数据迁移
在数据库中运行迁移脚本，将现有配置导入数据库：

```sql
-- 在项目根目录执行
sqlite3 D:/Lyric/backend/Lyric/data/Lyric_dev.db < data/migration_response_message.sql
```

或者在数据库管理工具中打开 `data/migration_response_message.sql` 并执行。

### 第二步：验证表结构
确保 `response_message` 表已创建，结构如下：

```sql
CREATE TABLE response_message (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    message_key VARCHAR(100) NOT NULL UNIQUE,
    message_type VARCHAR(20) NOT NULL,
    code VARCHAR(10) NOT NULL,
    message TEXT NOT NULL,
    CHECK ( message_type IN ('SUCCESS','business-error','system-error') )
);
```

### 第三步：启动应用
启动 Spring Boot 应用，系统会自动从数据库加载响应消息配置。

查看日志确认配置加载成功：
```
开始从数据库加载消息配置...
数据库消息配置加载完成：业务错误 XX 条，系统错误 X 条，成功消息 XX 条
```

## 功能说明

### 配置加载流程
1. 应用启动时，`MsgConfig` 构造函数自动调用 `ResponseMessageService`
2. 从数据库查询所有启用的响应消息
3. 按类型分组存储到内存 Map 中
4. 枚举类从内存 Map 中获取最新配置

### 配置更新流程
1. 调用 `/messages/updateMessageConfig` 接口
2. AI 生成新的响应消息配置
3. 更新内存中的配置
4. 批量保存到数据库（INSERT OR REPLACE）
5. 重新初始化枚举值

### 优势
- ✅ 动态管理：无需重启即可更新配置
- ✅ 版本控制：可追踪配置历史
- ✅ 多环境支持：不同环境使用不同数据库记录
- ✅ 管理友好：易于开发后台管理界面

## 注意事项

1. **首次使用必须执行迁移脚本**，否则应用启动时会提示配置为空
2. 数据库表名是 `response_message`（单数），不是复数
3. 消息类型使用 `SUCCESS`（大写），不是 `success`
4. 如果表结构中不包含 `created_at` 和 `updated_at` 字段，已在代码中移除相关 SQL

## 常见问题

### Q: 应用启动时报配置加载失败？
A: 检查是否已执行迁移脚本，确保 `response_message` 表中有数据。

### Q: 如何添加新的响应消息？
A: 有两种方式：
   1. 直接在数据库中插入 SQL 记录
   2. 通过 AI 配置接口动态更新

### Q: 原来的 YAML 配置文件还有用吗？
A: 不再使用。所有配置已迁移到数据库，YAML 文件可以删除或保留作为备份。

## 后续优化建议

1. 添加缓存机制（如 Redis）减少数据库查询
2. 提供管理 API 用于动态增删改查
3. 添加配置版本历史和回滚功能
4. 考虑国际化支持（添加 language 字段）
