package com.tuanzili.commons.utils

import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.RandomUtils
import java.math.BigInteger

class CodeUtil {
    companion object {

        private val SNOWFLAKE = Snowflake()

        private val TIMESTAMP_WORKER = TimestampWorker()

        /**
         * 2018-01-01 00:00:00的时间戳
         * 这不是一个常用值，暂时不考虑放到DateTimeConstant类中
         * */
        private const val TIMESTAMP_2018_0_0 = 1514736000000L

        /**
         * 生成一个随机数
         * */
        @JvmStatic
        fun nextInt(floor: Int = 0, ceiling: Int = 10): Int {
            return RandomUtils.nextInt(floor, ceiling)
        }

        @JvmStatic
        @JvmOverloads
        fun nextInt(digital: Int = 6): Int {
            val ceiling = Math.pow(10.0, digital.toDouble()).toInt()
            val floor = Math.pow(10.0, digital - 1.0).toInt()
            return nextInt(floor, ceiling)
        }

        /**
         * 生成一个随机字符串
         * @param digital 位数
         * */
        @JvmStatic
        fun nextString(digital: Int = 6): String {
            return RandomStringUtils.randomAlphanumeric(digital)
        }

        /**
         * 基于Twitter的雪花算法生成一个ID
         * 考虑到使用方便的话，入参是Int类型
         * @param workerId 机器ID，默认是1
         * */
        @JvmStatic
        @JvmOverloads
        fun nextId(workerId: Int = 1): Long {
            return snowflake(workerId).nextId()
        }

        @JvmStatic
        @JvmOverloads
        fun nextIdString(workerId: Int = 1): String {
            return snowflake(workerId).nextIdString()
        }

        /**
         * 创建雪花算法的对象，默认会静态存储一个workerId为1的对象
         *
         * */
        private fun snowflake(workerId: Int = 1): Snowflake {
            return if (workerId == 1) {
                SNOWFLAKE
            } else {
                Snowflake(workerId.toLong())
            }
        }


        /**
         * 基于时间戳获取一个ID
         * @param offset 偏移值，默认是
         * */
        @JvmStatic
        fun nextTimestampId(offset: Long = TIMESTAMP_2018_0_0): Long {
            return TIMESTAMP_WORKER.nextId(offset)
        }

        @JvmStatic
        fun nextTimestampIdString(offset: Long = TIMESTAMP_2018_0_0): String {
            return TIMESTAMP_WORKER.nextIdString(offset)
        }

    }

    internal class TimestampWorker {

        /**
         * 记录上一次生成的时间戳是哪一个，避免生成重复的时间戳
         * */
        private var lastTimestamp = -1L

        fun nextId(offset: Long = 0): Long {
            var timestamp = System.currentTimeMillis()
            while (timestamp == this.lastTimestamp) {
                timestamp = System.currentTimeMillis()
            }
            this.lastTimestamp = timestamp
            return timestamp - offset
        }

        /**
         * ID的字符串形式，通过32进制缩减了ID的长度
         * */
        fun nextIdString(offset: Long = 0): String {
            return nextId(offset).to32System()
        }

    }

    /**
     * 雪花算法（网上抄来的）
     * */
    internal class Snowflake(private var workerId: Long = 1) {

        private val snsEpoch = 1330328109047L
        private var sequence = 0L
        private val workerIdBits = 10L
        private val maxWorkerId = -1L xor (-1L shl this.workerIdBits.toInt())
        private val sequenceBits = 12L
        private val workerIdShift = this.sequenceBits
        private val timestampLeftShift = this.sequenceBits + this.workerIdBits
        private val sequenceMask = -1L xor (-1L shl this.sequenceBits.toInt())
        private var lastTimestamp = -1L

        init {
            if (workerId > this.maxWorkerId || workerId < 0) {
                throw IllegalArgumentException("""worker Id can't be greater than $maxWorkerId or less than 0""")
            }
        }

        @Synchronized
        @Throws(Exception::class)
        fun nextId(): Long {
            var timestamp = System.currentTimeMillis()
            if (this.lastTimestamp == timestamp) {
                this.sequence = this.sequence + 1 and this.sequenceMask
                if (this.sequence == 0L) {
                    timestamp = this.tilNextMillis()
                }
            } else {
                this.sequence = 0
            }
            if (timestamp < this.lastTimestamp) {
                throw Exception(String.format("Clock moved backwards.Refusing to generate id for %d milliseconds", this.lastTimestamp - timestamp))
            }
            this.lastTimestamp = timestamp
            return timestamp - this.snsEpoch shl this.timestampLeftShift.toInt() or (this.workerId shl this.workerIdShift.toInt()) or this.sequence
        }

        @Synchronized
        @Throws(Exception::class)
        fun nextIdString(): String {
            return nextId().toString()
        }

        /**
         * 保证返回的毫秒数在参数之后
         * @return
         */
        private fun tilNextMillis(): Long {
            var timestamp = System.currentTimeMillis()
            while (timestamp <= this.lastTimestamp) {
                timestamp = System.currentTimeMillis()
            }
            return timestamp
        }

    }

}

fun Byte.toHex(): String = this.toInt().toHex()

/**
 * 扩展kotlin的Int类，增加一个转为16进制字符串的函数
 * */
fun Int.toHex(): String = Integer.toHexString(this)

/**
 * 扩展kotlin的Long类，增加一个转为16进制字符串的函数
 * */
fun Long.toHex(): String = java.lang.Long.toHexString(this)

/**
 * 扩展kotlin的ByteArray类，增加一个转为16进制字符串的函数
 * */
fun ByteArray.toHex(): String = BigInteger(1, this).toString(16)

/**
 * 字符串转为16进制的函数
 * 这个函数只是简易的转换，并不是通用的
 * 不简易把转换的结果用以计算
 * */
fun String.toHex(): String {
    val hexString = try {
        this.toLong().toHex()
    } catch (e: Exception) {
        val builder = StringBuilder()
        var temp = StringBuilder()
        var digit = 0
        for (c in this) {
            if (digit % 8 != 0) {
                temp.append(c.toInt())
            } else {
                builder.insert(0, temp.toString().toHex())
                temp = StringBuilder()
            }
            digit++
        }
        builder.toString()
    }
    return hexString.toUpperCase()
}