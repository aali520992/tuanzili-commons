package com.jxpanda.common.utils

import com.jxpanda.common.base.Coordinate
import kotlin.math.*

class AtlasUtil {

    companion object {

        /**
         * 地球半径，单位：米
         * 6371.393千米
         * */
        private const val EARTH_RADIUS = 6371393.0

        /**
         * 一个单位π的弧度
         * */
        private const val RADIAN_PI = Math.PI / 180


        /**
         * 计算两个坐标之间的距离，计算结果的单位是【米】
         * 维基百科推荐使用Haversine公式，理由是Great-circle distance公式用到了大量余弦函数，
         * 而两点间距离很短时（比如地球表面上相距几百米的两点），余弦函数会得出0.999...的结果，
         * 会导致较大的舍入误差。而Haversine公式采用了正弦函数，即使距离很小，也能保持足够的有效数字。
         * 公式：
         * */
        fun distance(coordinateA: Coordinate, coordinateB: Coordinate): Double {
            val (longitudeA, latitudeA) = coordinateA.radianValue
            val (longitudeB, latitudeB) = coordinateB.radianValue

            val alpha = latitudeA - latitudeB
            val theta = longitudeA - longitudeB

            val delta = haversine(alpha) + cos(latitudeA) * cos(latitudeB) * haversine(theta)

            return EARTH_RADIUS * 2 * atan2(sqrt(delta), sqrt(1 - delta))
        }

        /**
         * 角度转弧度
         * */
        fun degree2radian(degree: Double): Double {
            return degree * RADIAN_PI
        }

        /**
         * Haversine公式
         * haversine(θ) = sin^2(θ/2) = (1-cos(θ)) / 2
         * 入参theta应该是弧度，而不是角度
         * @param theta 弧度值
         * */
        private fun haversine(theta: Double): Double {
            val d = theta / 2
            return sin(d) * sin(d)
        }


    }

}

fun Double.toRadian() = AtlasUtil.degree2radian(this)

fun Coordinate.distanceOf(coordinate: Coordinate): Double = AtlasUtil.distance(this, coordinate)

//fun main() {
//    val coordinateA = Coordinate(113.0796, 23.40995)
//    val coordinateB = Coordinate(113.0793, 23.40968)
//    println(AtlasUtil.distance(coordinateA, coordinateB))
//}