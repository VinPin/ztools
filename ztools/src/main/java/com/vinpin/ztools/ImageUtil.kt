package com.vinpin.ztools

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.annotation.WorkerThread
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * 图片工具类
 */
object ImageUtil {

    /**
     * 判断文件是不是gif图片，读取文件头信息时可能会耗时。
     *
     * @param filePath 文件路径
     * @return 是否是gif图片
     */
    @WorkerThread
    fun isGifByPath(filePath: String?): Boolean {
        if (filePath.isNullOrEmpty()) return false
        if (!FileUtil.isExist(filePath)) return false
        if (filePath.endsWith(".gif", true)) return true
        val header = getFileHeader(filePath)
        return "47494638" == header
    }

    /**
     * 获取文件的十六进制头信息
     *
     * @param filePath 文件路径
     * @return 文件头信息
     */
    @WorkerThread
    private fun getFileHeader(filePath: String?): String? {
        return filePath?.takeIf { it.isNotBlank() }
            ?.let { File(it) }
            ?.takeIf { it.exists() && it.isFile && it.canRead() }
            ?.runCatching {
                inputStream().use {
                    val buffer = ByteArray(4)
                    it.read(buffer)
                    bytesToHexString(buffer)
                }
            }?.onFailure {
                it.printStackTrace()
            }?.getOrNull()
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param src 字节数组
     * @return 十六进制字符串
     */
    private fun bytesToHexString(src: ByteArray?): String? {
        if (src == null || src.isEmpty()) return null
        val builder = StringBuilder()
        for (byte in src) {
            val hex = Integer.toHexString(byte.toInt() and 0xFF)
            if (hex.length == 1) builder.append('0')
            builder.append(hex)
        }
        return builder.toString()
    }

    /**
     * 压缩本地图片文件，结果直接覆盖原文件。
     *
     * @param filePath 原始图片文件路径
     * @param maxWidth 最大宽度限制
     * @param maxHeight 最大高度限制
     * @param maxSize 最大文件大小限制(字节)
     * @return 压缩成功或失败
     */
    @WorkerThread
    fun compressImage(filePath: String, maxWidth: Int, maxHeight: Int, maxSize: Int): Boolean {
        return try {
            val originalFile = File(filePath)
            if (!originalFile.exists() || !originalFile.isFile) {
                return false
            }
            if (isGifByPath(filePath)) {
                // GIF图片不压缩
                return false
            }
            // 读取图片原始尺寸
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(filePath, options)
            val originalWidth = options.outWidth
            val originalHeight = options.outHeight
            // 计算尺寸压缩比例
            var scaleFactor = 1
            if (maxWidth > 0 && maxHeight > 0 && (originalWidth > maxWidth || originalHeight > maxHeight)) {
                val widthRatio = originalWidth.toFloat() / maxWidth
                val heightRatio = originalHeight.toFloat() / maxHeight
                scaleFactor = (minOf(widthRatio, heightRatio)).toInt()
                // 确保缩放比例至少为1
                if (scaleFactor < 1) scaleFactor = 1
            }
            // 按比例加载图片(尺寸压缩)
            val scaledOptions = BitmapFactory.Options().apply {
                inSampleSize = scaleFactor // 设置缩放比例
                inPreferredConfig = Bitmap.Config.RGB_565 // 降低色彩精度，减少内存占用
            }
            var scaledBitmap = BitmapFactory.decodeFile(filePath, scaledOptions) ?: return false
            // 检查是否还需要进一步调整尺寸(处理精确缩放)
            val scaledWidth = scaledBitmap.width
            val scaledHeight = scaledBitmap.height
            if (maxWidth > 0 && maxHeight > 0 && (scaledWidth > maxWidth || scaledHeight > maxHeight)) {
                val widthScale = maxWidth.toFloat() / scaledWidth
                val heightScale = maxHeight.toFloat() / scaledHeight
                val finalScale = minOf(widthScale, heightScale)
                // 使用矩阵进行精确缩放
                val matrix = Matrix()
                matrix.postScale(finalScale, finalScale)
                scaledBitmap = Bitmap.createBitmap(
                    scaledBitmap, 0, 0, scaledWidth, scaledHeight, matrix, true
                )
            }
            // 质量压缩(如果需要)
            val outputStream = ByteArrayOutputStream()
            var quality = 100 // 初始质量100%
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            if (maxSize > 0) {
                // 如果压缩后大小仍超过限制，逐步降低质量
                while (outputStream.toByteArray().size > maxSize && quality > 5) {
                    outputStream.reset() // 重置输出流
                    quality -= 5 // 每次降低5%质量
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                }
            }
            // 保存压缩后的图片
            FileOutputStream(originalFile).use { fos ->
                fos.write(outputStream.toByteArray())
                fos.flush()
            }
            // 释放资源
            scaledBitmap.recycle()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}