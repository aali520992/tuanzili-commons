package com.jxpanda.common.utils

import java.lang.reflect.Field
import java.lang.reflect.Method


private const val GET_PREFIX = "get"
private const val SET_PREFIX = "set"
// getClass函数要从getter中排除
private const val GET_CLASS = "getClass"

class ReflectionUtil {

    companion object {

        /**
         * 迭代字段，然后把值拿出来，交给传入的处理函数处理
         * */
        @JvmStatic
        fun <T> forEachField(obj: T, clazz: Class<out T>, handle: (field: Field, value: Any) -> Unit) {
            // 只获取get开头的函数,并且以字段名为key，映射成一个Map，方便后面取值
            val getterMap = getterMap(clazz)
            // 获取类里的属性和父类的属性，然后合并到一个列表中统一处理
            fieldList(clazz).forEach {
                val getter = getterMap[it.name]
                if (getter != null) {
                    val value = getter.invoke(obj)
                    if (value != null) {
                        handle(it, value)
                    }
                }
            }
        }

        @JvmStatic
        fun <T> forEachFieldToMap(obj: T, clazz: Class<out T>, valueTransformer: (value: Any) -> Any = {}): HashMap<String, Any> {
            val resultMap = HashMap<String, Any>()
            forEachField(obj, clazz) { field, value ->
                resultMap[field.name] = valueTransformer(value)
            }
            return resultMap
        }

        @JvmStatic
        fun <T, R : MutableMap<String, Any>> forEachFieldToMap(obj: T, clazz: Class<out T>, mapClazz: Class<out R>, valueTransformer: (value: Any) -> Any): R {
            val resultMap = newInstance(mapClazz)
            forEachField(obj, clazz) { field, value ->
                resultMap[field.name] = valueTransformer(value)
            }
            return resultMap
        }

        /**
         * 复制一个java bean
         * 这个函数会返回复制出来的java bean
         * 复制出来的bean是一个新的对象
         * */
        @JvmStatic
        fun <T> beanCopy(source: T, clazz: Class<out T>): T {
            return beanCopy(source, newInstance(clazz), clazz, clazz)
        }

        /**
         * 复制一个java bean
         * source --> target
         * 返回值是target
         * */
        @JvmStatic
        fun <E, T> beanCopy(source: E, target: T, sourceClass: Class<out E>, targetClass: Class<out T>): T {
            val setterMap = setterMap(targetClass)
            forEachField(source, sourceClass) { field, value ->
                setterMap[field.name]?.invoke(target, value)
            }
            return target
        }

        /**
         * 基于类创建对象
         * 简易调用一下newInstance函数，以后可能会扩展
         * */
        @JvmStatic
        fun <T> newInstance(clazz: Class<T>): T {
            return clazz.getConstructor().newInstance()
        }

        /**
         * 获取类中的所有属性列表
         * 这个函数会把父类的属性也一起拉出来
         * 暂时只拉取一层父类（不会递归去找父类的父类）
         * */
        @JvmStatic
        fun <T> fieldList(clazz: Class<T>): List<Field> {
            val declaredFields = clazz.declaredFields
            val superFields = clazz.superclass?.declaredFields
            // 合并到一个列表里，一起处理
            val fieldList = declaredFields.toMutableList()
            fieldList.addAll(superFields ?: emptyArray())
            return fieldList
        }

        /**
         * 获取类中的所有getter函数，并且映射成一个map
         * key为属性的名称，value是getter函数
         * */
        @JvmStatic
        fun <T> getterMap(clazz: Class<out T>): Map<String, Method> {
            return clazz.methods
                    .filter { it.name.contains(GET_PREFIX) && it.name != GET_CLASS }
                    .associateBy { it.name.replace(GET_PREFIX, "").decapitalize() }
        }

        /**
         * 获取类中的所有setter函数，并且映射成一个map
         * key为属性的名称，value是setter函数
         * */
        @JvmStatic
        fun <T> setterMap(clazz: Class<out T>): Map<String, Method> {
            return clazz.methods
                    .filter { it.name.contains(SET_PREFIX) }
                    .associateBy { it.name.replace(SET_PREFIX, "").decapitalize() }
        }

        // 以下是一些inline的函数，方便kotlin使用的

        /**
         * 迭代字段，然后把值拿出来，交给传入的处理函数处理
         * */
        inline fun <reified T> forEachField(obj: T, noinline handle: (field: Field, value: Any) -> Unit) {
            val clazz = T::class.java
            // 只获取get开头的函数,并且以字段名为key，映射成一个Map，方便后面取值
            forEachField(obj, clazz, handle)
        }

        /**
         * 复制一个java bean
         * 这个函数会返回复制出来的java bean
         * 复制出来的bean是一个新的对象
         * */
        inline fun <reified T> beanCopy(source: T): T {
            return beanCopy(source, T::class.java)
        }

        /**
         * 复制一个java bean
         * source --> target
         * 返回值是target
         * */
        inline fun <reified E, reified T> beanCopy(source: E, target: T): T {
            val clazz = T::class.java
            val setterMap = setterMap(clazz)
            forEachField(source) { field, value ->
                setterMap[field.name]?.invoke(target, value)
            }
            return target
        }

    }

}

/**
 * 惯例，提供kotlin的扩展，方便使用
 * 这个扩展略坑
 * */
inline fun <reified T> T.forEachField(clazz: Class<out T> = T::class.java, noinline handle: (field: Field, value: Any) -> Unit) = ReflectionUtil.forEachField(this, clazz, handle)

inline fun <reified T, reified R : MutableMap<String, Any>> T.forEachFieldToMap(clazz: Class<out T> = T::class.java, mapClazz: Class<out R> = R::class.java, noinline valueTransformer: (value: Any) -> Any = { it }): R = ReflectionUtil.forEachFieldToMap(this, clazz, mapClazz, valueTransformer)

inline fun <reified E, reified T> E.copyTo(target: T): T = ReflectionUtil.beanCopy(this, target)

