package com.vinpin.ztools

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * MD5 工具类
 */
object MD5Util {

    /**
     * 获取信息摘要（通用方法）
     */
    @Throws(NoSuchAlgorithmException::class)
    fun getDigest(algorithm: String): MessageDigest {
        return MessageDigest.getInstance(algorithm)
    }

    /**
     * 计算字符串的 MD5 字节数组
     *
     * @param text 待加密字符串
     * @return MD5 字节数组，为空返回 null
     */
    fun md5(text: String?): ByteArray? {
        if (text.isNullOrEmpty()) return null
        return md5(text.toByteArray())
    }

    /**
     * 计算字节数组的 MD5 字节数组
     *
     * @param bytes 待加密字节数组
     * @return MD5 字节数组，为空返回 null
     */
    fun md5(bytes: ByteArray): ByteArray? {
        try {
            val digest = getDigest("MD5")
            digest.update(bytes)
            return digest.digest()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * 计算字符串 MD5 并返回 32 位小写十六进制字符串
     *
     * @param text 待加密字符串
     * @return MD5 十六进制字符串
     */
    fun md5Hex(text: String?): String? {
        val md5Bytes = md5(text) ?: return null
        return bytesToHexString(md5Bytes)
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param src 字节数组
     * @return 十六进制字符串
     */
    fun bytesToHexString(src: ByteArray?): String? {
        if (src == null || src.isEmpty()) return null
        val builder = StringBuilder()
        for (byte in src) {
            val hex = Integer.toHexString(byte.toInt() and 0xFF)
            if (hex.length == 1) builder.append('0')
            builder.append(hex)
        }
        return builder.toString()
    }
}