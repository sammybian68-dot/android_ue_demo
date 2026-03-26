# Android 项目结构规范

## 目录布局

```
app/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/btphone/
│   │   │   ├── MainActivity.java
│   │   │   ├── ui/
│   │   │   │   ├── contacts/
│   │   │   │   │   ├── ContactsListFragment.java
│   │   │   │   │   ├── ContactDetailDialog.java
│   │   │   │   │   └── LetterIndexView.java
│   │   │   │   ├── dialer/
│   │   │   │   │   ├── DialerFragment.java
│   │   │   │   │   └── FuzzySearchView.java
│   │   │   │   ├── call/
│   │   │   │   │   ├── IncomingCallActivity.java
│   │   │   │   │   ├── OutgoingCallActivity.java
│   │   │   │   │   ├── InCallActivity.java
│   │   │   │   │   └── ThirdPartyCallActivity.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── CallCenterActivity.java
│   │   │   │   │   ├── ICallServiceActivity.java
│   │   │   │   │   └── ECallServiceActivity.java
│   │   │   │   └── common/
│   │   │   │       ├── EmptyStateView.java
│   │   │   │       ├── LoadingStateView.java
│   │   │   │       ├── BluetoothDisconnectedView.java
│   │   │   │       └── SyncDialog.java
│   │   │   └── util/
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml
│   │   │   │   ├── fragment_contacts_list.xml
│   │   │   │   ├── fragment_dialer.xml
│   │   │   │   ├── item_contact.xml
│   │   │   │   ├── item_call_log.xml
│   │   │   │   ├── activity_incoming_call.xml
│   │   │   │   ├── activity_in_call.xml
│   │   │   │   └── ...
│   │   │   ├── drawable/          ← 切图资源放这里
│   │   │   ├── values/
│   │   │   │   ├── colors.xml     ← 从 colors.json 转换
│   │   │   │   ├── dimens.xml     ← 从画板坐标提取
│   │   │   │   ├── strings.xml
│   │   │   │   └── styles.xml
│   │   │   └── values-night/
│   │   │       └── colors.xml     ← 夜间/深色主题色
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/
```

## 命名规范

| 类型 | 格式 | 示例 |
|------|------|------|
| Activity | `XxxActivity.java` | `IncomingCallActivity.java` |
| Fragment | `XxxFragment.java` | `ContactsListFragment.java` |
| Dialog | `XxxDialog.java` | `ContactDetailDialog.java` |
| 自定义 View | `XxxView.java` | `LetterIndexView.java` |
| 布局文件 | `activity_xxx.xml` / `fragment_xxx.xml` / `item_xxx.xml` | `activity_main.xml` |
| drawable | 保持切图原名 | `bt_list_bg_n.png` |

## 坐标转换

设计稿分辨率 1920×1080，Android 开发注意：
- 设计稿坐标单位是 px，需根据目标设备密度转换为 dp
- 如果目标设备也是 1920×1080（车载屏幕），可以直接用 px
- 布局优先使用 ConstraintLayout，用比例和约束而非绝对坐标
- 间距和尺寸提取为 dimens.xml 资源

## component_type 映射

| manifest 中的类型 | Android 组件 | 说明 |
|------------------|-------------|------|
| `activity` | Activity + layout XML | 独占全屏 |
| `fragment` | Fragment + layout XML | 嵌入 Activity 的面板 |
| `dialog` | DialogFragment + layout XML | 弹窗 |
| `view` | 自定义 View / include layout | 可复用控件 |
