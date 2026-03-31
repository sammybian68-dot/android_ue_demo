---
name: auto-dev
description: End-to-end pipeline that transforms raw input documents (HMI PDF, SSRD PDF, Sketch annotations, SDK JARs) into a fully compiled and tested Android Java application. Chains doc-prepare → decompose → ssrd-enrich → sdk-enrich → sketch-enrich → dev-runner, with validation and self-repair between each step.
---

# 全自动开发流水线

从原始输入文档出发，逐步执行 6 个 skill，最终输出可编译、可测试的 Android Java 应用代码。每步执行完成后自动检查中间产物，发现问题立即修复，确认无误后继续下一步。

## 核心原则

**全自动、可断点续跑、自修复。**

1. 开始前先检测已有产物，跳过已完成的步骤（断点续跑）
2. 每步完成后执行验证清单，不通过则修复后重试（最多 3 轮）
3. 验证通过后自动进入下一步，无需人工干预
4. 全部完成后输出最终报告

## 输入要求

用户需要在项目中准备以下输入文件（路径可自适应）：

| 类型 | 默认路径 | 必需 |
|------|---------|------|
| HMI 交互设计 PDF | `输入/需求/*.HMI*.pdf` 或 `输入/需求/*交互设计*.pdf` | 是 |
| SSRD 需求文档 PDF | `输入/需求/*.SSRD*.pdf` 或 `输入/需求/*需求*.pdf` | 是 |
| Sketch 标注数据 | `输入/需求/标注/xx.json` + `preview/*.png` | 是 |
| 切图资源 | `输入/需求/切图/*.png` | 是 |
| SDK 依赖 | `输入/依赖/*.jar` + 文档目录 | 是 |

## 流水线总览

```
Step 1: doc-prepare     ──检查──→  docs/prepared/ 齐备？
                                     │
Step 2: decompose       ──检查──→  docs/modules/index.json + 各模块目录？
                                     │
Step 3: ssrd-enrich     ──检查──→  各模块的 ssrd_png/ + ssrd_data？
                                     │
Step 4: sdk-enrich      ──检查──→  各模块的 sdk_apis + shared_interfaces.json？
                                     │
Step 5: sketch-enrich   ──检查──→  各模块的 sketch_data.json + sketch_png/ + cut_images？
                                     │
Step 6: dev-runner      ──检查──→  assembleDebug PASS + testDebugUnitTest PASS？
                                     │
                               ✅ 最终报告
```

## 详细步骤

---

### Step 1: doc-prepare — 文档预处理

**执行条件**：`docs/prepared/interaction/page_index.json` 不存在 或 `docs/prepared/requirements/page_index.json` 不存在。

**操作**：按 `doc-prepare` skill 执行，将 HMI PDF 和 SSRD PDF 转为逐页 PNG + 文本，将 SDK Javadoc 提取为 API 摘要。

**验证清单**：

```
□ docs/prepared/interaction/page_index.json 存在且 pages 数组非空
□ docs/prepared/interaction/pages/ 下 PNG 文件数量与 page_index.json 一致
□ docs/prepared/requirements/page_index.json 存在且 pages 数组非空
□ docs/prepared/requirements/pages/ 下 PNG 文件数量与 page_index.json 一致
□ docs/prepared/visual/artboard_index.json 存在（如有 Sketch 标注输入）
```

**修复策略**：
- PNG 数量不匹配 → 重新执行 PDF 转换脚本
- page_index.json 为空 → 检查 PDF 路径是否正确

---

### Step 2: decompose — 项目分解

**执行条件**：`docs/modules/index.json` 不存在。

**操作**：按 `decompose` skill 执行，逐页阅读 HMI PNG，识别独立子功能，为每个子功能创建自包含目录。

**验证清单**：

```
□ docs/modules/index.json 存在
□ index.json 中 modules 数组至少有 5 个子功能（蓝牙电话 app 预期 10+）
□ 每个模块的目录存在：<module_id>/manifest.json + README.md + hmi_png/
□ 每个 manifest.json 的 artifacts.hmi_png 列表与 hmi_png/ 目录内文件一致
□ 每个 README.md 包含 5 个 section：功能描述、界面状态、状态机、后端交互、关键细节
□ 每张 HMI 内容页至少被一个子功能引用
```

**修复策略**：
- 某个模块目录缺文件 → 重新生成该模块
- README.md 缺 section → 补充缺失 section
- HMI 页未被引用 → 分析该页属于哪个子功能并追加

---

### Step 3: ssrd-enrich — SSRD 需求充实

**执行条件**：任一模块的 `manifest.json` 不包含 `ssrd_data` 字段。

**操作**：按 `ssrd-enrich` skill 执行，两遍扫描 SSRD，为每个子功能补充需求原文、业务规则、数据定义。

**验证清单**：

