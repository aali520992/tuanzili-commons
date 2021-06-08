package com.tuanzili.commons.encrypt

import com.tuanzili.commons.constants.enumerations.EncryptAlgorithm
import com.tuanzili.commons.utils.EncryptUtil

/**
 * SHA256加密算法
 * */
open class SHA256 : Digest(EncryptAlgorithm.SHA256)

/**
 * 把一个字符串做SHA256加密
 * */
fun String.encryptSHA256(): String = EncryptUtil.SHA256.encrypt(this)