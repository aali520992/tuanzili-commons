package com.tuanzili.commons.mq

import com.tuanzili.commons.utils.CodeUtil
import java.time.LocalDateTime

/**
 * Kafka消息的回复
 * 初学Kafka，简易设计
 * */
@Suppress("UNCHECKED_CAST")
data class KafkaReply<M, R>(
    /**
         * 消息原文
         * */
        val message: KafkaMessage<M>,
    /**
         * ID，默认使用雪花算法生成一个
         * */
        val id: String = CodeUtil.nextIdString(),
    /**
         * 消息的ID
         * */
        val messageId: String = message.id,
    /**
         * 消息回复的时间
         * */
        val timestamp: LocalDateTime = LocalDateTime.now(),
    /**
         * 消息处理状态
         * */
        val status: String = STATUS_SUCCESS,
    /**
         * 消息是否处理成功的标记
         * */
        val success: Boolean = status == STATUS_SUCCESS,
    /**
         * 回复的内容
         * */
        val data: R = Any() as R
) {

    companion object {
        const val STATUS_SUCCESS = "SUCCESS"
        const val STATUS_FAILED = "FAILED"
    }
}