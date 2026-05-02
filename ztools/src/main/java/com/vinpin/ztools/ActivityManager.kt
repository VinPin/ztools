package com.vinpin.ztools

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Process
import kotlin.system.exitProcess

/**
 * 全局Activity管理类
 * 采用栈结构管理所有Activity，提供添加、移除、关闭、获取当前Activity等通用操作
 * 用于统一管理应用生命周期，实现一键退出应用、关闭指定页面等功能
 */
object ActivityManager {

    /**
     * Activity存储栈（栈顶为当前前台显示的Activity）
     */
    private val activityStack = ArrayDeque<Activity>()

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
     * 注册Activity生命周期监听，防重复调用，仅第一次生效
     * 在Application的onCreate中调用，自动管理所有Activity的入栈/出栈
     *
     * @param application 应用Application
     */
    fun init(application: Application) {
        if (!isInitialized) {
            synchronized(lock) {
                if (!isInitialized) {
                    application.registerActivityLifecycleCallbacks(object :
                        Application.ActivityLifecycleCallbacks {
                        override fun onActivityCreated(
                            activity: Activity,
                            savedInstanceState: Bundle?
                        ) {
                            // Activity创建时自动添加到栈中
                            addActivity(activity)
                        }

                        override fun onActivityStarted(activity: Activity) {}
                        override fun onActivityResumed(activity: Activity) {}
                        override fun onActivityPaused(activity: Activity) {}
                        override fun onActivityStopped(activity: Activity) {}
                        override fun onActivitySaveInstanceState(
                            activity: Activity,
                            outState: Bundle
                        ) {
                        }

                        override fun onActivityDestroyed(activity: Activity) {
                            // Activity销毁时自动从栈中移除
                            removeActivity(activity)
                        }
                    })
                    // 标记为已初始化
                    isInitialized = true
                }
            }
        }
    }

    /**
     * 将Activity添加到栈中
     *
     * @param activity 要入栈的Activity
     */
    private fun addActivity(activity: Activity) {
        synchronized(lock) {
            if (!activityStack.contains(activity)) {
                activityStack.add(activity)
            }
        }
    }

    /**
     * 从栈中移除指定的Activity
     *
     * @param activity 要出栈的Activity
     */
    private fun removeActivity(activity: Activity) {
        synchronized(lock) {
            activityStack.remove(activity)
        }
    }

    /**
     * 获取当前栈顶的Activity（最后添加的Activity）
     *
     * @return 栈顶Activity，栈为空时返回null
     */
    fun currentActivity(): Activity? {
        synchronized(lock) {
            return activityStack.lastOrNull()
        }
    }

    /**
     * 关闭栈顶的Activity
     */
    fun finishActivity() {
        val activity = currentActivity()
        finishActivity(activity)
    }

    /**
     * 关闭指定的Activity，并从栈中移除
     *
     * @param activity 要关闭的Activity
     */
    fun finishActivity(activity: Activity?) {
        if (activity != null) {
            removeActivity(activity)
            activity.finish()
        }
    }

    /**
     * 关闭指定类名的Activity，仅关闭第一个匹配的
     *
     * @param cls 要关闭的Activity类
     */
    fun finishActivity(cls: Class<*>) {
        synchronized(lock) {
            if (activityStack.isEmpty()) return
            val tempList = ArrayList(activityStack)
            for (activity in tempList) {
                if (activity.javaClass == cls) {
                    finishActivity(activity)
                    break
                }
            }
        }
    }

    /**
     * 关闭指定类名的所有Activity
     *
     * @param cls 要关闭的Activity类
     */
    fun finishActivityOfClass(cls: Class<*>) {
        synchronized(lock) {
            if (activityStack.isEmpty()) return
            val tempList = ArrayList(activityStack).reversed()
            for (activity in tempList) {
                if (activity.javaClass == cls) {
                    finishActivity(activity)
                }
            }
        }
    }

    /**
     * 关闭栈中所有Activity，并清空栈
     * 用于一键退出应用
     */
    fun finishAllActivity() {
        synchronized(lock) {
            val tempList = ArrayList(activityStack)
            tempList.forEach { finishActivity(it) }
            activityStack.clear()
        }
    }

    /**
     * 判断栈中是否存在指定类名的Activity
     *
     * @param cls 要判断的Activity类
     * @return 栈中存在指定类名的Activity返回true，否则返回false
     */
    fun hasActivity(cls: Class<*>): Boolean {
        synchronized(lock) {
            return activityStack.any { it.javaClass == cls }
        }
    }

    /**
     * 获取栈中Activity数量
     *
     * @return 栈中Activity数量
     */
    fun size(): Int {
        synchronized(lock) {
            return activityStack.size
        }
    }

    /**
     * 退出应用程序，彻底杀死进程
     */
    fun appExit() {
        finishAllActivity()
        Process.killProcess(Process.myPid())
        exitProcess(0)
    }
}