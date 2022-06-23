package com.jxpanda.common.base

import java.io.Serializable
import java.time.LocalDateTime

/**
 * Created by Panda on 2018/8/27
 */
@Suppress("UNCHECKED_CAST")
abstract class DTO<E : Entity>(
        /**
         * 原生entity对象的指针
         * */
        open val entity: E? = null,
        /**
         * 主键ID
         * */
        open val id: String = entity?.id ?: "",
        /**
         * 数据创建时间
         * */
        val createdDate: LocalDateTime = entity?.createdDate ?: LocalDateTime.now(),
        /**
         * 数据更新时间
         * */
        val updatedDate: LocalDateTime = entity?.updatedDate ?: LocalDateTime.now(),
        /**
         * 逻辑删除标记（DTO对象中抹除逻辑删除时间的数据，只透传是否被删除）
         * */
        val deleted: Boolean = entity?.deleted == true,
        /**
         * 返回对象是否有效
         * */
        val effective: Boolean = id.isNotBlank() && id != "0"
) : Serializable