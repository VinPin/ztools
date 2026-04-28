package com.vinpin.ztools

/**
 * 唯一Id生成器
 */
object UniqueIdUtil {

    private const val workerId: Long = 0
    private const val datacenterId: Long = 0

    // 标记时间用来计算偏移量，距离当前时间不同，得到的数据的位数也不同。
    // Thu, 04 Nov 2010 01:42:54 GM
    private const val twepoch = 1288834974657L

    // 物理节点ID长度
    private const val workerIdBits = 5L

    // 数据中心ID长度
    private const val datacenterIdBits = 5L

    // 序列号12位，同毫秒内生成不同id的最大个数4095。
    private const val sequenceBits = 12L

    // 机器节点左移12位
    private const val workerIdShift = sequenceBits

    // 数据中心节点左移12+5位
    private const val datacenterIdShift = sequenceBits + workerIdBits

    // 时间毫秒数左移12+5+5位
    private const val timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits

    // 用于和当前时间戳做比较，以获取最新时间。
    private const val sequenceMask = -1L xor (-1L shl sequenceBits.toInt())

    @Volatile
    private var lastTimestamp = -1L

    @Volatile
    private var sequence = 0L

    /**
     * 获取唯一Id
     */
    @Synchronized
    fun getId(): Long {
        var currentTimeMillis: Long = System.currentTimeMillis()
        // 当前时间小于上一次生成id使用的时间，可能出现时钟回拨问题。
        if (currentTimeMillis < lastTimestamp) {
            currentTimeMillis = lastTimestamp
        }
        if (lastTimestamp == currentTimeMillis) {
            // 还是在同一毫秒内，则将序列号递增1，序列号最大值为4095
            // sequence自增，因为sequence只有12bit，所以和sequenceMask相与一下，去掉高位
            sequence = sequence + 1 and sequenceMask
            // 判断是否溢出,也就是每毫秒内超过4095，当为4096时，与sequenceMask相与，sequence就等于0
            if (sequence == 0L) {
                currentTimeMillis = tilNextMillis(lastTimestamp)
            }
        } else {
            // 不在同一毫秒内，则序列号重新从0开始，序列号最大值为4095。
            sequence = 0L
        }
        // 记录最后一次使用的毫秒时间戳
        lastTimestamp = currentTimeMillis
        // 核心算法，将不同部分的数值移动到指定的位置，然后进行或运行
        // 1位固定整数 time datacenterId workerId sequence
        return (currentTimeMillis - twepoch shl timestampLeftShift.toInt()) or (datacenterId shl datacenterIdShift.toInt()) or (workerId shl workerIdShift.toInt()) or sequence
    }

    // 获取指定时间戳的接下来的时间戳，也可以说是下一毫秒。
    private fun tilNextMillis(lastTimestamp: Long): Long {
        var currentTimeMillis = System.currentTimeMillis()
        while (currentTimeMillis <= lastTimestamp) {
            currentTimeMillis = System.currentTimeMillis()
        }
        return currentTimeMillis
    }
}