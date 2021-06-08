package com.tuanzili.commons.constants

import com.tuanzili.commons.constants.DateTimeConstant.Formatter.Companion.DATE_TIME_NORMAL
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Created by tuanzili on 2018/7/24
 */

open class DateTimeConstant {


    companion object {
        const val STRING_1970_0_0 = "1970-01-01 00:00:00"
        const val STRING_TIME_BEGIN = "00:00:00.000000000"
        const val STRING_TIME_END = "23:59:59.999999999"

        @JvmField
        val DATE_1970_0_0 = LocalDateTime.parse(STRING_1970_0_0, DATE_TIME_NORMAL)!!

        @JvmField
        val DELETED_DATE = DATE_1970_0_0
    }

    open class Pattern {
        companion object {
            const val DATE_TIME_NORMAL = "yyyy-MM-dd HH:mm:ss"

            const val DATE_NORMAL = "yyyy-MM-dd"

            const val DATE_SEQUENCE = "yyyyMMdd"

            const val TIME_NORMAL = "HH:mm:ss"
        }
    }

    open class Formatter {
        companion object {
            @JvmField
            val DATE_TIME_NORMAL = DateTimeFormatter.ofPattern(Pattern.DATE_TIME_NORMAL)!!

            @JvmField
            val DATE_NORMAL = DateTimeFormatter.ofPattern(Pattern.DATE_NORMAL)!!

            @JvmField
            val DATE_SEQUENCE = DateTimeFormatter.ofPattern(Pattern.DATE_SEQUENCE)!!

            @JvmField
            val TIME_NORMAL = DateTimeFormatter.ofPattern(Pattern.TIME_NORMAL)!!
        }
    }

    open class Zone {
        companion object {
            // 东八区
            @JvmField
            val UTC_8: ZoneOffset = ZoneOffset.ofHours(8)
        }
    }

}
