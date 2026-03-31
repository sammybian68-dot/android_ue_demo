---
name: decompose
description: Analyze project input documents (HMI interaction design, etc.) to decompose the application into independent sub-functions. Each sub-function gets a self-contained directory that downstream skills (ssrd-enrich, sdk-enrich, sketch-enrich, dev-runner) can process independently.
---

# 项目分解

分析项目的输入文档（交互设计、需求概览等），将应用拆解为**独立的子功能单元**。每个子功能输出一个自包含的目录，后续流程可以独立、完整地对其进行需求充实、SDK 映射、UI 标注、编码实现。

## 核心原则

**拆解的目标是独立性。** 每个子功能应该能被一个开发者（或 subagent）独立理解和实现，无需阅读其他子功能的全部细节。子功能之间通过共享接口协议交互。

**像人一样逐步阅读，不一次性全部读完。** 每次读 2-4 页，理解后标记出属于哪个子功能，不确定的多读几页。允许回头翻。

**每个子功能一个目录，目录即开发单元。** 开发者打开一个目录就能获取该子功能的全部信息。

## 拆分依据

主要依据交互设计文档中的**界面/功能边界**进行拆分，同时考虑：

- **功能独立性** — 该子功能能否被独立开发和测试？
- **数据边界** — 该子功能的数据输入输出是否清晰？
- **交互内聚性** — 相关的 UI 状态和操作是否归属同一单元？
- **开发粒度** — 子功能不宜过大（一个 subagent 难以完成）也不宜过小（过度碎片化）

## 前置条件

- `docs/prepared/interaction/page_index.json` 存在
- `docs/prepared/interaction/pages/*.png` 存在

## 工作流程

### Phase 1: 快速总览（识别子功能列表）

**1.1 读取 page_index.json**

```
读取 docs/prepared/interaction/page_index.json
→ 通过 preview 字段粗略了解每页内容
→ 识别出 meta 页（封面、目录、图例说明）和内容页
→ 记录总页数，规划阅读批次
```

**1.2 逐批阅读，识别子功能**

从第一张内容页开始，每次读 2-4 张 PNG：

```
for each batch of 2-4 pages:
    读取 PNG 图片
    识别这几页描述了哪些独立的功能/界面
    记录：
      - 子功能名称（中文，如"来电处理"）
      - 子功能 ID（英文 snake_case，如 "incoming_call"）
      - 涉及的页码
      - 该子功能是否在这几页内描述完整，还是延续到后续页
    如果当前子功能跨页且信息不完整 → 下一批多读几页
```

**1.3 确认子功能清单**

完成所有页阅读后，审视清单：

- 一页可能包含多个子功能（小功能/状态变体）
- 一个子功能可能跨多页
- 同一页可能同时被多个子功能引用（多对多关系）
- 检查是否有遗漏的功能或过度拆分的情况

### Phase 2: 逐子功能深度分析

对 Phase 1 识别出的每个子功能，重新精读其关联的页面，输出结构化分析。

**每个子功能输出以下 sections（写入 README.md）：**

#### 2.1 功能描述

用 1-3 句话描述这个子功能要实现什么。

#### 2.2 界面状态枚举

列出这个子功能的所有视觉状态/变体。

```markdown
| 状态 | 说明 | 对应 HMI 页 |
|------|------|-----------|
| 全屏-拨号中 | 正在拨号，显示对方号码和动画 | P8 左上 |
| 全屏-通话中 | 已接通，显示通话时长 | P8 右上 |
| 小窗-通话中 | 非电话界面时的通话悬浮窗 | P8 中部 |
```

#### 2.3 交互状态机

用 ASCII 状态图描述状态转换：

```
[空闲] --来电事件--> [响铃]
[响铃] --用户接听--> [通话中]
[响铃] --用户拒接--> [空闲]
[通话中] --用户挂断--> [通话结束] --1秒--> [空闲]
```

标注每个转换的**触发源**：
- 用户操作（点击按钮）
- 系统事件（来电、断开）
- 超时（N 秒无操作）

