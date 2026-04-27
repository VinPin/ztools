package com.vinpin.ztools

/**
 * 节流工具类
 *
 * 用于首次触发立即执行，冷却期内的调用会被合并，冷却结束后执行最后一次。
 * 适用于频繁消息通知、高频事件合并刷新等场景
 *
 * 使用示例：
 * ```
 * // 创建节流器，设置节流时间为 1 秒
 * val throttle = ZIMFirstLastThrottle(1000L)
 *
 * // 创建一个 Runnable 对象，包含需要节流执行的操作
 * val runnable = Runnable {
 *     // 执行实际操作
 *     doSomething()
 * }
 *
 * // 在需要的地方启动节流任务
 * throttle.launch(runnable)
 *
 * // 重置防抖节流状态
 * throttle.reset()
 * ```
 */
class FirstLastThrottleUtil(
    /**
     * 冷却时间（毫秒），默认 1000ms
     */
    var coolDownMillis: Long = 1000L
) {

    /**
     * 是否处于冷却期
     */
    @Volatile
    private var isCooling = false

    /**
     * 冷却期内是否有待执行任务
     */
    @Volatile
    private var hasPendingTask = false

    /**
     * 待执行的任务体
     */
    private var runnable: Runnable? = null

    /**
     * 冷却结束后的执行任务
     * 用于重置冷却状态，并执行冷却期间积累的最后一次任务
     */
    private val coolDownRunnable = Runnable {
        isCooling = false
        if (hasPendingTask) {
            hasPendingTask = false
            runnable?.run()
        }
    }

    /**
     * 启动节流任务
     *
     * 执行逻辑：
     * 1. 非冷却期 → 立即执行任务，并进入冷却期
     * 2. 冷却期内 → 标记有待执行任务，不立即执行
     * 3. 冷却结束 → 若有待执行任务，则执行一次
     *
     * @param runnable 需要执行的任务逻辑
     */
    fun launch(runnable: Runnable) {
        this.runnable = runnable
        if (!isCooling) {
            isCooling = true
            hasPendingTask = false
            runnable.run()
            UiThreadUtil.runOnUiThread(coolDownRunnable, coolDownMillis)
        } else {
            hasPendingTask = true
        }
    }

    /**
     * 重置节流状态
     */
    fun reset() {
        UiThreadUtil.removeCallbacks(coolDownRunnable)
        isCooling = false
        hasPendingTask = false
        runnable = null
    }
}