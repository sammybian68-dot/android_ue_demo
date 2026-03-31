---
name: sdk-enrich
description: Enrich each modules/ sub-directory with SDK API mapping, inter-module dependencies, and generate a global shared-interfaces contract. Use after decompose and ssrd-enrich have completed, and when SDK JARs + docs are available in 输入/依赖/.
---

# SDK 需求充实 & 全局接口协议

逐个子功能分析其所需的 SDK API（android.jar / android.car / autolink.sdk），写入各目录 manifest.json；同时提取跨模块交互，生成全局共享接口协议文件，确保各模块并行开发后可直接集成。

## 核心原则

**三步走：先盘 SDK、再映射模块、最后抽全局协议。**

1. 盘点可用 SDK — 读取 JAR 文档，提取所有与蓝牙电话相关的类/方法/常量
2. 逐模块映射 — 为每个模块指定所需的 SDK API + 模块间依赖
3. 全局协议 — 提取跨模块共享的服务/接口/数据模型/事件总线

**面向编码。** 输出直接指导代码生成，包含具体类名、方法签名、常量值。

## 前置条件

- `docs/modules/index.json` 已存在
- `docs/modules/*/manifest.json` 已存在（含 ssrd_data）
- `输入/依赖/` 目录下包含 SDK JARs 和文档

## 可用 SDK 清单

| SDK | 路径 | 用途 |
|-----|------|------|
| android.jar | `输入/依赖/android.jar` | Android 标准框架（Bluetooth API、TelecomManager 等） |
| android.car-1.0.21.jar | `输入/依赖/android.car_1.0.21_20250917/` | 车辆属性 ID（通话状态、音量、方控背光等） |
| autolink.sdk.v1.0.25.jar | `输入/依赖/autolink.sdk.v1.0.25/` | 平台 SDK（AudioManager、InputManager、TboxManager、ClusterInteractionManager、PowerManager） |

## 工作流程

### Phase 1: SDK API 盘点

读取各 SDK 的文档（HTML Javadoc / README），提取与蓝牙电话相关的全部 API：

**android.jar（标准 Android）：**
- `android.bluetooth.BluetoothAdapter` — 蓝牙开关、配对设备
- `android.bluetooth.BluetoothHeadsetClient` — HFP 免提协议（拨号、接听、挂断、三方、DTMF、音频路由）
- `android.bluetooth.BluetoothPbapClient` — PBAP 电话簿协议（联系人/通话记录同步）
- `android.provider.CallLog` — 通话记录内容提供者
- `android.provider.ContactsContract` — 联系人内容提供者
- `android.telecom.TelecomManager` — 通话管理
- `android.content.BroadcastReceiver` — 蓝牙状态广播

**android.car：**
- `ALN50VehiclePropertyIds.ICM_PHNMSGSTS_587` — 蓝牙电话状态（0=初始, 1=去电, 2=来电, 3=通话中, 4=挂断）
- `ALN50VehiclePropertyIds.ICM_PHNMSGCALLINGTIMEH/M/S_587` — 通话时长
- `ALN50VehiclePropertyIds.EHU_HFTVOLSET_46` — 免提电话音量
- `ALN50VehiclePropertyIds.EHU_STEERWHLPHNKEYBACKLI_529` — 方向盘电话按键背光
- `ALN50VehiclePropertyIds.MFS_SRCSWTBTN_514` — 方向盘按键输入

**autolink.sdk：**
- `AudioManager` — 蓝牙通话/铃声音频路由（USAGE_AL_BT_CALL/BT_RING）、麦克风静音、音量控制
- `InputManager` — 方控按键监听（SCENE_CALL、CALL_STATE_*）
- `TboxManager` — T-Box 电话控制（dial/acceptCall/terminateCall/sendDTMF + ITboxCallListener）
- `ClusterInteractionManager` — IVI↔仪表盘数据推送（来电信息、通话状态）
- `PowerManager` — 屏保控制（MODULE_PHONE）、主题模式

### Phase 2: 逐模块 SDK 映射 + 依赖声明

对每个模块，在 manifest.json 中追加：

```json
{
  "artifacts": {
    "sdk_apis": {
      "android": [
        {
          "class": "android.bluetooth.BluetoothHeadsetClient",
          "methods": ["dial", "acceptCall", "rejectCall", "terminateCall"],
          "purpose": "HFP 免提通话控制"
        }
      ],
      "android_car": [
        {
          "property": "ICM_PHNMSGSTS_587",
          "access": "WRITE",
          "purpose": "向仪表推送蓝牙电话状态"
        }
      ],
      "autolink_sdk": [
        {
          "manager": "AudioManager",
          "methods": ["setMicMute", "getGroupVolume", "setGroupVolume"],
          "purpose": "通话音频控制"
        }
      ]
    },
    "dependencies": [
      {
        "module_id": "bt_connection",
        "type": "required",
        "reason": "需要蓝牙已连接(HFP)才能发起通话"
      }
    ]
  }
}
```

### Phase 3: 全局共享接口协议

在 `docs/modules/` 下生成 `shared_interfaces.json`，定义所有模块共享的：

1. **共享服务（Shared Services）** — 多个模块依赖的单例服务
2. **事件总线协议（Event Bus Protocol）** — 模块间通信的事件定义
3. **共享数据模型（Shared Models）** — 多模块使用的数据类
4. **全局常量（Global Constants）** — 跨模块使用的常量定义

## 输出结构

```
docs/modules/
├── shared_interfaces.json            # 新增：全局共享接口协议
├── incoming_call/
│   ├── manifest.json                 # 更新：+sdk_apis +dependencies
│   └── ...
└── .../
```

## 下游

- sketch-enrich — 读取 index.json，为各子功能分发 Sketch 标注数据
- dev-runner — 读取 sdk_apis + dependencies + shared_interfaces.json，按拓扑排序编码
