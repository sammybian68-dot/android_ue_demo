# 北汽 N5 蓝牙电话 APK

车载中控蓝牙电话应用，基于 1920×1080 横屏车载中控设计。所有后端功能已 Mock 实现（无需真实蓝牙连接）。

## 快速开始

### 前置条件

- **Android Studio Hedgehog (2023.1.1)** 或更高版本
- **JDK 17**
- **Android SDK 34**（在 SDK Manager 中安装）

### 1. 用 Android Studio 打开项目

```bash
# 直接用 Android Studio 打开项目根目录
open -a "Android Studio" /Users/cmbian/workspace/codes/ue_demo
```

或在 Android Studio 中：File → Open → 选择 `/Users/cmbian/workspace/codes/ue_demo` 目录。

Android Studio 会自动：
- 下载 Gradle 8.5 wrapper
- 同步依赖
- 生成 ViewBinding 类

### 2. 创建车载模拟器 (AVD)

由于是 1920×1080 横屏车载应用，需要创建自定义模拟器配置：

**方法 A：通过 Android Studio GUI**

1. 打开 **Tools → Device Manager → Create Virtual Device**
2. 点击 **New Hardware Profile**，填写：
   - Device Name: `Car Display 1920x1080`
   - Screen Size: `10.1 inches`（或任意）
   - Resolution: `1920 x 1080`
   - 勾选 **Landscape**
   - Navigation: No Nav
3. 点击 **Finish** 保存 Hardware Profile
4. 选中刚创建的 Profile，点 **Next**
5. 选择系统镜像：**API 34 (Android 14)** x86_64 → 点 Download 下载
6. 点 **Next → Finish**
7. 启动模拟器

**方法 B：通过命令行**

```bash
# 确保 ANDROID_HOME 已设置
export ANDROID_HOME=~/Library/Android/sdk
export PATH=$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH

# 安装系统镜像（如未安装）
sdkmanager "system-images;android-34;google_apis;x86_64"

# 创建 AVD
avdmanager create avd \
  --name "CarDisplay" \
  --package "system-images;android-34;google_apis;x86_64" \
  --device "pixel_tablet" \
  --force

# 启动模拟器（横屏）
emulator -avd CarDisplay -skin 1920x1080 &
```

> **提示**：也可以直接使用 `Pixel Tablet` 预置配置（2560×1600），应用会自动适配，只是比例略有不同。

### 3. 构建并运行

**通过 Android Studio**：
- 选择刚创建的 AVD → 点击 ▶ Run

**通过命令行**：

```bash
cd /Users/cmbian/workspace/codes/ue_demo

# 生成 Gradle Wrapper（首次）
gradle wrapper --gradle-version 8.5

# 构建 Debug APK
./gradlew :app:assembleDebug

# 安装到模拟器
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 启动应用
adb shell am start -n com.baic.btphone/.ui.main.MainActivity
```

### 4. 运行测试

```bash
# 单元测试（不需要模拟器）
./gradlew :app:testDebugUnitTest

# 查看测试报告
open app/build/reports/tests/testDebugUnitTest/index.html

# Instrumented 测试（需要模拟器运行中）
./gradlew :app:connectedDebugAndroidTest

# 查看报告
open app/build/reports/androidTests/connected/index.html
```

## 应用演示流程

启动应用后会自动进行以下 Mock 流程：

1. **0.5 秒后**：自动连接蓝牙设备 "iPhone X"
2. **连接成功后**：自动同步通话记录和联系人（模拟 1-2 秒）
3. **显示主界面**：
   - 左侧：通话记录列表（30+ 条 Mock 数据）
   - 右侧：拨号键盘
4. **8 秒后**：自动模拟一个来电（李明 13800138000），弹出来电全屏界面
5. **手动操作**：
   - 点击"接听"→ 进入通话中界面，显示通话计时
   - 点击 Tab "联系人"→ 查看联系人列表，支持字母索引
   - 在拨号盘输入数字 → 实时 T9 模糊搜索
   - 点击联系人 → 查看详情弹窗，点击号码拨打
   - 拨号 3 秒后自动接通，显示通话中界面

## 项目结构

