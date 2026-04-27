package com.vinpin.ztools

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.DecimalFormat

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

    /**
     * 复制文件
     *
     * @param oldPath 源文件路径
     * @param newPath 目标文件路径
     * @return true：复制成功，false: 复制失败
     */
    fun copyFile(oldPath: String, newPath: String): Boolean {
        if (oldPath.isEmpty() || newPath.isEmpty()) return false
        val oldFile = File(oldPath)
        if (!oldFile.exists() || !oldFile.isFile || !oldFile.canRead()) return false
        return try {
            FileInputStream(oldFile).use { input ->
                FileOutputStream(newPath).use { output ->
                    val buffer = ByteArray(8192)
                    var bytes: Int
                    while (input.read(buffer).also { bytes = it } != -1) {
                        output.write(buffer, 0, bytes)
                    }
                    output.flush()
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 格式化文件大小
     *
     * @param fileSize 文件大小
     * @return 格式化后的文件大小
     */
    fun formatFileSize(fileSize: Long): String {
        if (fileSize.toInt() == 0) return "0B"
        val df = DecimalFormat("#.00")
        return when {
            fileSize < 1024 -> df.format(fileSize / 1.00) + "B"
            fileSize < 1048576 -> df.format(fileSize / 1024.00) + "KB"
            fileSize < 1073741824 -> df.format(fileSize / 1048576.00) + "MB"
            else -> df.format(fileSize / 1073741824.00) + "GB"
        }
    }
}