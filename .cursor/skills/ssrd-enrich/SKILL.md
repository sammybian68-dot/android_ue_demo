---
name: ssrd-enrich
description: Read SSRD requirement pages and enrich each modules/ sub-directory with relevant requirement details, extracted rules, and source page images. Use after decompose has produced per-module directories and doc-prepare has converted the SSRD PDF to PNGs.
---

# SSRD 需求充实

逐页阅读 SSRD（软件需求规格文档），将需求内容分配到 decompose 产出的各子功能目录中，为每个界面补充需求原文、业务规则、数据定义等关键信息。

## 核心原则

**两遍扫描：先分配，再深读。** 第一遍快速扫描建立"页→界面"映射表，第二遍按界面聚焦精读。避免对每个界面重复阅读全部 SSRD 页。

**只提取对编码有用的信息。** 不搬运原文，而是提炼业务规则、数据格式、边界条件、异常处理等开发者真正需要的内容。

## 何时使用

- `docs/modules/index.json` 已存在（decompose 完成）
- `docs/prepared/requirements/page_index.json` 已存在（doc-prepare 完成）
- 需要为各界面补充 SSRD 需求信息

## 前置条件

- `docs/modules/*/manifest.json` — 各界面目录
- `docs/prepared/requirements/pages/*.png` — SSRD 每页 PNG
- `docs/prepared/requirements/page_index.json` — SSRD 页索引

## 工作流程

### Phase 1: 快速扫描，建立页→界面映射

**1.1 读取双方索引**

```
读取 docs/modules/index.json → 获取界面 ID 列表
读取 docs/prepared/requirements/page_index.json → 获取 SSRD 总页数和 preview
```

**1.2 识别 SSRD meta 页**

通过 preview 判断哪些是 meta 页（封面、版本历史、评审记录、参考文档、功能矩阵总表等），标记为 meta 跳过或标记为"全局共用"。

典型 meta 页特征：
- 封面/标题页
- 版本更新记录
- 评审纪要
- 参考文档列表
- 功能需求矩阵总表（可能对所有界面都有用，标记为 "global"）

**1.3 逐批扫描内容页，建立映射**

每次读 3-4 张 SSRD PNG，判断每页与哪些界面相关：

```
mapping = {}   # page_num → [screen_id, ...]
global_pages = []  # 对多个/所有界面都相关的页

for each batch of 3-4 SSRD pages:
    读取 PNG 图片
    对每页判断：
      - 该页主要描述哪个功能模块？（通话记录/联系人/拨号/来电/通话中/三方/...）
      - 匹配到 modules 中的哪个 screen_id？
      - 如果是通用规则（如音频切换、蓝牙连接策略），标记为 global
      - 如果一页涉及多个界面，列出所有关联的 screen_id
    记录映射关系
```

**1.4 输出映射表**

```json
{
  "meta_pages": [1, 2, 3],
  "global_pages": [4, 5, 6, 7, 33, 34, 35],
  "screen_mapping": {
    "bt_connection": [9],
    "bt_sync": [10, 11, 12, 13, 16],
    "call_records": [14, 15],
    "contacts_list": [16, 17, 18],
    "dialer": [19],
    "outgoing_call": [20, 21, 22],
    "incoming_call": [23, 24, 25, 26],
    "in_call": [22, 27],
    "three_way_call": [28, 29, 30, 31],
    "privacy_mode": [26, 35],
    "tbox_call": [],
    "wechat_call": [32]
  }
}
```

### Phase 2: 逐界面精读，充实目录

对每个界面，精读其关联的 SSRD 页 + global 页中与之相关的部分。

**对每个 screen_id：**

#### 2.1 复制关联的 SSRD PNG

```
将映射到的 SSRD 页 PNG 复制到 <screen_id>/ssrd_png/
```

#### 2.2 提取关键需求信息

精读每张关联的 SSRD 页 PNG，提取以下类别的信息：

**业务规则**（直接影响代码逻辑的规则）：
```markdown
- 通话记录最多同步 200 条
- 号码显示规则：有名字显示名字，无名字显示号码
- 时间显示：当天显示 HH:MM，昨天显示"昨天"，前天显示"前天"...
```

