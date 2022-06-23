package com.jxpanda.common.base

import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import kotlin.math.max
import kotlin.math.min

/**
 * 简单分页对象
 * 本来一直使用mybatis-plus写的那个分页对象
 * 但是那个对象在序列化给前端的时候，会序列化很多数据出去
 * 想把那些数据屏蔽掉，所以单独写一个简易的，中间做一个转换就行了、
 *
 * 目前只是一个简易的实现，日后考虑支持游标分页
 *
 * */
data class Pagination<T>(
        /**
         * 当前是第几页
         * */
        var current: Int = 1,
        /**
         * 一共有多少页
         * */
        var pages: Int = 0,
        /**
         * 每页多少条
         * */
        var size: Int = 20,
        /**
         * 一共多少条数据
         * */
        var total: Int = 0,
        /**
         * 下一页的页码
         * */
        var next: Int = if (current * size < total) current + 1 else current,
        /**
         * 是否有下一页的标识
         * */
        var hasNext: Boolean = next > current,
        /**
         * 数据列表
         * */
        var records: List<T> = ArrayList()
) {

    private fun <E> duplicate(records: List<E>): Pagination<E> {
        return Pagination(this.current, this.pages, this.size, this.total, this.next, this.hasNext, records)
    }

    /**
     * 这个函数实现内存分页的功能
     * 把列表加载，然后根据参数构建出一个分页数据对象
     * */
    fun <E> pagingList(data: List<E>): Pagination<E> {
        // 计算偏移值，起始
        val fromIndex = max((current - 1) * size, 0)
        // 结束
        val endIndex = min(fromIndex + size, data.size)
        // 总数取数据长度
        val total = data.size
        // 共有多少页的计算逻辑
        val pages = total / size + (if (total % size > 0) 1 else 0)
        val records = if (fromIndex > endIndex) emptyList() else data.subList(fromIndex, endIndex)
        return Pagination(current = current, pages = pages, size = size, total = total, records = records)
    }

    fun <T> toPage(): Page<T> {
        return Page(current.toLong(), size.toLong())
    }

    fun filter(predicate: (T) -> Boolean): Pagination<T> {
        return duplicate(this.records.filter(predicate))
    }

    fun <R> map(transform: (T) -> R): Pagination<R> {
        return duplicate(this.records.map(transform))
    }

    fun distinct(): Pagination<T> {
        this.records.associateBy { }
        return duplicate(this.records.distinct())
    }

    fun <K> distinctBy(selector: (T) -> K): Pagination<T> {
        return duplicate(this.records.distinctBy(selector))
    }

    /**
     * 这个是给Java用的，kotlin可以使用扩展来简化
     * */
    companion object {
        fun <T> fromPage(page: Page<T>): Pagination<T> {
            return page.toPagination()
        }
    }

}

// 写一个给kotlin用的扩展
fun <T> IPage<T>.toPagination(): Pagination<T> {
    return Pagination(current = this.current.toInt(), pages = this.pages.toInt(), size = this.size.toInt(), total = this.total.toInt(), records = this.records)
}

fun main() {
//    val pagination = Pagination<String>(current = 1, total = 9900, size = 500).toPage<String>()
//    println(pagination.pages)

    val pagination = Pagination<Int>(current = 1, size = 3)
    val listOf = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
    println(pagination.pagingList(listOf))
}