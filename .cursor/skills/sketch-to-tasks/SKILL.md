---
name: sketch-to-tasks
description: Split Sketch MeaXure annotation files into per-artboard data and generate a development task manifest. Use when the user mentions Sketch annotation, design spec splitting, MeaXure, or asks to break down UI design into development tasks.
---

# Sketch 标注拆分与任务分配

将 Sketch MeaXure 导出的标注文件（index.html）拆分为可逐步执行的开发任务。

## 工作流程概览

```
Phase 1: 脚本拆分（机械）→ 每个画板一个 JSON
Phase 2: AI 分析（智能）  → 生成 manifest.json（模块分组 + 任务分配）
Phase 3: 输出任务文档     → 每个任务一个 markdown，供 sub-agent 执行
```

---

## Phase 1: 脚本拆分

运行内置脚本将大文件拆成小文件：

```bash
python .cursor/skills/sketch-to-tasks/scripts/split_sketch.py <input> <output_dir>
```

- `<input>`: Sketch MeaXure 导出的 `index.html` 或提取出的 JSON 文件
- `<output_dir>`: 输出目录（建议 `./docs/structure/`）

脚本输出结构：
```
docs/structure/
├── artboard_index.json   # 轻量索引：所有画板名称、尺寸、图层数（先看这个）
├── colors.json           # 设计令牌：颜色规范
├── slices.json           # 切图资源清单
└── artboards/            # 每个画板的完整图层数据
    ├── 01-xxx.json
    ├── 02-xxx.json
    └── ...
```

---

## Phase 2: AI 生成 manifest.json

脚本拆分完成后，AI 按以下步骤生成 manifest。

### Step 2.1: 读取 artboard_index.json

只读索引文件，了解全部画板的名称和基本信息。**不要**在此阶段读取任何画板详情。

### Step 2.2: 读取 preview 截图进行分组

按需读取关键画板的 preview 截图（PNG），判断：
- 哪些画板共享同一个外壳/布局（属于同一页面的不同状态）
- 哪些画板是独立全屏页面（需要独立 Activity）
- 哪些画板是嵌入式面板（Fragment）
- 哪些画板只是空状态/加载态提示（View）

**分组原则**：
1. 名称前缀相同 → 大概率是同一模块（如"联系人-按下"和"联系人"）
2. 无后缀的画板 → 通常是 base（基准状态）
3. 带"-按下"/"-选中"/"-展开"/"-小框" → variant（状态变体）
4. 包含 tab 栏（"通话"+"联系人"文字）→ 嵌在 MainActivity 里的 Fragment
5. 不包含 tab 栏、独占全屏 → 独立 Activity

### Step 2.3: 识别公共外壳

对比多个画板的图层，提取出现在大多数画板中的共有元素（如顶部蓝牙状态、tab 栏），记入 `shared_shell`。

### Step 2.4: 编排开发任务

将模块组织为有依赖关系的任务序列：
- **T0（基础架构）**: MainActivity 壳子、colors.xml、公共组件 → 无依赖，最先执行
- **T1~Tn（功能模块）**: 各 Fragment / Activity → 依赖 T0，可并行

### Step 2.5: 写入 manifest.json

输出到 `<output_dir>/manifest.json`。格式参考 [manifest-template.json](reference/manifest-template.json)。

manifest.json 核心结构：
```json
{
  "project_name": "...",
  "design_resolution": { "width": 1920, "height": 1080 },
  "source": "原始标注文件路径",
  "split_output": "拆分输出目录",
  "preview_dir": "preview 截图目录",
  "slice_dir": "切图资源目录",
  "shared_shell": {
    "description": "...",
    "elements": [ { "name": "...", "layer_name": "...", "rect": {} } ]
  },
  "modules": [
    {
      "id": "module_id",
      "name": "模块名",
      "component_type": "fragment | activity | view",
      "description": "...",
      "base_artboard": "基准画板文件名",
      "artboards": [
        { "file": "xx.json", "role": "base | variant", "description": "..." }
      ],
      "depends_on": []
    }
  ],
  "dev_tasks": [
    {
      "task_id": "T0",
      "name": "任务名",
      "description": "...",
      "modules": ["module_id"],
      "depends_on": ["task_id"],
      "priority": 0
    }
  ]
}
```

---

## Phase 3: 生成任务文档

为 `dev_tasks` 中的每个任务生成一个 markdown 文件，存放在 `<output_dir>/tasks/` 下。

格式参考 [task-template.md](reference/task-template.md)。

每个任务文档必须包含：
1. **需要读取的文件列表** — 画板 JSON、preview 截图、全局资源的具体路径
2. **开发步骤** — 从读画板 → 写布局 → 设置样式 → 导入切图 → 处理状态 → 验证
3. **base 与 variant 的关系** — 先看 base 写布局，再看 variant 补状态

---

## Sub-Agent 使用方式

manifest.json 和任务文档生成后，可以按以下方式分配给 sub-agent：

```
1. Sub-agent 读取 manifest.json 了解自己的任务范围
2. Sub-agent 读取对应的 tasks/T1-xxx.md 获取详细步骤
3. Sub-agent 按步骤读取画板 JSON + preview 截图
4. Sub-agent 编写 Android 代码
```

每个 sub-agent 只需加载自己任务相关的几个画板文件（~10-50KB），不需要加载全部数据。

---

## 快速参考

| 文件 | 何时读取 | 大小 |
|------|---------|------|
| artboard_index.json | Phase 2 开始时 | ~5KB |
| colors.json | 每个任务开始前 | ~3KB |
| 单个画板 JSON | 写具体模块时 | ~5-15KB |
| preview 截图 | 分组判断 / 验证布局时 | 图片 |
| manifest.json | sub-agent 启动时 | ~5KB |