```
□ docs/modules/ssrd_mapping.json 存在
□ 每个模块的 manifest.json 包含 artifacts.ssrd_data 字段
□ 每个 ssrd_data.business_rules 至少有 1 条（除非确实无相关需求）
□ 每个模块的 README.md 包含 "## 6. SSRD 需求摘要" section
□ manifest.json 中的 ssrd_png 列表与 ssrd_png/ 目录内文件一致
```

**修复策略**：
- ssrd_data 为空 → 重新精读该模块关联的 SSRD 页
- ssrd_png 数量不匹配 → 补充复制缺失的 PNG

---

### Step 4: sdk-enrich — SDK 接口映射 + 全局协议

**执行条件**：`docs/modules/shared_interfaces.json` 不存在 或 任一模块的 `manifest.json` 不包含 `sdk_apis` 字段。

**操作**：按 `sdk-enrich` skill 执行，分析各模块所需 SDK API，生成 shared_interfaces.json 全局协议。

**验证清单**：

```
□ docs/modules/shared_interfaces.json 存在
□ shared_interfaces.json 包含：shared_services / event_bus_protocol / shared_models / global_constants / dependency_graph
□ dependency_graph.build_order 包含 phase 0-7，覆盖所有模块
□ 每个模块的 manifest.json 包含 artifacts.sdk_apis 字段
□ 每个模块的 manifest.json 包含 artifacts.dependencies 数组
□ shared_models 中的每个 model 字段完整（name / package / type / fields 或 values）
```

**修复策略**：
- shared_interfaces.json 结构不完整 → 补充缺失字段
- 某模块缺 sdk_apis → 重新分析该模块的功能需求匹配 SDK
- dependency_graph 有遗漏模块 → 追加到合适的 phase

---

### Step 5: sketch-enrich — Sketch 标注充实

**执行条件**：任一模块的 `manifest.json` 不包含 `sketch_data` 字段 或 `docs/modules/design_tokens.json` 不存在。

**操作**：按 `sketch-enrich` skill 执行，解析 xx.json，按 artboard 映射到模块，提取标注数据和预览图。

**验证清单**：

```
□ docs/modules/design_tokens.json 存在，包含 colors 数组和 cut_images 数组
□ 每个模块的 sketch_data.json 存在
□ sketch_data.json 的 artboards 数组非空
□ 每个 artboard 有 preview_png 且对应文件存在于 sketch_png/
□ manifest.json 中的 sketch_png 列表与 sketch_png/ 目录内文件一致
□ manifest.json 中的 cut_images 列表非空（除非该界面确实无切图引用）
□ sketch_data.json 包含 referenced_slices 和 cut_images 字段
```

**修复策略**：
- 某模块的 sketch_data.json 不存在 → 检查 artboard 映射规则，可能漏匹配
- preview_png 文件缺失 → 检查 slug 匹配逻辑，从 preview/ 目录重新复制
- cut_images 为空 → 检查 layers 中 type=slice 的提取是否遗漏

---

### Step 6: dev-runner — 编码、编译、测试

**执行条件**：`docs/build_report.md` 不存在 或 报告中 assembleDebug 为 FAIL。

**操作**：按 `dev-runner` skill 执行三个阶段：

1. **Phase 0**（总工亲自）：生成共享基础层（model / service interface / event / constant / resource），编译验证
2. **Phase 1-7**（subagent 并行）：按依赖拓扑顺序派发 12 个模块的编码任务
3. **Phase 8**（总工亲自）：合并资源、全量编译、全量测试、覆盖率报告

**验证清单**：

```
□ 共享基础层编译通过（Phase 0 后）
□ 每个模块有 Java 源码 + layout XML + 单元测试
□ AndroidManifest.xml 声明了所有 Activity / Service
□ ./gradlew clean assembleDebug 通过
□ ./gradlew testDebugUnitTest 通过
□ docs/build_report.md 已生成
□ build_report.md 中所有模块状态为 DONE
```

**修复策略**：
- Phase 0 编译失败 → 分析错误日志，修复共享代码，重新编译（最多 3 轮）
- subagent 产出编译错误 → 总工定位问题，修复或重新派发（最多 3 轮/模块）
- 资源合并冲突 → 以 design_tokens.json 为准裁定
- 单元测试失败 → 分析是测试代码 bug 还是业务代码 bug，针对性修复（最多 3 轮）

---

## 断点续跑逻辑

每次执行时，按以下顺序检测已完成的步骤：

