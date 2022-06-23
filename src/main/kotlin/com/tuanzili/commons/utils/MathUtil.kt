package com.jxpanda.common.utils

import org.apache.commons.lang3.RandomUtils
import java.math.BigDecimal

class MathUtil {

    companion object {

        /**
         * 无符号处理（抹零，小于0的让其等于0）
         * */
        fun unsigned(i: Int?): Int {
            return if (i == null || i < 0) {
                0
            } else {
                i
            }
        }

    }

}

fun main() {
    val x = 0
    for (i in 1..x) {
        println(i)
    }
}