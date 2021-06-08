package com.tuanzili.commons.utils

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