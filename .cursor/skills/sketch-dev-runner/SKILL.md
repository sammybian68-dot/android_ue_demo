---
name: sketch-dev-runner
description: Orchestrate sub-agents to implement Android UI code from the docs/structure/ directory. Use when the user asks to develop, implement, or code the UI from the split design spec, or mentions running dev tasks from the manifest.
---

# Sketch 标注 → Android 代码编排器

读取 `sketch-to-tasks` 生成的 `docs/structure/` 目录，按依赖顺序驱动 sub-agent 完成所有 Android UI 开发任务。

## 前置条件

确认 `docs/structure/` 目录已就绪：
- `manifest.json` — 任务清单（必须存在）
- `colors.json` — 颜色规范
- `artboards/` — 画板数据
- `tasks/` — 任务文档（可选，有则传给 sub-agent）

如果缺少 manifest.json，先引导用户运行 `sketch-to-tasks` skill 生成 `docs/structure/` 目录。

---

## 编排流程

### Step 1: 读取 manifest.json

```
读取 docs/structure/manifest.json
提取：
  - project_name, design_resolution
  - preview_dir, slice_dir
  - dev_tasks[] （任务列表，含依赖和优先级）
  - modules[] （模块列表，含画板文件引用）
```

### Step 2: 初始化 Android 项目

在开始任何 dev_task 之前，检查 Android 项目是否已存在。如果不存在，创建基础骨架：

```
{project_root}/app/
├── app/src/main/kotlin/com/example/btphone/
├── app/src/main/res/layout/
├── app/src/main/res/drawable/
├── app/src/main/res/values/
├── app/src/main/AndroidManifest.xml
├── app/build.gradle.kts
├── build.gradle.kts
└── settings.gradle.kts
```

详细结构参考 [android-project-structure.md](reference/android-project-structure.md)。

### Step 3: 按依赖顺序执行任务

从 `dev_tasks` 中按 priority 排序，依赖已完成的任务优先执行。

```
执行顺序：
  priority 0 → T0（基础架构）— 必须最先完成
  priority 1 → T1, T2        — T0 完成后可并行
  priority 2 → T3, T4, T8    — T0 完成后可并行
  priority 3 → T5, T6, T7    — 等待各自依赖完成
```

**依赖规则**：一个任务的所有 `depends_on` 任务必须已完成才能开始。

### Step 4: 为每个任务构建 sub-agent prompt

对每个 dev_task，按以下步骤构建 prompt：

**4.1 收集任务相关文件**

```python
task = dev_tasks[i]
module_ids = task["modules"]
artboard_files = []
preview_files = []

for mid in module_ids:
    module = find_module(mid)
    for ab in module["artboards"]:
        artboard_files.append(f"docs/structure/{ab['file']}")
        # preview 路径从 artboard_index.json 或画板 JSON 的 imagePath 获取
```

**4.2 组装 prompt**

参考 [sub-agent-prompt-template.md](reference/sub-agent-prompt-template.md)，将以下信息填入：

- 项目名、分辨率、项目路径
- task_id、task_name、task_description
- 画板文件列表（含 role: base/variant 标注）
- preview 截图路径列表
- 切图目录路径
- 如果 `tasks/{task_id}-xxx.md` 存在，告诉 sub-agent 先读它

**4.3 选择 sub-agent 类型**

| 任务特征 | sub-agent 类型 | 说明 |
|---------|---------------|------|
| T0（基础架构） | `generalPurpose` | 需要创建多个文件、写配置 |
| 其他所有任务 | `generalPurpose` | 需要读图片 + 写代码 |

### Step 5: 启动 sub-agent

```
对每个任务：
  1. 检查依赖是否都已完成
  2. 用 Task 工具启动 sub-agent，传入构建好的 prompt
  3. 等待 sub-agent 完成
  4. 标记该任务为已完成
  5. 检查是否有新任务的依赖已满足，继续下一个
```

**并行策略**：
- 同一 priority 且依赖都满足的任务，可以同时启动多个 sub-agent（最多 4 个）
- T0 必须串行先完成
- T1 和 T2 可以并行（都只依赖 T0，互不影响）
- T5 必须等 T4 完成

### Step 6: 完成后验证

所有任务完成后：
1. 检查 Android 项目结构完整性
2. 确认所有 Activity 已注册到 AndroidManifest.xml
3. 确认 colors.xml 包含所有设计令牌
4. 汇总完成情况报告给用户

---

## 执行示例

用户说"帮我根据设计文档开始开发"时：

```
1. 读取 docs/structure/manifest.json
2. 确认 Android 项目目录（app/）
3. 启动 sub-agent 执行 T0（基础架构）
4. T0 完成后，并行启动 T1 + T2
5. T1, T2 完成后，并行启动 T3 + T4 + T8
6. T4 完成后，启动 T5
7. T3/T8 完成后（如果 T0 已完成），启动 T6 + T7
8. 全部完成，输出报告
```

---

## 单任务执行

用户也可以指定只执行某个任务：
- "帮我执行 T1 联系人模块"
- "只跑 T0 基础架构"

此时只执行指定任务，但仍需检查其依赖是否已完成。如果依赖未完成，提示用户先执行依赖任务。

---

## 故障处理

- **sub-agent 失败**：记录错误信息，跳过该任务，继续执行不依赖它的其他任务。最后汇报哪些任务失败。
- **画板文件缺失**：警告用户，用可用的画板继续开发。
- **切图缺失**：在代码中用占位符 `@drawable/placeholder`，标注 TODO。