```python
def determine_start_step():
    if not exists("docs/prepared/interaction/page_index.json"):
        return Step.DOC_PREPARE                          # Step 1

    if not exists("docs/modules/index.json"):
        return Step.DECOMPOSE                            # Step 2

    modules = load_json("docs/modules/index.json")["modules"]
    for s in modules:
        manifest = load_json(f"docs/modules/{s['id']}/manifest.json")
        if "ssrd_data" not in manifest.get("artifacts", {}):
            return Step.SSRD_ENRICH                      # Step 3

    if not exists("docs/modules/shared_interfaces.json"):
        return Step.SDK_ENRICH                           # Step 4

    for s in modules:
        manifest = load_json(f"docs/modules/{s['id']}/manifest.json")
        if "sketch_data" not in manifest.get("artifacts", {}):
            return Step.SKETCH_ENRICH                    # Step 5

    if not exists("docs/build_report.md"):
        return Step.DEV_RUNNER                           # Step 6

    report = read("docs/build_report.md")
    if "assembleDebug: FAIL" in report or "testDebugUnitTest: FAIL" in report:
        return Step.DEV_RUNNER                           # Step 6 (重跑)

    return Step.DONE                                     # 全部完成
```

## 执行流程伪代码

```
step = determine_start_step()

while step != DONE:
    print(f"▶ 正在执行 Step {step.number}: {step.skill_name}")

    execute_skill(step.skill_name)

    passed, issues = validate(step)

    retry = 0
    while not passed and retry < 3:
        print(f"  ⚠ 发现 {len(issues)} 个问题，尝试修复 (第 {retry+1} 轮)")
        for issue in issues:
            fix(issue)
        passed, issues = validate(step)
        retry += 1

    if not passed:
        print(f"  ✘ Step {step.number} 经 3 轮修复仍未通过，暂停并报告")
        generate_error_report(step, issues)
        break

    print(f"  ✔ Step {step.number} 验证通过")
    step = step.next()

if step == DONE:
    print("✅ 全部完成！")
    print_final_summary()
```

## 进度报告

每完成一个 Step，输出进度摘要：

```
═══════════════════════════════════════
  蓝牙电话 Android 应用 — 自动化开发流水线
═══════════════════════════════════════
  Step 1: doc-prepare     ✔ 已完成（HMI 13页, SSRD 36页, 50画板）
  Step 2: decompose       ✔ 已完成（12 个子功能模块）
  Step 3: ssrd-enrich     ✔ 已完成（12/12 模块已充实需求）
  Step 4: sdk-enrich      ✔ 已完成（3 个 SDK, 12/12 模块已映射）
  Step 5: sketch-enrich   ✔ 已完成（50 画板分配, 设计 token 提取）
  Step 6: dev-runner      ▶ 进行中（Phase 3/8: call_records, contacts_list）
═══════════════════════════════════════
```

## 异常处理

| 异常场景 | 处理方式 |
|---------|---------|
| PDF 转换脚本不存在 | 检查 Python 环境，pip install pymupdf |
| HMI PDF 页数过少（<3 页） | 警告用户确认 PDF 是否正确 |
| SSRD 与 HMI 界面无法映射 | 标记为 global，不阻塞流程 |
| SDK JAR 无文档 | 用 LLM 已有知识推断 API，标记为 "inferred" |
| Sketch xx.json 不存在 | 跳过 sketch-enrich，dev-runner 中 UI 使用近似布局 |
| 编译失败超 3 轮 | 暂停，生成错误报告，等待用户介入 |
| 单元测试失败超 3 轮 | 记录失败用例，继续生成 build_report.md，标记测试状态 |

## 输出总览

流水线完成后，项目目录结构：

```
ue_demo/
├── 输入/                              # 原始输入（不修改）
│   ├── 需求/
│   │   ├── *.pdf
│   │   ├── 标注/xx.json + preview/
│   │   └── 切图/*.png
│   └── 依赖/*.jar + docs/
│
├── docs/                              # 中间产物（每步追加）
│   ├── prepared/                      # Step 1: doc-prepare
│   │   ├── interaction/               #   HMI PNG + text + index
│   │   ├── requirements/              #   SSRD PNG + text + index
│   │   ├── visual/                    #   Sketch artboards + index
│   │   └── sdk/                       #   SDK API 摘要
│   ├── modules/                   # Step 2-5: 逐步充实的界面规格
│   │   ├── index.json                 #   界面清单
│   │   ├── shared_interfaces.json     #   全局共享协议
│   │   ├── design_tokens.json         #   全局设计 token
│   │   ├── ssrd_mapping.json          #   SSRD 页→界面映射
│   │   └── <module_id>/              #   每个子功能的自包含目录
│   │       ├── manifest.json          #     结构化元数据（逐步充实）
│   │       ├── README.md              #     交互规格 + SSRD 需求
│   │       ├── hmi_png/               #     HMI 原始页
│   │       ├── ssrd_png/              #     SSRD 原始页
│   │       ├── sketch_data.json       #     标注层数据
│   │       └── sketch_png/            #     标注预览图
│   └── build_report.md               # Step 6: 最终构建报告
│
└── app/app/                           # Step 6: 最终代码产物
    ├── src/main/java/                 #   Java 源码
    ├── src/main/res/                  #   布局 + 切图 + 资源
    ├── src/main/AndroidManifest.xml   #   组件声明
    ├── src/test/java/                 #   单元测试
    └── build.gradle.kts               #   构建配置
```
