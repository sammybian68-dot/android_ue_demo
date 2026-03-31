# 全自动 Android 开发流水线

## 核心思想

**一个 skill 启动，全流程自动完成。** 从原始文档到可编译、可测试的 Android Java 应用代码，无需人工干预。

## 流水线总览

```
auto-dev（总指挥）
 │
 ├── Step 1: doc-prepare      文档预处理（PDF → PNG + 文本 + SDK 摘要）
 ├── Step 2: decompose         项目分解（识别独立子功能，创建模块目录）
 ├── Step 3: ssrd-enrich       需求充实（SSRD 业务规则 → 各模块）
 ├── Step 4: sdk-enrich        SDK 映射（API 映射 + 全局共享协议）
 ├── Step 5: sketch-enrich     UI 标注（Sketch 标注 → 像素级 UI 规格）
 └── Step 6: dev-runner        编码（共享基础层 → 并行模块编码 → 合并编译测试）
```

## 7 个 Skill

| # | Skill | 做什么 | 产出 |
|---|-------|--------|------|
| 0 | **auto-dev** | 总指挥：检测进度，断点续跑，每步验证+自修复 | 全部 |
| 1 | **doc-prepare** | PDF/Sketch/SDK 转为 LLM 可读格式 | `docs/prepared/` |
| 2 | **decompose** | 分析交互设计，将项目拆解为独立子功能 | `docs/modules/index.json` + 各模块目录 |
| 3 | **ssrd-enrich** | 逐模块充实 SSRD 需求（业务规则、数据定义、边界条件） | 各模块 `ssrd_png/` + `ssrd_data` |
| 4 | **sdk-enrich** | 映射 SDK API，生成全局共享接口协议 | 各模块 `sdk_apis` + `shared_interfaces.json` |
| 5 | **sketch-enrich** | 分发 Sketch 标注数据和预览图 | 各模块 `sketch_data.json` + `sketch_png/` |
| 6 | **dev-runner** | 总工调度编码：搭基础 → 派 subagent → 合并编译测试 | `app/` 全量代码 + `build_report.md` |

## 数据流

```
输入文档 (PDF / Sketch / SDK JAR)
    │
    ▼ doc-prepare
docs/prepared/
├── interaction/     (HMI → PNG + 文本)
├── requirements/    (SSRD → PNG + 文本)
├── visual/          (Sketch → 画板 JSON)
└── sdk/             (SDK → API 摘要)
    │
    ▼ decompose
docs/modules/
├── index.json                    (子功能清单)
├── incoming_call/                (每个子功能一个自包含目录)
│   ├── manifest.json             (结构化元数据，逐步充实)
│   ├── README.md                 (交互规格)
│   └── hmi_png/                  (HMI 原始页)
    │
    ▼ ssrd-enrich → sdk-enrich → sketch-enrich (逐步充实每个模块)
│   ├── ssrd_png/                 (+ SSRD 原始页)
│   ├── sketch_data.json          (+ 标注层数据)
│   ├── sketch_png/               (+ 标注预览图)
│   └── manifest.json             (+ ssrd_data, sdk_apis, dependencies, cut_images)
├── shared_interfaces.json        (全局共享协议)
└── design_tokens.json            (全局颜色 + 切图清单)
    │
    ▼ dev-runner
app/app/src/main/java/...         (Java 源码)
app/app/src/main/res/...          (布局 + 切图 + 资源)
app/app/src/test/java/...         (单元测试)
docs/build_report.md              (构建报告)
```

## 使用方式

告诉 AI："请根据输入完成代码开发"，auto-dev skill 自动启动全流程。

支持断点续跑 — 如果中途中断，重新启动会自动跳过已完成的步骤。
