package com.tuanzili.commons.utils

import com.tuanzili.commons.constants.DateTimeConstant
import java.time.format.DateTimeFormatter

val EMAIL = Regex("^[\\w\\d]+([-_.][\\w\\d]+)*@([\\w\\d]+[-.])+(com|cn|com.cn)\$")
/**
 * 日期的正则表达式，匹配的是yyyy-MM-dd这种格式
 * */
val DATE = Regex("^[\\d]{4}-[\\d]{2}-[\\d]{2}\$")
/**
 * 时间的正则表达式，匹配的是HH:mm:ss这个格式
 * */
val TIME = Regex("^[\\d]{2}:[\\d]{2}:[\\d]{2}\$")
/**
 * 这个是上面两个的结合，匹配的是yyyy-MM-dd HH:mm:ss这个格式（只精确到秒）
 * */
val DATE_TIME = Regex("^[\\d]{4}-[\\d]{2}-[\\d]{2}\\s[\\d]{2}:[\\d]{2}:[\\d]{2}\$")

/**
 * 正则表达式工具
 * 急着用，先只整一个判断是否是邮箱的函数
 * */
class RegexUtil {

    companion object {
        @JvmStatic
        fun isEmail(text: String): Boolean {
            return EMAIL.matches(text)
        }

        @JvmStatic
        fun isDate(text: String): Boolean {
            return DATE.matches(text)
        }

        @JvmStatic
        fun isTime(text: String): Boolean {
            return TIME.matches(text)
        }

        @JvmStatic
        fun isDateTime(text: String): Boolean {
            return DATE_TIME.matches(text)
        }

        /**
         * 匹配字符串，返回日期格式
         * */
        fun matchDateFormatter(text: String): DateTimeFormatter? {
            return when {
                isDateTime(text) -> DateTimeConstant.Formatter.DATE_TIME_NORMAL
                isDate(text) -> DateTimeConstant.Formatter.DATE_NORMAL
                isTime(text) -> DateTimeConstant.Formatter.TIME_NORMAL
                else -> null
            }
        }
    }
}

/**
 * 惯例，给字符串提供扩展
 * */
fun String.isEmail() = RegexUtil.isEmail(this)

fun String.isDate() = RegexUtil.isDate(this)
fun String.isTime() = RegexUtil.isTime(this)
fun String.isDateTime() = RegexUtil.isDateTime(this)
