# PluxParty

> 高性能轻量级 Minecraft 组队插件 - 支持队伍管理/邀请系统/快速匹配/队伍聊天/成员集合 | 兼容 1.7.10 ~ 26.1+

![version](https://img.shields.io/badge/version-1.0.0-orange)
![Java](https://img.shields.io/badge/Java-8-blue)
![Folia](https://img.shields.io/badge/Folia-supported-green)

## 功能特性

- 👥 **队伍系统** - 完整的队伍创建、管理、解散功能
- 📩 **邀请系统** - 邀请玩家加入队伍，支持过期验证
- ⚡ **快速匹配** - 自动匹配加入未满队伍
- 💬 **队伍聊天** - 独立队伍聊天频道，支持格式化前缀
- 📍 **成员集合** - 队长一键召集所有队员到身边
- 🔢 **队伍ID** - 自动生成6位数字队伍ID，简洁易记
- 📊 **队伍信息** - 查看队伍成员、在线状态等详细信息
- 📜 **命令补全** - 完整的命令Tab补全支持
- 🔒 **权限系统** - 完善的权限节点控制
- 📈 **高性能** - 使用 ConcurrentHashMap 保证线程安全
- 🌐 **全版本兼容** - 支持 1.7.10 ~ 26.1+ 所有版本

## 兼容版本

- Minecraft 1.7.10 ~ 26.1+
- 支持 Folia 服务端
- 支持 Spigot / Paper / Purpur 等主流服务端

## 软依赖

| 插件 | 作用 |
|------|------|
| PlaceholderAPI | 变量支持（可选） |

## 快速开始

### 安装

1. 下载最新版本的 `PluxParty.jar`
2. 将插件放入服务器的 `plugins` 文件夹
3. 启动服务器，插件将自动生成配置文件
4. 根据需要修改 `plugins/PluxParty/config.yml` 配置文件
5. 执行 `/partyadmin reload` 重载配置

### 编译

项目使用 Gradle 构建：

```bash
gradle clean build
```

编译完成后，jar 文件位于 `build/libs/` 目录下。

## 命令列表

### 玩家命令

| 命令 | 描述 | 用法 |
|------|------|------|
| `/party` | 队伍主指令 | `/party <子命令>` |
| `/party create` | 创建队伍 | `/party create` |
| `/party invite <玩家>` | 邀请玩家 | `/party invite PlayerName` |
| `/party accept <玩家>` | 接受邀请 | `/party accept PlayerName` |
| `/party deny <玩家>` | 拒绝邀请 | `/party deny PlayerName` |
| `/party join [队伍ID]` | 加入队伍 | `/party join 114514` |
| `/party leave` | 离开队伍 | `/party leave` |
| `/party kick <玩家>` | 踢出成员 | `/party kick PlayerName` |
| `/party transfer <玩家>` | 转让队长 | `/party transfer PlayerName` |
| `/party disband` | 解散队伍 | `/party disband` |
| `/party info` | 查看队伍信息 | `/party info` |
| `/party list` | 查看队伍列表 | `/party list` |
| `/party chat <消息>` | 队伍频道发言 | `/party chat Hello` |
| `/party warp` | 召集队员 | `/party warp` |
| `/party help` | 显示帮助 | `/party help` |

### 管理员命令

| 命令 | 描述 | 用法 | 权限 |
|------|------|------|------|
| `/partyadmin reload` | 重载配置 | `/partyadmin reload` | op |
| `/partyadmin force <队伍ID>` | 强制解散队伍 | `/partyadmin force 114514` | op |
| `/partyadmin list` | 查看所有队伍 | `/partyadmin list` | op |

## 权限节点

| 权限节点 | 描述 | 默认值 |
|----------|------|--------|
| `party.*` | 所有队伍权限 | op |
| `party.admin` | 管理员权限 | op |
| `party.create` | 创建队伍 | true |
| `party.invite` | 邀请玩家加入队伍 | op |
| `party.join` | 加入队伍 | true |
| `party.leave` | 离开队伍 | true |
| `party.kick` | 踢出队伍成员 | op |
| `party.transfer` | 转让队长 | op |
| `party.disband` | 解散队伍 | op |
| `party.chat` | 在队伍频道发言 | true |
| `party.warp` | 召集队员 | op |
| `party.info` | 查看队伍信息 | true |

## 配置说明

### 队伍配置

```yaml
party:
  max-members: 8       # 队伍最大成员数
  warp-cooldown: 5     # 召集冷却时间（秒）
```

### 邀请配置

```yaml
invites:
  timeout: 60          # 邀请过期时间（秒）
  max-pending: 5       # 最大待处理邀请数
```

### 聊天配置

```yaml
chat:
  enabled: true        # 队伍聊天频道开关
  prefix: "&7[&6队伍&7] "  # 聊天前缀
```

## 文件结构

```
PluxParty/
├── src/main/java/com/plux/party/
│   ├── PluxParty.java             # 插件主类
│   ├── party/
│   │   ├── Party.java             # 队伍接口
│   │   ├── PlayerParty.java       # 队伍实现类
│   │   └── PartyManager.java      # 队伍管理器
│   ├── commands/
│   │   ├── PartyCommand.java      # 队伍主命令
│   │   └── PartyAdminCommand.java # 管理员命令
│   ├── listeners/
│   │   ├── PartyChatListener.java # 队伍聊天监听器
│   │   └── PartyPlayerListener.java # 玩家事件监听器
│   ├── config/
│   │   └── ConfigManager.java     # 配置管理器
│   ├── api/
│   │   ├── PartyAPI.java          # 公开 API
│   │   └── events/                # 自定义事件
│   │       ├── PartyCreateEvent.java
│   │       ├── PartyDisbandEvent.java
│   │       ├── PartyMemberJoinEvent.java
│   │       ├── PartyMemberLeaveEvent.java
│   │       └── PartyTransferEvent.java
│   ├── placeholder/
│   │   └── PartyPlaceholderExpansion.java # PlaceholderAPI 扩展
│   └── util/
│       ├── VersionUtil.java       # 版本检测工具
│       └── MaterialAdapter.java   # 材质适配工具
├── src/main/resources/
│   ├── plugin.yml                 # 插件描述文件
│   ├── config.yml                 # 主配置文件
│   └── messages.yml               # 消息配置文件
├── build.gradle                   # Gradle 构建配置
└── README.md                      # 项目说明文档
```

## 开发者信息

- **作者**: ya_xzer21145
- **Java 版本**: 8+
- **构建工具**: Gradle

## 许可证

MIT License 3.0
