package com.tuanzili.commons.utils

import com.tuanzili.commons.encrypt.AES
import com.tuanzili.commons.encrypt.MD5
import com.tuanzili.commons.encrypt.RSA
import com.tuanzili.commons.encrypt.SHA256


/**
 * Created by Panda on 2018/9/2
 * 各种加解密工具
 */
class EncryptUtil {
    companion object {
        val RSA = RSA()
        val MD5 = MD5()
        val SHA256 = SHA256()
        val AES = AES()
    }
}