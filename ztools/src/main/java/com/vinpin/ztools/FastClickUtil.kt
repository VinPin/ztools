package com.vinpin.ztools

/**
 * 快速点击工具类
 */
object FastClickUtil {

    /**
     * 上一次点击的时间
     */
    private var lastFastClickTime: Long = 0L

    /**
     * 判断是不是快速点击
     *
     * @param intervalTime 间隔时间，单位毫秒，默认值300毫秒
     * @return true:是快速点击，false:不是快速点击
     */
    fun isFastClick(intervalTime: Long = 300L): Boolean {
        val t = System.currentTimeMillis()
        val dt: Long = t - lastFastClickTime
        if (dt in 1..intervalTime) return true
        lastFastClickTime = t
        return false
    }
}