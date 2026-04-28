package com.vinpin.ztools

import org.json.JSONArray
import org.json.JSONObject

// 遍历JSONArray
inline fun JSONArray.forEach(action: (JSONObject) -> Unit) {
    for (i in 0 until this.length()) {
        this.optJSONObject(i)?.let {
            action.invoke(it)
        }
    }
}

// JSONArray转List<String>
fun JSONArray?.toStringList(): MutableList<String> {
    val list = mutableListOf<String>()
    if (this != null && this.length() > 0) {
        for (i in 0 until this.length()) {
            list.add(this.optString(i))
        }
    }
    return list
}

// JSONArray转List<Int>
fun JSONArray?.toIntList(): MutableList<Int> {
    val list = mutableListOf<Int>()
    if (this != null && this.length() > 0) {
        for (i in 0 until this.length()) {
            list.add(this.optInt(i))
        }
    }
    return list
}

// JSONArray转List<Any>
fun JSONArray?.toSafeList(): MutableList<Any> {
    val list = mutableListOf<Any>()
    if (this != null && this.length() > 0) {
        for (i in 0 until this.length()) {
            val value = this.opt(i)
            if (value != null) {
                when (value) {
                    is JSONObject -> list.add(value.toSafeHashMap())
                    is JSONArray -> list.add(value.toSafeList())
                    else -> list.add(value)
                }
            }
        }
    }
    return list
}

// 在JSONArray中查找第一个满足条件的JSONObject
inline fun JSONArray.find(block: (it: JSONObject) -> Boolean): JSONObject? {
    if (this.length() == 0) return null
    for (i in 0 until this.length()) {
        val item = this.optJSONObject(i)
        if (item != null && block.invoke(item)) {
            return item
        }
    }
    return null
}