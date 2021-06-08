package com.tuanzili.commons.encrypt

import com.tuanzili.commons.constants.enumerations.EncryptAlgorithm
import com.tuanzili.commons.utils.EncryptUtil
import java.io.ByteArrayOutputStream
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

/**
 * 先实现一些简易功能
 * */
open class RSA(
        private val keySize: Int = 2048,
        /**
         * keySize=1024时，分段不能大于117 keySize >= 2048时，分段不能大于keySize / 8 + 128
         */
        private val ENCRYPT_SIZE: Int = if (keySize == 1024) 117 else keySize / 8,
        /**
         * 等于keySize / 8
         * */
        private val DECRYPT_SIZE: Int = keySize / 8,
        private val algorithm: EncryptAlgorithm = EncryptAlgorithm.RSA
) : Encrypt(algorithm) {

    fun generatorKeyPair(keySize: Int = 2048): KeyPair {
        val generator = KeyPairGenerator.getInstance(algorithm.value)
        generator.initialize(keySize)
        return generator.genKeyPair()
    }

    /**
     * 加密
     * */
    override fun encrypt(content: String, key: Key): String {
        return Base64.encodeToString(sectionFunction(content.toByteArray(), encoder(key), ENCRYPT_SIZE))
    }

    /**
     * 解密
     * */
    override fun decrypt(content: String, key: Key): String {
        return String(sectionFunction(content.decodeBase64(), decoder(key), DECRYPT_SIZE))
    }

    /**
     * RSA签名函数
     * */
    @JvmOverloads
    fun signature(content: String, key: PrivateKey, algorithm: EncryptAlgorithm = EncryptAlgorithm.SHA256_WITH_RSA): ByteArray {
        val signature = Signature.getInstance(algorithm.value)
        signature.initSign(key)
        signature.update(content.toByteArray())
        return signature.sign()
    }

    /**
     * 验证签名
     * */
    @JvmOverloads
    fun verify(content: String, sign: ByteArray, key: PublicKey, algorithm: EncryptAlgorithm = EncryptAlgorithm.SHA256_WITH_RSA): Boolean {
        val signature = Signature.getInstance(algorithm.value)
        signature.initVerify(key)
        signature.update(content.toByteArray())
        return signature.verify(sign)
    }

    /**
     * 加密、解密的分段处理函数
     * */
    private fun sectionFunction(byteArray: ByteArray, cipher: Cipher, sectionLength: Int): ByteArray {
        val size = byteArray.size
        var buffer: ByteArray
        var offset = 0
        val bos = ByteArrayOutputStream()
        while (size - offset > 0) {
            buffer = if (size - offset >= sectionLength) {
                cipher.doFinal(byteArray, offset, sectionLength)
            } else {
                cipher.doFinal(byteArray, offset, size - offset)
            }
            bos.write(buffer)
            offset += sectionLength
        }
        bos.close()
        return bos.toByteArray()
    }

    companion object {

        private val keyFactory = KeyFactory.getInstance(EncryptAlgorithm.RSA.value)

        @JvmStatic
        fun publicKey(key: String): PublicKey {
            return keyFactory.generatePublic(X509EncodedKeySpec(key.trimIndent().replace("\n", "").decodeBase64()))
        }

        @JvmStatic
        fun privateKey(key: String): PrivateKey {
            return keyFactory.generatePrivate(PKCS8EncodedKeySpec(key.trimIndent().replace("\n", "").decodeBase64()))
        }

    }
}


fun String.toPublicKey(): PublicKey = RSA.publicKey(this)

fun String.toPrivateKey(): PrivateKey = RSA.privateKey(this)

fun PublicKey.fromPublicKey(): String = this.encoded.toBase64()

fun PrivateKey.fromPrivateKey(): String = this.encoded.toBase64()

fun String.encryptRSA(key: Key): String = EncryptUtil.RSA.encrypt(this, key)

fun String.decryptRSA(key: Key): String = EncryptUtil.RSA.decrypt(this, key)

fun String.signature(key: PrivateKey): ByteArray = EncryptUtil.RSA.signature(this, key)

fun String.verify(sign: ByteArray, key: PublicKey): Boolean = EncryptUtil.RSA.verify(this, sign, key)