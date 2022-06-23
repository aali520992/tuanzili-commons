package com.jxpanda.common.utils


import com.jxpanda.common.encrypt.MD5
import com.jxpanda.common.encrypt.AES
import com.jxpanda.common.encrypt.RSA
import com.jxpanda.common.encrypt.SHA256


/**
 * Created by Panda on 2018/9/2
 * 各种加解密工具
 */
class EncryptUtil {
    companion object {
        val RSA = RSA()
        val MD5 = MD5()
        val SHA256 = SHA256()
        val AES_EBC = AES.EBC()
        val AES_CBC = AES.CBC()
    }
}