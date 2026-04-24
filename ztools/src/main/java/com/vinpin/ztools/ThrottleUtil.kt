package com.vinpin.ztools

import java.util.concurrent.atomic.AtomicLong

/**
 * 节流工具类
 *
 * 用于限制操作的执行频率，确保在指定的时间间隔内只执行一次。
 * 适用于接口请求限频、消息发送控制等场景。
 *
 * 特性：
 * - 线程安全：使用 AtomicLong 和 synchronized 保证多线程环境下的正确性
 * - 可重置：支持手动重置节流状态
 *
 * 使用示例：
 * ```
 * // 创建节流器，设置节流时间为 1 秒
 * val throttle = ThrottleUtil(1000L)
 *
 * // 在需要节流的地方调用
 * if (!throttle.shouldThrottle()) {
 *     // 执行实际操作
 *     doSomething()
 * }
 *
 * // 重置节流状态
 * throttle.reset()
 * ```
 */
class ThrottleUtil(
    /**
     * 节流时间，默认1分钟，单位毫秒。
     */
    var throttleTime: Long = 1 * 60 * 1000L
) {

    private val lastThrottleTime = AtomicLong(0L)

    /**
     * 判断当前操作是否应该被节流
     *
     * @return true 表示应该节流（距离上次执行时间不足throttleTime），false 表示可以执行。
     */
    @Synchronized
    fun shouldThrottle(): Boolean {
        val nowTime = System.currentTimeMillis()
        val lastTime = lastThrottleTime.get()
        if (lastTime > 0 && nowTime - lastTime < throttleTime) return true
        lastThrottleTime.set(nowTime)
        return false
    }

    /**
     * 重置节流状态
     */
    fun reset() {
        lastThrottleTime.set(0L)
    }
}