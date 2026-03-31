---
name: dev-runner
description: Orchestrate sub-agents to implement Android Java code from modules/ specifications. Reads docs/modules/ specifications and shared_interfaces.json for dependency order, generates shared foundation first, then dispatches parallel module coding, and finally merges + compiles + tests.
---

# 总工编码调度器

读取 `docs/modules/` 中的全部中间产物，按依赖拓扑分阶段调度 subagent 完成全量编码，最后合并资源、编译、测试。

## 核心原则

**总工不写业务代码，只做三件事：搭基础、派任务、收尾验收。**

1. Phase 0 — 自己搭建共享基础层（model / interface / constant / resource）
2. Phase 1-7 — 按 `shared_interfaces.json → dependency_graph.build_order` 分批派发 subagent
3. Phase 8 — 合并资源、解决冲突、编译测试

**每个 subagent 只负责一个模块。** 同一 Phase 内的模块可并行。前置 Phase 必须全部完成后才能开始下一 Phase。

## 前置条件

- `docs/modules/index.json` — 全部模块清单
- `docs/modules/shared_interfaces.json` — 全局共享协议 + 依赖图
- `docs/modules/design_tokens.json` — 全局颜色 + 切图清单
- `docs/modules/<screen_id>/manifest.json` — 每个模块的完整规格
- `docs/modules/<screen_id>/README.md` — 交互规格
- `docs/modules/<screen_id>/sketch_data.json` — 标注数据
- `app/app/build.gradle.kts` — 已配置好的构建文件
- 切图资源目录：`输入/需求/切图/*.png`

## 工作流程

### Phase 0: 共享基础层（总工亲自执行）

读取 `shared_interfaces.json`，生成所有模块共享的代码和资源。

#### 0.1 共享数据模型

从 `shared_models` 生成 Java 类到 `com.example.btphone.model/`：

```
CallState.java          — enum（IDLE/DIALING/ALERTING/RINGING/ACTIVE/HELD/TERMINATED）
CallInfo.java           — class（mPhoneNumber/mContactName/mNumberType/mCallDirection/mStartTimeMillis/mIsConference）
CallDirection.java      — enum（INCOMING/OUTGOING）
NumberType.java         — enum（PHONE/OFFICE/HOME/OTHER/UNKNOWN）
AudioRoute.java         — enum（CAR/PHONE）
BluetoothConnectionState.java — enum（DISCONNECTED/CONNECTING/CONNECTED/CONNECTED_WITH_PBAP）
SyncState.java          — enum（IDLE/SYNCING/SUCCESS/FAILED）
SyncType.java           — enum（CONTACTS/CALL_LOG）
Contact.java            — class（已存在则更新使其与 shared_models 一致）
PhoneNumber.java        — class
CallRecord.java         — class
CallRecordType.java     — enum（OUTGOING/MISSED/INCOMING）
```

每个 enum/class 严格按照 `shared_interfaces.json` 中的字段定义生成，遵循 `m` 前缀命名规范。

#### 0.2 服务接口

从 `shared_services` 生成接口到 `com.example.btphone.service/`：

```
IBluetoothPhoneService.java    — 16 个方法签名
IContactsSyncService.java      — 9 个方法签名
VehiclePropertyBridge.java     — 4 个静态方法签名（先写接口/抽象类，具体实现由对应 subagent 完成）
SteeringWheelHandler.java      — 2 个方法签名
```

#### 0.3 回调接口

从 `callbacks.interfaces` 生成：

```
IPhoneCallback.java    — 5 个回调方法
ISyncCallback.java     — 3 个回调方法
```

#### 0.4 事件类

从 `event_bus_protocol.events` 生成到 `com.example.btphone.event/`：

```
BtConnectionStateEvent.java
CallStateChangedEvent.java
AudioRouteChangedEvent.java
SyncStateChangedEvent.java
PrivacyModeChangedEvent.java
```

#### 0.5 全局常量

从 `global_constants` 生成 `com.example.btphone.common.PhoneConstants.java`，包含所有 13 个常量。

#### 0.6 全局资源

**切图资源：** 从 `design_tokens.json → cut_images` 列表，将 `输入/需求/切图/` 目录下所有列出的 PNG 复制到 `app/app/src/main/res/drawable/`。

注意文件名清洗：Android drawable 文件名只允许小写字母、数字、下划线。需要处理：
- 空格 → 下划线
- 中文字符 → 移除或音译
- 特殊字符 → 移除
- 大写 → 小写

**colors.xml：** 从 `design_tokens.json → colors` 提取所有颜色，生成 `res/values/colors.xml`。颜色名转为合法 Android 资源名（替换 `/` 和中文）。

