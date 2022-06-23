package com.jxpanda.common.base

import com.baomidou.mybatisplus.core.conditions.Wrapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.core.toolkit.Constants
import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Param
import java.io.Serializable

private const val TABLE_NAME = "tableName"

interface KtMapper<T> : BaseMapper<T> {

    @Delete("DELETE FROM \${tableName} \${ew.customSqlSegment}")
    fun physicalDelete(@Param(TABLE_NAME) tableName: String, @Param(Constants.WRAPPER) queryWrapper: Wrapper<T>): Boolean

    @Delete("DELETE FROM \${tableName} WHERE id = \${id}")
    fun physicalDeleteById(@Param(TABLE_NAME) tableName: String, @Param(Entity.ID) id: Serializable): Boolean

    @Delete("<script>DELETE FROM \${tableName} WHERE id IN (<foreach collection=\"ids\" item=\"id\" separator=\",\">#{id}</foreach>)</script>")
    fun physicalDeleteByIds(@Param(TABLE_NAME) tableName: String, @Param("ids") ids: List<Serializable>): Boolean

}