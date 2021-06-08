package com.tuanzili.commons.base

import java.io.Serializable

const val MESSAGE_SUCCESS = "成功"
const val MESSAGE_FAILED = "请求失败"
const val MESSAGE_UN_LOGIN = "请登录后再操作"
const val MESSAGE_FORBIDDEN = "无访问权限"
const val MESSAGE_PARAM_ERROR = "参数有误"

/**
 * Created by Panda on 2018/7/23
 */
@Suppress("UNCHECKED_CAST")
data class Result<T>(
        /**
         * 返回值code
         * */
        var code: Int,
        /**
         * 基于code计算的一个计算值（成功或失败）
         * */
        var success: Boolean = when (code) {
            CODE.OK.code, CODE.CREATED.code, CODE.ACCEPTED.code, CODE.NO_CONTENT.code -> true
            else -> false
        },
        /**
         * 用户消息（一般来说，返回不成功的话，渲染这条消息）
         * */
        var message: String,
        /**
         * 要返回的数据
         * */
        var data: T,
        /**
         * 扩展参数
         * */
        var extend: Any = Any()
) : Serializable {

    /**
     * 为了方便，提供一个基于枚举类的构造器
     * */
    private constructor(codeEnum: CODE, data: T? = null, message: String = "", extend: Any = Any()) : this(code = codeEnum.code,
            data = data ?: Any() as T,
            message = if (message.isEmpty()) codeEnum.message else message,
            extend = extend
    )

    /**
     * 把当前结果转为另外一种结果
     * 这个函数在使用AOP拦截返回值的时候会需要使用到
     * */
    fun covert(result: Result<T>) {
        this.apply {
            this.code = result.code
            this.success = result.success
            this.message = result.message
            this.data = result.data
            this.extend = result.extend
        }
    }

    /**
     * 常用的静态工厂函数
     * */
    companion object {
        /**
         * 请求成功
         * */
        @JvmStatic
        @JvmOverloads
        fun <T> ok(data: T? = null,
                   message: String = MESSAGE_SUCCESS,
                   httpCode: CODE = if (data == null) CODE.NO_CONTENT else CODE.OK
        ): Result<T> = Result(httpCode, data, message)

        /**
         * 请求失败（返回404）
         * */
        @JvmStatic
        @JvmOverloads
        fun <T> failed(message: String = MESSAGE_FAILED): Result<T> = Result(CODE.NOT_FOUND, message = message)

        /**
         * 请求失败，重载（返回404）
         * */
        @JvmStatic
        @JvmOverloads
        fun <T> failed(message: String = MESSAGE_FAILED, extend: Any): Result<T> = Result(CODE.NOT_FOUND, message = message, extend = extend)

        /**
         * 未登录
         * */
        @JvmStatic
        @JvmOverloads
        fun <T> unLogin(message: String = MESSAGE_UN_LOGIN): Result<T> = Result(CODE.UNAUTHORIZED, message = message)

        /**
         * 无访问权限
         * */
        @JvmStatic
        @JvmOverloads
        fun <T> forbidden(message: String = MESSAGE_FORBIDDEN): Result<T> = Result(CODE.FORBIDDEN, message = message)

        /**
         * 参数校验出错
         * */
        @JvmStatic
        @JvmOverloads
        fun <T> paramError(message: String = MESSAGE_PARAM_ERROR, extend: Any = Any()): Result<T> = Result(CODE.UNPROCESSABLE_ENTITY, message = message, extend = extend)

        /**
         * 服务器内部报错
         * */
        @JvmStatic
        fun <T> error(message: String = ""): Result<T> = Result(CODE.INTERNAL_SERVER_ERROR,message = message)

    }

    /**
     * 返回值状态码的枚举类
     * 基于HTTP状态码
     * */
    enum class CODE(
            val code: Int,
            private val description: String,
            val message: String = ""
    ) {
        /**
         *  Successful 2xx
         * */
        OK(200, "成功", "SUCCESS"),
        CREATED(201, "请求已经被实现，而且有一个新的资源已经依据请求的需要而建立。"),
        ACCEPTED(202, "服务器已接受请求，但尚未处理。"),
        NO_CONTENT(204, "服务器成功处理了请求，但不需要返回任何实体内容。"),
        /**
         * Redirection 3xx
         * 暂时不记录3xx的状态码，因为很可能是用不到的
         * */
        /**
         * Client Error 4xx
         * */
        BAD_REQUEST(400, "语义有误，当前请求无法被服务器理解。"),
        UNAUTHORIZED(401, "当前请求需要用户验证。"),
        FORBIDDEN(403, "服务器已经理解请求，但是拒绝执行它。"),
        NOT_FOUND(404, "请求失败，请求所希望得到的资源未被在服务器上发现。"),
        UNPROCESSABLE_ENTITY(422, "请求格式正确，但是由于含有语义错误，无法响应。"),
        /**
         * Server Error 5xx
         * */
        INTERNAL_SERVER_ERROR(500, "服务器报错");

        operator fun component1(): Int = code
        operator fun component2(): String = description


    }
}
