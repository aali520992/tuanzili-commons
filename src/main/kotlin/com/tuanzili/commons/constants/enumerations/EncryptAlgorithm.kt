package com.tuanzili.commons.constants.enumerations

/**
 * 加密算法的枚举
 * */
enum class EncryptAlgorithm(val value: String, val description: String) {
    AES("AES", "AES加密算法"),
    RSA("RSA", "RSA加密算法"),
    MD5("MD5", "MD5加密算法"),
    SHA256("SHA-256", "SHA256加密算法"),
    SHA256_WITH_RSA("SHA256withRSA", "SHA256withRSA，做RSA签名验证使用的算法枚举值"),
}