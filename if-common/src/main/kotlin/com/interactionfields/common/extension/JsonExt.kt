package com.interactionfields.common.extension

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator
import com.interactionfields.common.extension.DateTimeExt.YYYY_MM_DD_HH_MM_SS
import org.ktorm.jackson.KtormModule
import java.io.InputStream
import java.text.SimpleDateFormat

/**
 * Utilities about the JSON.
 *
 * @author Ashinch
 * @date 2021/07/31
 */
object JsonExt {

    /**
     * Generate default [ObjectMapper].
     */
    val defaultMapper = ObjectMapper().apply {
        registerModule(KtormModule())
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        dateFormat = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS)
    }

    /**
     * Generate complete serialization [ObjectMapper].
     */
    val completeMapper = ObjectMapper().apply {
        setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
        activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        )
        dateFormat = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS)
    }

    /**
     * Returns the converted JSON format [String] of the [Any] object.
     */
    fun Any.toJson(customMapper: ObjectMapper? = null): String =
        (customMapper ?: defaultMapper).run { writeValueAsString(this@toJson) }

    /**
     * Returns an [T] object converted from a [String] in JSON format.
     */
    fun <T> String.toObj(customMapper: ObjectMapper? = null): T =
        (customMapper ?: defaultMapper).run { readValue(this@toObj, object : TypeReference<T>() {}) }

    /**
     * Returns an [Any] object converted from a [String] in JSON format.
     */
    fun String.toObj(type: Class<*>, customMapper: ObjectMapper? = null): Any =
        (customMapper ?: defaultMapper).run { readValue(this@toObj, type) }

    /**
     * Returns an [T] object converted from a [InputStream] in JSON format.
     * [MismatchedInputException] is thrown when no content to map due to end-of-input.
     */
    @Throws(MismatchedInputException::class)
    fun <T> InputStream.toObj(customMapper: ObjectMapper? = null): T =
        (customMapper ?: defaultMapper).run { readValue(this@toObj, object : TypeReference<T>() {}) }

    /**
     * Returns an [Any] object converted from a [InputStream] in JSON format.
     */
    fun InputStream.toObj(type: Class<*>, customMapper: ObjectMapper? = null): Any =
        (customMapper ?: defaultMapper).run { readValue(this@toObj, type) }
}
