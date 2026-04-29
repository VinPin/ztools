package com.vinpin.ztools

import android.os.Handler
import android.os.HandlerThread

/**
 * 单线程工作队列工具类
 * 基于 [HandlerThread] + [Handler] 实现全局串行任务队列
 * 特点：
 * 1. 单线程串行依次执行任务，任务排队不并发
 * 2. 线程发生异常自动销毁并重建，保证后续任务正常执行
 * 3. 支持即时任务、延迟任务、移除指定任务、清空所有任务
 * 4. 线程安全，支持多线程并发提交任务
 * 5. 支持主动安全退出线程，释放资源
 *
 * @param threadName 工作线程名称，便于调试查看线程
 *
 * 使用示例：
 * ```
 * // 1. 创建工作队列实例
 * val workQueue = WorkQueueUtil("upload-work-thread")
 *
 * // 2. 提交即时串行任务
 * workQueue.post {
 *     // 耗时操作：文件读写、数据库、分片上传、网络预处理等
 * }
 *
 * // 3. 提交延迟任务
 * workQueue.post({
 *     // 延迟后执行的业务逻辑
 * }, 1000L)
 *
 * // 4. 移除指定任务
 * val task = Runnable { }
 * workQueue.post(task)
 * workQueue.remove(task)
 *
 * // 5. 清空队列所有待执行任务
 * workQueue.removeAll()
 *
 * // 6. 页面/业务销毁时退出线程
 * override fun onDestroy() {
 *     workQueue.quit()
 * }
 * ```
 */
class WorkQueueUtil(var threadName: String = "work_queue_thread") {

    /**
     * 锁：保证线程安全
     */
    private val lock = Object()

    /**
     * 工作线程
     */
    @Volatile
    private var workThread: HandlerThread? = null

    /**
     * 工作线程中的Handler
     */
    @Volatile
    private var workHandler: Handler? = null

    /**
     * 异常处理器：崩溃后自动重建
     */
    private val exceptionHandler = Thread.UncaughtExceptionHandler { _, e ->
        e.printStackTrace()
        createHandlerThread()
    }

    /**
     * 初始化工作线程
     */
    init {
        createHandlerThread()
    }

    /**
     * 创建/重建线程
     */
    private fun createHandlerThread() {
        synchronized(lock) {
            // 先安全销毁旧线程
            destroyThread()
            val thread = HandlerThread(threadName).apply {
                start()
                uncaughtExceptionHandler = exceptionHandler
            }
            workThread = thread
            workHandler = Handler(thread.looper)
        }
    }

    /**
     * 销毁旧线程（安全退出）
     */
    private fun destroyThread() {
        synchronized(lock) {
            workHandler?.removeCallbacksAndMessages(null)
            workHandler = null
            workThread?.apply {
                quitSafely()
                interrupt()
            }
            workThread = null
        }
    }

    /**
     * 在工作线程中执行
     *
     * @param runnable 任务
     */
    fun post(runnable: Runnable) {
        synchronized(lock) {
            workHandler?.post(runnable)
        }
    }

    /**
     * 在工作线程中延迟执行
     *
     * @param runnable 任务
     * @param delayMillis 延迟时间
     */
    fun post(runnable: Runnable, delayMillis: Long) {
        synchronized(lock) {
            workHandler?.postDelayed(runnable, delayMillis)
        }
    }

    /**
     * 移除工作线程中的任务
     *
     * @param runnable 任务
     */
    fun remove(runnable: Runnable) {
        synchronized(lock) {
            workHandler?.removeCallbacks(runnable)
        }
    }

    /**
     * 移除工作线程中的所有任务
     */
    fun removeAll() {
        synchronized(lock) {
            workHandler?.removeCallbacksAndMessages(null)
        }
    }

    /**
     * 安全退出工作线程
     */
    fun quitSafely() {
        destroyThread()
    }
}