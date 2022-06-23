package com.jxpanda.common.utils

import org.apache.commons.lang3.StringUtils

class StringUtil {

    companion object {

        /**
         * 把字符串转为snake case命名
         * */
        fun snakeCase(string: String): String {
            return StringUtils.splitByCharacterTypeCamelCase(string).joinToString(separator = "_", transform = {
                it.toLowerCase()
            })
        }

        /**
         * 把字符串转为camel case命名
         * */
        fun camelCase(string: String): String {
            return string.split("_").joinToString(separator = "", transform = {
                it.capitalize()
            }).decapitalize()
        }

    }

}

fun String.snakeCase() = StringUtil.snakeCase(this)
fun String.camelCase() = StringUtil.camelCase(this)
