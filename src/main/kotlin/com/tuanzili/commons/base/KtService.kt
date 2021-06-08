package com.tuanzili.commons.base

import com.baomidou.mybatisplus.core.conditions.Wrapper
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.service.IService
import java.io.Serializable

/**
 * 先暂时写这么多，遇到问题再改，再迭代
 * 这个接口遇到的问题主要是为了解决空安全的问题
 * 基于之前项目开发经验来说，这些函数是最常用的
 * */
interface KtService<T> : IService<T> {

    override fun getById(id: Serializable): T

    override fun getOne(queryWrapper: Wrapper<T>): T

    override fun list(): List<T>

    override fun list(queryWrapper: Wrapper<T>): List<T>

    fun update(updater: Entity.Updater<T>): Boolean

    fun updateById(updater: Entity.Updater<T>): Boolean

    fun isExisting(queryWrapper: Wrapper<T>): Boolean

    fun selectById(id: Serializable): T

    fun selectOne(queryWrapper: Wrapper<T> = QueryWrapper()): T

    fun selectList(queryWrapper: Wrapper<T> = QueryWrapper()): List<T>

    fun selectListByIds(idList: Collection<Serializable>): List<T>

    fun mapByIds(idList: Collection<Serializable>): Map<Serializable, T>

    fun <K> associateBy(keySelector: (T) -> K, queryWrapper: Wrapper<T> = QueryWrapper()): Map<K, T>

    fun pagination(seeker: Seeker<T>): Pagination<T>

}