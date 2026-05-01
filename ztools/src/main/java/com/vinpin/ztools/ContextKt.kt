package com.vinpin.ztools

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

/**
 * 通过资源ID获取Drawable对象
 *
 * @param resId Drawable资源ID
 * @return 对应资源的Drawable对象，确保非空
 */
fun Context.getDrawableRes(@DrawableRes resId: Int): Drawable {
    return ContextCompat.getDrawable(this, resId)!!
}

/**
 * 通过资源ID获取颜色值
 *
 * @param id 颜色资源ID
 * @return 对应资源的颜色Int值
 */
fun Context.getColorRes(@ColorRes id: Int): Int {
    return ContextCompat.getColor(this, id)
}

/**
 * 通过资源ID获取尺寸对应的像素值
 *
 * @param id 尺寸资源ID
 * @return 像素值，获取异常时返回0
 */
fun Context.getDimensionPixelSize(@DimenRes id: Int): Int {
    return try {
        this.resources.getDimensionPixelSize(id)
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }
}

/**
 * 判断当前Context对应的Activity是否已销毁或正在关闭
 *
 * @return 已销毁/正在关闭返回true，否则返回false
 */
fun Context.isActivityDestroyed(): Boolean {
    return if (this is Activity) isFinishing || isDestroyed else false
}

/**
 * 将dp值转换为px值
 *
 * @param dp dp值
 * @return 对应的px值
 */
fun Context.dp2px(dp: Float): Int {
    val scale = this.resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

/**
 * 将sp值转换为px值
 *
 * @param sp sp值
 * @return 对应的px值
 */
fun Context.sp2px(sp: Float): Int {
    val scale = this.resources.displayMetrics.scaledDensity
    return (sp * scale + 0.5f).toInt()
}