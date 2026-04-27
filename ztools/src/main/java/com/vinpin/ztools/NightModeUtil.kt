package com.vinpin.ztools

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

/**
 * 夜间模式工具类
 */
object NightModeUtil {

    /**
     * 设置应用夜间模式
     *
     * @param mode 模式常量（MODE_NIGHT_FOLLOW_SYSTEM/MODE_NIGHT_YES/MODE_NIGHT_NO）
     */
    fun setAppNightMode(@AppCompatDelegate.NightMode mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    /**
     * 获取应用「实际生效的暗黑模式」（无论是否跟随系统，返回最终状态）
     *
     * @param context 上下文
     * @return true=当前生效暗黑模式，false=当前生效亮色模式
     */
    fun isAppNightMode(context: Context): Boolean {
        return when (getAppNightModeSetting()) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            else -> isSystemNightMode(context)
        }
    }

    /**
     * 获取应用「设置的夜间模式」（如跟随系统/强制开启/强制关闭）
     *
     * @return 模式常量（MODE_NIGHT_FOLLOW_SYSTEM/MODE_NIGHT_YES/MODE_NIGHT_NO）
     */
    private fun getAppNightModeSetting(): Int {
        return AppCompatDelegate.getDefaultNightMode()
    }

    /**
     * 判断系统是否开启夜间模式，和应用自身设置无关。
     *
     * @param context 上下文（Activity/Fragment/Application）
     * @return true=系统夜间模式开启，false=系统亮色模式
     */
    private fun isSystemNightMode(context: Context): Boolean {
        val uiMode = context.resources.configuration.uiMode
        return uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}