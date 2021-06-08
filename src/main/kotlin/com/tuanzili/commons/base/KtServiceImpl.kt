package com.tuanzili.commons.base

import com.baomidou.mybatisplus.core.conditions.Wrapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import java.io.Serializable

/**
 * 改为抽象类了，因为一定要知道泛型实体的具体类型是多少，为了实现尽可能的【空安全】设计
 * 一味拥抱空安全设计或许不太科学
 * 目前先激进的试用一段时间，有问题再迭代吧，路要一步步走
 * */
abstract class KtServiceImpl<M : BaseMapper<T>, T : Entity> : ServiceImpl<M, T>(), KtService<T> {

    abstract val emptyEntity: T

    override fun getById(id: Serializable): T {
        return super.getById(id) ?: emptyEntity
    }

    override fun getOne(queryWrapper: Wrapper<T>): T {
        return super.getOne(queryWrapper) ?: emptyEntity
    }

    override fun update(updater: Entity.Updater<T>): Boolean {
        return update(updater.buildWrapper())
    }

    override fun updateById(updater: Entity.Updater<T>): Boolean {
        return update(updater.buildWrapper().eq(Entity.ID, updater.id))
    }

    override fun list(): MutableList<T> {
        return super.list() ?: mutableListOf()
    }

    override fun list(queryWrapper: Wrapper<T>): MutableList<T> {
        return super.list(queryWrapper) ?: mutableListOf()
    }

    override fun isExisting(queryWrapper: Wrapper<T>): Boolean {
        return count(queryWrapper) > 0
    }

    override fun selectById(id: Serializable): T {
        return getById(id)
    }

    override fun selectOne(queryWrapper: Wrapper<T>): T {
        // 使用分页查询，只查询1条数据，可以排除TooManyResultsException错误
        return page(Page<T>(1, 1), queryWrapper).records.getOrNull(0) ?: emptyEntity
    }

    override fun selectList(queryWrapper: Wrapper<T>): List<T> {
        return list(queryWrapper)
    }

    override fun selectListByIds(idList: Collection<Serializable>): List<T> {
        return if (idList.isEmpty()) {
            emptyList()
        } else {
            listByIds(idList)
        }.toList()
    }

    override fun mapByIds(idList: Collection<Serializable>): Map<Serializable, T> {
        return selectListByIds(idList).associateBy { it.id }
    }

    override fun <K> associateBy(keySelector: (T) -> K, queryWrapper: Wrapper<T>): Map<K, T> {
        return selectList(queryWrapper).associateBy(keySelector)
    }

    override fun pagination(seeker: Seeker<T>): Pagination<T> {
        return page(seeker.pagination.toPage(), seeker.buildQueryWrapper()).toPagination()
    }

}
