package com.jxpanda.common.encrypt

import com.jxpanda.common.constants.enumerations.EncryptAlgorithm
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Key
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

open class Encrypt(protected val algorithm: EncryptAlgorithm) {

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    protected val cipher: Cipher = Cipher.getInstance(algorithm.value)!!

    open fun encoder(key: Key): Cipher {
        return cipher.apply {
            cipher.init(Cipher.ENCRYPT_MODE, key)
        }
    }

    open fun decoder(key: Key): Cipher {
        return cipher.apply {
            cipher.init(Cipher.DECRYPT_MODE, key)
        }
    }

    fun encrypt(content: String, key: Key, encryptFunction: () -> ByteArray = { encryptFunction(content, key) }): String {
        return encryptFunction().toBase64()
    }

    fun decrypt(content: String, key: Key, decryptFunction: () -> ByteArray = { decryptFunction(content, key) }): String {
        return String(decryptFunction())
    }

    open fun encrypt(content: String, key: String): String {
        return encrypt(content, key(key))
    }

    open fun decrypt(content: String, key: String): String {
        return decrypt(content, key(key))
    }

    private fun encryptFunction(content: String, key: Key): ByteArray {
        return encoder(key).doFinal(content.toByteArray())
    }

    private fun decryptFunction(content: String, key: Key): ByteArray {
        return decoder(key).doFinal(content.decodeBase64())
    }

    open fun key(password: String): Key {
        return SecretKeySpec(password.toByteArray(), algorithm.value)
    }


}