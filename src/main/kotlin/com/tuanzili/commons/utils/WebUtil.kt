package com.tuanzili.commons.utils

class WebUtil {
    companion object {
        /**
         * IP（v4）转Int
         * */
        fun ip2Int(ip: String): Int {
            var result = 0
            val ipAddressInArray = ip.split(".")
            for (i in 3 downTo 0) {
                //left shifting 24,16,8,0 and bitwise OR
                //1. 192 << 24
                //1. 168 << 16
                //1. 1   << 8
                //1. 2   << 0
                result = result or (ipAddressInArray[3 - i].toInt() shl i * 8)
            }
            return result
        }

        /**
         * int转IP（v4）
         * */
        fun int2IP(ip: Int): String {
            return "${(ip shr 24 and 0xFF)}.${(ip shr 16 and 0xFF)}.${(ip shr 8 and 0xFF)}.${(ip and 0xFF)}"
        }
    }
}

// 惯例，增加kotlin的扩展
fun Int.toIP(): String = WebUtil.int2IP(this)

fun String.toIntIP(): Int = WebUtil.ip2Int(this)