**dimens.xml：** 提取 sketch_data.json 中的公共尺寸（控件宽高、字号、间距），生成 `res/values/dimens.xml`。

**strings.xml：** 生成基础框架层字符串（应用名、公共提示文案如"通话结束"、"无联系人"、"暂无通话记录"等）。各模块的字符串由模块 subagent 追加。

#### 0.7 基础单元测试

为 Phase 0 生成的每个 model/enum/event/constant 类编写对应的单元测试。

#### 0.8 验证

```bash
cd app && ./gradlew assembleDebug
```

Phase 0 必须编译通过后，才能进入 Phase 1。

---

### Phase 1-7: 模块编码（派发 subagent）

按 `dependency_graph.build_order` 顺序执行：

| Phase | 模块 | 并行度 |
|-------|------|--------|
| 1 | bt_connection, tbox_call | 2 |
| 2 | bt_sync | 1 |
| 3 | call_records, contacts_list | 2 |
| 4 | dialer, incoming_call, wechat_call | 3 |
| 5 | outgoing_call | 1 |
| 6 | in_call | 1 |
| 7 | three_way_call, privacy_mode | 2 |

**同一 Phase 内的模块并行启动 subagent，所有 subagent 完成后才进入下一 Phase。**

#### 每个 subagent 的任务描述模板

派发 subagent 时，必须提供以下完整上下文（因为 subagent 没有会话历史）：

```
你是蓝牙电话 Android 应用的模块开发者。请完成 {screen_id} 模块的全量编码。

## 项目信息
- 包名：com.example.btphone
- 语言：Java 11（禁止 Kotlin）
- 布局：ConstraintLayout + ViewBinding
- 命名规范：成员变量 m 前缀、静态变量 s 前缀、常量 UPPER_SNAKE_CASE
- 字符串/颜色/尺寸必须提取到 XML 资源

## 你的模块
- screen_id: {screen_id}
- 模块名: {name}

## 输入文件（必须全部读取）
1. docs/modules/{screen_id}/manifest.json — 模块完整规格（SDK API、依赖、切图列表）
2. docs/modules/{screen_id}/README.md — 交互规格（功能描述、状态机、后端交互、SSRD 需求）
3. docs/modules/{screen_id}/sketch_data.json — UI 标注数据（每个控件的坐标、尺寸、字号、颜色）
4. docs/modules/shared_interfaces.json — 全局共享协议（服务接口、事件、模型）
5. docs/modules/design_tokens.json — 全局颜色 token

## 已有的共享代码（Phase 0 已生成，直接 import 使用，不要重复创建）
- com.example.btphone.model.* — 所有共享数据模型（CallState, CallInfo, Contact 等）
- com.example.btphone.service.IBluetoothPhoneService — 电话服务接口
- com.example.btphone.service.IContactsSyncService — 同步服务接口
- com.example.btphone.service.IPhoneCallback — 电话回调
- com.example.btphone.service.ISyncCallback — 同步回调
- com.example.btphone.event.* — 所有事件类
- com.example.btphone.common.PhoneConstants — 全局常量
- res/drawable/ — 所有切图已复制到位
- res/values/colors.xml — 已有全局颜色

## 你需要输出的文件

### 1. Java 源码
路径: app/app/src/main/java/com/example/btphone/

按模块功能创建类，参考已有的包结构：
- ui/{模块包名}/ — Activity / Fragment / 自定义 View
- adapter/ — RecyclerView Adapter（如需）
- 如果模块需要 Service 实现（如 bt_connection 需要实现 BluetoothPhoneService），放在 service/

### 2. XML 布局
路径: app/app/src/main/res/layout/

- 文件名: activity_{screen_id}.xml 或 fragment_{screen_id}.xml
- 使用 ConstraintLayout
- 控件坐标和尺寸从 sketch_data.json 的 layer rect 获取
- 切图引用: @drawable/{cut_image_name_without_extension}
- 颜色引用: @color/{color_name} 或直接使用 argb-hex
- 字号/间距引用: @dimen/{dimen_name} 或直接使用 dp/sp

### 3. 模块资源
- res/values/strings.xml — 追加本模块的字符串（注意不要删除已有的，只追加新的）
- res/values/dimens.xml — 追加本模块的尺寸（同上）

### 4. 单元测试
路径: app/app/src/test/java/com/example/btphone/

- 每个 public 类必须有对应的 Test 类
- 方法命名: 被测方法_场景_期望结果
- 使用 JUnit 4 + Mockito + Robolectric
- Given-When-Then 结构
- 覆盖所有 public 方法和分支

### 5. AndroidManifest.xml 条目
- 如果新增了 Activity，把需要添加的 <activity> 声明写入
  app/app/src/main/AndroidManifest.xml
- 如果新增了 Service，同理添加 <service> 声明

## 重要约束
- 不要修改其他模块的代码
- 不要重新创建 Phase 0 已有的共享类
- 使用共享服务接口（IBluetoothPhoneService 等）与其他模块交互
- 通过事件（event 包）进行模块间通信
- 所有 UI 文本硬编码必须提取到 strings.xml
- sketch_data.json 中的坐标是绝对坐标（1920x1080 画布），转为布局时需计算相对位置和 dp 值
  （画布区域为 x:616-1920, y:56-960，即应用区域 1304x904 px）
- 编写完后执行编译验证: cd app && ./gradlew assembleDebug
```

