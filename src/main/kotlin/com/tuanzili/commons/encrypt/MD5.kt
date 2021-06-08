package com.tuanzili.commons.encrypt

import com.tuanzili.commons.constants.enumerations.EncryptAlgorithm
import com.tuanzili.commons.utils.EncryptUtil
import com.tuanzili.commons.utils.toHex

/**
 * MD5加密算法
 * */
open class MD5 : Digest(EncryptAlgorithm.MD5) {
    override fun encrypt(content: String, toUpperCase: Boolean): String {
        val secretBytes: ByteArray = digest(content)
        val hexBuilder = StringBuilder()
        for (i in 0 until secretBytes.size) {
            val byte = secretBytes[i].toInt() and 0XFF
            if (byte < 16) {
                hexBuilder.append("0")
            }
            hexBuilder.append(byte.toHex())
        }
        return if (toUpperCase) hexBuilder.toString().toUpperCase() else hexBuilder.toString()
    }
}

/**
 * 把一个字符做MD5加密
 * */
fun String.encryptMD5(): String = EncryptUtil.MD5.encrypt(this)
