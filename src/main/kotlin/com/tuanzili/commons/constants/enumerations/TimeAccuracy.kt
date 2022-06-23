package com.jxpanda.common.constants.enumerations

import java.time.temporal.ChronoField

/**
 * Created by Panda on 2018/7/30
 *
 * 时间精度的枚举（无法枚举自然年和自然月的值，因为有闰年或者大小月的问题）
 * 单位取值用的就是ChronoField类里的常量
 * ChronoField里的那些常量看不太懂，整个翻译用用
 * @property temporal 单位
 * @property description 描述
 * */
enum class TimeAccuracy(val temporal: ChronoField, private val description: String) {
    MILLISECOND(ChronoField.MILLI_OF_DAY, "毫秒"),
    SECOND(ChronoField.SECOND_OF_DAY, "秒"),
    MINUTE(ChronoField.MINUTE_OF_DAY, "分"),
    HOUR(ChronoField.HOUR_OF_DAY, "时"),
    DAY(ChronoField.EPOCH_DAY, "日"),
    WEEK(ChronoField.ALIGNED_WEEK_OF_YEAR, "周"),
    MONTH(ChronoField.MONTH_OF_YEAR, "月"),
    YEAR(ChronoField.YEAR_OF_ERA, "年")
}