#### subagent 完成后的验证

每个 subagent 完成后，总工检查：
1. 预期的 Java 文件是否存在
2. 预期的 layout XML 是否存在
3. 预期的 Test 文件是否存在
4. 增量编译是否通过

如果某个 subagent 失败，总工尝试修复或重新派发。

---

### Phase 8: 合并与验收（总工亲自执行）

#### 8.1 资源合并去重

**strings.xml：** 扫描所有模块生成的 strings.xml，合并去重。如果同名 key 值不同，保留最后一个并打印警告。

**colors.xml：** 合并所有模块追加的颜色，去重。

**dimens.xml：** 合并所有模块追加的尺寸，去重。

**AndroidManifest.xml：** 确认所有 Activity/Service/Receiver 已声明。

#### 8.2 build.gradle.kts 依赖

检查各模块是否引入了额外依赖（如 contacts_list 需要 pinyin4j），统一追加到 `app/app/build.gradle.kts`。

#### 8.3 全量编译

```bash
cd app && ./gradlew clean assembleDebug 2>&1
```

如果编译失败：
1. 读取错误日志
2. 定位问题文件和行号
3. 修复后重新编译
4. 最多重试 3 轮

#### 8.4 全量单元测试

```bash
cd app && ./gradlew testDebugUnitTest 2>&1
```

如果测试失败：
1. 读取失败的测试用例
2. 分析原因（是测试写错还是业务代码 bug）
3. 修复后重跑
4. 最多重试 3 轮

#### 8.5 覆盖率报告

```bash
cd app && ./gradlew jacocoTestReport 2>&1
```

输出覆盖率概要。

#### 8.6 最终报告

生成 `docs/build_report.md`，内容包括：

```markdown
# 构建报告

## 编译状态
- assembleDebug: PASS/FAIL

## 测试状态
- testDebugUnitTest: X/Y passed

## 覆盖率
- 行覆盖: XX%
- 分支覆盖: XX%

## 模块完成状态
| 模块 | Java 文件数 | 测试文件数 | Layout 数 | 状态 |
|------|------------|-----------|----------|------|
| bt_connection | 3 | 3 | 1 | DONE |
| ... |

## 已知问题
- (如有)
```

## 容错机制

1. **subagent 超时/崩溃** — 重新派发同一模块，提供上次的错误信息作为上下文
2. **编译错误** — 总工分析错误，定位到具体模块，修复或重新派发
3. **资源冲突** — 总工统一裁定，以 design_tokens.json 为准
4. **循环依赖** — 不应出现（dependency_graph 已是 DAG），如果出现则打破循环并报告

## 输出结构

```
app/app/src/main/
├── java/com/example/btphone/
│   ├── model/          — Phase 0: 共享数据模型
│   ├── service/        — Phase 0: 服务接口 + Phase 1+: 服务实现
│   ├── event/          — Phase 0: 事件类
│   ├── common/         — Phase 0: 全局常量
│   ├── ui/
│   │   ├── contacts/   — Phase 3: 联系人列表
│   │   ├── dialer/     — Phase 4: 拨号键盘
│   │   ├── call/       — Phase 4-7: 来电/去电/通话中/三方/隐私
│   │   ├── common/     — 公共 UI 组件
│   │   └── service/    — T-Box 电话等
│   ├── adapter/        — 各模块 Adapter
│   └── util/           — 工具类
├── res/
│   ├── layout/         — 所有模块布局
│   ├── drawable/       — 所有切图
│   └── values/         — strings/colors/dimens 合并后
└── AndroidManifest.xml — 所有组件声明

app/app/src/test/
└── java/com/example/btphone/  — 与 main 一一对应的测试类

docs/build_report.md    — 最终构建报告
```
