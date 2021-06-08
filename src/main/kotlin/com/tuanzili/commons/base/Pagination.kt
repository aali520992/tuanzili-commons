package com.tuanzili.commons.base

import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.extension.plugins.pagination.Page

/**
 * 简单分页对象
 * 本来一直使用mybatis-plus写的那个分页对象
 * 但是那个对象在序列化给前端的时候，会序列化很多数据出去
 * 想把那些数据屏蔽掉，所以单独写一个简易的，中间做一个转换就行了、
 *
 * 目前只是一个简易的实现，日后考虑支持游标分页
 *
 * */
class Pagination<T>(
        /**
         * 当前是第几页
         * */
        val current: Int = 1,
        /**
         * 一共有多少页
         * */
        val pages: Int = 0,
        /**
         * 每页多少条
         * */
        val size: Int = 20,
        /**
         * 一共多少条数据
         * */
        val total: Int = 0,
        /**
         * 数据列表
         * */
        val records: MutableList<T> = ArrayList()
) {

    fun <T> toPage(): Page<T> {
        return Page(current.toLong(), size.toLong())
    }

    fun <R> map(transform: (T) -> R): Pagination<R> {
        return Pagination(this.current, this.pages, this.size, this.total, this.records.map(transform).toMutableList())
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
    return Pagination(this.current.toInt(), this.pages.toInt(), this.size.toInt(), this.total.toInt(), this.records)
}