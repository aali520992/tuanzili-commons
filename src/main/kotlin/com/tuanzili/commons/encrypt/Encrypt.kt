package com.tuanzili.commons.encrypt

import com.tuanzili.commons.constants.enumerations.EncryptAlgorithm
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

open class Encrypt(private val algorithm: EncryptAlgorithm) {

    private val cipher: Cipher = Cipher.getInstance(algorithm.value)!!

    fun encoder(key: Key): Cipher {
        return cipher.apply {
            init(Cipher.ENCRYPT_MODE, key)
        }
    }

    fun decoder(key: Key): Cipher {
        return cipher.apply {
            init(Cipher.DECRYPT_MODE, key)
        }
    }

    open fun encrypt(content: String, key: Key): String {
        return Base64.encodeToString(encoder(key).doFinal(content.toByteArray()))
    }

    open fun decrypt(content: String, key: Key): String {
        return String(decoder(key).doFinal(Base64.decode(content)))
    }

    open fun key(password: String): Key {
        return SecretKeySpec(password.toByteArray(), algorithm.value)
    }
}