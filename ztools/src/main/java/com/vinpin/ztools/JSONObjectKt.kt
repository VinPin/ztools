package com.vinpin.ztools

import org.json.JSONArray
import org.json.JSONObject

// 从JSONObject中安全取Long类型，因其optLong方法当值为String时转换会造成进度丢失。
fun JSONObject.optSafeLong(name: String, defaultValue: Long = 0L): Long {
    val value = when (val obj = opt(name)) {
        is Long -> obj
        is Number -> obj.toLong()
        is String -> obj.toSafeLong(defaultValue)
        else -> null
    }
    return value ?: defaultValue
}

// 从JSONObject中安全取Boolean类型，数字1会返回true，其他数字返回false。
fun JSONObject.optSafeBoolean(name: String, defaultValue: Boolean = false): Boolean {
    val value = when (val obj = opt(name)) {
        is Boolean -> obj
        is Number -> obj.toInt() == 1
        is String -> "true".equals(obj, ignoreCase = true)
        else -> null
    }
    return value ?: defaultValue
}

// 从JSONObject中安全取String类型，因其optString方法当值为null对象时会被转成"null"。
fun JSONObject.optSafeString(name: String, defaultValue: String? = ""): String? {
    if (isNull(name)) return defaultValue
    return opt(name)?.toString() ?: defaultValue
}

// 从JSONObject中安全取Map类型
fun JSONObject.toSafeHashMap(): HashMap<String, Any> {
    val hashMap = hashMapOf<String, Any>()
    val keys = this.keys()
    while (keys.hasNext()) {
        val key = keys.next()
        val value = this.opt(key)
        if (value != null) {
            when (value) {
                is JSONObject -> hashMap[key] = value.toSafeHashMap()
                is JSONArray -> hashMap[key] = value.toSafeList()
                else -> hashMap[key] = value
            }
        }
    }
    return hashMap
}