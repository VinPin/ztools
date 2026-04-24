plugins {
    alias(libs.plugins.android.application) apply false // 声明 Android 应用插件，不应用到根项目
    alias(libs.plugins.kotlin.android) apply false // 声明 Kotlin Android 插件，不应用到根项目
    alias(libs.plugins.android.library) apply false // 声明 Android 库插件，不应用到根项目
}