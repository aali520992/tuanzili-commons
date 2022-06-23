package com.jxpanda.common.encrypt

open class Base64 {
    companion object {

        private val BASE64 = org.apache.commons.codec.binary.Base64()

        @JvmStatic
        fun encode(byteArray: ByteArray): ByteArray {
            return BASE64.encode(byteArray)
        }

        @JvmStatic
        fun encode(content: String): ByteArray {
            return encode(content.toByteArray())
        }

        @JvmStatic
        fun encodeToString(byteArray: ByteArray): String {
            return String(encode(byteArray))
        }

        @JvmStatic
        fun encodeToString(content: String): String {
            return String(encode(content))
        }

        @JvmStatic
        fun decode(byteArray: ByteArray): ByteArray {
            return BASE64.decode(byteArray)
        }

        @JvmStatic
        fun decode(content: String): ByteArray {
            return BASE64.decode(content)
        }

        @JvmStatic
        fun decodeToString(byteArray: ByteArray): String {
            return String(decode(byteArray))
        }

        @JvmStatic
        fun decodeToString(content: String): String {
            return String(decode(content))
        }

    }
}


/**
 * 把字符串转编码为base64的byte数组
 * */
fun String.encodeBase64(): ByteArray = Base64.encode(this)

/**
 * 把base64的字符串解码为byte数组
 * */
fun String.decodeBase64(): ByteArray = Base64.decode(this)

/**
 * 字符串直接做base64编码
 * */
fun String.toBase64(): String = Base64.encodeToString(this)

/**
 * base64字符串解码
 * */
fun String.fromBase64(): String = Base64.decodeToString(this)

/**
 * byte数组做base64编码
 * */
fun ByteArray.toBase64(): String = Base64.encodeToString(this)
