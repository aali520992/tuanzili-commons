package com.tuanzili.commons.encrypt

open class Base32{

    companion object {

        private val BASE32 = org.apache.commons.codec.binary.Base32()

        @JvmStatic
        fun encode(byteArray: ByteArray): ByteArray {
            return BASE32.encode(byteArray)
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
            return BASE32.decode(byteArray)
        }

        @JvmStatic
        fun decode(content: String): ByteArray {
            return BASE32.decode(content)
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
 * 把字符串转编码为base32的byte数组
 * */
fun String.encodeBase32(): ByteArray = Base32.encode(this)

/**
 * 把Base32的字符串解码为byte数组
 * */
fun String.decodeBase32(): ByteArray = Base32.decode(this)

/**
 * 字符串直接做Base32编码
 * */
fun String.toBase32(): String = Base32.encodeToString(this)

/**
 * Base32字符串解码
 * */
fun String.fromBase32(): String = Base32.decodeToString(this)

/**
 * byte数组做Base32编码
 * */
fun ByteArray.toBase32(): String = Base32.encodeToString(this)