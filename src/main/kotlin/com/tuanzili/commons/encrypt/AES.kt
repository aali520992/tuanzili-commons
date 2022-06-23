package com.jxpanda.common.encrypt

import com.jxpanda.common.constants.enumerations.EncryptAlgorithm
import com.jxpanda.common.utils.EncryptUtil
import java.nio.charset.Charset
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


/**
 * AES加密算法
 * */
open class AES {

    open class EBC : Encrypt(EncryptAlgorithm.AES) {
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
    }

    open class CBC : Encrypt(EncryptAlgorithm.AES_CBC_PKCS7) {
        fun encrypt(content: String, key: String, iv: String): String {
            cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key.decodeBase64(), algorithm.value), IvParameterSpec(iv.decodeBase64()))
            return cipher.doFinal(content.toByteArray()).toBase64()
        }

        fun decrypt(content: String, key: String, iv: String): String {
            cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key.decodeBase64(), algorithm.value), IvParameterSpec(iv.decodeBase64()))
            return String(cipher.doFinal(content.decodeBase64()))
        }
    }

    companion object {
        const val DEFAULT_PWD = "123456789DEFAULT"
    }

}

/**
 * 把一个字符串用AES加密
 * */
fun String.encryptAES(password: String = AES.DEFAULT_PWD, iv: String = ""): String = if (iv.isBlank()) EncryptUtil.AES_EBC.encrypt(this, password) else EncryptUtil.AES_CBC.encrypt(this, password, iv)

/**
 * 把一个字符串用AES解密
 * */
fun String.decryptAES(password: String = AES.DEFAULT_PWD, iv: String = ""): String = if (iv.isBlank()) EncryptUtil.AES_EBC.decrypt(this, password) else EncryptUtil.AES_CBC.decrypt(this, password, iv)

fun main() {
    val appId = "wx4f4bc4dec97d474b"
    val sessionKey = "DzQEtV5vTa8Yb3+2nFufsg=="
    val encryptedData = """
PZxz/jfMs1q3+QUqkPznqgEJLHNhv2KM/99XOHYOmXK6dpjzP3ukTE7b8OPKcDe4Ula6zA5J+iJqng7+CfhmuVNG8AWQuAyvU9PtaC+jlFglSpV4VLX/ukovqel8DDyc0JKLpIDAEir8llABd32Rbr+O9Cih4T93tbKFPp3G/h0+FVS23JBYbDHM32WIhHGCmYL/MOMd4V8TobJ4F1UoLLEmAee1mtcC1HDJiqS1lUBT9YYQmXtOtxz6DGnCeAMZKHlLYamGkQ57TEKDjx4mMjdIcrxMaFS3uq2O8roa+d2DKBNsRyFI91/rr9KBF2zvZK0YId2iMzhwtuP6QJlE8YzKnhEXQPAPVlgav49f/wmX/3IVbdh4KtQGxClUbyhepZliE8hscOAO1s96n6rmqfEuXRUTVChBq423/V3tJFCKVAM7+ZZk4w1628CUvDz7dqswIJPr3V9tzA8ujwLkvFy0xIFnbkZa6xv+OTz124vfJgClQini+AkykpIcLYpY
    """.trimIndent()
    val iv = "InynLYBAI6D0n2hap8vgEA=="


    println(encryptedData)
    println(EncryptUtil.AES_CBC.decrypt(encryptedData, sessionKey, iv))
    println(encryptedData.decryptAES(sessionKey,iv))

}