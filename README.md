# ztools

[![](https://jitpack.io/v/VinPin/ztools.svg)](https://jitpack.io/#VinPin/ztools)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg)](https://developer.android.com/about/dashboards/platform-versions)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.6.21-blue.svg)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

ztools 是一个轻量级、高性能的 Android Kotlin 工具库，为 Android 开发提供一系列实用的工具类和扩展函数。该库经过精心设计，具有零依赖（仅需
AndroidX 和 Kotlin 标准库）、无反射、无运行时注解处理器的特点，确保最小的 APK 体积增量和最佳性能。

## 添加依赖

```kotlin
dependencies {
    implementation("com.github.VinPin:ztools:1.0.0")
}
```

## 使用方法

在 `Application` 的 `onCreate` 方法中初始化：

```kotlin
// 初始化全局工具类
Util.init(application)
``` 

## 构建

ztools 使用现代 Gradle 构建系统，支持 Kotlin DSL 和版本目录（Version Catalogs）。

- **编译 SDK**: 31
- **最低 SDK**: 21
- **Kotlin 版本**: 1.6.21
- **构建工具**: AGP 7.4.2

## 联系我们

如有任何问题或建议，请通过 GitHub Issues 联系我们。

## 致谢

感谢所有为 ztools 做出贡献的开发者！