package com.jxpanda.common.base

import com.baomidou.mybatisplus.core.conditions.Wrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import java.io.Serializable

/**
 * 改为抽象类了，因为一定要知道泛型实体的具体类型是多少，为了实现尽可能的【空安全】设计
 * 一味拥抱空安全设计或许不太科学
 * 目前先激进的试用一段时间，有问题再迭代吧，路要一步步走
 * */
abstract class KtServiceImpl<M : KtMapper<T>, T : Entity> : ServiceImpl<M, T>(), KtService<T> {

    abstract val emptyEntity: T

    override fun getById(id: Serializable): T {
        return super.getById(id) ?: emptyEntity
    }

    override fun delete(queryWrapper: Wrapper<T>): Boolean {
        return baseMapper.physicalDelete(emptyEntity.tableName(), queryWrapper)
    }

    override fun deleteById(id: Serializable): Boolean {
        return baseMapper.physicalDeleteById(emptyEntity.tableName(), id)
    }

    override fun deleteByIds(ids: List<Serializable>): Boolean {
        return baseMapper.physicalDeleteByIds(emptyEntity.tableName(), ids)
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

    override fun updateBatchById(updaterList: List<Entity.Updater<T>>): Boolean {
        if (updaterList.isEmpty()) {
            return false
        }
        updaterList.filter { !it.id.isNullOrBlank() }.forEach {
            updateById(it)
        }
        return true
    }

    override fun saveOrUpdate(entity: T): Boolean {
        return if (entity.id.isIdEffective()) {
            updateById(entity)
        } else {
            save(entity)
        }
    }


    override fun saveOrUpdate(updater: Entity.Updater<T>): Boolean {
        return if (updater.id.isNullOrBlank()) {
            save(updater.toEntity())
        } else {
            updateById(updater)
        }
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

    override fun selectList(seeker: Seeker<T>): List<T> {
        return selectList(seeker.buildQueryWrapper())
    }

}
