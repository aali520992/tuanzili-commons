package com.tuanzili.commons.encrypt

import com.tuanzili.commons.constants.enumerations.EncryptAlgorithm
import com.tuanzili.commons.utils.toHex
import java.security.MessageDigest

open class Digest(algorithm: EncryptAlgorithm) {

    private val instance = MessageDigest.getInstance(algorithm.value)

    open fun digest(content: String): ByteArray {
        return instance.digest(content.toByteArray())
    }

    open fun encrypt(content: String, toUpperCase: Boolean = true): String {
        return if (toUpperCase) digest(content).toHex().toUpperCase() else digest(content).toHex()
    }

}