#### 2.4 后端交互逻辑

描述这个子功能需要从后端 Service 获取/发送什么：

```markdown
| 交互 | 方向 | 说明 |
|------|------|------|
| 获取联系人列表 | UI ← Service | 页面加载时调用，返回按拼音排序的联系人 |
| 拨打电话 | UI → Service | 用户点击拨号按钮，传入号码 |
| 监听通话状态 | UI ← Service | 注册回调，状态变化时更新 UI |
```

#### 2.5 关键交互细节

从文档中提取的具体交互规则（非通用的、容易遗漏的点）。

### Phase 3: 写入文件

为每个子功能创建子目录，写入 README.md、manifest.json，复制关联的 HMI PNG。

## 输出结构

```
docs/modules/
├── index.json                         # 顶层子功能清单
├── incoming_call/                     # ← 每个子功能一个目录
│   ├── manifest.json                  # 结构化元数据（id、name、页码、文件清单）
│   ├── README.md                      # 交互规格（功能、状态机、后端交互、细节）
│   └── hmi_png/                       # 该子功能引用的 HMI 原始页（从 prepared 复制）
│       ├── page_07.png
│       └── page_08.png
├── dialer/
│   ├── manifest.json
│   ├── README.md
│   └── hmi_png/
└── .../
```

后续 skill 会在每个子功能目录中追加更多文件（目录是可扩展的）：

```
incoming_call/
├── manifest.json                      # ← artifacts 字段随每步 enrich 同步更新
├── README.md                          # 交互规格 + SSRD 需求（ssrd-enrich 追加）
├── hmi_png/                           # HMI 原始页
├── ssrd_png/                          # （ssrd-enrich 追加）SSRD 原始页
├── sketch_data.json                   # （sketch-enrich 追加）标注层数据
└── sketch_png/                        # （sketch-enrich 追加）标注预览图
```

### index.json 格式

```json
{
  "source_pdf": "<原始 PDF 路径>",
  "total_hmi_pages": 13,
  "meta_pages": [1, 2, 3],
  "modules": [
    { "id": "incoming_call", "name": "来电", "dir": "incoming_call/" }
  ]
}
```

### manifest.json 格式

每个子功能目录下的 manifest.json 是该目录的**结构化索引**：

```json
{
  "id": "incoming_call",
  "name": "来电",
  "summary": "来电全屏/小窗显示，接听/拒接操作",
  "hmi_pages": [7, 8],
  "artifacts": {
    "hmi_interaction": "README.md",
    "hmi_png": [
      "hmi_png/page_07.png",
      "hmi_png/page_08.png"
    ]
  }
}
```

### README.md 格式

```markdown
# <子功能中文名>

> 来源 HMI 页：P7, P8 | 原始文件：hmi_png/page_07.png, hmi_png/page_08.png

## 1. 功能描述

<1-3 句话>

## 2. 界面状态

| 状态 | 说明 | HMI 来源 |
|------|------|---------|
| ... | ... | ... |

## 3. 交互状态机

（ASCII 状态图）

## 4. 后端交互

| 交互 | 方向 | 说明 |
|------|------|------|
| ... | ... | ... |

## 5. 关键交互细节

- <具体规则>
- ...
```

## 验证

- [ ] 每张内容页至少被一个子功能引用
- [ ] 每个子功能目录包含 manifest.json + README.md + hmi_png/
- [ ] manifest.json 中的 artifacts 路径与实际文件一致
- [ ] 每个子功能的状态机有明确的起始和终止状态
- [ ] 每个状态转换都标注了触发源
- [ ] 后端交互表覆盖了该子功能的所有数据需求

## 下游

- ssrd-enrich — 读取 index.json，逐子功能充实 SSRD 需求
- sdk-enrich — 读取各子功能 manifest.json，映射 SDK API
- sketch-enrich — 读取 index.json，分发 Sketch 标注数据
- dev-runner — 读取完整的子功能目录，进行编码
