# Task: {task_name}

## 概览

- **任务 ID**: {task_id}
- **组件类型**: {component_type} (activity / fragment / view)
- **依赖**: {depends_on}

## 需要查看的文件

### 画板数据（按需读取）
| 文件 | 角色 | 说明 |
|------|------|------|
| `artboards/06-联系人.json` | base | 正常状态，以此为基准写布局 |
| `artboards/01-联系人-按下.json` | variant | 列表项按下时的视觉差异 |

### 预览截图（按需读取）
- `preview/页面-1-6-联系人.png` — 正常状态截图
- `preview/页面-1-1-联系人-按下.png` — 按下状态截图

### 全局资源（开始前读取）
- `colors.json` — 颜色规范
- `slices.json` — 切图清单

## 开发步骤

### Step 1: 阅读基准画板
读取 base 画板的 JSON 和 preview 截图，理解整体布局。

### Step 2: 搭建布局
根据画板中各图层的 rect 坐标，创建 XML 布局文件。

### Step 3: 设置样式
根据 colors.json 和图层的 color/fontSize/fontFace 字段设置文字、颜色样式。

### Step 4: 导入切图
将画板引用的切图文件复制到 Android 项目的 res/drawable 目录。

### Step 5: 处理交互状态
对比 variant 画板与 base 画板的差异，实现 pressed/disabled 等状态切换。

### Step 6: 验证
对比 preview 截图，确认布局还原度。
