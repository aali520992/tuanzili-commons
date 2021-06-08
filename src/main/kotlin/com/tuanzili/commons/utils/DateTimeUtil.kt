//@file:JvmName("DateTimeUtil")
package com.tuanzili.commons.utils

import com.tuanzili.commons.constants.DateTimeConstant
import com.tuanzili.commons.constants.enumerations.TimeAccuracy
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.abs

/**
 * Created by Panda on 2018/7/24
 *
 */
class DateTimeUtil {
    /**
     * 静态函数，常用的
     * */
    companion object {
        /**
         * 把一个字符串的入参修改为yyyy-MM-dd HH:mm:ss格式，用来对齐日期格式化使用
         * */
        @JvmStatic
        fun toDateTimeString(string: String, fixTimeString: String = DateTimeConstant.STRING_TIME_BEGIN): String {
            return if (string.isDate()) {
                "$string $fixTimeString"
            } else {
                string
            }
        }

        /**
         * 字符串转时间，默认的格式是
         * yyyy-MM-dd HH:mm:ss
         * @param string 日期字符串
         * @param format 日期格式
         * */
        @JvmStatic
        @JvmOverloads
        fun toDateTime(string: String, format: DateTimeFormatter = DateTimeConstant.Formatter.DATE_TIME_NORMAL): LocalDateTime {
            return LocalDateTime.parse(string, format)
        }

        @JvmStatic
        fun toDateTime(timeStamp: Long): LocalDateTime {
            return toDateTime(Date(timeStamp))
        }

        @JvmStatic
        fun toDateTime(date: Date, zone: ZoneId = ZoneId.systemDefault()): LocalDateTime {
            return date.toInstant().atZone(zone).toLocalDateTime()
        }

        @JvmStatic
        fun toJavaDate(localDateTime: LocalDateTime, zone: ZoneId = ZoneId.systemDefault()): Date {
            return Date.from(localDateTime.atZone(zone).toInstant())
        }

        @JvmStatic
        fun toJavaDate(localDate: LocalDate, zone: ZoneId = ZoneId.systemDefault()): Date {
            return Date.from(localDate.atStartOfDay(zone).toInstant())
        }

        /**
         * 获取一天的开始，默认获取当天日期
         * */
        @JvmStatic
        @JvmOverloads
        fun dayBegin(localDateTime: LocalDateTime = LocalDateTime.now()): LocalDateTime {
            return LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MIN)
        }

        /**
         * 获取一天的结束，默认获取当天日期
         * */
        @JvmStatic
        @JvmOverloads
        fun dayEnd(localDateTime: LocalDateTime = LocalDateTime.now()): LocalDateTime {
            return LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MAX)
        }

        /**
         * 获取一天的开始和结束
         * */
        @JvmStatic
        @JvmOverloads
        fun dayBeginAndEnd(localDateTime: LocalDateTime = LocalDateTime.now()): Array<LocalDateTime> {
            return arrayOf(dayBegin(localDateTime), dayEnd(localDateTime))
        }

        /**
         * 返回上一个距离某一个时间最近的星期X
         * @param localDateTime 原时间
         * @param dayOfWeek 要求的星期X
         * */
        fun lastDayOfWeek(dayOfWeek: DayOfWeek, localDateTime: LocalDateTime = LocalDateTime.now()): LocalDateTime {
            val x = dayOfWeek.value
            val n = localDateTime.dayOfWeek.value
            // 通过这条公式计算距离上一个最近的星期X 应该 减去 ？ 天
            val d = 7 - x + n - (if (x <= n) 7 else 0)
            return localDateTime.minusDays(d.toLong()).dayBegin()
        }

        @JvmStatic
        fun differenceOfDay(date1: LocalDate, date2: LocalDate): Int {
            return abs(date1.toEpochDay() - date2.toEpochDay()).toInt()
        }

        @JvmStatic
        fun differenceOfMinutes(date1: LocalDateTime, date2: LocalDateTime): Int {
            return abs(date1.toEpochSecond(DateTimeConstant.Zone.UTC_8) - date2.toEpochSecond(DateTimeConstant.Zone.UTC_8)).toInt() / 60
        }

        /**
         * 把日期转为时间
         * @param date 日期
         * @param format 日期格式
         * */
        @JvmStatic
        @JvmOverloads
        fun toString(date: LocalDateTime?, format: DateTimeFormatter = DateTimeConstant.Formatter.DATE_TIME_NORMAL): String = if (date == null) "" else date.format(format)

        /**
         * 判断两个日期是否是同一时刻（精确到毫秒）
         * */
        @JvmStatic
        @JvmOverloads
        fun isSame(date1: LocalDateTime?, date2: LocalDateTime?, accuracy: TimeAccuracy = TimeAccuracy.SECOND): Boolean {
            return if (date1 == null || date2 == null) false else when (accuracy) {
                TimeAccuracy.MILLISECOND,
                TimeAccuracy.SECOND,
                TimeAccuracy.MINUTE,
                TimeAccuracy.HOUR -> date1.toLocalDate().isEqual(date2.toLocalDate()) && date1.toLocalTime().getLong(accuracy.temporal) == date2.toLocalTime().getLong(accuracy.temporal)
                TimeAccuracy.DAY,
                TimeAccuracy.WEEK,
                TimeAccuracy.MONTH,
                TimeAccuracy.YEAR -> date1.getLong(accuracy.temporal) == date2.getLong(accuracy.temporal)
            }
        }
    }
}

