package com.jxpanda.common.utils

import com.alibaba.fastjson.JSON
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.jxpanda.common.constants.DateTimeConstant
import java.lang.StringBuilder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.collections.HashMap

class JsonUtil private constructor() {

    class JsonStringBuilder {

        private var stringBuilder = StringBuilder()

        fun setValue(key: String, value: Any) {
            stringBuilder.append("""
                "$key":"$value",
            """.trimIndent())
        }

        /**
         * 就是把最后一个逗号去掉
         * */
        private fun trim(): String {
            return stringBuilder.substring(0, stringBuilder.length - 1)
        }

        fun objectBuild(): String {
            return "{${this.trim()}}"
        }

        fun arrayBuild(): String {
            return "[${this.trim()}]"
        }

    }

    companion object {
        @JvmField
        val JACKSON = Jackson.objectMapper

        @JvmStatic
        fun <T> toJson(value: T): String {
            return JACKSON.writeValueAsString(value)
        }

        @JvmStatic
        fun <T> fromJson(json: String, clazz: Class<T>): T {
            return JACKSON.readValue(json, clazz)
        }

        inline fun <reified T> fromJson(json: String): T {
            return JACKSON.readValue(json)
        }

        @JvmStatic
        fun <T> fastToJson(value: T): String {
            return JSON.toJSONString(value)
        }

        @JvmStatic
        fun <T> fastFromJson(json: String, clazz: Class<T>): T {
            return JSON.parseObject(json, clazz)
        }

        inline fun <reified T> fastFromJson(json: String): T {
            return fastFromJson(json, T::class.java)
        }

    }

    private object Jackson {
        val objectMapper = ObjectMapper()

        init {
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

            val javaTimeModule = JavaTimeModule()
            javaTimeModule.addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(DateTimeConstant.Formatter.DATE_TIME_NORMAL))
            javaTimeModule.addSerializer(LocalDate::class.java, LocalDateSerializer(DateTimeConstant.Formatter.DATE_NORMAL))
            javaTimeModule.addSerializer(LocalTime::class.java, LocalTimeSerializer(DateTimeConstant.Formatter.TIME_NORMAL))

            javaTimeModule.addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(DateTimeConstant.Formatter.DATE_TIME_NORMAL))
            javaTimeModule.addDeserializer(LocalDate::class.java, LocalDateDeserializer(DateTimeConstant.Formatter.DATE_NORMAL))
            javaTimeModule.addDeserializer(LocalTime::class.java, LocalTimeDeserializer(DateTimeConstant.Formatter.TIME_NORMAL))

            objectMapper.registerModule(javaTimeModule)
                    .registerModule(ParameterNamesModule())
                    .registerModule(KotlinModule())
        }
    }

}

// 惯例，增加几个扩展
fun <T> T.toJson(): String = JsonUtil.toJson(this)

inline fun <reified T> String.fromJson() = JsonUtil.fromJson<T>(this)

fun <T> String.fromJson(clazz: Class<T>) = JsonUtil.fromJson(this, clazz)

fun <T> T.fastToJson(): String = JsonUtil.fastToJson(this)

inline fun <reified T> String.fastFromJson() = JsonUtil.fastFromJson<T>(this)

fun <T> String.fastFromJson(clazz: Class<T>) = JsonUtil.fastFromJson(this, clazz)

inline fun <reified T> T.toMap(): HashMap<String, Any> = this.toJson().fromJson()

inline fun <reified T> T.toTreeMap(): TreeMap<String, Any> = this.toJson().fromJson()