package com.jxpanda.common.mq

import com.jxpanda.common.utils.CodeUtil
import java.sql.Timestamp
import java.time.LocalDateTime

/**
 * 卡夫卡消息对象包装
 * 初学Kafka，简易封装
 * */
@Suppress("UNCHECKED_CAST")
data class KafkaMessage<T>(
        /**
         * 消息ID，默认使用雪花算法生成一个ID
         * */
        val id: String = CodeUtil.nextIdString(),
        /**
         * kafka的Topic
         * */
        val topic: String = "",
        /**
         * 消息发送的时间
         * */
        val timestamp: LocalDateTime = LocalDateTime.now(),
        /**
         * 消息体
         * */
        val data: T = Any() as T
) {

    fun <R> reply(status: String = KafkaReply.STATUS_SUCCESS, data: R = Any() as R): KafkaReply<T, R> {
        return KafkaReply(message = this, status = status, data = data)
    }

}