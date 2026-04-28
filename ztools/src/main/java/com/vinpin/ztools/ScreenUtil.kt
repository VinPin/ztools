package com.vinpin.ztools

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.View
import androidx.core.view.isVisible

/**
 * 屏幕工具类
 */
object ScreenUtil {

    /**
     * 返回包括虚拟键在内的总的屏幕高度
     * 即使虚拟按键隐藏着，也会加上虚拟按键的高度。
     */
    fun getRealScreenHeight(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    /**
     * 返回屏幕的宽度
     */
    fun getRealScreenWidth(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    /**
     * 返回屏幕可用高度
     * 当显示了虚拟按键时，会自动减去虚拟按键高度。
     */
    fun getScreenHeight(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    /**
     * 返回屏幕的宽度
     */
    fun getScreenWidth(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    /**
     * 状态栏高度
     */
    @SuppressLint("InternalInsetResource")
    fun getStatusBarHeight(activity: Activity): Int {
        val resourceId = activity.resources.getIdentifier(
            "status_bar_height", "dimen", "android"
        )
        return if (resourceId > 0) {
            activity.resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

    /**
     * 获取虚拟按键的高度
     * 会根据当前是否有显示虚拟按键来返回相应的值，即如果隐藏了虚拟按键，则返回零。
     */
    fun getVirtualBarHeightIfDisplay(activity: Activity): Int {
        val realMetrics = DisplayMetrics()
        val usableMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getRealMetrics(realMetrics)
        activity.windowManager.defaultDisplay.getMetrics(usableMetrics)
        return realMetrics.heightPixels - usableMetrics.heightPixels
    }

    /**
     * 获取虚拟按键的高度，不论虚拟按键是否显示都会返回其固定高度。
     */
    @SuppressLint("InternalInsetResource")
    fun getVirtualBarHeight(activity: Activity): Int {
        var result = 0
        val resourceId = activity.resources.getIdentifier(
            "navigation_bar_height", "dimen", "android"
        )
        if (resourceId > 0) {
            result = activity.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    /**
     * 获取虚拟按键的高度
     *
     * 1. 开启全面屏，则返回0
     * 2. 非全面屏，没有虚拟键或虚拟键隐藏-返回0，虚拟键存在且未隐藏-返回虚拟键实际高度
     */
    fun getNavigationBarHeightIfRoom(context: Context): Int {
        return if (navigationGestureEnabled(context)) {
            0
        } else {
            val activity = context as? Activity ?: return 0
            getCurrentNavigationBarHeight(activity)
        }
    }

    /**
     * 判断是否开启全面屏
     */
    fun navigationGestureEnabled(context: Context): Boolean {
        // 0关闭，1开启
        return Settings.Global.getInt(context.contentResolver, getDeviceInfo(), 0) != 0
    }

    /**
     * 获取设备信息(目前支持几大主流的全面屏手机，亲测华为、小米、oppo、魅族、vivo都可以)
     */
    private fun getDeviceInfo(): String {
        val brand = Build.BRAND
        if (TextUtils.isEmpty(brand)) return "navigationbar_is_min"
        return when {
            brand.equals("HUAWEI", ignoreCase = true) -> "navigationbar_is_min"
            brand.equals("XIAOMI", ignoreCase = true) -> "force_fsg_nav_bar"
            brand.equals("VIVO", ignoreCase = true) -> "navigation_gesture_on"
            brand.equals("OPPO", ignoreCase = true) -> "navigation_gesture_on"
            brand.equals("ONEPLUS", ignoreCase = true) -> "navigation_gesture_on"
            brand.equals("SAMSUNG", ignoreCase = true) -> "navigationbar_hide_bar_enabled"
            else -> "navigationbar_is_min"
        }
    }

    /**
     * 非全面屏下，虚拟键实际高度(隐藏后高度为0)
     */
    fun getCurrentNavigationBarHeight(activity: Activity): Int {
        return if (isNavigationBarShown(activity)) getVirtualBarHeight(activity) else 0
    }

    /**
     * 非全面屏下，虚拟按键是否打开
     */
    fun isNavigationBarShown(activity: Activity): Boolean {
        // 虚拟键的view,为空或者不可见时是隐藏状态
        val view: View? = activity.findViewById(android.R.id.navigationBarBackground)
        return view != null && view.isVisible
    }
}