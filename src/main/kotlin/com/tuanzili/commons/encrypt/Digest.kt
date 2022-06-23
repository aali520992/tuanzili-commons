package com.jxpanda.common.encrypt

import com.jxpanda.common.constants.enumerations.EncryptAlgorithm
import com.jxpanda.common.utils.toHex
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