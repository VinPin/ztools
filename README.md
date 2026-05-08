# ztools

[![](https://jitpack.io/v/VinPin/ztools.svg)](https://jitpack.io/#VinPin/ztools)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg)](https://developer.android.com/about/dashboards/platform-versions)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.6.21-blue.svg)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

> Android 开发过程中常用工具类

ztools 是一个轻量级、高性能的 Android Kotlin 工具库，为 Android 开发提供一系列实用的工具类和扩展函数。该库经过精心设计，具有零依赖（仅需
AndroidX 和 Kotlin 标准库）、无反射、无运行时注解处理器的特点，确保最小的 APK 体积增量和最佳性能。

## ✨ 特性

### 📱 Activity 生命周期管理

- **ActivityManager**: 全局 Activity 管理器，支持 Activity 的添加、移除、获取、遍历、栈操作（如 finish 所有
  Activity、finish 到指定 Activity）
- **ProcessLifecycleManager**: 应用进程生命周期监听器，可准确检测应用是否处于前台或后台状态

### 🌐 网络状态管理

- **NetworkManager**: 网络状态监控器，支持实时监听网络连接状态（WiFi、移动数据）、网络类型（4G、5G、WiFi）以及网络可用性

### 🖥️ 屏幕与显示工具

- **ScreenUtil**: 屏幕尺寸、密度、状态栏高度、导航栏高度等设备信息获取工具
- **ImageUtil**: 图片处理工具，支持 Bitmap 缩放、圆角裁剪、模糊处理等

### 🌙 主题与模式

- **NightModeUtil**: 深色模式（Dark Mode）管理工具，支持系统自动、强制深色、强制浅色三种模式切换

### ⚡ 性能优化工具

- **WorkQueueUtil**: 串行任务队列，确保任务按顺序执行，避免并发问题
- **ThrottleUtil** / **FirstLastThrottleUtil**: 防抖和节流工具，有效控制高频事件（如点击、滚动）的触发频率
- **FastClickUtil**: 快速点击防重复工具，防止用户短时间内多次点击同一控件

### 🔐 安全与加密

- **MD5Util**: MD5 哈希计算工具，支持字符串和文件的 MD5 计算
- **UniqueIdUtil**: 设备唯一标识符生成工具，支持多种标识符策略

### 📦 数据处理

- **JSONKt** / **JSONObjectKt** / **JSONArrayKt**: JSON 处理扩展，提供便捷的 JSON 解析、序列化和操作方法
- **StringKt**: 字符串处理扩展，包含丰富的字符串操作和验证方法
- **FileUtil**: 文件操作工具，支持文件读写、复制、删除、路径处理等

### 🧩 其他实用工具

- **ContextKt**: Context 扩展函数，提供便捷的资源获取、Intent 启动、Toast 显示等方法
- **UiThreadUtil**: UI 线程工具，简化主线程操作
- **Util**: 通用工具类，包含各种实用的静态方法

## 🚀 快速开始

### 添加依赖

在 `app/build.gradle.kts` 或 `build.gradle.kts` 中添加以下依赖：

```kotlin
dependencies {
    implementation("com.github.VinPin:ztools:1.0.0")
}
```

> 💡 提示：请将 `1.0.0` 替换为最新的版本号。

### 初始化

在 `Application` 的 `onCreate` 方法中初始化：

```kotlin
// 初始化全局工具类
Util.init(application)
``` 

## 🛠️ 构建

ztools 使用现代 Gradle 构建系统，支持 Kotlin DSL 和版本目录（Version Catalogs）。

- **编译 SDK**: 31
- **最低 SDK**: 21
- **Kotlin 版本**: 1.6.21
- **构建工具**: AGP 7.4.2

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！在贡献之前，请阅读我们的 [贡献指南](CONTRIBUTING.md)。

## 📬 联系我们

如有任何问题或建议，请通过 GitHub Issues 联系我们。

## 🙏 致谢

感谢所有为 ztools 做出贡献的开发者！