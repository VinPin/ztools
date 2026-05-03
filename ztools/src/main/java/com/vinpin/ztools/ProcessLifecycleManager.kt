package com.vinpin.ztools

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 应用进程生命周期观察者
 * 用于监听应用【切换到前台】、【切换到后台】的回调
 */
interface ProcessLifecycleObserver {

    /**
     * 应用从后台切换到前台时回调
     */
    fun onForeground() {}

    /**
     * 应用从前台切换到后台时回调
     */
    fun onBackground() {}
}

/**
 * 应用进程生命周期管理器
 * 通过监听全局 Activity 生命周期，实现应用前后台状态的精准监听
 * 支持添加/移除监听，自动过滤【应用首次启动】的无效前台回调
 */
object ProcessLifecycleManager {

    /**
     * 全局锁，保证线程安全
     */
    private val lock = Any()

    /**
     * 初始化标记（volatile保证多线程可见性，防止多次注册）
     */
    @Volatile
    private var isInitialized = false

    /**
     * 首次启动标识
     * 应用正常启动时，第一个Activity可见会触发前台回调，此场景需要过滤
     */
    private var isFirstTime = true

    /**
     * 前台Activity计数
     * >0：应用在前台 | =0：应用在后台
     */
    private var foregroundCount = 0

    /**
     * 生命周期观察者集合
     */
    private var lifecycleObservers = CopyOnWriteArrayList<ProcessLifecycleObserver>()

    /**
     * 初始化前后台监听
     * 注册全局Activity生命周期回调，防重复初始化
     *
     * @param application 应用Application实例
     */
    fun init(application: Application) {
        if (!isInitialized) {
            synchronized(lock) {
                if (!isInitialized) {
                    registerActivityLifecycle(application)
                    isInitialized = true
                }
            }
        }
    }

    /**
     * 注册全局Activity生命周期监听
     */
    private fun registerActivityLifecycle(application: Application) {
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityStarted(activity: Activity) {
                // 非首次启动，且从后台回到前台，分发前台回调
                if (foregroundCount <= 0 && !isFirstTime) {
                    dispatchForeground()
                }
                foregroundCount++
                // 首次启动完成后，重置标识
                isFirstTime = false
            }

            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {
                foregroundCount--
                if (foregroundCount <= 0) {
                    dispatchBackground()
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }
        })
    }

    /**
     * 添加应用前后台生命周期监听
     *
     * @param observer 生命周期观察者
     */
    fun addProcessLifecycleObserver(observer: ProcessLifecycleObserver) {
        if (!lifecycleObservers.contains(observer)) {
            lifecycleObservers.add(observer)
        }
    }

    /**
     * 移除应用前后台生命周期监听
     *
     * @param observer 生命周期观察者
     */
    fun removeProcessLifecycleObserver(observer: ProcessLifecycleObserver) {
        if (lifecycleObservers.contains(observer)) {
            lifecycleObservers.remove(observer)
        }
    }

    /**
     * 移除所有生命周期监听
     */
    fun removeAllProcessLifecycleObserver() {
        lifecycleObservers.clear()
    }

    private fun dispatchForeground() {
        lifecycleObservers.forEach {
            runCatching { it.onForeground() }.onFailure { e -> e.printStackTrace() }
        }
    }

    private fun dispatchBackground() {
        lifecycleObservers.forEach {
            runCatching { it.onBackground() }.onFailure { e -> e.printStackTrace() }
        }
    }
}