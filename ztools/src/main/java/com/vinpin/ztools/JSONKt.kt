package com.vinpin.ztools

import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONObject

// 全局单例 Gson
val gson by lazy {
    GsonBuilder().serializeNulls().create()
}

// Any转json字符串，支持JSONObject、JSONArray、String、Bean
fun Any?.toJson(): String? {
    if (this == null) return null
    if (this is JSONObject || this is JSONArray) {
        return this.toString()
    }
    if (this is String) {
        return this
    }
    return try {
        gson.toJson(this)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}