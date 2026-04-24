pluginManagement { // 插件管理
    repositories { // 仓库列表，Gradle 会按顺序从这里找插件
        google { // 谷歌官方 Maven 仓库，安卓所有官方插件都在这里
            content { // 过滤优化，只下载安卓 / 谷歌相关插件，加速构建
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral() // 全球最大的 Java/Kotlin 中央仓库
        gradlePluginPortal() // Gradle 官方插件仓库（第三方 Gradle 插件都在这里）
    }
}

dependencyResolutionManagement { // 依赖管理
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // 强制所有模块统一使用这里的仓库
    repositories { // 项目依赖的仓库
        google() // 安卓官方依赖库
        mavenCentral() // 三方开源依赖库
    }
}

rootProject.name = "ztools"
include(":app")
include(":ztools")