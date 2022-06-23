package com.jxpanda.common.utils

import java.lang.StringBuilder

/**
 * 标准32进制所用到的数位表
 * 数组下标对应的字符就是32进制下的数字
 * */
private val DIGITS32 = arrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
        'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W',
        'X', 'Y', 'Z')

/**
 * 空间换时间，基于DIGITS32数组，转置出一个字符与下标对应的Map
 * */
private val DIGITS32_MAP = DIGITS32.mapIndexed { index, c -> Pair(c, index) }.associateBy({ it.first }, { it.second })

/**
 * 现在需要一个32进制的工具
 * 暂且写在这个类里面，日后把所有数学、数字类的工具整理一下
 * 暂时只实现32进制的算法，而且只支持正整数，默认只实现10进制->32进制的算法
 * 2、8、16进制其实不用写
 * */
class NumberSystemUtil {

    companion object {
        @JvmStatic
        fun convertTo32System(number: Long): String {
            return number.to32System()
        }

        @JvmStatic
        fun convertFrom32System(number: String): Long {
            return number.from32System()
        }

    }

}

/**
 * 从10进制转为32进制
 * */
fun Long.to32System(): String {
    var number = this
    val stb = StringBuilder()
    while (number > 0) {
        stb.insert(0, DIGITS32[(number % 32).toInt()])
        number /= 32
    }
    return stb.toString()
}

/**
 * 从10进制转为32进制
 * */
fun String.to32System(): String = this.toLong().to32System()

/**
 * 从32进制转为10进制
 * */
fun String.from32System(): Long {
    var radix = 1
    return this.reversed().map {
        val decimalNumber = DIGITS32_MAP.getValue(it).toLong() * radix
        radix *= 32
        decimalNumber
    }.sum()
}
