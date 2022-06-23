package com.jxpanda.common.utils

import com.fasterxml.jackson.annotation.JsonProperty
import org.dom4j.DocumentHelper
import org.dom4j.io.SAXReader

/**
 * xml工具类
 * 解析xml数据
 * 目前只做简单封装，有高级需求的时候再说
 * 对象的转化目前不支持深度数据
 * */
class XmlUtil {

    companion object {

        @JvmStatic
        fun <T> fromXml(xml: String, clazz: Class<T>): T {
            return toJsonObject(xml).fromJson(clazz)
        }

        @JvmStatic
        fun toJsonObject(xml: String): String {
            return buildJson(xml).objectBuild()
        }

        inline fun <reified T> toXml(obj: T): String {
            val document = DocumentHelper.createDocument()
            val rootElement = document.addElement("xml")
            obj.toMap().forEach {
                rootElement.addElement(it.key).addCDATA(it.value.toString())
            }
            return document.rootElement.asXML()
        }

//        @JvmStatic
//        这个函数还不能工作，以后需要用的时候再具体实现
//        fun toJsonArray(xml: String): String {
//            return buildJson(xml).arrayBuild()
//        }

        private fun buildJson(xml: String): JsonUtil.JsonStringBuilder {
            val saxReader = SAXReader()
            val read = saxReader.read(xml.reader())
            val nodeIterator = read.rootElement.nodeIterator()
            val jsonStringBuilder = JsonUtil.JsonStringBuilder()
            while (nodeIterator.hasNext()) {
                val next = nodeIterator.next()
                if (!next.name.isNullOrBlank()) {
                    jsonStringBuilder.setValue(next.name, next.text)
                }
            }
            return jsonStringBuilder
        }

    }

}

inline fun <reified T> String.fromXml(): T = XmlUtil.fromXml(this, T::class.java)
inline fun <reified T> T.toXml(): String = XmlUtil.toXml(this)

class UnifiedOrder(
        @JsonProperty("appid")
        val appId: String = "",
        @JsonProperty("mch_id")
        val mchId: String = "",
        @JsonProperty("nonce_str")
        val nonceStr: String = "",
        @JsonProperty("sign")
        val sign: String = "",
        @JsonProperty("body")
        val body: String = "",
        @JsonProperty("out_trade_no")
        val orderId: String = "",
        @JsonProperty("total_fee")
        val totalFee: Int = 0,
        @JsonProperty("spbill_create_ip")
        val ip: String = "",
        @JsonProperty("notify_url")
        val notifyUrl: String = "",
        @JsonProperty("trade_type")
        val tradeType: String = "APP"
)

class Test(
        @JsonProperty("return_code")
        var returnCode: String = "",
        @JsonProperty("return_msg")
        var returnMsg: String = "",
        @JsonProperty("appid")
        var appId: String = "",
        @JsonProperty("mch_id")
        var mchId: String = "",
        @JsonProperty("nonce_str")
        var nonceStr: String = "",
        @JsonProperty("sign")
        var sign: String = "",
        @JsonProperty("result_code")
        var resultCode: String = "",
        @JsonProperty("prepay_id")
        var prepayId: String = "",
        @JsonProperty("trade_type")
        var tradeType: String = ""
)

fun main() {
//    val xml = """
//        <xml>
//           <return_code><![CDATA[SUCCESS]]></return_code>
//           <return_msg><![CDATA[OK]]></return_msg>
//           <appid><![CDATA[wx2421b1c4370ec43b]]></appid>
//           <mch_id><![CDATA[10000100]]></mch_id>
//           <nonce_str><![CDATA[IITRi8Iabbblz1Jc]]></nonce_str>
//           <sign><![CDATA[7921E432F65EB8ED0CE9755F0E86D72F]]></sign>
//           <result_code><![CDATA[SUCCESS]]></result_code>
//           <prepay_id><![CDATA[wx201411101639507cbf6ffd8b0779950874]]></prepay_id>
//           <trade_type><![CDATA[APP]]></trade_type>
//        </xml>
//    """.trimIndent()
//
//    println(xml.fromXml<Test>().toJson())

    println(UnifiedOrder().toXml())

}