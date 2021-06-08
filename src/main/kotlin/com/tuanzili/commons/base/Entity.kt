package com.tuanzili.commons.base

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableLogic
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.fasterxml.jackson.annotation.JsonIgnore
import com.tuanzili.commons.constants.DateTimeConstant
import com.tuanzili.commons.utils.isNotSame
import java.io.Serializable
import java.time.LocalDateTime

/**
 * Created by tuanzili on 2018/7/24
 * 基于mybatis-plus的基本实体类
 * tuanzili-generator使用的时候，可以标准化一些字段
 */
@Suppress("UNCHECKED_CAST")
open class Entity : Serializable {

    /**
     * 主键ID
     * */
    @TableId(type = IdType.ASSIGN_ID)
    @TableField("id")
    val id: String = ""

    /**
     * 数据创建时间
     * */
    @TableField("created_date")
    val createdDate: LocalDateTime = LocalDateTime.now()

    /**
     * 数据更新时间
     * */
    @TableField("updated_date")
    val updatedDate: LocalDateTime = LocalDateTime.now()

    /**
     * 数据删除时间
     * */
    @JsonIgnore
    @TableField("deleted_date")
    @TableLogic(value = "'${DateTimeConstant.STRING_1970_0_0}'", delval = "CURRENT_TIMESTAMP")
    val deletedDate: LocalDateTime = DateTimeConstant.DATE_1970_0_0

    /**
     * 逻辑删除标记，这个字段在数据库中是一个计算属性，计算公式和这里的一样
     * */
    @JsonIgnore
    @TableField("is_deleted", exist = false)
    val deleted: Boolean = DateTimeConstant.DATE_1970_0_0.isNotSame(deletedDate)

    /**
     * 返回对象是否有效
     * */
    @JsonIgnore
    @TableField(exist = false)
    val effective: Boolean = id.isNotBlank() && id != "0"

    /**
     * 更新器，由于mybatis-plus更新检查使用的是null判断
     * 虽然mybatis-plus也提供了empty的判断，但是仅针对字符串，数字类型就会失效
     * 所以在更新的时候，还是要借助于可空对象，通过字段是否为空来判断是否更新对象
     * 然而Entity设计初衷是设计会【空安全】的，所以需要借助一个对象来辅助更新
     * 这个对象配合生成器使用，每个对象作为内部类都生成一个来继承于他
     * */
    open class Updater<T>(
            /**
             * 主键ID
             * */
            var id: String? = null,

            /**
             * 数据创建时间
             * */
            var createdDate: LocalDateTime? = null,

            /**
             * 数据更新时间
             * */
            var updatedDate: LocalDateTime? = null,

            /**
             * 数据删除时间
             * */
            var deletedDate: LocalDateTime? = null
    ) {

        companion object {
            const val GET_PREFIX = "get"
        }

        fun buildWrapper(): UpdateWrapper<T> {
            val updateWrapper = UpdateWrapper<T>()
            val clazz = this::class.java
            val methods = clazz.methods.filter { it.name.contains(GET_PREFIX) }
            clazz.declaredFields.forEach {
                val first = methods.first { method -> method.name == "$GET_PREFIX${it.name.capitalize()}" }
                val value = first.invoke(this)
                if (value != null) {
                    updateWrapper.set(it.name, value)
                }
            }
            return updateWrapper
        }

    }

    companion object {
        const val ID = "id"
        const val CREATED_DATE = "created_date"
        const val UPDATED_DATE = "updated_date"
        const val DELETED_DATE = "deleted_date"
    }

}

