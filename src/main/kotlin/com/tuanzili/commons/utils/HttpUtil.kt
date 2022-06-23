package com.jxpanda.common.utils

import com.jxpanda.common.base.Result
import org.apache.http.HttpHeaders
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpOptions
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.ssl.SSLContextBuilder
import org.apache.http.util.EntityUtils
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private const val METHOD_GET = "GET"
private const val METHOD_POST = "POST"
private const val HTTPS = "https"

@Suppress("UNCHECKED_CAST")
class HttpUtil {

    companion object {

        @JvmStatic
        @JvmOverloads
        inline fun <reified T> get(url: String, param: Any = Any()): T {
            return get(url = url, param = param, clazz = T::class.java)
        }

        @JvmStatic
        @JvmOverloads
        inline fun <reified T> get(url: String, headers: Map<String, String>, param: Any = Any()): T {
            return get(url = url, headers = headers, param = param, clazz = T::class.java)
        }

        @JvmStatic
        @JvmOverloads
        fun <T> get(url: String, headers: Map<String, String> = emptyMap(), param: Any = Any(), clazz: Class<T>): T {
            return execute(buildRequest(url, headers, param, METHOD_GET), clazz)
        }

        /**
         * 默认预设了
         * "Content-Type", "application/json;charset=utf8"
         * */
        @JvmStatic
        @JvmOverloads
        inline fun <reified T> jsonPost(url: String, param: Any = Any()): T {
            return post(url = url, headers = mapOf(HttpHeaders.CONTENT_TYPE to ContentType.APPLICATION_JSON.mimeType), param = param, clazz = T::class.java)
        }

        /**
         * 默认预设了
         * "Content-Type", "application/x-www-form-urlencoded;charset=utf8"
         * */
        @JvmStatic
        @JvmOverloads
        inline fun <reified T> formPost(url: String, param: Any = Any()): T {
            return post(url = url, headers = mapOf(HttpHeaders.CONTENT_TYPE to ContentType.APPLICATION_FORM_URLENCODED.mimeType), param = param, clazz = T::class.java)
        }

        @JvmStatic
        @JvmOverloads
        inline fun <reified T> post(url: String, param: Any = Any()): T {
            return post(url = url, param = param, clazz = T::class.java)
        }

        @JvmStatic
        @JvmOverloads
        inline fun <reified T> post(url: String, headers: Map<String, String>, param: Any = Any()): T {
            return post(url = url, headers = headers, param = param, clazz = T::class.java)
        }

        @JvmStatic
        @JvmOverloads
        fun <T> post(url: String, headers: Map<String, String> = emptyMap(), param: Any = Any(), clazz: Class<T>): T {
            return execute(buildRequest(url, headers, param, METHOD_POST), clazz)
        }

        @JvmStatic
        private fun httpClient(https: Boolean = false, caPath: String = "", caAlias: String = ""): HttpClient {
            return if (https || (caPath.isNotBlank() && caAlias.isNotBlank())) {
                HttpClientBuilder.create().setSSLSocketFactory(buildSocketFactory(caPath, caAlias)).build()
            } else {
                HttpClientBuilder.create().build()
            }
        }

        /**
         * 创建https套接字，证书的加载方式下个版本再迭代
         * */
        @JvmStatic
        private fun buildSocketFactory(caPath: String = "", caAlias: String = ""): SSLConnectionSocketFactory {
            return SSLConnectionSocketFactory(SSLContextBuilder.create().build())
        }

        /**
         * 执行请求，返回字符串类型的
         * */
        @JvmStatic
        private fun <T> execute(request: HttpRequestBase, clazz: Class<T>): T {
            // 下个版本再考虑如何加载证书
            val response = EntityUtils.toString(httpClient(request.uri.scheme == HTTPS).execute(request).entity, StandardCharsets.UTF_8)
            return if (clazz == String::class.java) {
                response as T
            } else {
                JsonUtil.fromJson(response, clazz)
            }
        }

        @JvmStatic
        private fun buildRequest(url: String, headers: Map<String, String> = emptyMap(), param: Any, method: String): HttpRequestBase {
            val paramMap = param.toMap()
            return when (method) {
                METHOD_GET -> {
                    val query = if (paramMap.isEmpty()) {
                        ""
                    } else {
                        paramMap.map { "${it.key}=${URLEncoder.encode(it.value.toString(), "UTF-8")}" }.joinToString(separator = "&", prefix = "?")
                    }
                    HttpGet("$url$query")
                }
                METHOD_POST -> {
                    HttpPost(url).apply {
                        if (paramMap.isNotEmpty()) {
                            this.entity = StringEntity(param.toJson())
                        }
                    }
                }
                else -> {
                    HttpOptions(url)
                }
            }.apply {
                if (headers.isNotEmpty()) {
                    headers.forEach { (key, value) ->
                        this.addHeader(key, value)
                    }
                }
            }
        }

    }


}

fun main() {
    println(HttpUtil.get<Result<String>>("https://api.rrlsz.com.cn/sdd/auth/wechat/app/id"))
}