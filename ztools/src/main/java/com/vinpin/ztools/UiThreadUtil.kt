package com.vinpin.ztools

import android.os.Handler
import android.os.Looper

/**
 * 与UI线程进行交互的实用工具
 */
object UiThreadUtil {
    /**
     * 主线程的Handler
     */
    private val mainHandler by lazy { Handler(Looper.getMainLooper()) }

    /**
     * 判断当前是否在UI线程
     */
    fun isOnUiThread(): Boolean {
        return Looper.getMainLooper().thread === Thread.currentThread()
    }

    /**
     * 在UI线程上运行给定的`Runnable`
     */
    fun runOnUiThread(runnable: Runnable) {
        if (isOnUiThread()) {
            runnable.run()
        } else {
            runOnUiThread(runnable, 0)
        }
    }

    /**
     * 以指定的延迟在UI线程上运行给定的`Runnable`
     */
    fun runOnUiThread(runnable: Runnable, delayInMs: Long) {
        mainHandler.postDelayed(runnable, delayInMs)
    }

    /**
     * 删除消息队列中所有挂起的给定的`Runnable`
     */
    fun removeCallbacks(runnable: Runnable) {
        mainHandler.removeCallbacks(runnable)
    }
}