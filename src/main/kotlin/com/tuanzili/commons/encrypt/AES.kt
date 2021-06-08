package com.tuanzili.commons.encrypt

import com.tuanzili.commons.constants.enumerations.EncryptAlgorithm
import com.tuanzili.commons.utils.EncryptUtil
import java.security.Key

/**
 * AES加密算法
 * */
open class AES : Encrypt(EncryptAlgorithm.AES) {

    fun encrypt(content: String, key: String): String {
        return encrypt(content, key(key))
    }

    fun decrypt(content: String, key: String): String {
        return decrypt(content, key(key))
    }

    /**
     * 强制限制字符串长度为16个（AES加密的时候要用）
     * */
    private fun String.string16(): String {
        return if (this.length < 16) {
            val need = 16 - this.length
            val builder = StringBuilder(this)
            for (i in 1..need) {
                builder.append("0")
            }
            builder.toString()
        } else {
            this.substring(0, 16)
        }
    }

    override fun key(password: String): Key {
        return super.key(password.string16())
    }

    companion object {
        const val DEFAULT_PWD = "123456789DEFAULT"
    }

}

/**
 * 把一个字符串用AES加密
 * */
fun String.encryptAES(password: String = AES.DEFAULT_PWD): String = EncryptUtil.AES.encrypt(this, password)

/**
 * 把一个字符串用AES解密
 * */
fun String.decryptAES(password: String = AES.DEFAULT_PWD): String = EncryptUtil.AES.decrypt(this, password)