package com.jxpanda.common.base

import com.baomidou.mybatisplus.annotation.*
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.fasterxml.jackson.annotation.JsonIgnore
import com.jxpanda.common.constants.DateTimeConstant
import com.jxpanda.common.utils.ReflectionUtil
import com.jxpanda.common.utils.forEachField
import com.jxpanda.common.utils.isNotSame
import com.jxpanda.common.utils.snakeCase
import java.io.Serializable
import java.time.LocalDateTime

private const val DEFAULT_ID_VALUE = "0"
/**
 * updater处理过程中，需要跳过的字段名称
 * */
private val UPDATER_SKIP_FIELD = hashSetOf(Entity.ID, "entityClass")

/**
 * Created by Panda on 2018/7/24
 * 基于mybatis-plus的基本实体类
 * 配合jxpanda-generator使用的时候，可以标准化一些字段
 */
@Suppress("UNCHECKED_CAST")
open class Entity : Serializable {

    /**
     * 主键ID
     * */
    @TableId(type = IdType.ID_WORKER_STR)
    @TableField("id")
    var id: String = ""

    /**
     * 数据创建时间
     * */
    @TableField("created_date")
    var createdDate: LocalDateTime = LocalDateTime.now()

    /**
     * 数据更新时间
     * */
    @TableField("updated_date")
    var updatedDate: LocalDateTime = LocalDateTime.now()

    /**
     * 数据删除时间
     * */
    @JsonIgnore
    @TableField("deleted_date")
    @TableLogic(value = "'${DateTimeConstant.STRING_1970_0_0}'", delval = "CURRENT_TIMESTAMP")
    var deletedDate: LocalDateTime = DateTimeConstant.DATE_1970_0_0

    /**
     * 逻辑删除标记，这个字段在数据库中是一个计算属性，计算公式和这里的一样
     * */
    @get:JsonIgnore
    @TableField("is_deleted", exist = false)
    var deleted: Boolean = false
        get() {
            field = DateTimeConstant.DATE_1970_0_0.isNotSame(deletedDate)
            return field
        }

    /**
     * 返回对象是否有效
     * */
    @JsonIgnore
    @TableField(exist = false)
    var effective: Boolean = false
        get() {
            field = id.isIdEffective() && !deleted
            return field
        }

    /**
     * 更新器，由于mybatis-plus更新检查使用的是null判断
     * 虽然mybatis-plus也提供了empty的判断，但是仅针对字符串，数字类型就会失效
     * 所以在更新的时候，还是要借助于可空对象，通过字段是否为空来判断是否更新对象
     * 然而Entity设计初衷是设计会【空安全】的，所以需要借助一个对象来辅助更新
     * 这个对象配合生成器使用，每个对象作为内部类都生成一个来继承于他
     * */
    abstract class Updater<T>(
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
    ) : Serializable {

        abstract val entityClass: Class<T>

        fun buildWrapper(): UpdateWrapper<T> {
            val updateWrapper = UpdateWrapper<T>()
            forEachField(this::class.java) { field, value ->
                val fieldName = field.name
                if (!UPDATER_SKIP_FIELD.contains(fieldName)) {
                    updateWrapper.set(fieldName.snakeCase(), value)
                }
            }
            return updateWrapper
        }

        fun flushToEntity(entity: T): T {
            return ReflectionUtil.beanCopy(this, ReflectionUtil.beanCopy(entity, entityClass), this::class.java, entityClass)
        }

        fun toEntity(): T {
            val entity = ReflectionUtil.newInstance(entityClass)
            return ReflectionUtil.beanCopy(this, entity, this::class.java, entityClass)
        }

    }

    /**
     * 这个函数默认调用的是toString
     * 因为我在代码生成器中重写了toString函数，会构建成一个JSON字符串返回
     * 但是从函数语义上来说，调用toJSON函数的话，会更加显义一些
     * */
    fun toJsonString(): String {
        return this.toString()
    }

    /**
     * 不要用get开头，会影响getter的反射取值
     * */
    fun tableName(): String {
        val thisClazz = this::class.java
        val tableNameAnnotation = thisClazz.getAnnotation(TableName::class.java)
        return tableNameAnnotation?.value ?: thisClazz.name.snakeCase()
    }

    companion object {
        const val ID = "id"
        const val CREATED_DATE = "created_date"
        const val UPDATED_DATE = "updated_date"
        const val DELETED_DATE = "deleted_date"
    }

}

/**
 * 给String扩展一个判断ID是否有效的函数
 * 方便判断各种ID是否有效
 * */
fun String?.isIdEffective(): Boolean {
    // 不为空或者不是"0"则视为有效
    return !this.isNullOrBlank() && this != DEFAULT_ID_VALUE
}