```
ue_demo/
├── docs/prd.md                              # 产品需求文档
├── 输入/                                     # 设计文件（UE 标注、切图、需求文档）
├── app/
│   ├── build.gradle.kts                     # 应用构建配置
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/baic/btphone/
│       │   │   ├── BtPhoneApp.kt            # Application
│       │   │   ├── model/                   # 数据模型 (Contact, CallRecord, CallState)
│       │   │   ├── mock/                    # Mock 服务 (蓝牙、通话、联系人、通话记录)
│       │   │   ├── util/                    # 工具类 (号码格式化、拼音、时间)
│       │   │   └── ui/
│       │   │       ├── main/                # 主界面 (Activity + ViewModel)
│       │   │       ├── calllog/             # 通话记录 (Fragment + Adapter)
│       │   │       ├── contacts/            # 联系人 (Fragment + Adapter + Detail)
│       │   │       ├── dialer/              # 拨号盘模糊搜索 (Adapter)
│       │   │       ├── call/                # 去电/通话中 (Activity)
│       │   │       ├── incoming/            # 来电 (Activity)
│       │   │       ├── sync/                # 同步引导弹窗
│       │   │       ├── icall/               # I-Call 客服中心
│       │   │       ├── ecall/               # E-Call 紧急服务
│       │   │       └── widget/              # 自定义控件 (字母索引、浮窗)
│       │   └── res/
│       │       ├── drawable-xxhdpi/         # 135 张 UE 切图资源
│       │       ├── drawable/                # Selector、Shape 等 XML drawable
│       │       ├── layout/                  # 20 个布局文件
│       │       ├── values/                  # 颜色、尺寸、字符串、主题
│       │       └── anim/                    # 加载动画
│       ├── test/                            # 9 个单元测试文件
│       └── androidTest/                     # 6 个 Instrumented 测试文件
├── build.gradle.kts                         # 根构建文件
├── settings.gradle.kts                      # 项目设置
└── gradle.properties                        # Gradle 配置
```

## 测试覆盖

### 单元测试 (9 个文件)

| 测试文件 | 覆盖的 PRD 需求 |
|---------|---------------|
| PhoneNumberFormatterTest | 4.2.2 号码分割规范（三四四、四三、四四、区号） |
| PinyinHelperTest | 4.3.2 联系人拼音排序与索引 |
| TimeFormatterTest | 4.2.2 通话时间显示（今天/昨天/更早）、通话计时格式 |
| MockBluetoothServiceTest | 4.1 蓝牙连接状态流转 |
| MockCallManagerTest | 4.5/4.6/4.7 拨号→接通→挂断状态机、静音、切换手机 |
| MockContactsProviderTest | 4.3 联系人列表排序、搜索 |
| MockCallRecordProviderTest | 4.2 通话记录条数、类型分布 |
| CallLogViewModelTest | 4.4.3 T9 模糊搜索、输入清除恢复列表 |
| ContactsViewModelTest | 4.3.2 联系人按字母分组排序 |

### Instrumented 测试 (6 个文件)

| 测试文件 | 覆盖的 PRD 需求 |
|---------|---------------|
| MainActivityTest | 4.1.2 蓝牙已连接设备名显示、3.2 Tab 导航切换 |
| CallLogFragmentTest | 4.2.2 通话记录列表、4.4 拨号输入/删除/拨打、4.4.3 模糊搜索 |
| ContactsFragmentTest | 4.3.2 联系人分组/头像/详情弹窗、字母索引 |
| CallActivityTest | 4.5.1/4.7.1 去电状态文案、通话计时、静音、挂断、DTMF 键盘 |
| IncomingCallTest | 4.6.1 来电信息显示、接听/挂断 |
| NavigationTest | 4.1.1 蓝牙未连接、4.1.2 连接后跳转、4.2.1 同步失败空态 |

## 技术栈

- **语言**: Kotlin 1.9
- **架构**: MVVM (ViewModel + LiveData + StateFlow)
- **UI**: Android XML Layouts + ViewBinding
- **异步**: Kotlin Coroutines
- **测试**: JUnit 4 + Espresso + kotlinx-coroutines-test
- **最低 SDK**: 28 (Android 9)
- **目标 SDK**: 34 (Android 14)
