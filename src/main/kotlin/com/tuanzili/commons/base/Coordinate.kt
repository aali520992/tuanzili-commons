package com.jxpanda.common.base

import com.jxpanda.common.utils.toRadian
import java.math.BigDecimal

data class Coordinate(
        /**
         * 经度
         * */
        val longitude: Double = 0.0,
        /**
         * 纬度
         * */
        val latitude: Double = 0.0
) {

    /**
     * 弧度值对象
     * */
    val radianValue: RadianValue

    init {
        radianValue = RadianValue(longitude.toRadian(), latitude.toRadian())
    }

    /**
     * 坐标的弧度值对象，计算要用
     * */
    data class RadianValue(
            /**
             * 经度的弧度值
             * */
            val longitude: Double,
            /**
             * 纬度的弧度值
             * */
            val latitude: Double
    )


    /**
     * 支持BigDecimal类型入参
     * */
    constructor(longitude: BigDecimal, latitude: BigDecimal) : this(longitude.toDouble(), latitude.toDouble())

    /**
     * 提供单个坐标字符串的构造器
     * 约定格式一定是【经度,纬度】
     * */
    constructor(coordinate: String) : this(coordinate.split(","))

    /**
     * 支持以列表入参
     * 约定格式一定是[经度,纬度]
     * 这个暂时private掉，暂时只给上面那个构造器使用
     * */
    private constructor(coordinates: List<String>) : this(coordinates[0].toDouble(), coordinates[1].toDouble())

    /**
     * 支持字符串入参
     * */
    constructor(longitude: String, latitude: String) : this(longitude.toDouble(), latitude.toDouble())


}