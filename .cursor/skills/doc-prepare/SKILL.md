---
name: doc-prepare
description: Convert all raw input documents (PDFs, Sketch annotations, SDK docs, etc.) into LLM-friendly formats. Use when the user provides project documents and wants to start the development pipeline.
---

# 文档预处理

将项目的各类输入文档统一转为 LLM 可读格式，为后续 skill 提供干净的数据源。

## 何时使用

- 项目启动，用户提供了原始需求文档、交互设计、视觉标注、SDK 等
- 新增了文档或 SDK 需要纳入开发流程

## 支持的文档类型

| 类型 | 输入格式 | 处理方式 | 输出 |
|------|---------|---------|------|
| 需求文档 | PDF | 每页转 PNG + 提取文本 | images + text + page_index.json |
| 交互设计 | PDF | 每页转 PNG + 提取文本 | images + text + page_index.json |
| 视觉标注 | Sketch MeaXure (HTML/JSON) | 拆分为逐画板 JSON | artboards + artboard_index.json |
| SDK 依赖 | JAR + Javadoc HTML | 提取 API 摘要 | api_summary.md |

未来可扩展：Figma export、Zeplin、Axure、AAR、AIDL 等。

## 工作流程

### Step 1: 确认输入文档

询问用户有哪些输入文档，确定每个文档的类型：

```
用户可能说：
- "我有需求文档 xxx.pdf 和交互设计 yyy.pdf"
- "标注文件在 输入/标注/xx.json"
- "SDK 在 输入/依赖/ 目录下"
```

特别注意：检查是否有 **SDK / 依赖库** 目录。常见形式：
- 带 Javadoc HTML 文档的目录（含 `doc/reference/` 或 `docs/html/`）
- JAR 文件 + ReleaseNote
- AIDL 接口文件

### Step 2: 创建输出目录

```
docs/prepared/
├── requirements/    ← 需求文档（如 SSRD/PRD）
├── interaction/     ← 交互设计文档（如 HMI/UX spec）
├── visual/          ← 视觉标注（如 Sketch/Figma）
└── sdk/             ← SDK 依赖（API 摘要）
```

每类文档一个子目录。如果同类有多份文档，用文档简称区分子目录。

### Step 3: 执行转换 — PDF 与视觉标注

**PDF 文档**：

```bash
python3 .cursor/skills/doc-prepare/scripts/pdf_to_images.py "<input.pdf>" "docs/prepared/<type>/" [--dpi 200]
```

**Sketch MeaXure**：

```bash
python3 .cursor/skills/doc-prepare/scripts/split_sketch.py "<input>" "docs/prepared/visual/"
```

### Step 4: 执行转换 — SDK 依赖

对输入目录中的每个 SDK，判断其文档形式并处理：

**情况 A：有 Javadoc HTML 文档**

```bash
python3 .cursor/skills/doc-prepare/scripts/extract_javadoc.py \
    "<javadoc_html_root>" "docs/prepared/sdk/<sdk_name>/" \
    --sdk-name "<display_name>"
```

脚本会自动：
- 扫描所有 `.html` 文件
- 提取每个 public 类的方法签名、常量、Listener 接口
- 输出 `api_summary.md`（全部类的摘要）+ `class_index.json`（类清单索引）

**情况 B：只有 JAR 没有 Javadoc**

1. 对于 **标准 Android API**（如 `android.jar`）：不需要提取，LLM 已有知识。跳过。
2. 对于 **未知 vendor JAR**：用 `jar tf <file>.jar | grep '\.class$'` 列出类名，再结合 ReleaseNote 手工判断关键类。必要时让 LLM 直接从反编译或源码推断 API。
3. 对于 **有 ReleaseNote 但无 Javadoc 的**：将 ReleaseNote 内容摘要为 `api_summary.md`，标注为"不完整，仅含版本变更信息"。

**情况 C：无 SDK 依赖**

完全跳过本步骤。`docs/prepared/sdk/` 目录不创建。后续 skill 会检测该目录是否存在。

### Step 5: 识别标准平台 API

除 vendor SDK 外，还需识别项目用到的**标准 Android 平台 API**：

1. 阅读需求文档的功能范围（从 Step 1 或 page_index.json 的 preview 判断）
2. 推断对应的标准 Android 包，常见映射：
   - 蓝牙相关 → `android.bluetooth`
   - 电话/通信 → `android.telecom`
   - 音频/媒体 → `android.media`
   - 位置/地图 → `android.location`
   - 网络通信 → `java.net` / `android.net`
   - 传感器 → `android.hardware`
3. 在 `docs/prepared/sdk/` 下创建 `platform_apis.md`，列出关键包名和类名

这一步不从 `android.jar` 提取（LLM 已有知识），只需**告诉下游该关注哪些标准 API**。

### Step 6: 验证输出

- [ ] 每个 PDF 都有 `page_index.json` + `pages/` + `text/`
- [ ] Sketch 拆分有 `artboard_index.json` + `artboards/` + `colors.json` + `slices.json`
- [ ] 每个 vendor SDK（有 Javadoc 的）都有 `api_summary.md`，且内容非空
- [ ] 如有 SDK 依赖，`platform_apis.md` 存在且列出了标准 API 关键类
- [ ] 读取 `page_index.json`，确认 `preview` 字段有意义（非乱码）

### Step 7: 报告给用户

告诉用户每个文档转出了多少页/画板/SDK 类，建议下一步执行 decompose（项目分解）。

## 输出约定

```
docs/prepared/
├── requirements/          ← 需求文档
│   ├── page_index.json
│   ├── pages/page_01.png ~ page_NN.png
│   └── text/page_01.txt ~ page_NN.txt
├── interaction/           ← 交互设计
│   ├── page_index.json
│   ├── pages/page_01.png ~ page_NN.png
│   └── text/page_01.txt ~ page_NN.txt
├── visual/                ← 视觉标注
│   ├── artboard_index.json
│   ├── colors.json
│   ├── slices.json
│   └── artboards/*.json
└── sdk/                   ← SDK 依赖（可选，无 SDK 时此目录不存在）
    ├── <vendor_a>/
    │   ├── api_summary.md      (方法签名+常量+Listener)
    │   └── class_index.json    (类清单索引)
    ├── <vendor_b>/
    │   ├── api_summary.md
    │   └── class_index.json
    └── platform_apis.md        (项目用到的标准 Android API 清单)
```

## 依赖

- Python 3.6+
- PyMuPDF: `pip3 install --user pymupdf`（PDF 转换需要）
- BeautifulSoup4: `pip3 install --user beautifulsoup4`（Javadoc HTML 解析需要）

## 下游

- decompose — 读取本 skill 产出的交互设计 PNG，将项目分解为独立子功能
