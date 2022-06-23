package com.jxpanda.common.utils

import com.jxpanda.common.constants.DecimalConstant
import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * Created by Panda on 2018/7/31
 */
class DecimalUtil {
    companion object {
        @JvmStatic
        fun formatting(decimal: BigDecimal, format: DecimalFormat = DecimalConstant.FORMAT_DOUBLE): String {
            return decimal.formatting(format)
        }

        @JvmStatic
        fun formattingWithSign(decimal: BigDecimal, format: DecimalFormat = DecimalConstant.FORMAT_DOUBLE): String {
            return decimal.formattingWithSign(format)
        }

        @JvmStatic
        fun parse(string: String?): BigDecimal {
            return if (string.isNullOrBlank()) {
                BigDecimal.ZERO
            } else try {
                BigDecimal(string)
            } catch (ex: Exception) {
                BigDecimal.ZERO
            }
        }

        @JvmStatic
        fun toPercentage(bigDecimal: BigDecimal) = bigDecimal.toPercentage()

        /**
         * 无符号处理（抹零，小于0的让其等于0）
         * */
        fun unsigned(decimal: BigDecimal?): BigDecimal {
            return if (decimal == null || decimal < BigDecimal.ZERO) {
                BigDecimal.ZERO
            } else {
                decimal
            }
        }

        /**
         * Σ函数，列表求和
         * */
        fun sigma(list: List<BigDecimal>): BigDecimal {
            return if (list.isEmpty()) {
                BigDecimal.ZERO
            } else {
                list.reduce(BigDecimal::add)
            }
        }

        fun max(v1: BigDecimal, v2: BigDecimal): BigDecimal {
            return if (v1 > v2) v1 else v2
        }

        fun max(vararg values: BigDecimal): BigDecimal {
            return values.max() ?: BigDecimal.ZERO
        }

        fun min(v1: BigDecimal, v2: BigDecimal): BigDecimal {
            return if (v1 > v2) v2 else v1
        }

        fun min(vararg values: BigDecimal): BigDecimal {
            return values.min() ?: BigDecimal.ZERO
        }

    }
}

fun BigDecimal.formatting(format: DecimalFormat = DecimalConstant.FORMAT_DOUBLE): String = format.format(this)

fun BigDecimal.formattingWithSign(format: DecimalFormat = DecimalConstant.FORMAT_DOUBLE): String {
    return "${if (this > BigDecimal.ZERO) "+" else ""}${format.format(this)}"
}

fun String.toBigDecimal(): BigDecimal = DecimalUtil.parse(this)

fun <T> List<T>.sigma(selector: (T) -> BigDecimal): BigDecimal = DecimalUtil.sigma(this.map { selector(it) })

fun BigDecimal.toCNY(): String = "￥${this.formatting()}"

/**
 * 转成百分比的形式输出
 * */
fun BigDecimal.toPercentage() = "${this * BigDecimal(100)}%"

/**
 * Double类型由于暂时只有这个工具用到，暂时写在这里，并且不提供Java的调用
 * */
fun Double.toPercentage() = "${this * 100}%"