/**
 * 日期是否相同
 * */
fun LocalDateTime.isSame(date: LocalDateTime?, accuracy: TimeAccuracy = TimeAccuracy.SECOND): Boolean =
    DateTimeUtil.isSame(this, date, accuracy)

fun LocalDateTime.isNotSame(date: LocalDateTime?, accuracy: TimeAccuracy = TimeAccuracy.SECOND): Boolean = !this.isSame(date, accuracy)

fun LocalDateTime.differenceOfDay(date: LocalDateTime): Int =
    DateTimeUtil.differenceOfDay(this.toLocalDate(), date.toLocalDate())

fun LocalDateTime.differenceOfMinutes(date: LocalDateTime): Int = DateTimeUtil.differenceOfMinutes(this, date)

fun LocalDateTime.dayBegin() = DateTimeUtil.dayBegin(this)

fun LocalDateTime.dayEnd() = DateTimeUtil.dayEnd(this)

fun LocalDateTime.lastDayOfWeek(dayOfWeek: DayOfWeek) = DateTimeUtil.lastDayOfWeek(dayOfWeek, this)

fun String.toDateTime(format: DateTimeFormatter = DateTimeConstant.Formatter.DATE_TIME_NORMAL): LocalDateTime =
    DateTimeUtil.toDateTime(this, format)

fun String.toDateTimeString(fixTimeString: String = DateTimeConstant.STRING_TIME_BEGIN) =
    DateTimeUtil.toDateTimeString(this, fixTimeString)

fun String.secondToDateTime(): LocalDateTime = this.toLong().secondToDateTime()

fun Long.toDateTime(): LocalDateTime = DateTimeUtil.toDateTime(this)

fun Long.secondToDateTime(): LocalDateTime = DateTimeUtil.toDateTime(this * 1000)

fun Date.toDateTime(): LocalDateTime = DateTimeUtil.toDateTime(this)

fun LocalDateTime.toJavaDate(): Date = DateTimeUtil.toJavaDate(this)

fun LocalDate.toJavaDate(): Date = DateTimeUtil.toJavaDate(this)

fun LocalDateTime.formatting(format: DateTimeFormatter = DateTimeConstant.Formatter.DATE_TIME_NORMAL): String = this.format(format)

fun main(args: Array<String>) {
    val now = LocalDateTime.now()
    val x = DayOfWeek.MONDAY.value
    val n = now.dayOfWeek.value
    // 通过这条公式计算距离上一个最近的星期X 应该 减去 ？ 天
    val d = 7 - x + n - (if (x <= n) 7 else 0)
    println(now.minusDays(d.toLong()).formatting())


}