package com.vinpin.ztools

import android.app.Application

/**
 * 全局工具类初始化入口
 * 统一管理项目中所有工具类的初始化操作，提供全局 Application 实例获取功能
 * 需在 Application 的 onCreate 方法中完成初始化
 */
object Util {

    /**
     * 全局 Application 实例，保证整个应用生命周期内的唯一实例
     */
    private var application: Application? = null

    /**
     * 初始化全局工具类
     * 推荐在 Application 的 onCreate 中调用，完成工具类的统一初始化
     *
     * @param application 应用的 Application 实例
     */
    fun init(application: Application) {
        this.application = application
        // 初始化Activity管理器，自动注册Activity生命周期监听
        ActivityManager.init(application)
        // 初始化进程管理器，自动注册进程生命周期监听
        ProcessLifecycleManager.init(application)
        // 初始化网络状态管理器，自动注册网络状态监听
        NetworkManager.init(application)
    }

    /**
     * 获取全局 Application 实例
     *
     * @return 全局唯一的 Application 对象
     * @throws NullPointerException 未调用 init() 初始化时抛出该异常
     */
    fun getApp(): Application {
        if (application != null) {
            return application!!
        } else {
            throw NullPointerException("请先调用 Util.init(application) 完成初始化")
        }
    }
}