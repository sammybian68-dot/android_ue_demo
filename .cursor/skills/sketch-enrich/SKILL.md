---
name: sketch-enrich
description: Map Sketch MeaXure annotation artboards to each modules/ sub-directory, extracting layer data (position, size, font, color, slices) and preview PNGs for pixel-perfect UI coding. Use after decompose has produced per-module directories.
---

# Sketch 标注充实

将 Sketch MeaXure 导出的标注数据（xx.json + preview PNGs + 切图资源）按界面拆分到各模块目录，为每个子功能提供精确到像素的 UI 规格。

## 核心原则

**一次解析，按界面分发。** 读取 xx.json 全量数据，按 artboard 名称映射到 screen_id，将标注数据和预览图写入各界面目录。

**面向布局编码。** 输出的 JSON 结构直接告诉 coding agent 每个控件的坐标、尺寸、字号、颜色，无需再读 HTML。

## 前置条件

- `输入/需求/标注/xx.json` — Sketch MeaXure 导出的全量标注数据
- `输入/需求/标注/preview/*.png` — 各画板预览图
- `输入/需求/切图/*.png` — 切图资源
- `docs/modules/index.json` — 各界面目录

## 工作流程

### Phase 1: 解析 xx.json，建立 artboard → 模块映射

读取 xx.json，按 artboard 名称关键词匹配到 screen_id：

```
artboard 名称关键词 → screen_id 映射规则：
  "蓝牙未连接"                        → bt_connection
  "同步操作"                          → bt_sync
  "没有通话记录" / "通话记录loading"     → call_records
  "联系人" / "模糊搜索" / "没有联系人" / "联系人loading" → contacts_list
  "拨号键盘"                          → dialer
  "拨号展开" / "拨号小框"              → outgoing_call
  "来电展开" / "来电小框"              → incoming_call
  "来电小框-隐私模式"                  → privacy_mode
  "通话中" / "通话键盘" / "通话-顶栏"   → in_call
  "第三方"                            → three_way_call
  "呼叫中心" / "I-Call" / "E-Call"     → tbox_call
```

一个 artboard 可以映射到多个模块（如"来电小框-隐私模式"同时归 incoming_call 和 privacy_mode）。

### Phase 2: 提取全局设计 token

从 xx.json 的 colors 数组提取颜色设计 token，从 slices 提取切图清单，写入全局文件。

### Phase 3: 逐界面写入标注数据

对每个 screen_id：

1. 复制关联的 preview PNG 到 `<screen_id>/sketch_png/`
2. 提取该模块关联的所有 artboard 的完整 layer 数据
3. 写入 `<screen_id>/sketch_data.json`，结构如下：

```json
{
  "screen_id": "dialer",
  "artboards": [
    {
      "name": "拨号键盘",
      "index": 12,
      "width": 1920,
      "height": 1080,
      "preview_png": "sketch_png/拨号键盘.png",
      "layers": [
        {
          "type": "text",
          "name": "通话记录",
          "rect": {"x": 672, "y": 226, "width": 120, "height": 31},
          "css": ["font-size: 30px;"],
          "text": "通话记录"
        },
        {
          "type": "slice",
          "name": "keyboard_btn_number_1_n",
          "rect": {"x": 1320, "y": 339, "width": 160, "height": 96},
          "exportable": [{"name": "keyboard_btn_number_1_n", "format": "png"}]
        }
      ]
    }
  ]
}
```

4. 更新 manifest.json 的 artifacts：

```json
{
  "artifacts": {
    "sketch_data": "sketch_data.json",
    "sketch_png": ["sketch_png/页面-1-13-拨号键盘.png"],
    "cut_images": ["keyboard_btn_number_1_n.png", "keyboard_btn_number_answer_n_1.png", "..."]
  }
}
```

### 关键实现细节

**Preview 匹配**：使用 artboard 的 `slug` 字段（与 preview 文件名一致），而非 `name`（大小写可能不同）。

**切图资源映射**：遍历每个 artboard 的 layers，收集所有 `type=slice` 的 `exportable.name`，再从 `输入/需求/切图/` 目录中匹配同名及状态变体（_n/_p/_d）的 PNG 文件。

**sketch_data.json 内容**：除 artboard layers 外，还包含 `referenced_slices`（该模块引用的切图名列表）和 `cut_images`（实际对应的切图文件名列表），以及 `cut_images_dir` 路径。

## 输出结构

```
docs/modules/
├── design_tokens.json                # 新增：全局颜色 token + 全量切图清单
├── dialer/
│   ├── sketch_data.json              # 新增：标注层数据 + referenced_slices + cut_images
│   ├── sketch_png/                   # 新增：预览图（用 slug 精确匹配）
│   │   └── 页面-1-13-拨号键盘.png
│   ├── manifest.json                 # 更新：+sketch_data +sketch_png +cut_images
│   └── ...
└── .../
```
