package com.tuanzili.commons.base

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.fasterxml.jackson.databind.ObjectMapper
import com.tuanzili.commons.constants.DateTimeConstant
import com.tuanzili.commons.utils.toDateTimeString
import java.lang.reflect.Method
import java.time.LocalDateTime
import java.util.function.Function

/**
 * 搜索对象封装，该类的功能是把外部传入的筛选条件构造成为一个mybatis-plus的QueryWrapper对象
 * 思路：把外部对象通过反射的形式，转为QueryWrapper对象
 * 本来想命名为Filter，但是觉得Seeker比较帅
 * */
@Suppress("UNCHECKED_CAST")
data class Seeker<T>(
    /**
         * 筛选条件列表（一群探机）
         * */
        val probes: MutableList<Probe> = mutableListOf(),
    /**
         * 排序条件列表（一群分拣机）
         * */
        val sorters: MutableList<Sorter> = mutableListOf(),
    /**
         * 分页对象
         * */
        val pagination: Pagination<T> = Pagination()
) {

    /**
     * 构建mybatis-plus用的查询对象
     * */
    fun buildQueryWrapper(): QueryWrapper<T> {
        val queryWrapper = QueryWrapper<T>()
        // 构建条件（测试阶段，暂时不考虑突触的链接关系）
        probes.filter { it.extend != Extend.SKIP }.forEach {
            val probe = it.extend.handle(it)
            // 如果有OR条件拼接，执行OR条件（复杂的AND和OR条件拼接暂时不实现）
            if (probe.synapse == Synapse.OR) {
                probe.synapse.method.invoke(queryWrapper)
            }
            probe.rule.execute(queryWrapper, probe.field, probe.value)
        }
        // 构建排序
        sorters.forEach {
            it.sorting.method.invoke(queryWrapper, it.field)
        }
        return queryWrapper
    }

    fun and(probe: Probe): Seeker<T> {
        this.probes.add(probe)
        return this
    }

    fun and(field: String, value: Any, rule: Rule = Rule.EQ): Seeker<T> {
        this.probes.add(Probe(field, value, rule))
        return this
    }

    fun or(field: String, value: Any): Seeker<T> {
        this.probes.add(Probe(field = field, value = value, synapse = Synapse.OR))
        return this
    }

    fun asc(vararg fields: String): Seeker<T> {
        this.sorters.addAll(fields.map { Sorter(it) })
        return this
    }

    fun desc(vararg fields: String): Seeker<T> {
        this.sorters.addAll(fields.map { Sorter(it, Sorting.DESC) })
        return this
    }

    /**
     * 探机对象
     * */
    data class Probe(
        /**
             * 字段
             * */
            val field: String = "",
        /**
             * 值（以列表形式入参）
             * */
            val value: Any = Any(),
        /**
             * 规则（取值都是QueryWrapper的【函数名】）
             * */
            val rule: Rule = Rule.EQ,
        /**
             * 扩展参数（例如：日期筛选的时候，入参DAY，就表明精确到日；入参SECOND，表明精确到秒）
             * 这个参数是一个补充参数，方便以后扩展
             * 默认不取值
             * */
            val extend: Extend = Extend.NONE,
        /**
             * 这个单词是突触的意思，取值，AND、OR，默认AND
             * 连接两个条件对象之间的方式，AND（交集）或者OR（并集）
             * */
            val synapse: Synapse = Synapse.AND
    )

    /**
     * 排序对象
     * sorter有分拣机的意思
     * */
    data class Sorter(
            /**
             * 排序字段
             * */
            val field: String = "",
            /**
             * 排序，两个取值，ASC（正序）、DESC（倒序）
             * 默认正序排序
             * */
            val sorting: Sorting = Sorting.ASC
    )

    /**
     * 规则对象，负责枚举目前支持的筛选类型，以及对应的函数
     * 这个只做常用的封装，非常用的，等项目开发过程中使用到了，再更新
     * */
    enum class Rule(
            val method: Method
    ) {
        EQ(QueryWrapper::class.java.getMethod("eq", Any::class.java, Any::class.java)),
        NE(QueryWrapper::class.java.getMethod("ne", Any::class.java, Any::class.java)),
        GT(QueryWrapper::class.java.getMethod("gt", Any::class.java, Any::class.java)),
        GE(QueryWrapper::class.java.getMethod("ge", Any::class.java, Any::class.java)),
        LT(QueryWrapper::class.java.getMethod("lt", Any::class.java, Any::class.java)),
        LE(QueryWrapper::class.java.getMethod("le", Any::class.java, Any::class.java)),
        IN(QueryWrapper::class.java.getMethod("in", Any::class.java, Collection::class.java)),
        LIKE(QueryWrapper::class.java.getMethod("like", Any::class.java, Any::class.java)),
        BETWEEN(QueryWrapper::class.java.getMethod("between", Any::class.java, Any::class.java, Any::class.java)) {
            override fun <T> execute(queryWrapper: QueryWrapper<T>, field: String, param: Any) {
                // BETWEEN的调用要把入参拆分，不然会报参数数量不对的异常
                val arrayParam = param as ArrayList<*>
                this.method.invoke(queryWrapper, field, arrayParam[0], arrayParam[1])
            }
        };

        /**
         * 执行函数的代理函数
         * 重写这个函数可以做一些适配或者是预处理的事情
         * */
        open fun <T> execute(queryWrapper: QueryWrapper<T>, field: String, param: Any) {
            this.method.invoke(queryWrapper, field, param)
        }

    }

    enum class Sorting(
            val method: Method
    ) {
        ASC(QueryWrapper::class.java.getMethod("orderByAsc", Any::class.java)),
        DESC(QueryWrapper::class.java.getMethod("orderByDesc", Any::class.java));
    }

    enum class Synapse(
            val method: Method
    ) {
        AND(QueryWrapper::class.java.getMethod("and", Function::class.java)),
        OR(QueryWrapper::class.java.getMethod("or"));
    }

    /**
     * 扩展枚举，扩展时用来处理一切特殊情况的
     * 例如，SKIP标记为整个Probe都跳过，不处理
     * DATE则说明参数入参是日期，要预处理日期参数
     * 其中包含一个名为handle的用来预处理probe对象的函数，通过自定义该函数，可以实现灵活处理probe对象的功能
     * */
    enum class Extend(
            val handle: (probe: Probe) -> Probe = { it }
    ) {

        NONE, SKIP, DATE({
            // 日期类型的预处理，如果是BETWEEN逻辑，要把入参做一次处理，第一个参数设置时间到日期的00:00:00，第二个则设定到23:59:59
            if (it.rule == Rule.BETWEEN) {
                // 这是与前端的一个约定，所有的日期入参都以字符串的形式入参
                // 其格式为【yyyy-MM-dd】或【yyyy-MM-dd HH:mm:ss】
                val param = it.value as ArrayList<String>
                // 处理逻辑是这样的，如果一个时间是yyyy-MM-dd HH:mm:ss的格式，不错处理
                // 否则，如果是第一个参数，日期设定到当天的00:00:00，第二个参数设定为23:59:59.999999999
                // 另外一个约定，这个函数之所以要这么处理，是因为所有数据库日期类型都用datetime类型
                param[0] = param[0].toDateTimeString()
                param[1] = param[1].toDateTimeString(DateTimeConstant.STRING_TIME_END)
            }
            it
        });
    }

}

data class Test(
        val username: String,
        val createDate: LocalDateTime
)

fun main() {
    val json = """
    {
        "probes":[
            {
                "field":"created_date",
                "value":["2019-07-28","2019-07-29"],
                "rule":"BETWEEN"
            }
        ]
    }
""".trimIndent()

    val jackson = ObjectMapper()
    val seeker = jackson.readValue(json, Seeker::class.java)
    val queryWrapper = seeker.buildQueryWrapper()
    println(queryWrapper.sqlSegment)
    println(queryWrapper.paramNameValuePairs)

}