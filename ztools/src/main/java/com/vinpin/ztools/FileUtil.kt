package com.vinpin.ztools

import java.io.File

object FileUtil {

    /**
     * 判断文件是否存在
     *
     * @param fileName 文件路径
     * @return true：存在，false: 不存在
     */
    fun isExist(fileName: String): Boolean {
        if (fileName.isEmpty()) return false
        return try {
            File(fileName).exists()
        } catch (e: Throwable) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 获取文件后缀，不带.的
     *
     * @param path 文件路径
     * @return 文件后缀，不带.的
     */
    fun getFileSuffix(path: String?): String? {
        if (path.isNullOrEmpty()) return null
        var result: String? = null
        val lastIndexOf = path.lastIndexOf('.')
        if (lastIndexOf >= 0) {
            result = path.substring(lastIndexOf)
            if (result.startsWith(".")) {
                result = result.substring(1)
            }
        }
        return result
    }
}