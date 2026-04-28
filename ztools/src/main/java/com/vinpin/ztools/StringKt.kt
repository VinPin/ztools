package com.vinpin.ztools

import android.net.Uri
import androidx.core.net.toUri
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import androidx.core.graphics.toColorInt

// URL编码
fun String.encode(enc: String = "UTF-8"): String {
    return try {
        URLEncoder.encode(this, enc)
    } catch (e: Exception) {
        e.printStackTrace()
        this
    }
}

// 转换为JSONObject，如果转换失败则返回null
fun String?.toJSONObject(printStackTrace: Boolean = true): JSONObject? {
    if (this.isNullOrEmpty()) return null
    return try {
        JSONObject(this)
    } catch (e: Exception) {
        if (printStackTrace) e.printStackTrace()
        null
    }
}

// 转换为JSONArray，如果转换失败则返回null
fun String?.toJSONArray(printStackTrace: Boolean = true): JSONArray? {
    if (this.isNullOrEmpty()) return null
    return try {
        JSONArray(this)
    } catch (e: Exception) {
        if (printStackTrace) e.printStackTrace()
        null
    }
}

// 转换为Int，如果转换失败则返回默认值
fun String?.toSafeInt(defaultValue: Int = 0): Int {
    if (this == null) return defaultValue
    return try {
        this.toInt()
    } catch (e: Exception) {
        defaultValue
    }
}

// 转换为Long，如果转换失败则返回默认值
fun String?.toSafeLong(defaultValue: Long = 0L): Long {
    if (this == null) return defaultValue
    return try {
        this.toLong()
    } catch (e: Exception) {
        defaultValue
    }
}

// 转换为Boolean，如果转换失败则返回默认值
fun String?.toSafeBoolean(defaultValue: Boolean = false): Boolean {
    if (this == null) return defaultValue
    if (this == "0") return false
    if (this == "1") return true
    return try {
        this.toBoolean()
    } catch (e: Exception) {
        defaultValue
    }
}

// 转换为Uri，如果转换失败则返回null
fun String?.toSafeUri(printStackTrace: Boolean = true): Uri? {
    if (this.isNullOrEmpty()) return null
    return try {
        this.toUri()
    } catch (e: Exception) {
        if (printStackTrace) e.printStackTrace()
        null
    }
}

// 转换为String列表，如果转换失败则返回空列表
fun String?.toStringList(): MutableList<String> {
    val list = mutableListOf<String>()
    val array = this.toJSONArray()
    if (array != null && array.length() > 0) {
        for (i in 0 until array.length()) {
            list.add(array.optString(i))
        }
    }
    return list
}

// 转换为ColorInt，如果转换失败则返回null
fun String?.toSafeColorInt(): Int? {
    if (this.isNullOrEmpty()) return null
    return try {
        this.toColorInt()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}