**数据定义**（字段、格式、类型）：
```markdown
- 通话类型：去电（拨出）、未接来电（未接/拒接/对方挂断）、已接来电
- 号码类型：手机(Phone)、办公(Office)、住宅(Home)、其他(Other)
- 通话时间格式：XX:XX:XX
```

**边界条件和异常处理**：
```markdown
- 联系人最多 5000 条（含手机 + SIM 卡）
- 同步失败时 toast 提示"同步出现问题，请重试"
- PBAP 未连接时显示同步失败状态
```

**平台差异**（iOS/Android 行为差异，如果 SSRD 中提及）

#### 2.3 更新 README.md

在已有 README.md 末尾追加新的 section：

```markdown
## 6. SSRD 需求摘要

> 来源：SSRD P14, P15 | 原始文件：ssrd_png/page_14.png, ssrd_png/page_15.png

### 业务规则

- <规则1>
- <规则2>

### 数据定义

| 字段 | 类型/格式 | 说明 |
|------|---------|------|
| ... | ... | ... |

### 边界条件与异常

- <条件1>
- <条件2>
```

#### 2.4 更新 manifest.json

在 `artifacts` 中追加 SSRD 相关字段：

```json
{
  "artifacts": {
    "hmi_interaction": "README.md",
    "hmi_png": ["hmi_png/page_07.png"],
    "ssrd_png": ["ssrd_png/page_23.png", "ssrd_png/page_24.png"],
    "ssrd_data": {
      "business_rules": [
        "来电时自动将手机端铃声静音",
        "来电显示：有名字显示名字，无名字显示号码"
      ],
      "data_fields": [
        {"field": "来电号码", "format": "string", "note": "含区号时自动分割"},
        {"field": "号码类型", "values": ["Phone","Office","Home","Other"]}
      ],
      "edge_cases": [
        "电话铃声：手机支持时用手机铃声，不支持用车机自带铃声"
      ]
    }
  }
}
```

`ssrd_data` 是结构化提取的关键信息，**面向编码场景**，后续 dev-runner 可直接消费。

### Phase 3: 验证与输出映射文件

**3.1 写入全局映射文件**

将 Phase 1 的映射表写入 `docs/modules/ssrd_mapping.json`，方便回溯。

**3.2 验证**

```
for each screen_id:
    - manifest.json 的 ssrd_png 列表与 ssrd_png/ 目录内容一致
    - README.md 包含 "## 6. SSRD 需求摘要" section
    - ssrd_data 中的 business_rules 至少有 1 条（除非 SSRD 确实无相关内容）
```

## 输出结构（增量，在 decompose 基础上追加）

```
docs/modules/
├── index.json                        # 不变
├── ssrd_mapping.json                 # 新增：SSRD 页→界面映射表
├── incoming_call/
│   ├── manifest.json                 # 更新：新增 ssrd_png + ssrd_data
│   ├── README.md                     # 更新：追加 "6. SSRD 需求摘要"
│   ├── hmi_png/                      # 不变
│   └── ssrd_png/                     # 新增：关联的 SSRD 原始页
│       ├── page_23.png
│       └── page_24.png
└── .../
```

## 关键设计决策

### 为什么分两遍而不是每个界面读一遍全部 SSRD？

假设 SSRD 36 页、12 个界面：
- 逐界面全读：36 × 12 = 432 次读取（大量重复）
- 两遍法：36（映射扫描）+ ~36（各界面精读各自页，有重叠）≈ 72 次读取

### 为什么提取到 manifest.json 而不只写 README.md？

README.md 是人读的，manifest.json 是机器读的。后续 dev-runner skill 生成代码时，可以直接解析 `ssrd_data` 获取业务规则和数据定义，无需再次理解自然语言。

### global_pages 怎么处理？

功能矩阵总表、通用规则页等 global_pages 不复制到每个界面目录（避免冗余），但在精读时会参考。如果 global 页中有某条规则明确关联到某个界面，则提取到该界面的 ssrd_data 中。

## 下游

- sdk-enrich — 读取各子功能 manifest.json（含 ssrd_data），映射 SDK API 并生成全局共享协议
- sketch-enrich — 读取 index.json，为各子功能分发 Sketch 标注数据
- dev-runner — 直接消费 manifest.json 中的 ssrd_data 进行编码
